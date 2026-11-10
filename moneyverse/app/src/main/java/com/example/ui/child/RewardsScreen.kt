package com.example.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoneyVerseState
import com.example.ui.components.GlassCard
import com.example.ui.components.MoneyVerseHeader
import com.example.ui.theme.*

@Composable
fun RewardsScreen(
    state: MoneyVerseState,
    onClaimMission: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = state.userProfile
    val nextLevelTarget = profile.levelNumber * 100
    val levelProgress = (profile.points % 100).toFloat() / 100f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("rewards_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            MoneyVerseHeader(
                userProfile = profile,
                title = "Rewards & Achievements 🏆",
                subtitle = "Level up by making smart financial decisions!",
                showBack = true,
                onBackClick = onBack
            )
        }

        // Level & Streak Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("rewards_level_card"),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF6D38E2), Color(0xFF4C1D95))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Level ${profile.levelNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFFDDD6FE)
                                )
                                Text(
                                    text = profile.levelTitle,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = FredokaFontFamily
                                )
                            }

                            // Streak pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFFB800))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🔥", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${profile.streakDays} Day Streak!",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = DeepPurple,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // XP Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${profile.points} Total XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                            Text(
                                text = "Next Level at $nextLevelTarget XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFDDD6FE)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { levelProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = Color(0xFFFFB800),
                            trackColor = Color(0x33FFFFFF)
                        )
                    }
                }
            }
        }

        // Daily Missions Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Daily Missions 🎯",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                state.dailyMissions.forEach { mission ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (mission.isClaimed) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (mission.isClaimed) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                                    } else {
                                        Text(text = "🪙", fontSize = 16.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = mission.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = "+${mission.rewardXp} XP Reward",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PrimaryPurple,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            when {
                                mission.isClaimed -> {
                                    Text(
                                        text = "Claimed ✓",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF059669),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                mission.isCompleted -> {
                                    Button(
                                        onClick = { onClaimMission(mission.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("claim_mission_${mission.id}")
                                    ) {
                                        Text("Claim!", fontWeight = FontWeight.Bold)
                                    }
                                }
                                else -> {
                                    Text(
                                        text = "In Progress",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Achievements Gallery
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Achievement Badges 🎖️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }

        items(state.achievements, key = { it.id }) { ach ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .testTag("achievement_${ach.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (ach.isUnlocked) Color(0xFFFEF3C7) else Color(0xFFF3F4F6))
                            .border(
                                2.dp,
                                if (ach.isUnlocked) Color(0xFFFFB800) else Color(0xFFD1D5DB),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (ach.isUnlocked) {
                            Text(text = ach.iconEmoji, fontSize = 28.sp)
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF9CA3AF))
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ach.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (ach.isUnlocked) TextDark else TextMuted
                            )

                            if (ach.isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFD1FAE5))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Unlocked! 🌟",
                                        fontSize = 10.sp,
                                        color = Color(0xFF047857),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = ach.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Progress toward badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = {
                                    if (ach.progressTarget > 0) ach.progressCurrent.toFloat() / ach.progressTarget.toFloat() else 0f
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (ach.isUnlocked) Color(0xFFFFB800) else PrimaryPurple,
                                trackColor = Color(0xFFE5E7EB)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${ach.progressCurrent}/${ach.progressTarget}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
