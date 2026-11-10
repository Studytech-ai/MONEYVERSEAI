package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RequestStatus
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun MoneyVerseHeader(
    userProfile: UserProfile,
    title: String,
    subtitle: String = "",
    onProfileClick: () -> Unit = {},
    onParentModeClick: () -> Unit = {},
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (showBack) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDark
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Points pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        )
                    )
                    .clickable { onProfileClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("points_badge")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "☀️", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${userProfile.points} Points",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Avatar button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurpleLight.copy(alpha = 0.25f))
                    .border(2.dp, PrimaryPurple, CircleShape)
                    .clickable { onProfileClick() }
                    .testTag("avatar_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userProfile.avatarEmoji,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: RequestStatus) {
    val (bg, textCol, label) = when (status) {
        RequestStatus.PENDING -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), "Pending Approval")
        RequestStatus.APPROVED -> Triple(Color(0xFFD1FAE5), Color(0xFF047857), "Approved! 🎉")
        RequestStatus.REJECTED -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), "Not This Time")
        RequestStatus.PURCHASED -> Triple(Color(0xFFE0E7FF), Color(0xFF4338CA), "Purchased! 🛍️")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = textCol,
            fontWeight = FontWeight.Bold,
            fontFamily = FredokaFontFamily
        )
    }
}

@Composable
fun CurrencyText(
    amount: Double,
    modifier: Modifier = Modifier,
    fontSize: Int = 28,
    color: Color = TextDark
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "₹",
            fontSize = (fontSize * 0.85).toInt().sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FredokaFontFamily,
            color = color
        )
        Text(
            text = if (amount % 1.0 == 0.0) amount.toInt().toString() else "%.1f".format(amount),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FredokaFontFamily,
            color = color
        )
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 2.dp,
    backgroundColor: Color = Color.White,
    borderColor: Color = BorderLight,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(cornerRadius), ambientColor = Color(0x1A000000))
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}
