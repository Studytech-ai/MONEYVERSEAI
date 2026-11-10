package com.example.ui.child

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.components.CurrencyText
import com.example.ui.components.GlassCard
import com.example.ui.components.MoneyVerseHeader
import com.example.ui.theme.*

enum class TimeHorizon(val label: String, val months: Int) {
    ONE_MONTH("1 Month", 1),
    SIX_MONTHS("6 Months", 6),
    ONE_YEAR("1 Year", 12)
}

enum class StrategyPreset(val title: String, val desc: String, val needs: Int, val wants: Int, val savings: Int) {
    INSTANT_SPENDER("Instant Spender", "Spend ₹400 now on trending toys", 50, 400, 50),
    BALANCED_50_30_20("The 50/30/20 Wizard", "Needs ₹250, Wants ₹150, Save ₹100", 250, 150, 100),
    DREAM_SAVER("Super Goal Saver", "Keep spending minimal, save ₹350!", 100, 50, 350)
}

@Composable
fun TimeMachineScreen(
    state: MoneyVerseState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var monthlyIncome by remember { mutableDoubleStateOf(500.0) }
    var selectedPreset by remember { mutableStateOf(StrategyPreset.BALANCED_50_30_20) }
    var selectedHorizon by remember { mutableStateOf(TimeHorizon.ONE_YEAR) }

    // Allocation values based on chosen preset
    val needsAmount = selectedPreset.needs
    val wantsAmount = selectedPreset.wants
    val savingsAmount = selectedPreset.savings

    // Compounding & future simulation calculations:
    // Simulated annual bonus/interest rate: 5%
    val monthlyRate = 0.05 / 12.0
    val months = selectedHorizon.months

    // Future savings formula: FV = P * [((1 + r)^n - 1) / r]
    val projectedSavings = if (monthlyRate > 0) {
        savingsAmount * ((Math.pow(1.0 + monthlyRate, months.toDouble()) - 1.0) / monthlyRate)
    } else {
        (savingsAmount * months).toDouble()
    }

    val totalSpent = (needsAmount + wantsAmount) * months

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0A1E))
            .testTag("time_machine_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            MoneyVerseHeader(
                userProfile = state.userProfile,
                title = "Money Time Machine 🔮",
                subtitle = "Travel into the future to see the power of your choices!",
                showBack = true,
                onBackClick = onBack
            )
        }

        // Crystal Ball Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .shadow(6.dp, RoundedCornerShape(26.dp))
                    .testTag("time_machine_hero"),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2A1254), Color(0xFF14082B))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.timemachine_orb),
                            contentDescription = "Magical Crystal Orb",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFF8B5CF6), CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x33A78BFA))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "FUTURE SIMULATOR",
                                    fontSize = 10.sp,
                                    color = Color(0xFFC4B5FD),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "What if you receive ₹500 every month?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select a strategy below to activate the time warp!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA5B4FC)
                            )
                        }
                    }
                }
            }
        }

        // Strategy Selector Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Choose Your Spending Strategy:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                StrategyPreset.values().forEach { preset ->
                    val isSelected = selectedPreset == preset
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF8B5CF6) else Color(0x33FFFFFF),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedPreset = preset }
                            .testTag("strategy_preset_${preset.name}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF26134D) else Color(0xFF190F30)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedPreset = preset },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8B5CF6))
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = preset.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = preset.desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFC7D2FE)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Time Horizon Filter
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Travel Forward in Time:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeHorizon.values().forEach { horizon ->
                        val isSelected = selectedHorizon == horizon
                        Button(
                            onClick = { selectedHorizon = horizon },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF8B5CF6) else Color(0xFF1E1338)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = horizon.label,
                                color = Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Future Outcome Report Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .testTag("time_machine_results_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1D123A)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7))))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Future Outcome in ${selectedHorizon.label} 🚀",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF10B981))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "+5% Growth",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Projected Savings
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF0F291E))
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Accumulated Treasure",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF6EE7B7)
                                )
                                Text(
                                    text = "₹${projectedSavings.toInt()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Total Spent
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF2C131E))
                                .border(1.dp, Color(0xFFF43F5E), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Spent on Treats & Fun",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFDA4AF)
                                )
                                Text(
                                    text = "₹$totalSpent",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Narrative Takeaway
                    val wisdom = when (selectedPreset) {
                        StrategyPreset.INSTANT_SPENDER ->
                            "⚠️ In ${selectedHorizon.label}, you enjoyed candies and gadgets, but your savings jar only has ₹${projectedSavings.toInt()}. If your bicycle breaks or a dream trip arrives, you won't have funds ready."
                        StrategyPreset.BALANCED_50_30_20 ->
                            "🌟 Perfect harmony! You enjoyed ₹$totalSpent on fun, while building ₹${projectedSavings.toInt()} in savings. You are ready for emergencies AND dream goals!"
                        StrategyPreset.DREAM_SAVER ->
                            "🏆 Incredible champion discipline! In ${selectedHorizon.label}, you accumulated ₹${projectedSavings.toInt()}! You can buy a gaming laptop, a high-end bicycle, or fund a major trip with zero stress!"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x338B5CF6))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = wisdom,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE0E7FF),
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "* Disclaimer: Educational simulation for learning, not guaranteed investment returns.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
