package com.example.model

import com.squareup.moshi.JsonClass

enum class DayOverride(val label: String) {
  AUTO("Auto"),
  FORCE_WEEKDAY("Force Weekday"),
  FORCE_WEEKEND("Force Weekend"),
}

enum class TransactionType {
  DEPOSIT,
  BOT_PROFIT,
  DAY_ADVANCE,
}

@JsonClass(generateAdapter = true)
data class TransactionRecord(
  val id: String,
  val timestampMillis: Long,
  val actionType: String,
  val type: TransactionType,
  val amountCents: Long,
  val balanceAfterCents: Long,
  val simulatedDayNumber: Int,
)

@JsonClass(generateAdapter = true)
data class SimulatorPersistedState(
  val virtualBalanceCents: Long = 0L,
  val totalEarnedCents: Long = 0L,
  val isSwitchOn: Boolean = false,
  val claimedDayKey: String? = null,
  val simulatedDayOffset: Int = 0,
  val lastRealDateIso: String = "",
  val dayOverride: DayOverride = DayOverride.AUTO,
  val transactions: List<TransactionRecord> = emptyList(),
)
