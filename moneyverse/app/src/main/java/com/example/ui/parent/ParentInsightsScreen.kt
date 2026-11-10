package com.example.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoneyVerseState
import com.example.model.TransactionCategory
import com.example.model.TransactionType
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun ParentInsightsScreen(
    state: MoneyVerseState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expenses = state.transactions.filter { it.type == TransactionType.EXPENSE }
    val totalExpense = expenses.sumOf { it.amount }.coerceAtLeast(1.0)

    val foodExpense = expenses.filter { it.category == TransactionCategory.FOOD_TREATS }.sumOf { it.amount }
    val toysExpense = expenses.filter { it.category == TransactionCategory.TOYS_GAMES }.sumOf { it.amount }
    val goalExpense = expenses.filter { it.category == TransactionCategory.SAVINGS_GOAL }.sumOf { it.amount }
    val otherExpense = expenses.filter { it.category != TransactionCategory.FOOD_TREATS && it.category != TransactionCategory.TOYS_GAMES && it.category != TransactionCategory.SAVINGS_GOAL }.sumOf { it.amount }

    val completedStoriesCount = state.stories.count { it.isCompleted }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F2))
            .testTag("parent_insights_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Learning Insights & Analytics 📊",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Real-time behavioral insights for ${state.userProfile.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Financial IQ Score Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                backgroundColor = Color(0xFFF5F3FF)
            ) {
                Text(
                    text = "Financial Habit Health Index",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "86 / 100",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            fontFamily = FredokaFontFamily
                        )
                        Text(
                            text = "Rating: Very Mindful Saver 🌿",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEDE9FE))
                            .padding(12.dp)
                    ) {
                        Text(text = "🏅 Level 3", fontWeight = FontWeight.Bold, color = PrimaryPurple)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown Indicators
                InsightProgressRow(title = "Goal Orientation & Patience", progress = 0.90f, color = Color(0xFF10B981))
                Spacer(modifier = Modifier.height(8.dp))
                InsightProgressRow(title = "Needs vs Wants Awareness", progress = 0.82f, color = Color(0xFF6366F1))
                Spacer(modifier = Modifier.height(8.dp))
                InsightProgressRow(title = "Story Literacy & Quiz Mastery", progress = (completedStoriesCount / 5f).coerceIn(0.2f, 1f), color = Color(0xFFF59E0B))
            }
        }

        // Spending Categories Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Where Does Pocket Money Go?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                CategorySpendBar(label = "🎯 Savings Goals Contributions", amount = goalExpense, total = totalExpense, color = Color(0xFF10B981))
                Spacer(modifier = Modifier.height(8.dp))
                CategorySpendBar(label = "🎮 Toys & Games", amount = toysExpense, total = totalExpense, color = Color(0xFF8B5CF6))
                Spacer(modifier = Modifier.height(8.dp))
                CategorySpendBar(label = "🍦 Food & Treats", amount = foodExpense, total = totalExpense, color = Color(0xFFEF4444))
                if (otherExpense > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CategorySpendBar(label = "✨ Other Expenses", amount = otherExpense, total = totalExpense, color = Color(0xFF6B7280))
                }
            }
        }

        // Key Pedagogical Recommendations
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                backgroundColor = Color(0xFFFFFBEB)
            ) {
                Text(
                    text = "Pedagogical Recommendations 💡",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "1. ${state.userProfile.name} shows high motivation when saving for the Bicycle! Celebrate reaching the 64% milestone.\n\n" +
                            "2. Try the 'Money Time Machine' together to demonstrate how saving ₹100 each month compounds into thousands over a year.\n\n" +
                            "3. Review the pending Watercolor Art Set request and talk about how creative tools benefit school projects.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun InsightProgressRow(title: String, progress: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextDark)
            Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFFE5E7EB)
        )
    }
}

@Composable
fun CategorySpendBar(label: String, amount: Double, total: Double, color: Color) {
    val ratio = (amount / total).toFloat().coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextDark)
            Text(text = "₹${amount.toInt()} (${(ratio * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFF3F4F6)
        )
    }
}
