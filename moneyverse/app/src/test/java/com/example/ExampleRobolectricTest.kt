package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MoneyVerseRepository
import com.example.model.RequestStatus
import com.example.model.TransactionCategory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var repository: MoneyVerseRepository

    @Before
    fun setup() {
        repository = MoneyVerseRepository()
    }

    @Test
    fun `read string from context verifies app_name is MoneyVerse`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MoneyVerse", appName)
    }

    @Test
    fun `initial state matches MoneyVerse reference dashboard`() {
        val state = repository.state.value
        assertEquals("Kid A", state.userProfile.name)
        assertEquals(380.0, state.walletBalance, 0.01)
        assertEquals(50.0, state.weeklyDelta, 0.01)
        assertEquals(250, state.userProfile.points)

        val cycleGoal = state.savingsGoals.find { it.title == "Cycle" }
        assertNotNull(cycleGoal)
        assertEquals(500.0, cycleGoal!!.targetAmount, 0.01)
        assertEquals(320.0, cycleGoal.currentAmount, 0.01)
        assertEquals(0.64f, cycleGoal.progress, 0.01f)
    }

    @Test
    fun `add pocket money updates wallet balance and creates transaction`() {
        val initialBalance = repository.state.value.walletBalance
        val success = repository.addPocketMoney(100.0, "Test Allowance", byParent = true)
        assertTrue(success)

        val newState = repository.state.value
        assertEquals(initialBalance + 100.0, newState.walletBalance, 0.01)
        assertEquals("Pocket Money from Parent", newState.transactions.first().title)
    }

    @Test
    fun `prevent overspending beyond wallet balance`() {
        val currentBalance = repository.state.value.walletBalance
        val overspendAmount = currentBalance + 500.0
        val success = repository.recordExpense("Expensive Toy", overspendAmount, TransactionCategory.TOYS_GAMES, "Too expensive")
        assertFalse("Overspending should not be allowed", success)
        assertEquals(currentBalance, repository.state.value.walletBalance, 0.01)
    }

    @Test
    fun `goal contribution updates both goal and wallet atomically`() {
        val initialBalance = repository.state.value.walletBalance
        val goalId = "goal-cycle"
        val success = repository.contributeToGoal(goalId, 50.0)
        assertTrue(success)

        val newState = repository.state.value
        assertEquals(initialBalance - 50.0, newState.walletBalance, 0.01)

        val cycle = newState.savingsGoals.find { it.id == goalId }!!
        assertEquals(370.0, cycle.currentAmount, 0.01)
    }

    @Test
    fun `purchase request approval and simulation lifecycle`() {
        // 1. Child submits request
        val created = repository.createPurchaseRequest("Chess Set", 200.0, "For chess club training")
        assertTrue(created)
        val req = repository.state.value.purchaseRequests.first { it.title == "Chess Set" }
        assertEquals(RequestStatus.PENDING, req.status)

        // 2. Parent approves request with feedback
        repository.parentReviewRequest(req.id, approve = true, feedback = "Proud of your chess enthusiasm!")
        val approvedReq = repository.state.value.purchaseRequests.first { it.id == req.id }
        assertEquals(RequestStatus.APPROVED, approvedReq.status)

        // 3. Child explicitly confirms purchase
        val balanceBefore = repository.state.value.walletBalance
        val bought = repository.confirmPurchase(req.id)
        assertTrue(bought)
        val purchasedReq = repository.state.value.purchaseRequests.first { it.id == req.id }
        assertEquals(RequestStatus.PURCHASED, purchasedReq.status)
        assertEquals(balanceBefore - 200.0, repository.state.value.walletBalance, 0.01)
    }

    @Test
    fun `story completion awards XP and marks completed`() {
        val initialPoints = repository.state.value.userProfile.points
        repository.completeStory("story-wise-saver", 50)

        val state = repository.state.value
        val story = state.stories.find { it.id == "story-wise-saver" }!!
        assertTrue(story.isCompleted)
        assertEquals(initialPoints + 50, state.userProfile.points)
    }

    @Test
    fun `parent chore completion and reward payout credits wallet`() {
        val initialBalance = repository.state.value.walletBalance
        val chore = repository.state.value.chores.first()
        val success = repository.completeAndPayChore(chore.id)
        assertTrue(success)

        val newState = repository.state.value
        assertEquals(initialBalance + chore.rewardAmount, newState.walletBalance, 0.01)
        assertTrue(newState.chores.first { it.id == chore.id }.isPaid)
    }

    @Test
    fun `reset demo data restores original state`() {
        repository.addPocketMoney(1000.0, "Giant amount")
        assertEquals(1380.0, repository.state.value.walletBalance, 0.01)

        repository.resetDemoData()
        assertEquals(380.0, repository.state.value.walletBalance, 0.01)
    }
}
