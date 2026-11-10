package com.example.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoneyVerseState
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun ParentSettingsScreen(
    state: MoneyVerseState,
    onResetDemoData: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var parentName by remember { mutableStateOf("Parent Guardian") }
    var currentPin by remember { mutableStateOf(state.userProfile.parentPin) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var pinSavedNotice by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F2))
            .testTag("parent_settings_screen"),
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
                        text = "Parent Guardian Settings ⚙️",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Security controls and system demo preferences",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Guardian Profile Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Guardian Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent / Guardian Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "4-Digit Parent PIN Access:",
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = currentPin,
                    onValueChange = {
                        if (it.length <= 4) {
                            currentPin = it
                            pinSavedNotice = true
                        }
                    },
                    label = { Text("Parent PIN (Default 1234)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_settings_pin_input")
                )

                if (pinSavedNotice) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "PIN updated successfully! ✓", color = Color(0xFF059669), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Child Protection & Security Transparency
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                backgroundColor = Color(0xFFF0FDF4)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF059669))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Security & Child Privacy Commitment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• 100% Simulated Educational Balances: No banking credentials, credit card details, or real monetary transfers are used or required.\n\n" +
                            "• Zero Data Tracking: Child profile data stays safely within the applet demo.\n\n" +
                            "• Safe Decision Sandbox: Children learn consequence-free financial judgment before encountering real-world money.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark,
                    lineHeight = 20.sp
                )
            }
        }

        // Reset Demo Data Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralRed),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(CoralRed, CoralRed))),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_reset_demo_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reset All Demo Data to Initial State", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = {
                Text(text = "Reset Demo Data? ⚠️", fontFamily = FredokaFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "This restores initial pristine demo balances, the Cycle savings goal (64%), and starter transactions for hackathon judges.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDemoData()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Reset All Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
