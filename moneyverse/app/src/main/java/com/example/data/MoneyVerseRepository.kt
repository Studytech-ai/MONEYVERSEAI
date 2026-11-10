package com.example.data

import com.example.R
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

data class MoneyVerseState(
    val userProfile: UserProfile = UserProfile(),
    val walletBalance: Double = 380.0,
    val weeklyDelta: Double = 50.0,
    val transactions: List<Transaction> = emptyList(),
    val savingsGoals: List<SavingsGoal> = emptyList(),
    val purchaseRequests: List<PurchaseRequest> = emptyList(),
    val stories: List<Story> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val dailyMissions: List<DailyMission> = emptyList(),
    val dailyTips: List<DailyTip> = emptyList(),
    val chores: List<ParentChore> = emptyList(),
    val dailySpendingCap: Double = 150.0,
    val requireApprovalForWants: Boolean = true,
    val toastMessage: String? = null
)

class MoneyVerseRepository {

    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<MoneyVerseState> = _state.asStateFlow()

    private fun getCurrentTimeDisplay(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return "Today, " + sdf.format(Date())
    }

    fun clearToast() {
        _state.update { it.copy(toastMessage = null) }
    }

    fun showToast(msg: String) {
        _state.update { it.copy(toastMessage = msg) }
    }

    // --- WALLET & EXPENSES ---

    fun addPocketMoney(amount: Double, note: String, byParent: Boolean = false): Boolean {
        if (amount <= 0) {
            showToast("Please enter an amount greater than 0")
            return false
        }
        val title = if (byParent) "Pocket Money from Parent" else "Added Pocket Money"
        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            title = title,
            category = TransactionCategory.POCKET_MONEY,
            amount = amount,
            type = TransactionType.INCOME,
            dateDisplay = getCurrentTimeDisplay(),
            note = note.ifBlank { "Simulated pocket money" }
        )

