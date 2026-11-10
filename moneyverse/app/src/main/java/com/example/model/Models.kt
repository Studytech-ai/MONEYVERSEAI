package com.example.model

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class TransactionCategory(val displayName: String, val icon: String) {
    POCKET_MONEY("Pocket Money", "💰"),
    SAVINGS_GOAL("Savings Goal", "🎯"),
    FOOD_TREATS("Food & Treats", "🍦"),
    TOYS_GAMES("Toys & Games", "🎮"),
    BOOKS_LEARNING("Books & Learning", "📚"),
    GIFTS_REWARD("Gifts & Rewards", "🎁"),
    OTHER("Other", "✨")
}

data class Transaction(
    val id: String,
    val title: String,
    val category: TransactionCategory,
    val amount: Double,
    val type: TransactionType,
    val dateDisplay: String,
    val note: String = ""
)

data class GoalContribution(
    val id: String,
    val amount: Double,
    val dateDisplay: String,
    val note: String = ""
)

data class SavingsGoal(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val daysLeft: Int,
    val category: String,
    val icon: String = "🚲",
    val contributions: List<GoalContribution> = emptyList()
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    val isCompleted: Boolean
        get() = currentAmount >= targetAmount
}

enum class RequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    PURCHASED
}

data class PurchaseRequest(
    val id: String,
    val title: String,
    val price: Double,
    val reason: String,
    val status: RequestStatus = RequestStatus.PENDING,
    val dateDisplay: String,
    val parentFeedback: String = ""
)

data class StoryChoice(
    val text: String,
    val feedback: String,
    val coinsSavedBonus: Int = 0
)

data class StoryChapter(
    val chapterNumber: Int,
    val title: String,
    val storyText: String,
    val choices: List<StoryChoice> = emptyList()
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class Story(
    val id: String,
    val title: String,
    val subtitle: String,
    val coverDrawableRes: Int?,
    val badge: String,
    val chapters: List<StoryChapter>,
    val quiz: QuizQuestion,
    val xpReward: Int = 50,
    val isCompleted: Boolean = false
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val xpReward: Int,
    val isUnlocked: Boolean,
    val progressCurrent: Int,
    val progressTarget: Int
)

data class DailyMission(
    val id: String,
    val title: String,
    val rewardXp: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean
)

data class UserProfile(
    val name: String = "Kid A",
    val avatarEmoji: String = "👦",
    val ageGroup: String = "10-12 yrs",
    val points: Int = 250,
    val levelTitle: String = "Smart Saver",
    val levelNumber: Int = 3,
    val streakDays: Int = 4,
    val isParentMode: Boolean = false,
    val parentPin: String = "1234"
)

data class DailyTip(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val actionText: String,
    val tag: String
)

data class ParentChore(
    val id: String,
    val title: String,
    val rewardAmount: Double,
    val isCompleted: Boolean = false,
    val isPaid: Boolean = false,
    val dueDateDisplay: String = "Due this weekend"
)
