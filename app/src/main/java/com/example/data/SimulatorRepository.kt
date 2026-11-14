package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.DayOverride
import com.example.model.SimulatorPersistedState
import com.example.model.TransactionRecord
import com.example.model.TransactionType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.simulatorDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "paper_trade_simulator_store")

object SimulatorRules {
  const val MINIMUM_BALANCE_CENTS = 10_000L // $100.00
  const val DEPOSIT_AMOUNT_CENTS = 10_000L // +$100.00
  const val DAILY_BOT_REWARD_CENTS = 100L // +$1.00
}

object SimulatorFormatter {
  private val usdFormatter: NumberFormat
    get() = NumberFormat.getCurrencyInstance(Locale.US)

  fun formatCurrency(cents: Long): String {
    return usdFormatter.format(cents / 100.0)
  }

  fun formatSignedCurrency(cents: Long): String {
    if (cents == 0L) return "$0.00"
    val absFormatted = formatCurrency(kotlin.math.abs(cents))
    return if (cents > 0) "+$absFormatted" else "-$absFormatted"
  }

  fun formatTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("MMM d, h:mm:ss a", Locale.US)
    return sdf.format(Date(millis))
  }
}

object SimulatorCalendar {
  fun currentDateIso(calendar: Calendar = Calendar.getInstance()): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return sdf.format(calendar.time)
  }

  fun simulatedCalendar(
    simulatedDayOffset: Int,
    baseCalendar: Calendar = Calendar.getInstance(),
  ): Calendar {
    return (baseCalendar.clone() as Calendar).apply {
      add(Calendar.DAY_OF_YEAR, simulatedDayOffset)
    }
  }

  fun activeDayKey(
    simulatedDayOffset: Int,
    baseCalendar: Calendar = Calendar.getInstance(),
  ): String {
    val cal = simulatedCalendar(simulatedDayOffset, baseCalendar)
    return "${currentDateIso(cal)}#d$simulatedDayOffset"
  }

  fun isCalendarWeekday(calendar: Calendar): Boolean {
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY
  }

  fun isEffectiveWeekday(
    override: DayOverride,
    simulatedDayOffset: Int,
    baseCalendar: Calendar = Calendar.getInstance(),
  ): Boolean {
    return when (override) {
      DayOverride.AUTO -> isCalendarWeekday(simulatedCalendar(simulatedDayOffset, baseCalendar))
      DayOverride.FORCE_WEEKDAY -> true
      DayOverride.FORCE_WEEKEND -> false
    }
  }

  fun effectiveDayBadgeText(
    override: DayOverride,
    simulatedDayOffset: Int,
    baseCalendar: Calendar = Calendar.getInstance(),
  ): String {
    val cal = simulatedCalendar(simulatedDayOffset, baseCalendar)
    val dayFmt = SimpleDateFormat("EEE, MMM d", Locale.US)
    val baseLabel = dayFmt.format(cal.time).uppercase(Locale.US)
    return when (override) {
      DayOverride.AUTO -> "$baseLabel • DAY +$simulatedDayOffset"
      DayOverride.FORCE_WEEKDAY -> "WEEKDAY OVERRIDE • DAY +$simulatedDayOffset"
      DayOverride.FORCE_WEEKEND -> "WEEKEND OVERRIDE • DAY +$simulatedDayOffset"
    }
  }

  fun effectiveDayDescription(
    override: DayOverride,
    simulatedDayOffset: Int,
    baseCalendar: Calendar = Calendar.getInstance(),
  ): String {
    val cal = simulatedCalendar(simulatedDayOffset, baseCalendar)
    val fullDayName = SimpleDateFormat("EEEE", Locale.US).format(cal.time)
    val realIsWeekday = isCalendarWeekday(cal)
    return when (override) {
      DayOverride.AUTO -> {
        val marketStatus = if (realIsWeekday) "Weekday (Market Open)" else "Weekend (Market Closed)"
        "Effective Day: $fullDayName — $marketStatus"
      }
      DayOverride.FORCE_WEEKDAY ->
        "Effective Day: Forced Weekday (Mon–Fri Simulated • Real: $fullDayName)"
      DayOverride.FORCE_WEEKEND ->
        "Effective Day: Forced Weekend (Sat–Sun Simulated • Real: $fullDayName)"
    }
  }
}

class SimulatorRepository(private val context: Context) {

  private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
  private val stateAdapter = moshi.adapter(SimulatorPersistedState::class.java)
  private val statePreferenceKey = stringPreferencesKey("simulator_state_json")

  val stateFlow: Flow<SimulatorPersistedState> =
    context.simulatorDataStore.data.map { prefs ->
      val rawJson = prefs[statePreferenceKey]
      val parsed =
        if (!rawJson.isNullOrBlank()) {
          runCatching { stateAdapter.fromJson(rawJson) }.getOrNull()
        } else {
          null
        }
      reconcileRealDateRollover(parsed ?: SimulatorPersistedState())
    }

  suspend fun reconcileOnLaunch() {
    updateState { current -> reconcileRealDateRollover(current) }
  }

