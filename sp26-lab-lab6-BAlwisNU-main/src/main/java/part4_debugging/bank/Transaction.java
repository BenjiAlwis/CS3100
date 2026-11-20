package part4_debugging.bank;

import java.util.Date;

public class Transaction {
    private TransactionType type;
    private double amount;
    private String description;
    private Date date;

    public Transaction(TransactionType type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = new Date();
    }

    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public Date getDate() { return date; }
}
