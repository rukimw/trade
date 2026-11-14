package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DayOverride
import com.example.ui.SimulatorUiState
import com.example.ui.SwitchLockReason
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonSoft
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceElevated
import com.example.ui.theme.SlateSurfaceHigh
import com.example.ui.theme.SlateVoid

@Composable
fun EducationalBanner(modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier.fillMaxWidth().testTag("educational_banner"),
    color = AmberContainer,
    border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.35f)),
    shape = RoundedCornerShape(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      Icon(
        imageVector = Icons.Filled.School,
        contentDescription = "Educational simulator notice",
        tint = AmberWarning,
        modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = stringResource(R.string.educational_banner),
        style = MaterialTheme.typography.labelSmall,
        color = AmberWarning,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
fun WalletHeroCard(
  uiState: SimulatorUiState,
  onSimulateDeposit: () -> Unit,
  onResetAccount: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.fillMaxWidth().testTag("wallet_hero_card"),
    shape = MaterialTheme.shapes.large,
    color = SlateSurface,
    border = BorderStroke(1.dp, SlateOutline),
    tonalElevation = 2.dp,
  ) {
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .background(
            brush =
              Brush.radialGradient(
                colors = listOf(EmeraldPrimary.copy(alpha = 0.14f), Color.Transparent),
                center = Offset(120f, 60f),
                radius = 580f,
              )
          )
          .padding(20.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header Row: Title + Day Pill
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier =
                Modifier.size(8.dp)
                  .clip(CircleShape)
                  .background(if (uiState.isEffectiveWeekday) EmeraldPrimary else AmberWarning)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = stringResource(R.string.virtual_balance_label).uppercase(),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Surface(
            color = SlateSurfaceHigh,
            shape = RoundedCornerShape(999.dp),
            border = BorderStroke(1.dp, SlateOutline),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = "Simulated calendar day",
                tint = CyanAccent,
                modifier = Modifier.size(12.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = uiState.dayBadgeText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
        }

        // Main Balance + Total Earned Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = uiState.formattedBalance,
              style = MaterialTheme.typography.displayMedium,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.testTag("virtual_balance_value"),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              color = EmeraldContainer,
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                  contentDescription = "Total earned",
                  tint = EmeraldBright,
                  modifier = Modifier.size(15.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${stringResource(R.string.total_earned_label)}: +${uiState.formattedTotalEarned}",
                  style = MaterialTheme.typography.labelMedium,
                  color = EmeraldBright,
                  modifier = Modifier.testTag("total_earned_value"),
                )
              }
            }
          }
        }

        // Virtual Equity Sparkline derived from actual transaction history
        EquitySparkline(
          pointsCents = uiState.equityPointsCents,
          modifier = Modifier.fillMaxWidth().height(54.dp),
        )

        // Primary Wallet Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Button(
            onClick = onSimulateDeposit,
            modifier =
              Modifier.weight(1.45f)
                .minimumInteractiveComponentSize()
                .testTag("simulate_deposit_button"),
            shape = RoundedCornerShape(12.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = EmeraldPrimary,
                contentColor = SlateVoid,
              ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.AddCircleOutline,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(R.string.simulate_deposit_cta),
              style = MaterialTheme.typography.labelLarge,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }

          OutlinedButton(
            onClick = onResetAccount,
            modifier =
              Modifier.weight(0.95f)
                .minimumInteractiveComponentSize()
                .testTag("reset_account_button"),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.45f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonSoft),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.Refresh,
              contentDescription = null,
              modifier = Modifier.size(17.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(R.string.reset_account_cta),
              style = MaterialTheme.typography.labelLarge,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun EquitySparkline(pointsCents: List<Long>, modifier: Modifier = Modifier) {
  val lineColor = EmeraldBright
  val fillTop = EmeraldPrimary.copy(alpha = 0.25f)
  val gridColor = SlateOutline.copy(alpha = 0.7f)

  Box(
    modifier =
      modifier
        .clip(RoundedCornerShape(12.dp))
        .background(SlateSurfaceElevated.copy(alpha = 0.75f))
        .border(1.dp, SlateOutline.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
        .padding(horizontal = 12.dp, vertical = 10.dp)
  ) {
    Canvas(modifier = Modifier.matchParentSize()) {
      val w = size.width
      val h = size.height
      if (w <= 0f || h <= 0f) return@Canvas

      // Baseline grid
      drawLine(
        color = gridColor,
        start = Offset(0f, h * 0.5f),
        end = Offset(w, h * 0.5f),
        strokeWidth = 1.dp.toPx(),
      )

      val pts = if (pointsCents.size >= 2) pointsCents else listOf(0L, pointsCents.firstOrNull() ?: 0L)
      val minVal = pts.minOrNull() ?: 0L
      val maxVal = pts.maxOrNull() ?: 0L
      val span = (maxVal - minVal).coerceAtLeast(100L)

      val stepX = w / (pts.size - 1).coerceAtLeast(1)
      val strokePath = Path()
      val fillPath = Path()

      pts.forEachIndexed { index, value ->
        val x = index * stepX
        val normalized = (value - minVal).toFloat() / span.toFloat()
        val y = h - (normalized * (h * 0.8f)) - (h * 0.1f)
        if (index == 0) {
          strokePath.moveTo(x, y)
          fillPath.moveTo(x, h)
          fillPath.lineTo(x, y)
        } else {
          strokePath.lineTo(x, y)
          fillPath.lineTo(x, y)
        }
        if (index == pts.lastIndex) {
          fillPath.lineTo(x, h)
          fillPath.close()
        }
      }

      drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(colors = listOf(fillTop, Color.Transparent)),
      )
      drawPath(
        path = strokePath,
        color = lineColor,
        style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
      )

      // End node highlight
      val lastVal = pts.last()
      val lastNorm = (lastVal - minVal).toFloat() / span.toFloat()
      val lastY = h - (lastNorm * (h * 0.8f)) - (h * 0.1f)
      drawCircle(color = EmeraldBright, radius = 4.dp.toPx(), center = Offset(w, lastY))
    }
  }
}

@Composable
fun DailyBotTradingCard(
  uiState: SimulatorUiState,
  onToggleSwitch: (Boolean) -> Unit,
  onSimulateNextDay: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val borderColor by
    animateColorAsState(
      targetValue =
        when {
          uiState.isSwitchOn -> EmeraldPrimary.copy(alpha = 0.7f)
          !uiState.isEffectiveWeekday -> CrimsonAlert.copy(alpha = 0.45f)
          !uiState.hasMinimumBalance -> AmberWarning.copy(alpha = 0.45f)
          else -> SlateOutline
        },
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "switch_border_color",
    )

  val badgeColor =
    when {
      uiState.isSwitchOn -> EmeraldContainer
      !uiState.isEffectiveWeekday -> CrimsonContainer
      !uiState.hasMinimumBalance -> AmberContainer
      else -> CyanContainer
    }
  val badgeTextColor =
    when {
      uiState.isSwitchOn -> EmeraldBright
      !uiState.isEffectiveWeekday -> CrimsonSoft
      !uiState.hasMinimumBalance -> AmberWarning
      else -> CyanAccent
    }
  val badgeLabel =
    when {
      uiState.isSwitchOn -> "ACTIVE"
      !uiState.isEffectiveWeekday -> "CLOSED"
      !uiState.hasMinimumBalance -> "LOCKED"
      uiState.claimedToday -> "CLAIMED"
      else -> "READY"
    }

  val glowAlpha by
    animateFloatAsState(
      targetValue = if (uiState.isSwitchOn) 0.16f else 0f,
      label = "switch_glow_alpha",
    )

  Surface(
    modifier = modifier.fillMaxWidth().testTag("daily_bot_card"),
    shape = MaterialTheme.shapes.large,
    color = SlateSurface,
    border = BorderStroke(1.2.dp, borderColor),
    tonalElevation = 3.dp,
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(EmeraldPrimary.copy(alpha = glowAlpha), Color.Transparent)
            )
          )
          .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      // Title + Status Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier =
              Modifier.size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(badgeColor)
                .border(1.dp, badgeTextColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector =
                when {
                  uiState.isSwitchOn -> Icons.Filled.Bolt
                  !uiState.isSwitchEnabled -> Icons.Filled.Lock
                  else -> Icons.Filled.Bolt
                },
              contentDescription = "Trading bot status icon",
              tint = badgeTextColor,
              modifier = Modifier.size(22.dp),
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = stringResource(R.string.trading_switch_title),
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = "Mon–Fri • +$1.00 daily simulated bot target",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Surface(
          color = badgeColor,
          shape = RoundedCornerShape(999.dp),
          border = BorderStroke(1.dp, badgeTextColor.copy(alpha = 0.4f)),
        ) {
          Text(
            text = badgeLabel,
            style = MaterialTheme.typography.labelSmall,
            color = badgeTextColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          )
        }
      }

      // Prominent Toggle Control Row
      Surface(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(
              if (uiState.isSwitchEnabled) {
                Modifier.clickable { onToggleSwitch(!uiState.isSwitchOn) }
              } else {
                Modifier
              }
            ),
        shape = RoundedCornerShape(16.dp),
        color = SlateSurfaceElevated,
        border = BorderStroke(1.dp, SlateOutline),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
              text =
                when {
                  uiState.isSwitchOn -> stringResource(R.string.active_target_reached)
                  uiState.claimedToday && uiState.isSwitchEnabled ->
                    "Switch OFF — Daily +$1.00 already claimed today"
                  uiState.isSwitchEnabled -> "Switch OFF — Tap to activate & earn +$1.00"
                  else -> "Switch Locked"
                },
              style = MaterialTheme.typography.titleSmall,
              color =
                when {
                  uiState.isSwitchOn -> EmeraldBright
                  uiState.isSwitchEnabled -> MaterialTheme.colorScheme.onSurface
                  else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
              modifier = Modifier.testTag("switch_status_text"),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text =
                if (uiState.claimedToday) {
                  "Same-day re-toggles will not credit another $1.00"
                } else {
                  "Credits +$1.00 virtual profit immediately on activation"
                },
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Switch(
            checked = uiState.isSwitchOn,
            onCheckedChange = { enabled -> onToggleSwitch(enabled) },
            enabled = uiState.isSwitchEnabled,
            modifier =
              Modifier.minimumInteractiveComponentSize()
                .testTag("daily_bot_switch")
                .semantics {
                  contentDescription = "Daily Bot Trading Switch"
                },
            colors =
              SwitchDefaults.colors(
                checkedThumbColor = SlateVoid,
                checkedTrackColor = EmeraldPrimary,
                checkedBorderColor = EmeraldBright,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = SlateSurfaceHigh,
                uncheckedBorderColor = SlateOutline,
                disabledCheckedThumbColor = SlateVoid,
                disabledCheckedTrackColor = EmeraldPrimary.copy(alpha = 0.35f),
                disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                disabledUncheckedTrackColor = SlateSurfaceHigh.copy(alpha = 0.5f),
              ),
          )
        }
      }

      // Rule 1 Message: Minimum $100.00 Balance
      AnimatedVisibility(
        visible =
          uiState.lockReason == SwitchLockReason.INSUFFICIENT_BALANCE ||
            uiState.lockReason == SwitchLockReason.BOTH_BALANCE_AND_WEEKEND,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
      ) {
        RuleAlertStrip(
          icon = Icons.Filled.Lock,
          message = stringResource(R.string.min_balance_warning),
          containerColor = AmberContainer,
          accentColor = AmberWarning,
          testTag = "min_balance_warning_banner",
        )
      }

      // Rule 2 Message: Weekdays Only
      AnimatedVisibility(
        visible =
          uiState.lockReason == SwitchLockReason.WEEKEND_CLOSED ||
            uiState.lockReason == SwitchLockReason.BOTH_BALANCE_AND_WEEKEND,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
      ) {
        RuleAlertStrip(
          icon = Icons.Filled.Schedule,
          message = stringResource(R.string.market_closed_warning),
          containerColor = CrimsonContainer,
          accentColor = CrimsonSoft,
          testTag = "market_closed_warning_banner",
        )
      }

      // Active Daily Target Reached Confirmation Strip
      AnimatedVisibility(
        visible = uiState.isSwitchOn && uiState.claimedToday,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
      ) {
        RuleAlertStrip(
          icon = Icons.Filled.CheckCircle,
          message = stringResource(R.string.active_target_reached),
          containerColor = EmeraldContainer,
          accentColor = EmeraldBright,
          testTag = "active_target_reached_banner",
        )
      }

      // Simulate Next Day Button
      OutlinedButton(
        onClick = onSimulateNextDay,
        modifier =
          Modifier.fillMaxWidth()
            .minimumInteractiveComponentSize()
            .testTag("simulate_next_day_button"),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.45f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      ) {
        Icon(
          imageVector = Icons.Filled.SkipNext,
          contentDescription = null,
          modifier = Modifier.size(19.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "${stringResource(R.string.simulate_next_day_cta)} (Current: Day +${uiState.simulatedDayOffset})",
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}

@Composable
private fun RuleAlertStrip(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  message: String,
  containerColor: Color,
  accentColor: Color,
  testTag: String,
) {
  Surface(
    modifier = Modifier.fillMaxWidth().testTag(testTag),
    shape = RoundedCornerShape(12.dp),
    color = containerColor,
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = accentColor,
        modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = accentColor,
        fontWeight = FontWeight.Medium,
      )
    }
  }
}

@Composable
fun SimulatorDayOverrideCard(
  selectedOverride: DayOverride,
  effectiveDayDescription: String,
  onSelectOverride: (DayOverride) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.fillMaxWidth().testTag("simulator_day_override_card"),
    shape = MaterialTheme.shapes.medium,
    color = SlateSurface,
    border = BorderStroke(1.dp, SlateOutline),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Filled.Tune,
          contentDescription = "Developer testing override",
          tint = CyanAccent,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stringResource(R.string.simulator_day_override_title),
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }

      Row(
        modifier =
          Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateSurfaceElevated)
            .border(1.dp, SlateOutline, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        DayOverride.entries.forEach { option ->
          val isSelected = option == selectedOverride
          val containerColor =
            when {
              !isSelected -> Color.Transparent
              option == DayOverride.FORCE_WEEKDAY -> EmeraldContainer
              option == DayOverride.FORCE_WEEKEND -> CrimsonContainer
              else -> CyanContainer
            }
          val textColor =
            when {
              !isSelected -> MaterialTheme.colorScheme.onSurfaceVariant
              option == DayOverride.FORCE_WEEKDAY -> EmeraldBright
              option == DayOverride.FORCE_WEEKEND -> CrimsonSoft
              else -> CyanAccent
            }

          Surface(
            modifier =
              Modifier.weight(1f)
                .minimumInteractiveComponentSize()
                .clip(RoundedCornerShape(9.dp))
                .clickable { onSelectOverride(option) }
                .testTag("override_${option.name.lowercase()}"),
            shape = RoundedCornerShape(9.dp),
            color = containerColor,
            border =
              if (isSelected) BorderStroke(1.dp, textColor.copy(alpha = 0.45f)) else null,
          ) {
            Box(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = option.label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
        }
      }

      Text(
        text = effectiveDayDescription,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
