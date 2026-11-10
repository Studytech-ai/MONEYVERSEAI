package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
    initialName: String = "Kid A",
    initialAvatar: String = "👦",
    initialAgeGroup: String = "10-12 yrs",
    onEnterChildMode: (name: String, avatar: String, ageGroup: String) -> Unit,
    onEnterParentMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember { mutableStateOf(initialName) }
    var selectedAvatar by remember { mutableStateOf(initialAvatar) }
    var selectedAgeGroup by remember { mutableStateOf(initialAgeGroup) }

    val avatars = listOf("👦", "👧", "🚀", "🐷", "🧙", "👑", "🦊", "🎨")
    val ageGroups = listOf("8-10 yrs", "10-12 yrs", "13-15 yrs", "16-18 yrs")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1E0E38), Color(0xFF2A1450), Color(0xFF150B28))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("welcome_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Logo & Title
        Image(
            painter = painterResource(id = R.drawable.ic_moneyverse_logo),
            contentDescription = "MoneyVerse Logo",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(3.dp, Color(0xFF8B5CF6), CircleShape)
                .shadow(8.dp, CircleShape)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "MoneyVerse",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = FredokaFontFamily
        )

        Text(
            text = "Your Money. Your Choices. Your Future.",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFC4B5FD),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Hero Scenery Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_storybook_scene),
                contentDescription = "Magical MoneyVerse World",
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
            Text(
                text = "✨ A magical universe where kids learn real financial wisdom",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Customization Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF22113D),
            borderColor = Color(0x448B5CF6)
        ) {
            Text(
                text = "Set Up Your Explorer Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Display Name / Explorer Name", color = Color(0xFFC4B5FD)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF8B5CF6),
                    unfocusedBorderColor = Color(0x66C4B5FD)
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Choose Your Avatar:", style = MaterialTheme.typography.labelSmall, color = Color(0xFFC4B5FD))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                avatars.forEach { av ->
                    val isSelected = selectedAvatar == av
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF6D28D9) else Color(0x33FFFFFF))
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) Color(0xFFFFB800) else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedAvatar = av }
                            .testTag("onboarding_avatar_$av"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = av, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Age Group:", style = MaterialTheme.typography.labelSmall, color = Color(0xFFC4B5FD))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ageGroups.forEach { ag ->
                    FilterChip(
                        selected = selectedAgeGroup == ag,
                        onClick = { selectedAgeGroup = ag },
                        label = { Text(ag, fontSize = 11.sp, color = if (selectedAgeGroup == ag) DeepPurple else Color.White) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFB800),
                            containerColor = Color(0x22FFFFFF)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Enter Buttons
        Button(
            onClick = {
                onEnterChildMode(
                    if (nameInput.isNotBlank()) nameInput else "Kid A",
                    selectedAvatar,
                    selectedAgeGroup
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("enter_moneyverse_btn")
        ) {
            Text(
                text = "Enter MoneyVerse (Child Mode) 🚀",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onEnterParentMode,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC4B5FD)),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)))
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("welcome_parent_mode_btn")
        ) {
            Text(
                text = "Switch to Parent Guardian Mode 👨‍👩‍👦",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ready-to-use demo profile preloaded with ₹380, Cycle savings goal, and stories for hackathon judges.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
