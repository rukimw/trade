package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.SimulatorFormatter
import com.example.model.TransactionRecord
import com.example.model.TransactionType
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceElevated
import com.example.ui.theme.SlateSurfaceHigh

@Composable
fun TransactionLogCard(
  transactions: List<TransactionRecord>,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.fillMaxWidth().testTag("transaction_log_card"),
    shape = MaterialTheme.shapes.large,
    color = SlateSurface,
    border = BorderStroke(1.dp, SlateOutline),
    tonalElevation = 1.dp,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.ReceiptLong,
            contentDescription = "Activity log",
            tint = EmeraldBright,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.activity_log_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }

        Surface(
          color = SlateSurfaceHigh,
          shape = RoundedCornerShape(999.dp),
          border = BorderStroke(1.dp, SlateOutline),
        ) {
          Text(
            text = "${transactions.size} ${if (transactions.size == 1) "entry" else "entries"}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
      }

      if (transactions.isEmpty()) {
        EmptyTransactionState()
      } else {
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(SlateSurfaceElevated)
              .border(1.dp, SlateOutline, RoundedCornerShape(14.dp))
        ) {
          transactions.forEachIndexed { index, record ->
            TransactionRow(record = record)
            if (index < transactions.lastIndex) {
              HorizontalDivider(color = SlateOutline.copy(alpha = 0.7f), thickness = 1.dp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun EmptyTransactionState() {
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(SlateSurfaceElevated)
        .border(1.dp, SlateOutline, RoundedCornerShape(14.dp))
        .padding(horizontal = 20.dp, vertical = 28.dp),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        imageVector = Icons.Filled.AccountBalanceWallet,
        contentDescription = "No transactions yet",
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier.size(34.dp),
      )
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "No simulated activity yet",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Tap 'Simulate Deposit (+$100 Virtual)' above to fund your paper wallet and unlock the weekday trading switch.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun TransactionRow(record: TransactionRecord) {
  val (icon, iconTint, iconBg) =
    when (record.type) {
      TransactionType.DEPOSIT ->
        Triple(Icons.Filled.ArrowDownward, CyanAccent, CyanContainer)
      TransactionType.BOT_PROFIT ->
        Triple(Icons.Filled.Bolt, EmeraldBright, EmeraldContainer)
      TransactionType.DAY_ADVANCE ->
        Triple(Icons.Filled.SkipNext, MaterialTheme.colorScheme.onSurfaceVariant, SlateSurfaceHigh)
    }

  val amountColor =
    when {
      record.amountCents > 0L -> EmeraldBright
      else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f).padding(end = 10.dp),
    ) {
      Box(
        modifier =
          Modifier.size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(iconBg)
            .border(1.dp, iconTint.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = record.actionType,
          tint = iconTint,
          modifier = Modifier.size(18.dp),
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = record.actionType,
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = SimulatorFormatter.formatTimestamp(record.timestampMillis),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    Column(horizontalAlignment = Alignment.End) {
      Text(
        text =
          if (record.type == TransactionType.DAY_ADVANCE) {
            "DAY +${record.simulatedDayNumber}"
          } else {
            SimulatorFormatter.formatSignedCurrency(record.amountCents)
          },
        style = MaterialTheme.typography.labelMedium,
        color = amountColor,
        fontWeight = FontWeight.Bold,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Bal: ${SimulatorFormatter.formatCurrency(record.balanceAfterCents)}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
