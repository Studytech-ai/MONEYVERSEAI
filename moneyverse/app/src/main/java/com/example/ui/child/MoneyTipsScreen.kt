package com.example.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoneyVerseState
import com.example.ui.components.GlassCard
import com.example.ui.components.MoneyVerseHeader
import com.example.ui.theme.*

data class NeedWantItem(
    val title: String,
    val iconEmoji: String,
    val isNeed: Boolean,
    val explanation: String
)

@Composable
fun MoneyTipsScreen(
    state: MoneyVerseState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizItems = remember {
        listOf(
            NeedWantItem("Nutritious School Lunch", "🥗", true, "Food and health are fundamental human needs!"),
            NeedWantItem("Latest Video Game Skin", "🎮", false, "Fun to look at, but your game works 100% fine without it!"),
            NeedWantItem("Winter Coat in the Snow", "🧥", true, "Staying warm and healthy prevents severe illness."),
            NeedWantItem("Designer Sunglasses", "🕶️", false, "Ordinary sunglasses protect your eyes just as well; designer tags are wants."),
            NeedWantItem("School Geometry Box", "📐", true, "Required for mathematics and learning in class."),
            NeedWantItem("Giant 5-Foot Teddy Bear", "🧸", false, "Cute and cozy, but definitely a want!")
        )
    }

    var currentItemIndex by remember { mutableIntStateOf(0) }
    var gameScore by remember { mutableIntStateOf(0) }
    var answerFeedback by remember { mutableStateOf<String?>(null) }
    var isAnswerCorrect by remember { mutableStateOf(false) }

    val currentItem = quizItems[currentItemIndex]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("money_tips_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            MoneyVerseHeader(
                userProfile = state.userProfile,
                title = "Money Tips & Magazine 💡",
                subtitle = "Master financial habits and play the Needs vs Wants game!",
                showBack = true,
                onBackClick = onBack
            )
        }

        // Mini-Game: Needs vs Wants Classifier
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .testTag("needs_wants_game_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Needs vs. Wants Mini-Game 🎯",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryPurple)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Score: $gameScore / ${quizItems.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Item Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = currentItem.iconEmoji, fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentItem.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Choices buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val correct = currentItem.isNeed
                                isAnswerCorrect = correct
                                if (correct) gameScore++
                                answerFeedback = currentItem.explanation
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("game_choice_need_btn")
                        ) {
                            Text(text = "It's a NEED! 🍏", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val correct = !currentItem.isNeed
                                isAnswerCorrect = correct
                                if (correct) gameScore++
                                answerFeedback = currentItem.explanation
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("game_choice_want_btn")
                        ) {
                            Text(text = "It's a WANT! 🎁", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Answer feedback
                    answerFeedback?.let { fb ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAnswerCorrect) Color(0xFFD1FAE5) else Color(0xFFFEE2E2))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isAnswerCorrect) "🎉 Correct!" else "🤔 Not quite!",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAnswerCorrect) Color(0xFF047857) else Color(0xFFB91C1C)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = fb,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextDark
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        answerFeedback = null
                                        currentItemIndex = (currentItemIndex + 1) % quizItems.size
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("game_next_item_btn")
                                ) {
                                    Text("Next Item →", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Financial Wisdom Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Smart Spender Magazine 📚",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }

        items(state.dailyTips, key = { it.id }) { tip ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tip.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tip.tag,
                            fontSize = 10.sp,
                            color = Color(0xFFB45309),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = tip.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDark,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEDE9FE))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "🚀 Action: ${tip.actionText}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
