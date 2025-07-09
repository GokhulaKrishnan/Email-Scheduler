package com.spring.emailscheduler.model;

public enum PaymentFrequency {

    WEEKLY("Weekly"),
    BIWEEKLY("Bi-weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    SEMIANNUALLY("Semi-annually"),
    ANNUALLY("Annually"),
    ONE_TIME("One-time");

    private final String displayName;

    PaymentFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDaysToAdd() {
        switch (this) {
            case WEEKLY: return 7;
            case BIWEEKLY: return 14;
            case MONTHLY: return 30;
            case QUARTERLY: return 90;
            case SEMIANNUALLY: return 182;
            case ANNUALLY: return 365;
            case ONE_TIME: return 0;
            default: return 30;
        }
    }
}
