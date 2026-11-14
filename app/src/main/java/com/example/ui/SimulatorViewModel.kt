package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SimulatorCalendar
import com.example.data.SimulatorFormatter
import com.example.data.SimulatorRepository
import com.example.data.SimulatorRules
import com.example.model.DayOverride
import com.example.model.SimulatorPersistedState
import com.example.model.TransactionRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SwitchLockReason {
  NONE,
  INSUFFICIENT_BALANCE,
  WEEKEND_CLOSED,
  BOTH_BALANCE_AND_WEEKEND,
}

data class SimulatorUiState(
  val isLoading: Boolean = true,
  val virtualBalanceCents: Long = 0L,
  val formattedBalance: String = "$0.00",
  val totalEarnedCents: Long = 0L,
  val formattedTotalEarned: String = "$0.00",
  val isSwitchOn: Boolean = false,
  val isSwitchEnabled: Boolean = false,
  val hasMinimumBalance: Boolean = false,
  val isEffectiveWeekday: Boolean = true,
  val claimedToday: Boolean = false,
  val lockReason: SwitchLockReason = SwitchLockReason.INSUFFICIENT_BALANCE,
  val simulatedDayOffset: Int = 0,
  val dayOverride: DayOverride = DayOverride.AUTO,
  val dayBadgeText: String = "",
  val effectiveDayDescription: String = "",
  val transactions: List<TransactionRecord> = emptyList(),
  val equityPointsCents: List<Long> = listOf(0L),
)

class SimulatorViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = SimulatorRepository(application.applicationContext)

  val uiState: StateFlow<SimulatorUiState> =
    repository.stateFlow
      .map { persisted -> mapToUiState(persisted) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SimulatorUiState(),
      )

  init {
    viewModelScope.launch { repository.reconcileOnLaunch() }
  }

  fun onSimulateDeposit() {
    viewModelScope.launch { repository.simulateDeposit() }
  }

  fun onToggleTradingSwitch(enable: Boolean) {
    viewModelScope.launch { repository.toggleTradingSwitch(enable) }
  }

  fun onSimulateNextDay() {
    viewModelScope.launch { repository.simulateNextDay() }
  }

  fun onSelectDayOverride(override: DayOverride) {
    viewModelScope.launch { repository.setDayOverride(override) }
  }

  fun onResetAccount() {
    viewModelScope.launch { repository.resetAccount() }
  }

  private fun mapToUiState(persisted: SimulatorPersistedState): SimulatorUiState {
    val hasMinBalance = persisted.virtualBalanceCents >= SimulatorRules.MINIMUM_BALANCE_CENTS
    val isWeekday =
      SimulatorCalendar.isEffectiveWeekday(persisted.dayOverride, persisted.simulatedDayOffset)
    val activeKey = SimulatorCalendar.activeDayKey(persisted.simulatedDayOffset)
    val claimedToday = persisted.claimedDayKey == activeKey
    val canEnableSwitch = hasMinBalance && isWeekday

    val lockReason =
      when {
        !hasMinBalance && !isWeekday -> SwitchLockReason.BOTH_BALANCE_AND_WEEKEND
        !hasMinBalance -> SwitchLockReason.INSUFFICIENT_BALANCE
        !isWeekday -> SwitchLockReason.WEEKEND_CLOSED
        else -> SwitchLockReason.NONE
      }

    // Build chronological equity curve from transactions (reversed since newest is first)
    val equityPoints =
      if (persisted.transactions.isEmpty()) {
        listOf(0L, persisted.virtualBalanceCents)
      } else {
        listOf(0L) + persisted.transactions.asReversed().map { it.balanceAfterCents }
      }

    return SimulatorUiState(
      isLoading = false,
      virtualBalanceCents = persisted.virtualBalanceCents,
      formattedBalance = SimulatorFormatter.formatCurrency(persisted.virtualBalanceCents),
      totalEarnedCents = persisted.totalEarnedCents,
      formattedTotalEarned = SimulatorFormatter.formatCurrency(persisted.totalEarnedCents),
      isSwitchOn = persisted.isSwitchOn && canEnableSwitch,
      isSwitchEnabled = canEnableSwitch,
      hasMinimumBalance = hasMinBalance,
      isEffectiveWeekday = isWeekday,
      claimedToday = claimedToday,
      lockReason = lockReason,
      simulatedDayOffset = persisted.simulatedDayOffset,
      dayOverride = persisted.dayOverride,
      dayBadgeText =
        SimulatorCalendar.effectiveDayBadgeText(
          persisted.dayOverride,
          persisted.simulatedDayOffset,
        ),
      effectiveDayDescription =
        SimulatorCalendar.effectiveDayDescription(
          persisted.dayOverride,
          persisted.simulatedDayOffset,
        ),
      transactions = persisted.transactions,
      equityPointsCents = equityPoints,
    )
  }
}
