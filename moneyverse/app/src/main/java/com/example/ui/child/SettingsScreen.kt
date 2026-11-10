package com.example.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun SettingsScreen(
    state: MoneyVerseState,
    onUpdateProfile: (String, String, String) -> Unit,
    onResetDemoData: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = state.userProfile

    var displayName by remember { mutableStateOf(profile.name) }
    var selectedAvatar by remember { mutableStateOf(profile.avatarEmoji) }
    var selectedAgeGroup by remember { mutableStateOf(profile.ageGroup) }
    var soundEnabled by remember { mutableStateOf(true) }
    var animationsEnabled by remember { mutableStateOf(true) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    val avatars = listOf("👦", "👧", "🚀", "🐷", "🧙", "👑", "🦊", "🎨")
    val ageGroups = listOf("8-10 yrs", "10-12 yrs", "13-15 yrs", "16-18 yrs")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            MoneyVerseHeader(
                userProfile = profile,
                title = "Child Profile & Settings ⚙️",
                subtitle = "Personalize your journey and customize preferences",
                showBack = true,
                onBackClick = onBack
            )
        }

        // Profile Editor Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Profile Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = displayName,
                    onValueChange = {
                        displayName = it
                        onUpdateProfile(it, selectedAvatar, selectedAgeGroup)
                    },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_display_name_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Choose Your Avatar:", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    avatars.forEach { av ->
                        val isSelected = selectedAvatar == av
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFFDDD6FE) else Color(0xFFF3F4F6))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) PrimaryPurple else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedAvatar = av
                                    onUpdateProfile(displayName, av, selectedAgeGroup)
                                }
                                .testTag("avatar_option_$av"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = av, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Age Group:", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ageGroups.forEach { ag ->
                        FilterChip(
                            selected = selectedAgeGroup == ag,
                            onClick = {
                                selectedAgeGroup = ag
                                onUpdateProfile(displayName, selectedAvatar, ag)
                            },
                            label = { Text(ag, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Preferences Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "App Experience",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Sound & Audio Effects", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Play coins and level-up sounds", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Celebration Animations", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Show sparkling confetti on milestones", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                    Switch(
                        checked = animationsEnabled,
                        onCheckedChange = { animationsEnabled = it }
                    )
                }
            }
        }

        // About MoneyVerse & Safety Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                backgroundColor = Color(0xFFFDFBF7)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About MoneyVerse",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tagline: Your Money. Your Choices. Your Future.\n\n" +
                            "Mission: Transform financial literacy into a magical, interactive adventure where kids master saving, budgeting, needs vs wants, and responsible decision making.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "🛡️ Child Privacy & Simulated Security:\n" +
                                "All balances, transactions, and allowances in MoneyVerse are simulated educational money. No real bank accounts or credentials are ever accessed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF92400E),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Reset Demo Data Button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedButton(
                    onClick = { showResetConfirmDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralRed),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(CoralRed, CoralRed))),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reset_demo_data_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reset Demo Data to Initial State", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Confirmation Dialog for Reset
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(text = "Reset Demo Data? ⚠️", fontFamily = FredokaFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "This will restore the pristine judge demo data (Kid A, ₹380 balance, Cycle savings goal, and initial transactions).")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDemoData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                    modifier = Modifier.testTag("confirm_reset_demo_btn")
                ) {
                    Text("Reset All Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
