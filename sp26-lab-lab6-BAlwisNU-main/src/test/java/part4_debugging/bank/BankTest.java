package part4_debugging.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for BankAccount.
 * Run these tests to verify your bug fixes work correctly.
 * 
 * To run these tests with Gradle:
 *   ./gradlew test --tests "part4_debugging.bank.BankTest"
 * 
 * Or run all Part 4 tests:
 *   ./gradlew test --tests "part4_debugging.*"
 */
class BankTest {
    private BankAccount checking;
    private BankAccount savings;
    private BankAccount moneyMarket;

    @BeforeEach
    void setUp() {
        checking = new BankAccount("CHK001", "John Doe", AccountType.CHECKING, 1500.0);
        savings = new BankAccount("SAV001", "Jane Smith", AccountType.SAVINGS, 5000.0);
        moneyMarket = new BankAccount("MM001", "Bob Wilson", AccountType.MONEY_MARKET, 3000.0);
    }

    // ==================== Deposit Tests ====================

    @Test
    @DisplayName("Deposit: Valid positive amount should succeed")
    void testDeposit_ValidAmount_ShouldSucceed() {
        assertTrue(checking.deposit(500.0), "Valid deposit should succeed");
        assertEquals(2000.0, checking.getBalance(), 0.01);
    }

    @Test
    @DisplayName("Deposit: Zero amount should fail")
    void testDeposit_ZeroAmount_ShouldFail() {
        assertFalse(checking.deposit(0.0), "Zero deposit should fail");
        assertEquals(1500.0, checking.getBalance(), 0.01, "Balance should remain unchanged");
    }

    @Test
    @DisplayName("Deposit: Negative amount should fail")
    void testDeposit_NegativeAmount_ShouldFail() {
        assertFalse(checking.deposit(-100.0), "Negative deposit should fail");
    }

    // ==================== Withdrawal Tests ====================

    @Test
    @DisplayName("Withdraw: Valid amount from checking should succeed")
    void testWithdraw_ValidAmount_ShouldSucceed() {
        assertTrue(checking.withdraw(100.0), "Valid withdrawal should succeed");
        assertEquals(1400.0, checking.getBalance(), 0.01);
    }

    @Test
    @DisplayName("Withdraw: Insufficient funds should fail")
    void testWithdraw_InsufficientFunds_ShouldFail() {
        assertFalse(checking.withdraw(2000.0), "Withdrawal exceeding balance should fail");
    }

    @Test
    @DisplayName("Withdraw: Savings should enforce 6 withdrawal limit")
    void testWithdraw_SavingsLimit_ShouldEnforceAfterSix() {
        // First 6 withdrawals should succeed
        for (int i = 0; i < 6; i++) {
            assertTrue(savings.withdraw(50.0), "Withdrawal " + (i + 1) + " should succeed");
        }
        // 7th withdrawal should fail
        assertFalse(savings.withdraw(50.0), "7th withdrawal should fail due to limit");
    }

    @Test
    @DisplayName("Withdraw: Money market should enforce 6 withdrawal limit")
    void testWithdraw_MoneyMarketLimit_ShouldEnforceAfterSix() {
        // First 6 withdrawals should succeed
        for (int i = 0; i < 6; i++) {
            assertTrue(moneyMarket.withdraw(50.0), "Withdrawal " + (i + 1) + " should succeed");
        }
        // 7th withdrawal should fail
        assertFalse(moneyMarket.withdraw(50.0), "7th withdrawal should fail due to limit");
    }

    // ==================== Monthly Fee Tests ====================

    @Test
    @DisplayName("Monthly fee: Money market should apply correct fee (not CD fee)")
    void testApplyMonthlyFee_MoneyMarket_ShouldNotFallThrough() {
        // Money market with balance >= 2500 should have no fee
        moneyMarket.applyMonthlyFee();
        assertEquals(3000.0, moneyMarket.getBalance(), 0.01, 
            "Money market with high balance should have no fee");
    }

    @Test
    @DisplayName("Monthly fee: Low balance checking should apply $10 fee")
    void testApplyMonthlyFee_LowBalanceChecking_ShouldApplyFee() {
        BankAccount lowChecking = new BankAccount("CHK002", "Test", AccountType.CHECKING, 500.0);
        lowChecking.applyMonthlyFee();
        assertEquals(490.0, lowChecking.getBalance(), 0.01);
    }

    // ==================== Transaction Tests ====================

    @Test
    @DisplayName("Get transactions by type should not modify original list")
    void testGetTransactionsByType_ShouldNotModifyList() {
        checking.deposit(100.0);
        checking.deposit(200.0);
        
        // Get deposits - this should NOT remove them from the account
        var deposits = checking.getTransactionsByType(TransactionType.DEPOSIT);
        
        // Should have 3 deposits (initial + 2 more)
        assertEquals(3, deposits.size(), "Should find 3 deposit transactions");
        
        // Getting deposits again should return same count (not modified)
        var depositsAgain = checking.getTransactionsByType(TransactionType.DEPOSIT);
        assertEquals(3, depositsAgain.size(), "Second call should also find 3 deposits");
    }

    // ==================== Account Health Tests ====================

    @Test
    @DisplayName("Account health: Negative balance should be 'Overdrawn'")
    void testGetAccountHealth_NegativeBalance_ShouldBeOverdrawn() {
        // Create account and force negative balance through fees
        BankAccount lowAccount = new BankAccount("CHK003", "Test", AccountType.CHECKING, 5.0);
        lowAccount.applyMonthlyFee(); // Will apply $10 fee, making balance -5
        
        assertEquals("Overdrawn", lowAccount.getAccountHealth(), 
            "Negative balance should return 'Overdrawn'");
    }

    @Test
    @DisplayName("Account health: Zero balance should be 'Low Balance' not 'Overdrawn'")
    void testGetAccountHealth_ZeroBalance_ShouldBeLowBalance() {
        BankAccount zeroAccount = new BankAccount("CHK004", "Test", AccountType.CHECKING, 10.0);
        zeroAccount.applyMonthlyFee(); // Will apply $10 fee, making balance 0
        
        assertEquals("Low Balance", zeroAccount.getAccountHealth(), 
            "Zero balance should return 'Low Balance', not 'Overdrawn'");
    }

    @Test
    @DisplayName("Account health: Good balance should return 'Good'")
    void testGetAccountHealth_GoodBalance_ShouldBeGood() {
        assertEquals("Good", checking.getAccountHealth(), 
            "Balance of 1500 should return 'Good'");
    }
}
