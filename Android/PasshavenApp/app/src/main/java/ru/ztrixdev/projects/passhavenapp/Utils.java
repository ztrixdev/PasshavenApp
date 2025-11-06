package ru.ztrixdev.projects.passhavenapp;

public class Utils {
    public static Boolean IntegerToBoolean(Integer val) {
        return val != 0;
    }

    public static Integer BooleanToInteger(Boolean val) {
        if (val == true) return 1;
        else return 0;
    }
}
