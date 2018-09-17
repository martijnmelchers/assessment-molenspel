package com.avans.molenspel;

import java.util.Arrays;

/* This class shows errors in the console */
public class ExceptionHandler {
    static void handleException(Exception e) {
        System.out.println("***" + e.getMessage() + "***");
    }
}