  suspend fun simulateDeposit(nowMillis: Long = System.currentTimeMillis()) {
    updateState { current ->
      val newBalance = current.virtualBalanceCents + SimulatorRules.DEPOSIT_AMOUNT_CENTS
      val record =
        TransactionRecord(
          id = UUID.randomUUID().toString(),
          timestampMillis = nowMillis,
          actionType = "Virtual Deposit",
          type = TransactionType.DEPOSIT,
          amountCents = SimulatorRules.DEPOSIT_AMOUNT_CENTS,
          balanceAfterCents = newBalance,
          simulatedDayNumber = current.simulatedDayOffset,
        )
      current.copy(
        virtualBalanceCents = newBalance,
        lastRealDateIso = SimulatorCalendar.currentDateIso(),
        transactions = listOf(record) + current.transactions,
      )
    }
  }

  suspend fun toggleTradingSwitch(
    enable: Boolean,
    nowMillis: Long = System.currentTimeMillis(),
  ) {
    updateState { current ->
      val isWeekday =
        SimulatorCalendar.isEffectiveWeekday(current.dayOverride, current.simulatedDayOffset)
      val hasMinBalance = current.virtualBalanceCents >= SimulatorRules.MINIMUM_BALANCE_CENTS

      if (!enable) {
        return@updateState current.copy(isSwitchOn = false)
      }

      if (!hasMinBalance || !isWeekday) {
        return@updateState current.copy(isSwitchOn = false)
      }

      val activeKey = SimulatorCalendar.activeDayKey(current.simulatedDayOffset)
      val alreadyClaimedToday = current.claimedDayKey == activeKey

      if (alreadyClaimedToday) {
        current.copy(isSwitchOn = true)
      } else {
        val newBalance = current.virtualBalanceCents + SimulatorRules.DAILY_BOT_REWARD_CENTS
        val newEarned = current.totalEarnedCents + SimulatorRules.DAILY_BOT_REWARD_CENTS
        val record =
          TransactionRecord(
            id = UUID.randomUUID().toString(),
            timestampMillis = nowMillis,
            actionType = "Weekday Bot Profit +$1.00",
            type = TransactionType.BOT_PROFIT,
            amountCents = SimulatorRules.DAILY_BOT_REWARD_CENTS,
            balanceAfterCents = newBalance,
            simulatedDayNumber = current.simulatedDayOffset,
          )
        current.copy(
          virtualBalanceCents = newBalance,
          totalEarnedCents = newEarned,
          isSwitchOn = true,
          claimedDayKey = activeKey,
          lastRealDateIso = SimulatorCalendar.currentDateIso(),
          transactions = listOf(record) + current.transactions,
        )
      }
    }
  }

  suspend fun simulateNextDay(nowMillis: Long = System.currentTimeMillis()) {
    updateState { current ->
      val nextOffset = current.simulatedDayOffset + 1
      val record =
        TransactionRecord(
          id = UUID.randomUUID().toString(),
          timestampMillis = nowMillis,
          actionType = "Simulated Next Day (Day +$nextOffset)",
          type = TransactionType.DAY_ADVANCE,
          amountCents = 0L,
          balanceAfterCents = current.virtualBalanceCents,
          simulatedDayNumber = nextOffset,
        )
      current.copy(
        simulatedDayOffset = nextOffset,
        isSwitchOn = false,
        lastRealDateIso = SimulatorCalendar.currentDateIso(),
        transactions = listOf(record) + current.transactions,
      )
    }
  }

  suspend fun setDayOverride(override: DayOverride) {
    updateState { current ->
      val nextIsWeekday =
        SimulatorCalendar.isEffectiveWeekday(override, current.simulatedDayOffset)
      current.copy(
        dayOverride = override,
        isSwitchOn = if (nextIsWeekday) current.isSwitchOn else false,
      )
    }
  }

  suspend fun resetAccount() {
    updateState {
      SimulatorPersistedState(lastRealDateIso = SimulatorCalendar.currentDateIso())
    }
  }

  private fun reconcileRealDateRollover(state: SimulatorPersistedState): SimulatorPersistedState {
    val todayIso = SimulatorCalendar.currentDateIso()
    return if (state.lastRealDateIso.isNotEmpty() && state.lastRealDateIso != todayIso) {
      state.copy(isSwitchOn = false, lastRealDateIso = todayIso)
    } else if (state.lastRealDateIso.isEmpty()) {
      state.copy(lastRealDateIso = todayIso)
    } else {
      state
    }
  }

  private suspend fun updateState(
    transform: (SimulatorPersistedState) -> SimulatorPersistedState
  ) {
    context.simulatorDataStore.edit { prefs ->
      val rawJson = prefs[statePreferenceKey]
      val current =
        if (!rawJson.isNullOrBlank()) {
          runCatching { stateAdapter.fromJson(rawJson) }.getOrNull()
        } else {
          null
        } ?: SimulatorPersistedState()
      val updated = transform(reconcileRealDateRollover(current))
      prefs[statePreferenceKey] = stateAdapter.toJson(updated)
    }
  }
}
