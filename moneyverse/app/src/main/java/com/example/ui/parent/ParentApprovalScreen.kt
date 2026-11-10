package com.example.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoneyVerseState
import com.example.model.PurchaseRequest
import com.example.model.RequestStatus
import com.example.ui.components.CurrencyText
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ParentApprovalScreen(
    state: MoneyVerseState,
    onReviewRequest: (String, Boolean, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRequestToReview by remember { mutableStateOf<Pair<PurchaseRequest, Boolean>?>(null) }

    val pendingList = state.purchaseRequests.filter { it.status == RequestStatus.PENDING }
    val reviewedList = state.purchaseRequests.filter { it.status != RequestStatus.PENDING }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F2))
            .testTag("parent_approval_screen"),
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
                        text = "Purchase Approvals ✅",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Review and guide ${state.userProfile.name}'s purchase requests",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Pending Section
        item {
            Text(
                text = "Pending Requests (${pendingList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (pendingList.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    backgroundColor = Color(0xFFF0FDF4)
                ) {
                    Text(
                        text = "🎉 All caught up! No pending purchase requests from ${state.userProfile.name}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF166534)
                    )
                }
            }
        } else {
            items(pendingList, key = { it.id }) { req ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("parent_pending_req_${req.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = req.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "Requested: ${req.dateDisplay}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        CurrencyText(amount = req.price, fontSize = 22, color = Color(0xFFE11D48))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F3FF), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Child's Reason:",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryPurple,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"${req.reason}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedRequestToReview = Pair(req, false) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("parent_decline_btn_${req.id}")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Decline")
                        }

                        Button(
                            onClick = { selectedRequestToReview = Pair(req, true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("parent_approve_btn_${req.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve")
                        }
                    }
                }
            }
        }

        // Reviewed History Section
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Reviewed History (${reviewedList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        items(reviewedList, key = { it.id }) { req ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
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
                            text = "₹${req.price.toInt()} • ${req.dateDisplay}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        if (req.parentFeedback.isNotBlank()) {
                            Text(
                                text = "Your Note: \"${req.parentFeedback}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryPurple,
                                fontSize = 11.sp
                            )
                        }
                    }

                    StatusBadge(status = req.status)
                }
            }
        }
    }

    // Feedback Dialog for Approval / Rejection
    selectedRequestToReview?.let { (req, isApprove) ->
        var feedbackInput by remember {
            mutableStateOf(
                if (isApprove) "Great job meeting your study goals! Remember to check your remaining balance before buying."
                else "Let's wait for next month and focus on finishing current savings goal first."
            )
        }

        AlertDialog(
            onDismissRequest = { selectedRequestToReview = null },
            title = {
                Text(
                    text = if (isApprove) "Approve Purchase Request ✅" else "Decline Purchase Request ❌",
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Item: ${req.title} (₹${req.price.toInt()})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Leave constructive parent guidance for your child:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = feedbackInput,
                        onValueChange = { feedbackInput = it },
                        label = { Text("Parent Feedback Note") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_feedback_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReviewRequest(req.id, isApprove, feedbackInput)
                        selectedRequestToReview = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isApprove) Color(0xFF10B981) else CoralRed
                    ),
                    modifier = Modifier.testTag("confirm_parent_review_btn")
                ) {
                    Text(if (isApprove) "Confirm Approval" else "Confirm Decline")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRequestToReview = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
