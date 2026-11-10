package com.example.ui.parent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MoneyVerseState
import com.example.model.ParentChore
import com.example.model.RequestStatus
import com.example.ui.components.CurrencyText
import com.example.ui.components.GlassCard
import com.example.ui.components.ParentNavDestination
import com.example.ui.theme.*

@Composable
fun ParentDashboardScreen(
    state: MoneyVerseState,
    onNavigate: (ParentNavDestination) -> Unit,
    onSwitchToChild: () -> Unit,
    onPayChore: (String) -> Unit,
    onAddChore: (String, Double) -> Unit,
    onQuickAllowance: (Double, String) -> Unit,
    onReviewRequest: (String, Boolean, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = state.userProfile
    val pendingRequests = state.purchaseRequests.filter { it.status == RequestStatus.PENDING }
    val cycleGoal = state.savingsGoals.firstOrNull()

    var showAddChoreDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F2))
            .testTag("parent_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // Parent Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👩‍👧‍👦", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Parent Guardian Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            fontFamily = FredokaFontFamily
                        )
                        Text(
                            text = "Managing ${profile.name}'s financial learning",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                // Child Mode shortcut button
                Button(
                    onClick = onSwitchToChild,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("parent_switch_to_child_btn")
                ) {
                    Text("👦 Child Mode", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Family Tree Artwork Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .shadow(3.dp, RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.parent_family_banner),
                    contentDescription = "Parent and Child Golden Tree",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xCC1E0E38))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Cultivating Lifelong Financial Habits 🌿",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Guide savings goals, review requests, and reward positive effort.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFDDD6FE)
                    )
                }
            }
        }

        // 4 Financial Overview Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Child Wallet Balance
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(ParentNavDestination.ALLOWANCE) },
                    backgroundColor = Color(0xFFF5F3FF)
                ) {
                    Text(text = "Child Balance", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    CurrencyText(amount = state.walletBalance, fontSize = 22, color = PrimaryPurple)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Send Allowance →", style = MaterialTheme.typography.labelSmall, color = PrimaryPurple, fontWeight = FontWeight.Bold)
                }

                // Total Saved in Goals
                val totalSavedInGoals = state.savingsGoals.sumOf { it.currentAmount }
                GlassCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFFECFDF5)
                ) {
                    Text(text = "Saved in Goals", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    CurrencyText(amount = totalSavedInGoals, fontSize = 22, color = Color(0xFF059669))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Cycle: ${(cycleGoal?.progress?.times(100))?.toInt() ?: 0}% done", style = MaterialTheme.typography.labelSmall, color = Color(0xFF059669))
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pending Approvals
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(ParentNavDestination.APPROVALS) },
                    backgroundColor = if (pendingRequests.isNotEmpty()) Color(0xFFFFF1F2) else Color(0xFFF0FDF4)
                ) {
                    Text(text = "Pending Requests", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${pendingRequests.size} Pending",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (pendingRequests.isNotEmpty()) Color(0xFFE11D48) else Color(0xFF059669)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Review Now →", style = MaterialTheme.typography.labelSmall, color = if (pendingRequests.isNotEmpty()) Color(0xFFE11D48) else Color(0xFF059669))
                }

                // Financial Health Index
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(ParentNavDestination.INSIGHTS) },
                    backgroundColor = Color(0xFFFFFBEB)
                ) {
                    Text(text = "Financial IQ", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "86 / 100",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706),
                        fontFamily = FredokaFontFamily
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Healthy Saver 🌿", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD97706))
                }
            }
        }

        // Quick Allowance Presets Bar
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                backgroundColor = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Send Allowance 💸",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    TextButton(onClick = { onNavigate(ParentNavDestination.ALLOWANCE) }) {
                        Text("Custom →", color = PrimaryPurple, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(50, 100, 200, 500).forEach { amt ->
                        Button(
                            onClick = {
                                onQuickAllowance(amt.toDouble(), "Quick pocket money from parent")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_allowance_$amt")
                        ) {
                            Text("+₹$amt", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Chores & Tasks Reward Center (Parent Feature)
        item {
            Column(
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
                        text = "Chore & Study Rewards 🧹",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Button(
                        onClick = { showAddChoreDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("add_new_chore_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Chore", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                state.chores.forEach { chore ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = chore.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "${chore.dueDateDisplay} • Reward: ₹${chore.rewardAmount.toInt()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }

                            if (chore.isPaid) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFD1FAE5))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Rewarded ✓",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF047857),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { onPayChore(chore.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("pay_chore_${chore.id}")
                                ) {
                                    Text("Pay ₹${chore.rewardAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pending Purchase Request Quick Card
        if (pendingRequests.isNotEmpty()) {
            val req = pendingRequests.first()
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Needs Your Decision 🔔",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE11D48)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0xFFFFF1F2)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = req.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "\"${req.reason}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }

                            CurrencyText(amount = req.price, fontSize = 20, color = Color(0xFFE11D48))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onReviewRequest(req.id, false, "Let's focus on completing your savings goal first.")
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Decline")
                            }

                            Button(
                                onClick = {
                                    onReviewRequest(req.id, true, "Approved! Great job keeping up with your studies.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Approve ✅")
                            }
                        }
                    }
                }
            }
        }

        // Navigation Quick Links to other Parent sections
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Parent Guardian Tools 🛠️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ParentToolShortcutCard(
                        title = "Allowance Manager",
                        desc = "Send & schedule pocket coins",
                        icon = "💳",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentNavDestination.ALLOWANCE) }
                    )
                    ParentToolShortcutCard(
                        title = "Approvals Centre",
                        desc = "Review child desires",
                        icon = "📋",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentNavDestination.APPROVALS) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ParentToolShortcutCard(
                        title = "Learning Insights",
                        desc = "Habit & spending analytics",
                        icon = "📊",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentNavDestination.INSIGHTS) }
                    )
                    ParentToolShortcutCard(
                        title = "Family Learning",
                        desc = "Challenges & conversation",
                        icon = "👨‍👩‍👧",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentNavDestination.FAMILY_LEARNING) }
                    )
                }
            }
        }
    }

    // Add Chore Modal Dialog
    if (showAddChoreDialog) {
        var choreTitle by remember { mutableStateOf("") }
        var choreReward by remember { mutableStateOf("50") }

        AlertDialog(
            onDismissRequest = { showAddChoreDialog = false },
            title = {
                Text(text = "Assign New Chore or Goal 🧹", fontFamily = FredokaFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = choreTitle,
                        onValueChange = { choreTitle = it },
                        label = { Text("Chore Title (e.g. Read 5 chapters of book)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_chore_title_input")
                    )

                    OutlinedTextField(
                        value = choreReward,
                        onValueChange = { choreReward = it },
                        label = { Text("Reward Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_chore_reward_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val reward = choreReward.toDoubleOrNull() ?: 0.0
                        if (choreTitle.isNotBlank() && reward > 0) {
                            onAddChore(choreTitle, reward)
                            showAddChoreDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("Assign Chore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddChoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ParentToolShortcutCard(
    title: String,
    desc: String,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(95.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = icon, fontSize = 20.sp)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1)
                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}
