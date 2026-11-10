package com.example.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoneyVerseState
import com.example.model.PurchaseRequest
import com.example.model.RequestStatus
import com.example.ui.components.CurrencyText
import com.example.ui.components.GlassCard
import com.example.ui.components.MoneyVerseHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun PurchaseRequestsScreen(
    state: MoneyVerseState,
    onCreateRequest: (String, Double, String) -> Unit,
    onConfirmPurchase: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedRequestToPurchase by remember { mutableStateOf<PurchaseRequest?>(null) }
    var selectedFilter by remember { mutableStateOf<RequestStatus?>(null) }

    val filteredList = when (selectedFilter) {
        null -> state.purchaseRequests
        else -> state.purchaseRequests.filter { it.status == selectedFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("purchase_requests_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            MoneyVerseHeader(
                userProfile = state.userProfile,
                title = "Purchase Requests 📋",
                subtitle = "Ask your parents before spending on big wants",
                showBack = true,
                onBackClick = onBack
            )
        }

        // Info card & Create request button
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                backgroundColor = Color(0xFFFFF1F2)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Smart Habit 💡",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE11D48),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Writing reasons helps you decide if something is a True Need or a Temporary Want!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDark
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("new_request_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Request", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All (${state.purchaseRequests.size})") }
                )
                FilterChip(
                    selected = selectedFilter == RequestStatus.PENDING,
                    onClick = { selectedFilter = RequestStatus.PENDING },
                    label = { Text("Pending") }
                )
                FilterChip(
                    selected = selectedFilter == RequestStatus.APPROVED,
                    onClick = { selectedFilter = RequestStatus.APPROVED },
                    label = { Text("Approved") }
                )
                FilterChip(
                    selected = selectedFilter == RequestStatus.PURCHASED,
                    onClick = { selectedFilter = RequestStatus.PURCHASED },
                    label = { Text("Purchased") }
                )
            }
        }

        // Requests items
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No purchase requests found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(filteredList, key = { it.id }) { req ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("request_item_${req.id}")
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Requested: ${req.dateDisplay}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        StatusBadge(status = req.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Why I want this:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = "\"${req.reason}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark
                    )

                    // Parent feedback if any
                    if (req.parentFeedback.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEDE9FE), RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "💬 Parent note: ${req.parentFeedback}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5B21B6)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CurrencyText(amount = req.price, fontSize = 20, color = Color(0xFFE11D48))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (req.status == RequestStatus.PENDING) {
                                TextButton(
                                    onClick = { onCancelRequest(req.id) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextMuted)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cancel", color = TextMuted)
                                }
                            }

                            if (req.status == RequestStatus.APPROVED) {
                                Button(
                                    onClick = { selectedRequestToPurchase = req },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("simulate_purchase_btn_${req.id}")
                                ) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Simulate Purchase", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- CREATE REQUEST DIALOG ---
    if (showCreateDialog) {
        var inputTitle by remember { mutableStateOf("") }
        var inputPrice by remember { mutableStateOf("") }
        var inputReason by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(text = "Ask for Something 🛍️", fontFamily = FredokaFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("Product / Item Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_title_input")
                    )

                    OutlinedTextField(
                        value = inputPrice,
                        onValueChange = {
                            inputPrice = it
                            errorMessage = null
                        },
                        label = { Text("Estimated Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_price_input")
                    )

                    OutlinedTextField(
                        value = inputReason,
                        onValueChange = { inputReason = it },
                        label = { Text("Why do you want this?") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_reason_input")
                    )

                    errorMessage?.let {
                        Text(text = it, color = CoralRed, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = inputPrice.toDoubleOrNull() ?: 0.0
                        if (inputTitle.isBlank()) {
                            errorMessage = "Please enter product name"
                        } else if (price <= 0) {
                            errorMessage = "Please enter a valid price"
                        } else if (inputReason.isBlank()) {
                            errorMessage = "Please write a reason"
                        } else {
                            onCreateRequest(inputTitle, price, inputReason)
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    modifier = Modifier.testTag("submit_purchase_request_btn")
                ) {
                    Text("Send to Parent")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- CONFIRM PURCHASE DIALOG ---
    selectedRequestToPurchase?.let { req ->
        AlertDialog(
            onDismissRequest = { selectedRequestToPurchase = null },
            title = {
                Text(text = "Confirm Purchase 🎉", fontFamily = FredokaFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to buy \"${req.title}\" for ₹${req.price.toInt()}?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Current wallet balance: ₹${state.walletBalance.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Remaining balance after purchase: ₹${(state.walletBalance - req.price).toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmPurchase(req.id)
                        selectedRequestToPurchase = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.testTag("confirm_simulated_purchase_btn")
                ) {
                    Text("Yes, Buy It!")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRequestToPurchase = null }) {
                    Text("Not Yet")
                }
            }
        )
    }
}
