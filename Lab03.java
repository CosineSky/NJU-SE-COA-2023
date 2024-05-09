package COA2023;

import COA2023.Lab08.util.Transformer;

import java.util.Collections;

public class Lab03 {
    public static String ZERO = "00000000000000000000000000000000";
    public static String ONE = "00000000000000000000000000000001";
    public static String TWO = "00000000000000000000000000000010";
    public static DataType remainderReg = new DataType(ZERO);

    public static char[] addBit(char a, char b, char c) {
        return new char[]{
                ((a - '0') ^ (b - '0') ^ (c - '0')) == 1 ? '1' : '0',
                (a - '0') + (b - '0') + (c - '0') > 1 ? '1' : '0'
        };
    }

    public static DataType add(DataType src, DataType dest) {
        String s1 = src.toString(), s2 = dest.toString();
        char[] ans = new char[32];
        char cin = '0';
        for ( int i = s1.length() - 1; i >= 0; i-- ) {
            ans[i] = addBit(s1.charAt(i), s2.charAt(i), cin)[0];
            cin = addBit(s1.charAt(i), s2.charAt(i), cin)[1];
        }
        return new DataType(new String(ans));
    }

    public static DataType sub(DataType src, DataType dest) {
        return add(add(dest, new DataType(src.toString().replace('1', '*').replace('0', '1').replace('*', '0'))), new DataType(ONE));
    }

    public static DataType mul(DataType src, DataType dest) {
        DataType ret = new DataType(ZERO);
        String number2 = dest.toString() + "0";
        for ( int i = number2.length() - 1; i >= 1; i-- ) {
            ret = number2.charAt(i) > number2.charAt(i - 1) ? add(src, ret) :
                    (number2.charAt(i) < number2.charAt(i - 1) ? sub(src, ret) : ret);
            src = add(src, src);
        }
        return ret;
    }

//    public static DataType div(DataType src, DataType dest) {
//        DataType ZERO_ = new DataType(ZERO);
//        DataType ONE_ = new DataType(ONE);
//        DataType TWO_ = new DataType(TWO);
//
//        if ( src.toString().equals(ZERO) && !dest.toString().equals(ZERO) ) {
//            throw new ArithmeticException("L");
//        }
//
//        // init.
//        boolean remainderNeg = dest.toString().charAt(0) == '1',
//                quotientNeg = dest.toString().charAt(0) == '1' ^ src.toString().charAt(0) == '1';
//        DataType remainder = new DataType(ZERO), quotient = new DataType(ZERO);
//        src = src.toString().charAt(0) == '1' ? sub(src, ZERO_) : src;
//        dest = dest.toString().charAt(0) == '1' ? sub(dest, ZERO_) : dest;
//
//        // calc.
//        for ( int i = 0; i < src.toString().length(); i++ ) {
//            remainder = dest.toString().charAt(i) == '0' ? mul(remainder, TWO_) :
//                    add(ONE_, mul(remainder, TWO_));
//            if ( sub(src, remainder).toString().charAt(0) == '0' ) {
//                quotient = add(quotient, ONE_);
//                remainder = sub(src, remainder);
//            }
//            quotient = i != src.toString().length() - 1 ? mul(quotient, TWO_) : quotient;
//        }
//
//        // ret.
//        remainderReg = remainderNeg ? sub(remainder, ZERO_) : remainder;
//        return quotientNeg ? sub(quotient, ZERO_) : quotient;
//    }
    public static char sgn(String s) { return s.charAt(0); }

    public static DataType div(DataType src, DataType dest) {
        String a = dest.toString();
        String b = src.toString();
        String q = "";
        String r = (a.charAt(0) + "").repeat(a.length()) + a;

        for ( int i = 0; i < 32 + 1; i++ ) {

            String tmp = r.substring(i, i + 32);
            if ( sgn(b) == sgn(tmp) ) {
                tmp = sub(new DataType(b), new DataType(tmp)).toString();
            }
            else {
                tmp = add(new DataType(b), new DataType(tmp)).toString();
            }

            if ( sgn(b) == sgn(tmp) ) {
                q += '1';
            }
            else {
                q += '0';
            }

            r = r.substring(0, i) + tmp + r.substring(i + 32);
            System.out.println(tmp + " " + r + " " + q);
        }

        q = q.substring(1);
        r = r.substring(32);
        System.out.println("..." + q);

        if ( sgn(a) != sgn(b) ) {
            q = add(new DataType(q), new DataType(ONE)).toString();
            r = sub(new DataType(b), new DataType(r)).toString();
        }
        else {
            r = add(new DataType(b), new DataType(r)).toString();
        }
//        if ( r.equals(b) ) r = ZERO;
        System.out.println("..." + q);
        remainderReg = new DataType(r);
        return new DataType(q);
    }



    public static void main(String[] args) {
        DataType dt1 = new DataType(Lab01.intToBinary("2"));
        DataType dt2 = new DataType(Lab01.intToBinary("-7"));
//        System.out.println("Result: " + mul(dt1, dt2));
        System.out.println("Quotient: " + Lab01.binaryToInt(div(dt1, dt2).toString()));
        System.out.println("Remainder: " + Lab01.binaryToInt(remainderReg.toString()));
    }
}
