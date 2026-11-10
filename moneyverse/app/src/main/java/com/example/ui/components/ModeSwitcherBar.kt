package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun ModeSwitcherBar(
    isParentMode: Boolean,
    onSelectChildMode: () -> Unit,
    onSelectParentMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(if (isParentMode) Color(0xFF1E1035) else Color(0xFF26134D))
                .border(1.dp, Color(0x33A78BFA), RoundedCornerShape(22.dp))
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Child Mode Option
                val childBg by animateColorAsState(
                    targetValue = if (!isParentMode) Color(0xFF7C3AED) else Color.Transparent,
                    label = "childBg"
                )
                val childTextColor by animateColorAsState(
                    targetValue = if (!isParentMode) Color.White else Color(0xFFC4B5FD),
                    label = "childText"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(childBg)
                        .clickable { onSelectChildMode() }
                        .padding(horizontal = 12.dp)
                        .testTag("switch_to_child_mode_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👦", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Child Mode",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (!isParentMode) FontWeight.Bold else FontWeight.Medium,
                            color = childTextColor,
                            fontFamily = FredokaFontFamily
                        )
                        if (!isParentMode) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF34D399))
                            )
                        }
                    }
                }

                // Parent Mode Option
                val parentBg by animateColorAsState(
                    targetValue = if (isParentMode) Color(0xFF4F46E5) else Color.Transparent,
                    label = "parentBg"
                )
                val parentTextColor by animateColorAsState(
                    targetValue = if (isParentMode) Color.White else Color(0xFFC4B5FD),
                    label = "parentText"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(parentBg)
                        .clickable { onSelectParentMode() }
                        .padding(horizontal = 12.dp)
                        .testTag("switch_to_parent_mode_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👨‍👩‍👧", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Parent Mode",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isParentMode) FontWeight.Bold else FontWeight.Medium,
                            color = parentTextColor,
                            fontFamily = FredokaFontFamily
                        )
                        if (isParentMode) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8))
                            )
                        }
                    }
                }
            }
        }
    }
}
