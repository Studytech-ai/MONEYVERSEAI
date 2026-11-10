package com.example.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoneyVerseState
import com.example.model.SavingsGoal
import com.example.ui.components.CurrencyText
import com.example.ui.components.GlassCard
import com.example.ui.components.MoneyVerseHeader
import com.example.ui.theme.*

@Composable
fun SavingsGoalsScreen(
    state: MoneyVerseState,
    onCreateGoal: (String, Double, String, String) -> Unit,
    onContribute: (String, Double) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedGoalForContribute by remember { mutableStateOf<SavingsGoal?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("savings_goals_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            MoneyVerseHeader(
                userProfile = state.userProfile,
                title = "Savings Goals 🎯",
                subtitle = "Turn your dreams into reality, one rupee at a time!",
                showBack = true,
                onBackClick = onBack
            )
        }

        // Summary Bar & Create Goal CTA
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                backgroundColor = Color(0xFFF5F3FF)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Available in Wallet",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        CurrencyText(amount = state.walletBalance, fontSize = 22, color = PrimaryPurple)
                    }

                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("create_new_goal_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "New Goal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Goal Cards List
        items(state.savingsGoals, key = { it.id }) { goal ->
            val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("goal_card_${goal.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = goal.icon, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = goal.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            if (goal.isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFD1FAE5))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Goal Reached! 🌟",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF047857),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(
                                    text = "₹${goal.currentAmount.toInt()} / ₹${goal.targetAmount.toInt()}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PrimaryPurple,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${goal.category} • ${goal.daysLeft} days remaining",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { goal.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = if (goal.isCompleted) Color(0xFF10B981) else PrimaryPurple,
                            trackColor = Color(0xFFE5E7EB)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${(goal.progress * 100).toInt()}% saved (${remaining.toInt()} to go)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )

                            if (!goal.isCompleted) {
                                Button(
                                    onClick = { selectedGoalForContribute = goal },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("contribute_btn_${goal.id}")
                                ) {
                                    Text(
                                        text = "+ Save Coins",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Recent contributions list
                if (goal.contributions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Contribution History",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    goal.contributions.take(2).forEach { c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${c.note.ifBlank { "Deposit" }} (${c.dateDisplay})",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDark,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "+₹${c.amount.toInt()}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        }
    }

    // --- CREATE GOAL DIALOG ---
    if (showCreateDialog) {
        var inputTitle by remember { mutableStateOf("") }
        var inputTarget by remember { mutableStateOf("") }
        var selectedIcon by remember { mutableStateOf("🚲") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        val iconOptions = listOf("🚲", "🎧", "📚", "🎨", "🎮", "⚽", "⛺")

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(text = "Create a Dream Goal ✨", fontFamily = FredokaFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("Goal Name (e.g. Science Telescope)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_title_input")
                    )

                    OutlinedTextField(
                        value = inputTarget,
                        onValueChange = {
                            inputTarget = it
                            errorMessage = null
                        },
                        label = { Text("Target Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_target_input")
                    )

                    Text(text = "Choose an Icon:", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconOptions.forEach { ic ->
                            FilterChip(
                                selected = selectedIcon == ic,
                                onClick = { selectedIcon = ic },
                                label = { Text(ic, fontSize = 18.sp) }
                            )
                        }
                    }

                    errorMessage?.let {
                        Text(text = it, color = CoralRed, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = inputTarget.toDoubleOrNull() ?: 0.0
                        if (inputTitle.isBlank()) {
                            errorMessage = "Please enter goal name"
                        } else if (target <= 0) {
                            errorMessage = "Target amount must be > 0"
                        } else {
                            onCreateGoal(inputTitle, target, "Personal Goal", selectedIcon)
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    modifier = Modifier.testTag("confirm_create_goal_btn")
                ) {
                    Text("Create Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- CONTRIBUTE TO GOAL DIALOG ---
    selectedGoalForContribute?.let { goal ->
        var inputAmount by remember { mutableStateOf("50") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { selectedGoalForContribute = null },
            title = {
                Text(text = "Save for ${goal.title} 🎯", fontFamily = FredokaFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Wallet balance: ₹${state.walletBalance.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Current saved: ₹${goal.currentAmount.toInt()} / ₹${goal.targetAmount.toInt()}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = {
                            inputAmount = it
                            errorMessage = null
                        },
                        label = { Text("Contribution Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contribute_amount_input")
                    )

                    errorMessage?.let {
                        Text(text = it, color = CoralRed, style = MaterialTheme.typography.bodySmall)
                    }

                    // Quick amounts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(20, 50, 100).forEach { amt ->
                            OutlinedButton(
                                onClick = { inputAmount = amt.toString() },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+₹$amt")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = inputAmount.toDoubleOrNull() ?: 0.0
                        when {
                            amount <= 0 -> errorMessage = "Amount must be greater than 0"
                            amount > state.walletBalance -> errorMessage = "Cannot exceed wallet balance (₹${state.walletBalance.toInt()})!"
                            else -> {
                                onContribute(goal.id, amount)
                                selectedGoalForContribute = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.testTag("confirm_contribute_btn")
                ) {
                    Text("Confirm Deposit")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedGoalForContribute = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
