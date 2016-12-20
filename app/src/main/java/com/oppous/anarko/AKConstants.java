package com.oppous.anarko;

public class AKConstants {
    static int timeResend = 180; // unit seconds
    static int heightRedAlert = 100; // unit pixel
    static String countryCode = "1";

    public static final String PUBLISHABLE_KEY = "pk_test_6pRNASCoBOKtIshFeQd4XMUh";

    public static final boolean DEBUG = Boolean.parseBoolean("true");

    public enum Report_Type {
        Sexual(0),
        Violent(1),
        Hateful(2),
        Harmful(3),
        Child_Abuse(4),
        Spam(5),
        Other(6),
        Undefined(7);

        private final int number;

        Report_Type(int number){
            this.number = number;
        }

        public int getNumber(){
            return number;
        }
    }
}
