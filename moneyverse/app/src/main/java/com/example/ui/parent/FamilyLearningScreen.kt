package com.example.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.example.ui.theme.*

data class FamilyActivity(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    var isDone: Boolean = false
)

@Composable
fun FamilyLearningScreen(
    state: MoneyVerseState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activities by remember {
        mutableStateOf(
            listOf(
                FamilyActivity(
                    "act-1",
                    "Dinner Conversation: The Double-Penny Riddle",
                    "Ask your child: Would you rather receive ₹1,000,000 today, or 1 paisa that doubles every single day for 30 days? (Answer: The doubling coin turns into over ₹53 Lakhs!)",
                    "🪙",
                    true
                ),
                FamilyActivity(
                    "act-2",
                    "The Supermarket Price Detective",
                    "On your next grocery trip, give your child a list of 3 items (e.g. oats, apples, milk) and ask them to compare unit prices between brands.",
                    "🛒",
                    false
                ),
                FamilyActivity(
                    "act-3",
                    "The 50/50 Savings Match Challenge",
                    "Agree to match 50% of whatever your child deposits into their Cycle goal this week! Watch their saving speed skyrocket.",
                    "🤝",
                    false
                ),
                FamilyActivity(
                    "act-4",
                    "Wants vs Needs Wall Sort",
                    "Take 5 magazine cutouts or toy catalogs. Draw two circles on a sheet: 'Needs' and 'Wants'. Sort them together over tea.",
                    "✂️",
                    false
                )
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F2))
            .testTag("family_learning_screen"),
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
                        text = "Family Financial Learning 👨‍👩‍👧",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Real-life games and conversations to inspire smart money habits",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Introduction
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                backgroundColor = Color(0xFFF5F3FF)
            ) {
                Text(
                    text = "Why Family Conversations Matter 💬",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Studies show that children who openly discuss money choices with parents develop 3x higher savings rates as young adults.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark
                )
            }
        }

        // Activities List
        items(activities, key = { it.id }) { act ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (act.isDone) Color(0xFFD1FAE5) else Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = act.iconEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = act.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = act.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    activities = activities.map {
                                        if (it.id == act.id) it.copy(isDone = !it.isDone) else it
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (act.isDone) Color(0xFF10B981) else Color(0xFF6366F1)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                if (act.isDone) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Completed!", style = MaterialTheme.typography.labelSmall)
                                } else {
                                    Text("Mark Completed", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