        _state.update { curr ->
            val newBalance = curr.walletBalance + amount
            val newWeekly = curr.weeklyDelta + amount
            val newTxList = listOf(tx) + curr.transactions
            curr.copy(
                walletBalance = newBalance,
                weeklyDelta = newWeekly,
                transactions = newTxList,
                toastMessage = "Added ₹${amount.toInt()} pocket money!"
            )
        }
        checkAchievements()
        return true
    }

    fun recordExpense(title: String, amount: Double, category: TransactionCategory, note: String): Boolean {
        if (amount <= 0) {
            showToast("Amount must be greater than 0")
            return false
        }
        val currentBalance = _state.value.walletBalance
        if (amount > currentBalance) {
            showToast("Cannot spend ₹${amount.toInt()}! You only have ₹${currentBalance.toInt()} in your wallet.")
            return false
        }

        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            title = title.ifBlank { category.displayName },
            category = category,
            amount = amount,
            type = TransactionType.EXPENSE,
            dateDisplay = getCurrentTimeDisplay(),
            note = note
        )

        _state.update { curr ->
            curr.copy(
                walletBalance = curr.walletBalance - amount,
                transactions = listOf(tx) + curr.transactions,
                toastMessage = "Recorded expense ₹${amount.toInt()} for $title"
            )
        }
        checkAchievements()
        return true
    }

    // --- SAVINGS GOALS ---

    fun createSavingsGoal(title: String, targetAmount: Double, category: String, icon: String): Boolean {
        if (title.isBlank()) {
            showToast("Please enter a goal title")
            return false
        }
        if (targetAmount <= 0) {
            showToast("Target amount must be greater than 0")
            return false
        }

        val newGoal = SavingsGoal(
            id = UUID.randomUUID().toString(),
            title = title,
            targetAmount = targetAmount,
            currentAmount = 0.0,
            daysLeft = 14,
            category = category,
            icon = icon.ifBlank { "🎯" }
        )

        _state.update { curr ->
            curr.copy(
                savingsGoals = curr.savingsGoals + newGoal,
                toastMessage = "Created goal: $title!"
            )
        }
        addPoints(15, "Goal created")
        checkAchievements()
        return true
    }

    fun contributeToGoal(goalId: String, amount: Double): Boolean {
        if (amount <= 0) {
            showToast("Contribution must be greater than 0")
            return false
        }
        val currentBalance = _state.value.walletBalance
        if (amount > currentBalance) {
            showToast("Not enough money in wallet! Balance: ₹${currentBalance.toInt()}")
            return false
        }

        val goal = _state.value.savingsGoals.find { it.id == goalId }
        if (goal == null) {
            showToast("Goal not found")
            return false
        }

        val contribution = GoalContribution(
            id = UUID.randomUUID().toString(),
            amount = amount,
            dateDisplay = getCurrentTimeDisplay(),
            note = "Contributed from wallet"
        )

        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            title = "Saved for ${goal.title}",
            category = TransactionCategory.SAVINGS_GOAL,
            amount = amount,
            type = TransactionType.EXPENSE,
            dateDisplay = getCurrentTimeDisplay(),
            note = "Transferred to goal '${goal.title}'"
        )

        _state.update { curr ->
            val updatedGoals = curr.savingsGoals.map { g ->
                if (g.id == goalId) {
                    val updatedAmount = g.currentAmount + amount
                    g.copy(
                        currentAmount = updatedAmount,
                        contributions = listOf(contribution) + g.contributions
                    )
                } else g
            }
            curr.copy(
                walletBalance = curr.walletBalance - amount,
                savingsGoals = updatedGoals,
                transactions = listOf(tx) + curr.transactions,
                toastMessage = "Saved ₹${amount.toInt()} toward ${goal.title}!"
            )
        }

        addPoints(20, "Savings contribution")
        checkAchievements()
        return true
    }

    // --- PURCHASE REQUESTS ---

    fun createPurchaseRequest(title: String, price: Double, reason: String): Boolean {
        if (title.isBlank()) {
            showToast("Please enter product name")
            return false
        }
        if (price <= 0) {
            showToast("Price must be greater than 0")
            return false
        }
        if (reason.isBlank()) {
            showToast("Please explain why you want this")
            return false
        }

        val req = PurchaseRequest(
            id = UUID.randomUUID().toString(),
            title = title,
            price = price,
            reason = reason,
            status = RequestStatus.PENDING,
            dateDisplay = getCurrentTimeDisplay()
        )

        _state.update { curr ->
            curr.copy(
                purchaseRequests = listOf(req) + curr.purchaseRequests,
                toastMessage = "Purchase request sent to parent!"
            )
        }
        addPoints(10, "Thoughtful purchase request")
        return true
    }

    fun parentReviewRequest(requestId: String, approve: Boolean, feedback: String) {
        _state.update { curr ->
            val updated = curr.purchaseRequests.map { req ->
                if (req.id == requestId && req.status == RequestStatus.PENDING) {
                    req.copy(
                        status = if (approve) RequestStatus.APPROVED else RequestStatus.REJECTED,
                        parentFeedback = feedback
                    )
                } else req
            }
            curr.copy(
                purchaseRequests = updated,
                toastMessage = if (approve) "Request approved!" else "Request declined with feedback"
            )
        }
    }

    fun confirmPurchase(requestId: String): Boolean {
        val req = _state.value.purchaseRequests.find { it.id == requestId } ?: return false
        if (req.status != RequestStatus.APPROVED) {
            showToast("This item is not approved yet")
            return false
        }

        val currentBalance = _state.value.walletBalance
        if (req.price > currentBalance) {
            showToast("Not enough money! You need ₹${req.price.toInt()}, but have ₹${currentBalance.toInt()}")
            return false
        }

        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            title = "Bought ${req.title}",
            category = TransactionCategory.TOYS_GAMES,
            amount = req.price,
            type = TransactionType.EXPENSE,
            dateDisplay = getCurrentTimeDisplay(),
            note = "Approved purchase completed"
        )

        _state.update { curr ->
            val updatedReqs = curr.purchaseRequests.map {
                if (it.id == requestId) it.copy(status = RequestStatus.PURCHASED) else it
            }
            curr.copy(
                walletBalance = curr.walletBalance - req.price,
                purchaseRequests = updatedReqs,
                transactions = listOf(tx) + curr.transactions,
                toastMessage = "Woohoo! Purchased ${req.title}!"
            )
        }
        addPoints(30, "Smart completed purchase")
        checkAchievements()
        return true
    }

    fun cancelPurchaseRequest(requestId: String) {
        _state.update { curr ->
            val updated = curr.purchaseRequests.filterNot { it.id == requestId && it.status == RequestStatus.PENDING }
            curr.copy(
                purchaseRequests = updated,
                toastMessage = "Request cancelled"
            )
        }
    }

    // --- STORIES & QUIZZES ---

    fun completeStory(storyId: String, bonusXp: Int) {
        var alreadyDone = false
        _state.update { curr ->
            val updated = curr.stories.map { s ->
                if (s.id == storyId) {
                    if (s.isCompleted) alreadyDone = true
                    s.copy(isCompleted = true)
                } else s
            }
            curr.copy(stories = updated)
        }

        if (!alreadyDone) {
            addPoints(bonusXp, "Story finished")
            showToast("Story finished! +$bonusXp XP earned! 🌟")
            checkAchievements()
        }
    }

    // --- MISSIONS & ACHIEVEMENTS ---

    fun claimMission(missionId: String) {
        val mission = _state.value.dailyMissions.find { it.id == missionId } ?: return
        if (mission.isClaimed || !mission.isCompleted) return

        _state.update { curr ->
            val updated = curr.dailyMissions.map {
                if (it.id == missionId) it.copy(isClaimed = true) else it
            }
            curr.copy(dailyMissions = updated)
        }
        addPoints(mission.rewardXp, "Mission claimed")
        showToast("Claimed ${mission.rewardXp} XP!")
    }

    private fun addPoints(amount: Int, reason: String) {
        _state.update { curr ->
            val newPoints = curr.userProfile.points + amount
            val newLevel = (newPoints / 100) + 1
            val levelTitle = when {
                newLevel >= 5 -> "Financial Master 🏆"
                newLevel >= 4 -> "Budget Wizard 🧙"
                newLevel >= 3 -> "Smart Saver 💡"
                newLevel >= 2 -> "Money Explorer 🧭"
                else -> "Curious Beginner 🌱"
            }
            curr.copy(
                userProfile = curr.userProfile.copy(
                    points = newPoints,
                    levelNumber = newLevel,
                    levelTitle = levelTitle
                )
            )
        }
    }

    private fun checkAchievements() {
        val curr = _state.value
        val hasSavings = curr.savingsGoals.any { it.currentAmount > 0 }
        val expenseCount = curr.transactions.count { it.type == TransactionType.EXPENSE }
        val hasCompletedGoal = curr.savingsGoals.any { it.isCompleted }
        val completedStories = curr.stories.count { it.isCompleted }
        val totalPoints = curr.userProfile.points

        _state.update { state ->
            val updatedAchievements = state.achievements.map { ach ->
                when (ach.id) {
                    "first_saver" -> if (!ach.isUnlocked && hasSavings) ach.copy(isUnlocked = true, progressCurrent = 1) else ach
                    "smart_spender" -> ach.copy(
                        isUnlocked = ach.isUnlocked || expenseCount >= 3,
                        progressCurrent = expenseCount.coerceAtMost(3)
                    )
                    "goal_getter" -> if (!ach.isUnlocked && hasCompletedGoal) ach.copy(isUnlocked = true, progressCurrent = 1) else ach
                    "money_explorer" -> ach.copy(
                        isUnlocked = ach.isUnlocked || completedStories >= 2,
                        progressCurrent = completedStories.coerceAtMost(2)
                    )
                    "champion" -> ach.copy(
                        isUnlocked = ach.isUnlocked || totalPoints >= 300,
                        progressCurrent = totalPoints.coerceAtMost(300)
                    )
                    else -> ach
                }
            }
            state.copy(achievements = updatedAchievements)
        }
    }

    fun updateProfile(name: String, avatarEmoji: String, ageGroup: String) {
        _state.update { curr ->
            curr.copy(
                userProfile = curr.userProfile.copy(
                    name = name.ifBlank { curr.userProfile.name },
                    avatarEmoji = avatarEmoji,
                    ageGroup = ageGroup
                ),
                toastMessage = "Profile updated!"
            )
        }
    }

    fun addChore(title: String, rewardAmount: Double): Boolean {
        if (title.isBlank() || rewardAmount <= 0) {
            showToast("Please enter chore name and positive reward")
            return false
        }
        val chore = ParentChore(
            id = UUID.randomUUID().toString(),
            title = title,
            rewardAmount = rewardAmount,
            isCompleted = false,
            isPaid = false
        )
        _state.update { curr ->
            curr.copy(chores = listOf(chore) + curr.chores, toastMessage = "Added new chore: $title")
        }
        return true
    }

    fun completeAndPayChore(choreId: String): Boolean {
        val chore = _state.value.chores.find { it.id == choreId } ?: return false
        if (chore.isPaid) return false

        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            title = "Reward for ${chore.title}",
            category = TransactionCategory.POCKET_MONEY,
            amount = chore.rewardAmount,
            type = TransactionType.INCOME,
            dateDisplay = getCurrentTimeDisplay(),
            note = "Chore completed & approved by parent"
        )

        _state.update { curr ->
            val updatedChores = curr.chores.map {
                if (it.id == choreId) it.copy(isCompleted = true, isPaid = true) else it
            }
            curr.copy(
                walletBalance = curr.walletBalance + chore.rewardAmount,
                weeklyDelta = curr.weeklyDelta + chore.rewardAmount,
                chores = updatedChores,
                transactions = listOf(tx) + curr.transactions,
                toastMessage = "Paid ₹${chore.rewardAmount.toInt()} reward to ${curr.userProfile.name}!"
            )
        }
        addPoints(25, "Chore reward")
        return true
    }

    fun updateParentControls(dailyCap: Double, requireApproval: Boolean) {
        _state.update { curr ->
            curr.copy(
                dailySpendingCap = dailyCap,
                requireApprovalForWants = requireApproval,
                toastMessage = "Parent controls updated!"
            )
        }
    }

    fun resetDemoData() {
        _state.value = createInitialState()
        showToast("Demo data successfully reset!")
    }

    // --- INITIAL SEED DATA ---

    private fun createInitialState(): MoneyVerseState {
        val initialTransactions = listOf(
            Transaction(
                id = "tx-1",
                title = "Pocket Money from Parent",
                category = TransactionCategory.POCKET_MONEY,
                amount = 500.0,
                type = TransactionType.INCOME,
                dateDisplay = "Today, 9:00 AM",
                note = "Weekly allowance bonus"
            ),
            Transaction(
                id = "tx-2",
                title = "Ice Cream",
                category = TransactionCategory.FOOD_TREATS,
                amount = 50.0,
                type = TransactionType.EXPENSE,
                dateDisplay = "Yesterday, 6:20 PM",
                note = "Chocolate swirl cone"
            ),
            Transaction(
                id = "tx-3",
                title = "Toy",
                category = TransactionCategory.TOYS_GAMES,
                amount = 100.0,
                type = TransactionType.EXPENSE,
                dateDisplay = "2 days ago",
                note = "Mini building blocks"
            )
        )

        val cycleGoal = SavingsGoal(
            id = "goal-cycle",
            title = "Cycle",
            targetAmount = 500.0,
            currentAmount = 320.0,
            daysLeft = 2,
            category = "Outdoor & Sports",
            icon = "🚲",
            contributions = listOf(
                GoalContribution("c1", 100.0, "5 days ago", "Birthday gift saving"),
                GoalContribution("c2", 120.0, "3 days ago", "Chores reward"),
                GoalContribution("c3", 100.0, "Yesterday", "Saved pocket money")
            )
        )

        val headphonesGoal = SavingsGoal(
            id = "goal-headphones",
            title = "Gaming Headphones",
            targetAmount = 800.0,
            currentAmount = 250.0,
            daysLeft = 18,
            category = "Electronics",
            icon = "🎧",
            contributions = listOf(
                GoalContribution("c4", 250.0, "Last week", "Initial deposit")
            )
        )

        val initialRequests = listOf(
            PurchaseRequest(
                id = "req-1",
                title = "Watercolor Art Set",
                price = 280.0,
                reason = "For my upcoming school art fair poster",
                status = RequestStatus.PENDING,
                dateDisplay = "Today, 11:30 AM"
            ),
            PurchaseRequest(
                id = "req-2",
                title = "Adventure Comic Book Vol 3",
                price = 150.0,
                reason = "Finished reading Vol 1 & 2 during summer camp",
                status = RequestStatus.APPROVED,
                dateDisplay = "Yesterday",
                parentFeedback = "Great job finishing your reading goals! You can buy it when ready."
            )
        )

        val initialStories = listOf(
            Story(
                id = "story-wise-saver",
                title = "The Story of The Wise Saver",
                subtitle = "Learn how Riya saved for her dream trip!",
                coverDrawableRes = R.drawable.story_wise_saver_art,
                badge = "Recommended",
                chapters = listOf(
                    StoryChapter(
                        chapterNumber = 1,
                        title = "The Island Brochure",
                        storyText = "Ten-year-old Riya found a colorful postcard showing palm trees, sparkling turquoise sea, and baby turtles hatching on the sand. 'I want to visit Turtle Island on our family vacation!' she smiled excitedly.\n\nHer father smiled gently: 'Turtle Island tickets cost extra pocket coins. If you set a goal, you can save enough by winter!'"
                    ),
                    StoryChapter(
                        chapterNumber = 2,
                        title = "The Temptation at the Sweet Shop",
                        storyText = "On Wednesday, Riya passed Uncle Bheema's sweet cart. Fresh golden jalebis and crunchy candies sparkled. Her friend Samir yelled: 'Riya, let's spend all our coins on extra candies!'\n\nRiya paused, holding her ₹50 note. She remembered Turtle Island.",
                        choices = listOf(
                            StoryChoice(
                                text = "Buy a small ₹10 treat, save ₹40 for Turtle Island",
                                feedback = "Smart balance! You enjoyed a treat while protecting your big dream.",
                                coinsSavedBonus = 40
                            ),
                            StoryChoice(
                                text = "Spend all ₹50 on candies today",
                                feedback = "The candy lasted 5 minutes, but the island trip got delayed. Every rupee counts!",
                                coinsSavedBonus = 0
                            )
                        )
                    ),
                    StoryChapter(
                        chapterNumber = 3,
                        title = "The Piggy Bank Milestone",
                        storyText = "Week after week, Riya tracked every saved coin in her notebook. She washed bicycles, helped sort library books, and resisted impulsive toys.\n\nWhen winter arrived, Riya unlocked her piggy bank with a bright clinking sound: ₹500 saved! She booked the ferry ticket to Turtle Island with pride."
                    )
                ),
                quiz = QuizQuestion(
                    question = "What helped Riya reach her dream of visiting Turtle Island?",
                    options = listOf(
                        "Spending all her pocket money on candies",
                        "Setting a clear goal and making conscious savings choices",
                        "Waiting for someone else to pay for everything",
                        "Forgetting to count her coins"
                    ),
                    correctIndex = 1,
                    explanation = "Setting a clear goal and resisting small instant temptations helps achieve big dream milestones!"
                ),
                xpReward = 50,
                isCompleted = false
            ),
            Story(
                id = "story-needs-wants",
                title = "The Mystery of Needs and Wants",
                subtitle = "Detective Benny solves the case of the vanished allowance!",
                coverDrawableRes = R.drawable.story_needs_wants,
                badge = "Detective Case",
                chapters = listOf(
                    StoryChapter(
                        chapterNumber = 1,
                        title = "The Mystery Begins",
                        storyText = "Detective Benny put on his detective cap and held up his magnifying glass. 'Every week, children ask: Where did all my coins vanish?' Benny chuckled. 'The culprit is called Impulsive Want disguised as a Need!'"
                    ),
                    StoryChapter(
                        chapterNumber = 2,
                        title = "The Two Baskets",
                        storyText = "Benny showed two baskets. One was labeled 'NEEDS: Things required to live safely and learn' (like lunch, warm jacket, notebook). The other was labeled 'WANTS: Things fun to have, but okay to wait for' (like fancy glowing shoes, extra video game skins).",
                        choices = listOf(
                            StoryChoice(
                                text = "Classify winter shoes as NEED, video game skin as WANT",
                                feedback = "Brilliant detective work! Needs always take priority over wants.",
                                coinsSavedBonus = 25
                            ),
                            StoryChoice(
                                text = "Classify everything as a NEED",
                                feedback = "If everything is a need, you'll run out of money when real needs appear!",
                                coinsSavedBonus = 0
                            )
                        )
                    )
                ),
                quiz = QuizQuestion(
                    question = "Which of the following is a genuine NEED?",
                    options = listOf(
                        "The newest shiny gaming skin",
                        "Healthy nutritious lunch for school",
                        "A fourth pair of trendy sunglasses",
                        "A pack of extra trading cards"
                    ),
                    correctIndex = 1,
                    explanation = "Food, shelter, basic clothing, and education are essential Needs!"
                ),
                xpReward = 50,
                isCompleted = false
            ),
            Story(
                id = "story-magic-budget",
                title = "The Magical Budget",
                subtitle = "Master Leo and the Three Enchanted Jars",
                coverDrawableRes = R.drawable.story_magic_budget,
                badge = "Magic Tale",
                chapters = listOf(
                    StoryChapter(
                        chapterNumber = 1,
                        title = "The Wizard's Gift",
                        storyText = "In the town of Coinshire, an old wizard presented young Leo with three enchanted glowing glass jars: SAVE, SPEND, and GIVE.\n\n'Divide your pocket money wisely,' the wizard whispered. 'The jars will make your wealth flourish!'"
                    ),
                    StoryChapter(
                        chapterNumber = 2,
                        title = "The 50-30-20 Enchantment",
                        storyText = "Leo learned the magic rule: Put 50% for daily essentials and fun (Spend), 30% for your dream future (Save), and 20% for helping friends and charity (Give).",
                        choices = listOf(
                            StoryChoice(
                                text = "Follow the 50-30-20 enchanted rule",
                                feedback = "The jars glowed bright emerald and gold! Your future is secure.",
                                coinsSavedBonus = 30
                            ),
                            StoryChoice(
                                text = "Put 100% in the Spend jar immediately",
                                feedback = "The Spend jar overflowed, but the Save jar was empty when emergencies came.",
                                coinsSavedBonus = 0
                            )
                        )
                    )
                ),
                quiz = QuizQuestion(
                    question = "Why is it wise to divide money into multiple jars or categories?",
                    options = listOf(
                        "Because jars look pretty on a shelf",
                        "It ensures you save for goals and give back while still having fun",
                        "It makes money vanish into thin air",
                        "You never have to count coins again"
                    ),
                    correctIndex = 1,
                    explanation = "Budgeting with categories ensures all your important priorities are cared for!"
                ),
                xpReward = 50,
                isCompleted = false
            ),
            Story(
                id = "story-missing-coins",
                title = "The Adventure of the Missing Coins",
                subtitle = "Mia discovers the invisible leaks in her daily pocket money!",
                coverDrawableRes = R.drawable.story_magic_budget,
                badge = "Adventure",
                chapters = listOf(
                    StoryChapter(
                        chapterNumber = 1,
                        title = "The Tiny Leaks",
                        storyText = "Mia wondered why she never had enough to buy her favorite science microscope. 'I only spend ₹10 on stickers and ₹15 on chips every single day!' she protested.\n\nHer older brother calculated on a board: '₹25 every day equals ₹750 a month! That is the cost of your microscope in just six weeks!'"
                    )
                ),
                quiz = QuizQuestion(
                    question = "What does the 'microscope lesson' teach us about small daily spending?",
                    options = listOf(
                        "Small purchases don't add up to anything",
                        "Small daily leaks quickly add up to huge amounts over weeks and months",
                        "You should never buy anything ever",
                        "Math is not useful for money"
                    ),
                    correctIndex = 1,
                    explanation = "Even small purchases of ₹10 or ₹20 add up to hundreds of rupees over time!"
                ),
                xpReward = 50,
                isCompleted = false
            ),
            Story(
                id = "story-dream-bicycle",
                title = "The Dream Bicycle",
                subtitle = "Arjun races toward his bright blue two-wheeler goal!",
                coverDrawableRes = R.drawable.cycle_goal_art,
                badge = "Real Journey",
                chapters = listOf(
                    StoryChapter(
                        chapterNumber = 1,
                        title = "The Blue Speedster",
                        storyText = "Arjun saw a shiny sky-blue bicycle with a ringing bell in the shop window. The price was ₹500. Arjun's father offered: 'For every chore you complete and every test you prepare well for, I will match your savings!'\n\nArjun put on his helmet in imagination and started his countdown."
                    )
                ),
                quiz = QuizQuestion(
                    question = "What is a 'savings match'?",
                    options = listOf(
                        "A competition where you lose all money",
                        "When someone agrees to add extra money for every rupee you save",
                        "A toy game with wooden sticks",
                        "Spending money twice as fast"
                    ),
                    correctIndex = 1,
                    explanation = "Savings matching rewards your hard work by multiplying your savings!"
                ),
                xpReward = 50,
                isCompleted = false
            )
        )

        val initialAchievements = listOf(
            Achievement("first_saver", "First Saver", "Deposit your first contribution into a goal", "🌱", 50, true, 1, 1),
            Achievement("smart_spender", "Smart Spender", "Track 3 expenses without exceeding wallet balance", "💡", 75, true, 2, 3),
            Achievement("goal_getter", "Goal Getter", "Reach 100% on any savings goal", "🎯", 100, false, 0, 1),
            Achievement("budget_master", "Budget Master", "Run an educational simulation in Money Time Machine", "🔮", 60, false, 0, 1),
            Achievement("money_explorer", "Money Explorer", "Read 2 illustrated financial stories", "📚", 80, false, 0, 2),
            Achievement("champion", "Financial Champion", "Accumulate over 300 MoneyVerse XP", "👑", 150, false, 250, 300)
        )

        val initialMissions = listOf(
            DailyMission("m1", "Check your wallet balance & weekly summary", 20, isCompleted = true, isClaimed = false),
            DailyMission("m2", "Complete a chapter in Money Storybook", 30, isCompleted = false, isClaimed = false),
            DailyMission("m3", "Add a savings contribution to your Cycle goal", 25, isCompleted = false, isClaimed = false)
        )

        val initialTips = listOf(
            DailyTip(
                id = "t1",
                title = "The 24-Hour Rule for Big Wants",
                category = "Smart Spending",
                summary = "When you feel like buying a toy or video game, wait 24 hours. If you still really need it tomorrow, talk with your parent. 70% of the time, the urge passes!",
                actionText = "Try it on your next toy urge",
                tag = "Pro Tip ⏱️"
            ),
            DailyTip(
                id = "t2",
                title = "Pay Yourself First",
                category = "Saving",
                summary = "Whenever you receive pocket money or a gift, immediately put a slice into your savings goal BEFORE buying treats. What you don't see, you won't casually spend!",
                actionText = "Save first, spend what remains",
                tag = "Golden Rule 🪙"
            ),
            DailyTip(
                id = "t3",
                title = "Needs vs. Wants Radar",
                category = "Decision Making",
                summary = "A Need is something you cannot live or study without (like lunch or school notebook). A Want is something fun that you can delay (like candy or extra stickers).",
                actionText = "Check your cart before checkout",
                tag = "Clarity 🎯"
            ),
            DailyTip(
                id = "t4",
                title = "The Magic of Compounding",
                category = "Future Wealth",
                summary = "Coins saved together earn interest or rewards over time. Just like a tiny snowball rolling down a snowy hill gets bigger and bigger, small habits create big results!",
                actionText = "Start small today",
                tag = "Snowball ❄️"
            )
        )

        val initialChores = listOf(
            ParentChore("chore-1", "Clean bedroom & organize study table", 50.0, isCompleted = true, isPaid = false, "Completed today"),
            ParentChore("chore-2", "Score 90%+ in Weekly Math Quiz", 100.0, isCompleted = false, isPaid = false, "Due Friday"),
            ParentChore("chore-3", "Help wash family dinner dishes", 30.0, isCompleted = false, isPaid = false, "Daily evening chore")
        )

        return MoneyVerseState(
            userProfile = UserProfile(),
            walletBalance = 380.0,
            weeklyDelta = 50.0,
            transactions = initialTransactions,
            savingsGoals = listOf(cycleGoal, headphonesGoal),
            purchaseRequests = initialRequests,
            stories = initialStories,
            achievements = initialAchievements,
            dailyMissions = initialMissions,
            dailyTips = initialTips,
            chores = initialChores,
            dailySpendingCap = 150.0,
            requireApprovalForWants = true
        )
    }
}
