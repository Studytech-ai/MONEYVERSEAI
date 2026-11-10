package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

enum class AppNavDestination(val label: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "nav_home"),
    MY_MONEY("My Money", Icons.Default.AccountBalanceWallet, "nav_my_money"),
    SAVINGS_GOALS("Savings Goals", Icons.Default.TrackChanges, "nav_savings_goals"),
    PURCHASE_REQUESTS("Purchase Requests", Icons.Default.ShoppingCart, "nav_purchase_requests"),
    MONEY_STORIES("Money Stories", Icons.Default.MenuBook, "nav_money_stories"),
    REWARDS("Rewards", Icons.Default.EmojiEvents, "nav_rewards"),
    TIME_MACHINE("Time Machine", Icons.Default.AutoAwesome, "nav_time_machine"),
    MONEY_TIPS("Money Tips", Icons.Default.Lightbulb, "nav_money_tips"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_settings")
}

enum class ParentNavDestination(val label: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Parent Home", Icons.Default.Dashboard, "pnav_dashboard"),
    ALLOWANCE("Allowance", Icons.Default.Payments, "pnav_allowance"),
    APPROVALS("Approvals", Icons.Default.FactCheck, "pnav_approvals"),
    INSIGHTS("Insights", Icons.Default.Insights, "pnav_insights"),
    FAMILY_LEARNING("Family Learning", Icons.Default.FamilyRestroom, "pnav_family_learning"),
    SETTINGS("Settings", Icons.Default.AdminPanelSettings, "pnav_settings")
}

@Composable
fun MoneyVerseSidebar(
    currentDestination: AppNavDestination,
    onNavigate: (AppNavDestination) -> Unit,
    onSwitchToParent: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(DarkSidebarPurple)
            .padding(horizontal = 14.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Logo & Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_moneyverse_logo),
                    contentDescription = "MoneyVerse Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "MoneyVerse",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFontFamily
                    )
                    Text(
                        text = "Your Money. Your Choices.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFC4B5FD),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Items
            AppNavDestination.values().forEach { destination ->
                val isSelected = destination == currentDestination
                val bgBrush = if (isSelected) {
                    Brush.horizontalGradient(listOf(Color(0xFF6D38E2), Color(0xFF8B5CF6)))
                } else {
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgBrush)
                        .clickable { onNavigate(destination) }
                        .padding(horizontal = 14.dp, vertical = 11.dp)
                        .testTag(destination.tag),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                        tint = if (isSelected) Color.White else Color(0xFFD4CCF7),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) Color.White else Color(0xFFD4CCF7),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Bottom promotional badge & switch
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x338B5CF6), Color(0x664C1D95))
                        )
                    )
                    .border(1.dp, Color(0x33A78BFA), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🏆", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Be a\nMoney Smart Kid!",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Switch to Parent Mode button in sidebar
            Button(
                onClick = onSwitchToParent,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sidebar_parent_mode_button")
            ) {
                Text(
                    text = "Parent Mode 👨‍👩‍👧",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ParentSidebar(
    currentDestination: ParentNavDestination,
    onNavigate: (ParentNavDestination) -> Unit,
    onSwitchToChild: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(Color(0xFF1E1035))
            .padding(horizontal = 14.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Parent Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6366F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👨‍👩‍👦", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Parent Hub",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "MoneyVerse Guardian",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA5B4FC),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ParentNavDestination.values().forEach { destination ->
                val isSelected = destination == currentDestination
                val bgBrush = if (isSelected) {
                    Brush.horizontalGradient(listOf(Color(0xFF4F46E5), Color(0xFF6366F1)))
                } else {
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgBrush)
                        .clickable { onNavigate(destination) }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag(destination.tag),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                        tint = if (isSelected) Color.White else Color(0xFFC7D2FE),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) Color.White else Color(0xFFC7D2FE),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            OutlinedButton(
                onClick = onSwitchToChild,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Color(0xFF818CF8), Color(0xFFA78BFA)))),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exit_parent_mode_button")
            ) {
                Text(
                    text = "Back to Kid Mode 🚀",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
