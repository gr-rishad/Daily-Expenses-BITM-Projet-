package com.example.asus.dailyexpensenote.model_class;

public class Expense {

    private String id;
    private String expenseType;
    private String expenseAmount;
    private String expenseDate;
    private String expenseTime;
    private String expenseImage;

    public Expense(String id, String expenseType, String expenseAmount, String expenseDate, String expenseTime, String expenseImage) {
        this.id = id;
        this.expenseType = expenseType;
        this.expenseAmount = expenseAmount;
        this.expenseDate = expenseDate;
        this.expenseTime = expenseTime;
        this.expenseImage = expenseImage;
    }

    public String getId() {
        return id;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public String getExpenseTime() {
        return expenseTime;
    }

    public String getExpenseImage() {
        return expenseImage;
    }
}
