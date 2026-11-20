package part4_debugging.bank;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages different types of bank accounts with transactions.
 * Expected behavior: Handle deposits, withdrawals, and account management.
 */
public class BankAccount {
    private String accountNumber;
    private String ownerName;
    private AccountType accountType;
    private AccountStatus status;
    private double balance;
    private List<Transaction> transactions;
    private int withdrawalCount;
 
    public BankAccount(String accountNumber, String ownerName, AccountType accountType, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.accountType = accountType;
        this.status = AccountStatus.ACTIVE;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        this.withdrawalCount = 0;

        // Record initial deposit
        transactions.add(new Transaction(TransactionType.DEPOSIT, initialDeposit, "Initial deposit"));
    }

    public String getAccountNumber() { return accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public AccountStatus getStatus() { return status; }
    public double getBalance() { return balance; }
    public int getWithdrawalCount() { return withdrawalCount; }

    // Deposit money into account
    public boolean deposit(double amount) {
        if (amount < 0) {  // BUG: Should be <= 0
            System.out.println("Deposit amount must be positive.");
            return false;
        }

        if (status != AccountStatus.ACTIVE) {
            System.out.println("Cannot deposit to non-active account.");
            return false;
        }

        balance += amount;
        transactions.add(new Transaction(TransactionType.DEPOSIT, amount, "Deposit"));
        return true;
    }

    // Withdraw money from account
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return false;
        }

        if (status == AccountStatus.ACTIVE) {  // BUG: Should be != ACTIVE
            System.out.println("Account is not active.");
            return false;
        }

        // Check account type restrictions
        switch (accountType) {
            case SAVINGS:
            case MONEY_MARKET:
                if (withdrawalCount > 6) {  // BUG: Should be >= 6
                    System.out.println("Monthly withdrawal limit reached for this account type.");
                    return false;
                }
                break;
            case CERTIFICATE_OF_DEPOSIT:
                System.out.println("Cannot withdraw from CD account before maturity.");
                return false;
            // BUG: Missing CHECKING case (though it has no restrictions)
        }

        if (balance - amount < 0) {
            System.out.println("Insufficient funds.");
            return false;
        }

        balance -= amount;
        withdrawalCount++;
        transactions.add(new Transaction(TransactionType.WITHDRAWAL, amount, "Withdrawal"));
        return true;
    }

    // Apply monthly fees based on account type
    public void applyMonthlyFee() {
        double fee = 0;

        switch (accountType) {
            case CHECKING:
                fee = balance < 1000 ? 10.0 : 0.0;
                break;
            case SAVINGS:
                fee = balance < 500 ? 5.0 : 0.0;
                break;
            case MONEY_MARKET:
                fee = balance < 2500 ? 15.0 : 0.0;
                // BUG: Missing break statement
            case CERTIFICATE_OF_DEPOSIT:
                fee = 0.0;
                break;
        }

        if (fee > 0) {
            balance -= fee;
            transactions.add(new Transaction(TransactionType.FEE, fee, "Monthly maintenance fee"));
        }

        // Reset monthly withdrawal count
        withdrawalCount = 0;
    }

    // Apply interest based on account type
    public void applyInterest() {
        double interestRate = 0;

        if (accountType == AccountType.SAVINGS) {
            interestRate = 0.02;  // 2% annual
        } else if (accountType == AccountType.MONEY_MARKET) {
            interestRate = 0.03;  // 3% annual
        } else if (accountType == AccountType.CERTIFICATE_OF_DEPOSIT) {
            interestRate = 0.05;  // 5% annual
        }

        double interest = balance * (interestRate / 12);  // Monthly interest
        balance += interest;
        transactions.add(new Transaction(TransactionType.INTEREST, interest, "Monthly interest"));
    }

    // Get transactions of a specific type
    public List<Transaction> getTransactionsByType(TransactionType type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getType() == type) {
                result.add(t);
                transactions.remove(t);  // BUG: Modifying list while iterating
            }
        }
        return result;
    }

    // Calculate total fees paid
    public double getTotalFees() {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.FEE) {
                total += t.getAmount();
            }
        }
        return total;
    }

    // Get account status based on balance and activity
    public String getAccountHealth() {
        if (balance == 0) {  // BUG: Should check < 0 for overdrawn
            return "Overdrawn";
        } else if (balance < 100) {
            return "Low Balance";
        } else if (balance < 1000) {
            return "Fair";
        } else {
            return "Good";
        }
    }
}
