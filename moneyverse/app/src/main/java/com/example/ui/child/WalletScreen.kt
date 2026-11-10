package com.example.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoneyVerseState
import com.example.model.TransactionCategory
import com.example.model.TransactionType
import com.example.ui.components.CurrencyText
import com.example.ui.components.GlassCard
import com.example.ui.components.MoneyVerseHeader
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    state: MoneyVerseState,
    onAddMoney: (Double, String) -> Unit,
    onRecordExpense: (String, Double, TransactionCategory, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddMoneyDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<TransactionType?>(null) }

    val totalIncome = state.transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = state.transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    val filteredTransactions = when (selectedFilter) {
        TransactionType.INCOME -> state.transactions.filter { it.type == TransactionType.INCOME }
        TransactionType.EXPENSE -> state.transactions.filter { it.type == TransactionType.EXPENSE }
        null -> state.transactions
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            MoneyVerseHeader(
                userProfile = state.userProfile,
                title = "My Treasure Wallet 👛",
                subtitle = "Track your virtual income and expenses",
                showBack = true,
                onBackClick = onBack
            )
        }

        // Hero Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .shadow(4.dp, RoundedCornerShape(26.dp))
                    .testTag("wallet_hero_card"),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF6D38E2), Color(0xFF4C1D95))
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current Balance",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFDDD6FE)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x33FFFFFF))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Simulated Money",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        CurrencyText(
                            amount = state.walletBalance,
                            fontSize = 38,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showAddMoneyDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_pocket_money_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Add Money", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showExpenseDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("record_expense_btn")
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Log Spend", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total In
                GlassCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFFF0FDF4)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Total In", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(text = "₹${totalIncome.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                    }
                }

                // Total Out
                GlassCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFFFEF2F2)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Total Out", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(text = "₹${totalExpense.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        }
                    }
                }
            }
        }

        // Filters Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaction History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = selectedFilter == TransactionType.INCOME,
                            onClick = { selectedFilter = TransactionType.INCOME },
                            label = { Text("Income") }
                        )
                        FilterChip(
                            selected = selectedFilter == TransactionType.EXPENSE,
                            onClick = { selectedFilter = TransactionType.EXPENSE },
                            label = { Text("Expense") }
                        )
                    }
                }
            }
        }

        // Transaction list items
        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found in this category",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { tx ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (badgeBg, badgeCol, symbol) = if (tx.type == TransactionType.INCOME) {
                            Triple(Color(0xFFD1FAE5), Color(0xFF059669), "+")
                        } else {
                            Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), "−")
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(badgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tx.category.icon,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "${tx.category.displayName} • ${tx.dateDisplay}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            if (tx.note.isNotBlank()) {
                                Text(
                                    text = "\"${tx.note}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF7C3AED),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "$symbol₹${tx.amount.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = badgeCol
                        )
                    }
                }
            }
        }
    }

    // --- ADD POCKET MONEY DIALOG ---
    if (showAddMoneyDialog) {
        var inputAmount by remember { mutableStateOf("100") }
        var inputNote by remember { mutableStateOf("Weekly chores") }

        AlertDialog(
            onDismissRequest = { showAddMoneyDialog = false },
            title = {
                Text(text = "Add Pocket Money 🪙", fontFamily = FredokaFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Enter simulated allowance or pocket money amount:", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_money_amount_input")
                    )
                    OutlinedTextField(
                        value = inputNote,
                        onValueChange = { inputNote = it },
                        label = { Text("Note / Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = inputAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            onAddMoney(amount, inputNote)
                            showAddMoneyDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.testTag("confirm_add_money_btn")
                ) {
                    Text("Add to Wallet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMoneyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- RECORD EXPENSE DIALOG ---
    if (showExpenseDialog) {
        var inputTitle by remember { mutableStateOf("") }
        var inputAmount by remember { mutableStateOf("") }
        var inputNote by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf(TransactionCategory.FOOD_TREATS) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = {
                Text(text = "Record an Expense 🧾", fontFamily = FredokaFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Available balance: ₹${state.walletBalance.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("What did you buy? (e.g. Comic Book)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_title_input")
                    )

                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = {
                            inputAmount = it
                            errorMessage = null
                        },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = errorMessage != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_amount_input")
                    )

                    errorMessage?.let {
                        Text(text = it, color = CoralRed, style = MaterialTheme.typography.bodySmall)
                    }

                    Text(text = "Category:", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            TransactionCategory.FOOD_TREATS,
                            TransactionCategory.TOYS_GAMES,
                            TransactionCategory.BOOKS_LEARNING
                        ).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text("${cat.icon} ${cat.displayName.take(8)}") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = inputAmount.toDoubleOrNull() ?: 0.0
                        when {
                            amount <= 0 -> errorMessage = "Please enter a valid amount"
                            amount > state.walletBalance -> errorMessage = "Cannot spend more than wallet balance (₹${state.walletBalance.toInt()})!"
                            inputTitle.isBlank() -> errorMessage = "Please enter item name"
                            else -> {
                                onRecordExpense(inputTitle, amount, selectedCategory, inputNote)
                                showExpenseDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("confirm_record_expense_btn")
                ) {
                    Text("Record Spend")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExpenseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
