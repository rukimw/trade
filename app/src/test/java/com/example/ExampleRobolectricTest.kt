package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SimulatorCalendar
import com.example.data.SimulatorFormatter
import com.example.data.SimulatorRepository
import com.example.model.DayOverride
import java.util.Calendar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `app name matches PaperTrade`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PaperTrade", appName)
  }

  @Test
  fun `currency formatting outputs USD`() {
    assertEquals("$0.00", SimulatorFormatter.formatCurrency(0L))
    assertEquals("$100.00", SimulatorFormatter.formatCurrency(10_000L))
    assertEquals("+$1.00", SimulatorFormatter.formatSignedCurrency(100L))
  }

  @Test
  fun `weekday and override rules behave accurately`() {
    val saturday =
      Calendar.getInstance().apply {
        set(2026, Calendar.NOVEMBER, 14, 10, 0, 0) // Saturday
      }
    val monday =
      Calendar.getInstance().apply {
        set(2026, Calendar.NOVEMBER, 16, 10, 0, 0) // Monday
      }

    assertFalse(SimulatorCalendar.isEffectiveWeekday(DayOverride.AUTO, 0, saturday))
    assertTrue(SimulatorCalendar.isEffectiveWeekday(DayOverride.FORCE_WEEKDAY, 0, saturday))
    assertTrue(SimulatorCalendar.isEffectiveWeekday(DayOverride.AUTO, 0, monday))
    assertFalse(SimulatorCalendar.isEffectiveWeekday(DayOverride.FORCE_WEEKEND, 0, monday))
  }

  @Test
  fun `repository enforces minimum balance, weekday lock, single daily reward, and next day reset`() =
    runBlocking {
      val context = ApplicationProvider.getApplicationContext<Context>()
      val repo = SimulatorRepository(context)
      repo.resetAccount()

      // Starts at $0.00 and switch cannot turn ON
      repo.setDayOverride(DayOverride.FORCE_WEEKDAY)
      repo.toggleTradingSwitch(true)
      var state = repo.stateFlow.first()
      assertEquals(0L, state.virtualBalanceCents)
      assertFalse(state.isSwitchOn)

      // Deposit +$100.00
      repo.simulateDeposit()
      state = repo.stateFlow.first()
      assertEquals(10_000L, state.virtualBalanceCents)

      // Force weekend blocks switch even with $100.00
      repo.setDayOverride(DayOverride.FORCE_WEEKEND)
      repo.toggleTradingSwitch(true)
      state = repo.stateFlow.first()
      assertFalse(state.isSwitchOn)
      assertEquals(10_000L, state.virtualBalanceCents)

      // Force weekday allows switch ON and credits +$1.00 once
      repo.setDayOverride(DayOverride.FORCE_WEEKDAY)
      repo.toggleTradingSwitch(true)
      state = repo.stateFlow.first()
      assertTrue(state.isSwitchOn)
      assertEquals(10_100L, state.virtualBalanceCents)
      assertEquals(100L, state.totalEarnedCents)

      // Toggle OFF then ON on same day does NOT credit another $1.00
      repo.toggleTradingSwitch(false)
      repo.toggleTradingSwitch(true)
      state = repo.stateFlow.first()
      assertTrue(state.isSwitchOn)
      assertEquals(10_100L, state.virtualBalanceCents)
      assertEquals(100L, state.totalEarnedCents)

      // Simulate Next Day resets switch and allows earning next $1.00
      repo.simulateNextDay()
      state = repo.stateFlow.first()
      assertFalse(state.isSwitchOn)
      repo.toggleTradingSwitch(true)
      state = repo.stateFlow.first()
      assertTrue(state.isSwitchOn)
      assertEquals(10_200L, state.virtualBalanceCents)
      assertEquals(200L, state.totalEarnedCents)

      // Reset account restores $0.00 and clears logs
      repo.resetAccount()
      state = repo.stateFlow.first()
      assertEquals(0L, state.virtualBalanceCents)
      assertEquals(0L, state.totalEarnedCents)
      assertTrue(state.transactions.isEmpty())
    }
}
