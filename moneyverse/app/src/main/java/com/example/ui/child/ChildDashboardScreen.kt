package com.example.ui.child

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MoneyVerseState
import com.example.model.TransactionType
import com.example.ui.components.AppNavDestination
import com.example.ui.components.MoneyVerseHeader
import com.example.ui.theme.*

@Composable
fun ChildDashboardScreen(
    state: MoneyVerseState,
    onNavigate: (AppNavDestination) -> Unit,
    onSwitchToParent: () -> Unit,
    onOpenStory: (String) -> Unit,
    onQuickContribute: (String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile = state.userProfile
    val cycleGoal = state.savingsGoals.find { it.title.equals("Cycle", ignoreCase = true) }
        ?: state.savingsGoals.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("child_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Top Header with greetings & points
        item {
            MoneyVerseHeader(
                userProfile = userProfile,
                title = "Hello, ${userProfile.name}! 👋",
                subtitle = "Ready to make smart money choices today?",
                onProfileClick = { onNavigate(AppNavDestination.SETTINGS) },
                onParentModeClick = onSwitchToParent
            )
        }

        // 2. Hero Storybook Landscape & Balance Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(230.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .shadow(4.dp, RoundedCornerShape(26.dp))
            ) {
                // Background Scene
                Image(
                    painter = painterResource(id = R.drawable.hero_storybook_scene),
                    contentDescription = "Magical MoneyVerse Landscape",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient scrim overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xEE1E0E38),
                                    Color(0x992B1556),
                                    Color(0x103B1E78),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Wallet Card Overlay inside Hero
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 18.dp)
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xCC6D38E2),
                                    Color(0xEE4C1D95)
                                )
                            )
                        )
                        .border(1.5.dp, Color(0x66C4B5FD), RoundedCornerShape(22.dp))
                        .padding(16.dp)
                        .clickable { onNavigate(AppNavDestination.MY_MONEY) }
                        .testTag("dashboard_wallet_card")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF10B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👛", fontSize = 18.sp)
                        }
                        Text(
                            text = "Your Balance",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFE9D5FF),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "₹${state.walletBalance.toInt()}",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFontFamily
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // +₹50 this week pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF059669))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "↑ +₹${state.weeklyDelta.toInt()} this week",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Middle Section: Savings Goal, Recent Activity, Money Storybook
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Goal Card
                cycleGoal?.let { goal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(22.dp))
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { onNavigate(AppNavDestination.SAVINGS_GOALS) }
                            .testTag("dashboard_goal_card"),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🎯", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Your Savings Goal",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                }
                                TextButton(
                                    onClick = { onNavigate(AppNavDestination.SAVINGS_GOALS) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "View All →",
                                        color = PrimaryPurple,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.cycle_goal_art),
                                    contentDescription = goal.title,
                                    modifier = Modifier
                                        .size(100.dp, 70.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = goal.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "₹${goal.currentAmount.toInt()} / ₹${goal.targetAmount.toInt()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = PrimaryPurple,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Progress Bar
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { goal.progress },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = Color(0xFF10B981),
                                            trackColor = Color(0xFFE5E7EB)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${(goal.progress * 100).toInt()}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "⏱️ ${goal.daysLeft} days left!",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFB45309),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = { onQuickContribute(goal.id, 20.0) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("quick_save_20_button")
                                ) {
                                    Text(
                                        text = "+ Save ₹20",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Recent Activity Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .testTag("dashboard_recent_activity_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🕒", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Recent Activity",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }
                            TextButton(
                                onClick = { onNavigate(AppNavDestination.MY_MONEY) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "View All →",
                                    color = PrimaryPurple,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        state.transactions.take(3).forEach { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val (bgCol, iconEmoji) = when (tx.type) {
                                    TransactionType.INCOME -> Pair(Color(0xFFD1FAE5), "+")
                                    TransactionType.EXPENSE -> if (tx.title.contains("Toy", ignoreCase = true)) Pair(Color(0xFFEDE9FE), "🎁") else Pair(Color(0xFFFEE2E2), "−")
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(bgCol),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = iconEmoji,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (tx.type == TransactionType.INCOME) Color(0xFF059669) else Color(0xFFDC2626)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = tx.dateDisplay,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }

                                val amountSign = if (tx.type == TransactionType.INCOME) "+₹" else "-₹"
                                val amountColor = if (tx.type == TransactionType.INCOME) Color(0xFF059669) else Color(0xFFDC2626)

                                Text(
                                    text = "$amountSign${tx.amount.toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = amountColor
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 0.8.dp)
                        }
                    }
                }

                // Money Storybook Featured Card
                val recommendedStory = state.stories.find { it.badge == "Recommended" } ?: state.stories.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { onOpenStory(recommendedStory.id) }
                        .testTag("dashboard_storybook_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF6D38E2))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.3f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📖", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Money Storybook",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFDDD6FE),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = recommendedStory.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = recommendedStory.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFEDE9FE)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFB800))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Read Now (+50 XP) →",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DeepPurple,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Image(
                            painter = painterResource(id = R.drawable.story_wise_saver_art),
                            contentDescription = recommendedStory.title,
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // 4. Explore More Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Explore More",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Money Time Machine
                    ExploreItemCard(
                        title = "Money Time Machine",
                        subtitle = "See the future!",
                        iconText = "🔮",
                        bgColor = Color(0xFFD1FAE5),
                        iconBg = Color(0xFF10B981),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("explore_time_machine"),
                        onClick = { onNavigate(AppNavDestination.TIME_MACHINE) }
                    )

                    // Rewards
                    ExploreItemCard(
                        title = "Rewards",
                        subtitle = "Earn badges!",
                        iconText = "🏆",
                        bgColor = Color(0xFFFEF3C7),
                        iconBg = Color(0xFFF59E0B),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("explore_rewards"),
                        onClick = { onNavigate(AppNavDestination.REWARDS) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Purchase Requests
                    ExploreItemCard(
                        title = "Purchase Requests",
                        subtitle = "Ask your parents!",
                        iconText = "📋",
                        bgColor = Color(0xFFFFE4E6),
                        iconBg = Color(0xFFF43F5E),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("explore_purchase_requests"),
                        onClick = { onNavigate(AppNavDestination.PURCHASE_REQUESTS) }
                    )

                    // Money Tips
                    ExploreItemCard(
                        title = "Money Tips",
                        subtitle = "Be a smart spender!",
                        iconText = "💡",
                        bgColor = Color(0xFFEDE9FE),
                        iconBg = Color(0xFF8B5CF6),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("explore_money_tips"),
                        onClick = { onNavigate(AppNavDestination.MONEY_TIPS) }
                    )
                }
            }
        }

        // 5. Parent Mode Access Card (Matches reference bottom right)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onSwitchToParent() }
                    .testTag("dashboard_parent_mode_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFFEDD5), Color(0xFFFDBA74))))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEDD5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👩‍👦", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Parent Mode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9A3412)
                            )
                            Text(
                                text = "Manage your kid's money journey.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC2410C)
                            )
                        }
                    }

                    Button(
                        onClick = onSwitchToParent,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("dashboard_switch_parent_btn")
                    ) {
                        Text(
                            text = "Switch →",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreItemCard(
    title: String,
    subtitle: String,
    iconText: String,
    bgColor: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(iconBg.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconText, fontSize = 18.sp)
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
