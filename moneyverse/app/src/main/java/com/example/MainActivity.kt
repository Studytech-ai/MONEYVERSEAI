package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MoneyVerseRepository
import com.example.ui.WelcomeScreen
import com.example.ui.child.*
import com.example.ui.components.*
import com.example.ui.parent.*
import com.example.ui.theme.LavenderBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val repository = MoneyVerseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MoneyVerseApp(repository = repository)
            }
        }
    }
}

@Composable
fun MoneyVerseApp(
    repository: MoneyVerseRepository,
    modifier: Modifier = Modifier
) {
    val state by repository.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigation states
    var showWelcomeScreen by remember { mutableStateOf(false) }
    var isParentMode by remember { mutableStateOf(false) }
    var currentChildNav by remember { mutableStateOf(AppNavDestination.HOME) }
    var currentParentNav by remember { mutableStateOf(ParentNavDestination.DASHBOARD) }
    var activeStoryId by remember { mutableStateOf<String?>(null) }
    var showParentPinDialog by remember { mutableStateOf(false) }

    // Toast message trigger
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            repository.clearToast()
        }
    }

    // Back button handling
    BackHandler(enabled = isParentMode || currentChildNav != AppNavDestination.HOME || activeStoryId != null || showWelcomeScreen) {
        when {
            showWelcomeScreen -> showWelcomeScreen = false
            activeStoryId != null -> activeStoryId = null
            currentChildNav != AppNavDestination.HOME && !isParentMode -> currentChildNav = AppNavDestination.HOME
            isParentMode && currentParentNav != ParentNavDestination.DASHBOARD -> currentParentNav = ParentNavDestination.DASHBOARD
            isParentMode -> isParentMode = false
        }
    }

    if (showWelcomeScreen) {
        WelcomeScreen(
            initialName = state.userProfile.name,
            initialAvatar = state.userProfile.avatarEmoji,
            initialAgeGroup = state.userProfile.ageGroup,
            onEnterChildMode = { name, avatar, age ->
                repository.updateProfile(name, avatar, age)
                showWelcomeScreen = false
                isParentMode = false
                currentChildNav = AppNavDestination.HOME
            },
            onEnterParentMode = {
                showWelcomeScreen = false
                isParentMode = true
                currentParentNav = ParentNavDestination.DASHBOARD
            }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // On compact screens (phone portrait), show responsive bottom bar
            BoxWithConstraints {
                if (maxWidth < 650.dp) {
                    if (!isParentMode && activeStoryId == null) {
                        NavigationBar(
                            containerColor = Color(0xFF22113E),
                            contentColor = Color.White
                        ) {
                            listOf(
                                AppNavDestination.HOME,
                                AppNavDestination.MY_MONEY,
                                AppNavDestination.SAVINGS_GOALS,
                                AppNavDestination.PURCHASE_REQUESTS,
                                AppNavDestination.MONEY_STORIES
                            ).forEach { dest ->
                                val isSelected = currentChildNav == dest
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentChildNav = dest },
                                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                                    label = { Text(dest.label) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        indicatorColor = Color(0xFF6D38E2),
                                        unselectedIconColor = Color(0xFFC4B5FD),
                                        unselectedTextColor = Color(0xFFC4B5FD)
                                    ),
                                    modifier = Modifier.testTag("bottom_${dest.tag}")
                                )
                            }
                        }
                    } else if (isParentMode) {
                        NavigationBar(
                            containerColor = Color(0xFF1E1035),
                            contentColor = Color.White
                        ) {
                            listOf(
                                ParentNavDestination.DASHBOARD,
                                ParentNavDestination.ALLOWANCE,
                                ParentNavDestination.APPROVALS,
                                ParentNavDestination.INSIGHTS,
                                ParentNavDestination.FAMILY_LEARNING
                            ).forEach { dest ->
                                val isSelected = currentParentNav == dest
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentParentNav = dest },
                                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                                    label = { Text(dest.label, fontSize = 10.sp, maxLines = 1) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        indicatorColor = Color(0xFF4F46E5),
                                        unselectedIconColor = Color(0xFFA5B4FC),
                                        unselectedTextColor = Color(0xFFA5B4FC)
                                    ),
                                    modifier = Modifier.testTag("bottom_${dest.tag}")
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 650.dp

            Row(modifier = Modifier.fillMaxSize()) {
                // Adaptive Left Sidebar for Wide Screens / Tablets / Streaming emulator
                if (isWideScreen) {
                    if (!isParentMode) {
                        MoneyVerseSidebar(
                            currentDestination = currentChildNav,
                            onNavigate = { dest ->
                                activeStoryId = null
                                currentChildNav = dest
                            },
                            onSwitchToParent = {
                                isParentMode = true
                                currentParentNav = ParentNavDestination.DASHBOARD
                            }
                        )
                    } else {
                        ParentSidebar(
                            currentDestination = currentParentNav,
                            onNavigate = { dest -> currentParentNav = dest },
                            onSwitchToChild = { isParentMode = false }
                        )
                    }
                }

                // Main Content Area with Persistent Two Modes Switcher
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (isParentMode) Color(0xFFFAF7F2) else LavenderBackground)
                ) {
                    // Top TWO MODES Switcher Bar
                    if (activeStoryId == null) {
                        ModeSwitcherBar(
                            isParentMode = isParentMode,
                            onSelectChildMode = {
                                isParentMode = false
                                currentChildNav = AppNavDestination.HOME
                            },
                            onSelectParentMode = {
                                isParentMode = true
                                currentParentNav = ParentNavDestination.DASHBOARD
                            }
                        )
                    }

                    // Content screens
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        if (!isParentMode) {
                            // Child Experience
                            if (activeStoryId != null) {
                                val activeStory = state.stories.find { it.id == activeStoryId }
                                if (activeStory != null) {
                                    StoryReaderScreen(
                                        story = activeStory,
                                        onCompleteStory = { id, xp ->
                                            repository.completeStory(id, xp)
                                        },
                                        onBack = { activeStoryId = null }
                                    )
                                } else {
                                    activeStoryId = null
                                }
                            } else {
                                when (currentChildNav) {
                                    AppNavDestination.HOME -> {
                                        ChildDashboardScreen(
                                            state = state,
                                            onNavigate = { dest -> currentChildNav = dest },
                                            onSwitchToParent = {
                                                isParentMode = true
                                                currentParentNav = ParentNavDestination.DASHBOARD
                                            },
                                            onOpenStory = { storyId -> activeStoryId = storyId },
                                            onQuickContribute = { goalId, amt ->
                                                repository.contributeToGoal(goalId, amt)
                                            }
                                        )
                                    }
                                    AppNavDestination.MY_MONEY -> {
                                        WalletScreen(
                                            state = state,
                                            onAddMoney = { amt, note -> repository.addPocketMoney(amt, note) },
                                            onRecordExpense = { title, amt, cat, note ->
                                                repository.recordExpense(title, amt, cat, note)
                                            },
                                            onBack = { currentChildNav = AppNavDestination.HOME }
                                        )
                                    }
                                    AppNavDestination.SAVINGS_GOALS -> {
                                        SavingsGoalsScreen(
                                            state = state,
                                            onCreateGoal = { title, target, cat, icon ->
                                                repository.createSavingsGoal(title, target, cat, icon)
                                            },
                                            onContribute = { goalId, amt ->
                                                repository.contributeToGoal(goalId, amt)
                                            },
                                            onBack = { currentChildNav = AppNavDestination.HOME }
                                        )
                                    }
                                    AppNavDestination.PURCHASE_REQUESTS -> {
                                        PurchaseRequestsScreen(
                                            state = state,
                                            onCreateRequest = { title, price, reason ->
                                                repository.createPurchaseRequest(title, price, reason)
                                            },
                                            onConfirmPurchase = { reqId ->
                                                repository.confirmPurchase(reqId)
                                            },
                                            onCancelRequest = { reqId ->
                                                repository.cancelPurchaseRequest(reqId)
                                            },
                                            onBack = { currentChildNav = AppNavDestination.HOME }
                                        )
                                    }
                                    AppNavDestination.MONEY_STORIES -> {
                                        StoriesScreen(
                                            state = state,
                                            onSelectStory = { storyId -> activeStoryId = storyId },
                                            onBack = { currentChildNav = AppNavDestination.HOME }
                                        )
                                    }
                                    AppNavDestination.REWARDS -> {
                                        RewardsScreen(
                                            state = state,
                                            onClaimMission = { missionId -> repository.claimMission(missionId) },
                                            onBack = { currentChildNav = AppNavDestination.HOME }
                                        )
                                    }
                                    AppNavDestination.TIME_MACHINE -> {
                                        TimeMachineScreen(
                                            state = state,
                                            onBack = { currentChildNav = AppNavDestination.HOME }
                                        )
                                    }
                                    AppNavDestination.MONEY_TIPS -> {
                                        MoneyTipsScreen(
                                            state = state,
                                            onBack = { currentChildNav = AppNavDestination.HOME }
                                        )
                                    }
                                    AppNavDestination.SETTINGS -> {
                                        SettingsScreen(
                                            state = state,
                                            onUpdateProfile = { name, avatar, age ->
                                                repository.updateProfile(name, avatar, age)
                                            },
                                            onResetDemoData = { repository.resetDemoData() },
                                            onBack = { currentChildNav = AppNavDestination.HOME }
                                        )
                                    }
                                }
                            }
                        } else {
                            // Parent Experience
                            when (currentParentNav) {
                                ParentNavDestination.DASHBOARD -> {
                                    ParentDashboardScreen(
                                        state = state,
                                        onNavigate = { dest -> currentParentNav = dest },
                                        onSwitchToChild = { isParentMode = false },
                                        onPayChore = { choreId -> repository.completeAndPayChore(choreId) },
                                        onAddChore = { title, amt -> repository.addChore(title, amt) },
                                        onQuickAllowance = { amt, note -> repository.addPocketMoney(amt, note, byParent = true) },
                                        onReviewRequest = { reqId, approve, note -> repository.parentReviewRequest(reqId, approve, note) }
                                    )
                                }
                                ParentNavDestination.ALLOWANCE -> {
                                    ParentAllowanceScreen(
                                        state = state,
                                        onTransferAllowance = { amt, note ->
                                            repository.addPocketMoney(amt, note, byParent = true)
                                        },
                                        onBack = { currentParentNav = ParentNavDestination.DASHBOARD }
                                    )
                                }
                                ParentNavDestination.APPROVALS -> {
                                    ParentApprovalScreen(
                                        state = state,
                                        onReviewRequest = { reqId, approve, feedback ->
                                            repository.parentReviewRequest(reqId, approve, feedback)
                                        },
                                        onBack = { currentParentNav = ParentNavDestination.DASHBOARD }
                                    )
                                }
                                ParentNavDestination.INSIGHTS -> {
                                    ParentInsightsScreen(
                                        state = state,
                                        onBack = { currentParentNav = ParentNavDestination.DASHBOARD }
                                    )
                                }
                                ParentNavDestination.FAMILY_LEARNING -> {
                                    FamilyLearningScreen(
                                        state = state,
                                        onBack = { currentParentNav = ParentNavDestination.DASHBOARD }
                                    )
                                }
                                ParentNavDestination.SETTINGS -> {
                                    ParentSettingsScreen(
                                        state = state,
                                        onResetDemoData = { repository.resetDemoData() },
                                        onBack = { currentParentNav = ParentNavDestination.DASHBOARD }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Optional Parent PIN Dialog
    if (showParentPinDialog) {
        ParentPinDialog(
            correctPin = state.userProfile.parentPin,
            onSuccess = {
                showParentPinDialog = false
                isParentMode = true
                currentParentNav = ParentNavDestination.DASHBOARD
            },
            onDismiss = { showParentPinDialog = false }
        )
    }
}
