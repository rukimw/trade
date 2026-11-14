package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DayOverride
import com.example.ui.components.DailyBotTradingCard
import com.example.ui.components.EducationalBanner
import com.example.ui.components.SimulatorDayOverrideCard
import com.example.ui.components.TransactionLogCard
import com.example.ui.components.WalletHeroCard
import kotlinx.coroutines.launch

@Composable
fun PaperTradeScreen(viewModel: SimulatorViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  fun showToast(message: String) {
    scope.launch {
      snackbarHostState.currentSnackbarData?.dismiss()
      snackbarHostState.showSnackbar(message)
    }
  }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets.safeDrawing,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
  ) { innerPadding ->
    BoxWithConstraints(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      val isWideLayout = maxWidth >= 760.dp

      if (isWideLayout) {
        LazyColumn(
          modifier = Modifier.widthIn(max = 1040.dp).fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          item { EducationalBanner() }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.Top,
            ) {
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
              ) {
                WalletHeroCard(
                  uiState = uiState,
                  onSimulateDeposit = {
                    viewModel.onSimulateDeposit()
                    showToast("Deposited +$100.00 virtual funds")
                  },
                  onResetAccount = {
                    viewModel.onResetAccount()
                    showToast("Paper-trading account reset to $0.00")
                  },
                )
                DailyBotTradingCard(
                  uiState = uiState,
                  onToggleSwitch = { enable ->
                    val willCredit = enable && !uiState.claimedToday && uiState.isSwitchEnabled
                    viewModel.onToggleTradingSwitch(enable)
                    if (willCredit) {
                      showToast("Bot active! Credited +$1.00 virtual profit")
                    }
                  },
                  onSimulateNextDay = {
                    viewModel.onSimulateNextDay()
                    showToast("Advanced to Day +${uiState.simulatedDayOffset + 1}")
                  },
                )
                SimulatorDayOverrideCard(
                  selectedOverride = uiState.dayOverride,
                  effectiveDayDescription = uiState.effectiveDayDescription,
                  onSelectOverride = { override ->
                    viewModel.onSelectDayOverride(override)
                    showOverrideFeedback(override, ::showToast)
                  },
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                TransactionLogCard(transactions = uiState.transactions)
              }
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.widthIn(max = 600.dp).fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          item { EducationalBanner() }

          item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "PaperTrade Simulator",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
              )
              Text(
                text = "Test weekday bot activation rules & daily virtual compounding",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }

          item {
            WalletHeroCard(
              uiState = uiState,
              onSimulateDeposit = {
                viewModel.onSimulateDeposit()
                showToast("Deposited +$100.00 virtual funds")
              },
              onResetAccount = {
                viewModel.onResetAccount()
                showToast("Paper-trading account reset to $0.00")
              },
            )
          }

          item {
            DailyBotTradingCard(
              uiState = uiState,
              onToggleSwitch = { enable ->
                val willCredit = enable && !uiState.claimedToday && uiState.isSwitchEnabled
                viewModel.onToggleTradingSwitch(enable)
                if (willCredit) {
                  showToast("Bot active! Credited +$1.00 virtual profit")
                }
              },
              onSimulateNextDay = {
                viewModel.onSimulateNextDay()
                showToast("Advanced to Day +${uiState.simulatedDayOffset + 1}")
              },
            )
          }

          item { TransactionLogCard(transactions = uiState.transactions) }

          item {
            SimulatorDayOverrideCard(
              selectedOverride = uiState.dayOverride,
              effectiveDayDescription = uiState.effectiveDayDescription,
              onSelectOverride = { override ->
                viewModel.onSelectDayOverride(override)
                showOverrideFeedback(override, ::showToast)
              },
            )
          }

          item { Spacer(modifier = Modifier.height(8.dp)) }
        }
      }
    }
  }
}

private fun showOverrideFeedback(override: DayOverride, showToast: (String) -> Unit) {
  when (override) {
    DayOverride.AUTO -> showToast("Day Override: Auto (Real calendar)")
    DayOverride.FORCE_WEEKDAY -> showToast("Day Override: Forced Weekday (Mon–Fri)")
    DayOverride.FORCE_WEEKEND -> showToast("Day Override: Forced Weekend (Market Closed)")
  }
}
