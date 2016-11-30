package com.ygames.ysoccer.framework;

public enum Month {

    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    private static final String[] labels = {
            "MONTHS.JANUARY",
            "MONTHS.FEBRUARY",
            "MONTHS.MARCH",
            "MONTHS.APRIL",
            "MONTHS.MAY",
            "MONTHS.JUNE",
            "MONTHS.JULY",
            "MONTHS.AUGUST",
            "MONTHS.SEPTEMBER",
            "MONTHS.OCTOBER",
            "MONTHS.NOVEMBER",
            "MONTHS.DECEMBER"
    };

    public static String getLabel(Month month) {
        return labels[month.ordinal()];
    }
}
