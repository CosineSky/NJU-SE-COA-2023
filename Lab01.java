package COA2023;

import COA2023.Lab07.test.MemTestHelper;

import java.util.Objects;

public class Lab01 {
    private static String inverse(String s) {
        return s.replace('0', '*').replace('1', '0').replace('*', '1');
    }

    private static String adjust(String s, int length) {
        StringBuilder sb = new StringBuilder(s);
        while ( sb.length() < length ) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    public static String intToBinary(String numStr) {
        boolean neg = numStr.charAt(0) == '-';
        int number = Integer.parseInt(numStr);
        number = neg ? - (number + 1) : number;
        StringBuilder result = new StringBuilder();
        while ( number > 0 ) {
            result.insert(0, (number % 2));
            number /= 2;
        }
        while ( result.length() < 32 ) {
            result.insert(0, '0');
        }
        return neg ? inverse(result.toString()) : result.toString();
    }

    public static String binaryToInt(String binStr) {
        int number = 0;
        boolean neg = binStr.charAt(0) == '1';
        binStr = neg ? inverse(binStr) : binStr;
        for ( int i = binStr.length() - 1, j = 0; i >= 0; i--, j++ ) {
            number += Math.pow(2, j) * (binStr.charAt(i) == '1' ? 1 : 0);
        }
        return "" + (neg ? - number - 1 : number);
    }

    public static String decimalToNBCD(String decimalStr) {
        return null;
    }

    public static String NBCDToDecimal(String NBCDStr) {
        return null;
    }

    public static String binaryToFloat(String binStr) {
        boolean neg = binStr.charAt(0) == '1';
        String exp = binStr.substring(1, 9);
        String tail = binStr.substring(9);
        double result = 0;
        if ( exp.equals("11111111") ) {
            return tail.equals("00000000000000000000000") ? (!neg ? "+Inf" : "-Inf") : "NaN";
        }
        else {
            int exp_ = Integer.parseInt(binaryToInt(adjust(exp, 32)));
            if ( exp_ == 0 ) {
                result = Math.pow(2, -126 - 23) * Integer.parseInt(binaryToInt(tail));
            }
            else {
                result = Math.pow(2, -127 - 23 + exp_) * Integer.parseInt(binaryToInt("01" + tail));
            }
        }
        result = neg ? - result : result;
        return String.valueOf(result);
    }

    public static String floatToBinary(String floatStr) {
        return null;
    }

    public static void main(String[] args) {
        System.out.println(intToBinary("1"));
        System.out.println(binaryToInt("11111111111111111111111111111110"));
        System.out.println(binaryToFloat("00111111101100000000000000000000"));
        System.out.println(floatToBinary("0.75"));
//        System.out.println(floatToBinary("0.01"));


    }
}