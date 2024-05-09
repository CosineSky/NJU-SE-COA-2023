package COA2023.Lab04;

import java.util.Collections;
import COA2023.Lab04.util.*;

public class FPU {

    public static String ZERO = "00000000000000000000000";
    public static class Float {
        char sign;
        int exp;
        String tail;
        Float(String s) {
            sign = s.charAt(0);
            exp = Integer.parseInt(s.substring(1, 9), 2);
            tail = (exp == 0 ? "0" : "1") + s.substring(9) + "000";
            exp = (exp == 0) ? 1 : exp;
        }
    }

    private final String[][] addCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN}
    };

    private final String[][] subCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN}
    };

    private final String[][] mulCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.N_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.P_ZERO},
            {IEEE754Float.P_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_ZERO, IEEE754Float.NaN}
    };

    private final String[][] divCorner = new String[][]{
            {IEEE754Float.P_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_ZERO, IEEE754Float.N_ZERO, IEEE754Float.NaN},
            {IEEE754Float.N_ZERO, IEEE754Float.P_ZERO, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.P_INF, IEEE754Float.N_INF, IEEE754Float.NaN},
            {IEEE754Float.N_INF, IEEE754Float.P_INF, IEEE754Float.NaN},
    };

    public DataType add(DataType src, DataType dest) {
        String a = src.toString();
        String b = dest.toString();
        if ( cornerCheck(addCorner, a, b) != null ) {
            return new DataType(cornerCheck(addCorner, a, b));
        }
        return new DataType(floatAdd(new Float(a), new Float(b)));
    }

    public DataType sub(DataType src, DataType dest) {
        String a = src.toString();
        String b = dest.toString();
        if ( cornerCheck(subCorner, a, b) != null ) {
            return new DataType(cornerCheck(subCorner, a, b));
        }
        a = negation("" + a.charAt(0)) + a.substring(1);
        return new DataType(floatAdd(new Float(a), new Float(b)));
    }

    public DataType mul(DataType src, DataType dest) {
        String a = src.toString();
        String b = dest.toString();
        if ( cornerCheck(mulCorner, a, b) != null ) {
            return new DataType(cornerCheck(mulCorner, a, b));
        }
        return new DataType(floatMul(new Float(a), new Float(b)));
    }

    public DataType div(DataType src, DataType dest) {
        String a = src.toString();
        String b = dest.toString();
        if ( cornerCheck(divCorner, a, b) != null ) {
            return new DataType(cornerCheck(divCorner, a, b));
        }
        if ( isZero(a.substring(1)) ) {
            throw new ArithmeticException();
        }
        if ( isZero(b.substring(1)) ) {
            return (a.charAt(0) == b.charAt(0)) ? new DataType(IEEE754Float.P_ZERO) : new DataType(IEEE754Float.N_ZERO);
        }
        return new DataType(floatDiv(new Float(b), new Float(a)));
    }


    /**
     * check corner cases of mul and div
     *
     * @param cornerMatrix corner cases pre-stored
     * @param oprA first operand (String)
     * @param oprB second operand (String)
     * @return the result of the corner case (String)
     */
    private String cornerCheck(String[][] cornerMatrix, String oprA, String oprB) {
        for (String[] matrix : cornerMatrix) {
            if (oprA.equals(matrix[0]) && oprB.equals(matrix[1])) {
                return matrix[2];
            }
        }
        if ( oprA.matches(IEEE754Float.NaN_Regular) || oprB.matches(IEEE754Float.NaN_Regular) ) {
            return IEEE754Float.NaN;
        }
        return null;
    }

    /**
     * right shift a num without considering its sign using its string format
     *
     * @param operand to be moved
     * @param n       moving nums of bits
     * @return after moving
     */
    private String rightShift(String operand, int n) {
        StringBuilder result = new StringBuilder(operand);  //保证位数不变
        boolean sticky = false;
        for (int i = 0; i < n; i++) {
            sticky = sticky || result.toString().endsWith("1");
            result.insert(0, "0");
            result.deleteCharAt(result.length() - 1);
        }
        if (sticky) {
            result.replace(operand.length() - 1, operand.length(), "1");
        }
        return result.substring(0, operand.length());
    }

    /**
     * 对GRS保护位进行舍入
     *
     * @param sign    符号位
     * @param exp     阶码
     * @param sig_grs 带隐藏位和保护位的尾数
     * @return 舍入后的结果
     */
    private String round(char sign, String exp, String sig_grs) {
        int grs = Integer.parseInt(sig_grs.substring(24, 27), 2);
        if ((sig_grs.substring(27).contains("1")) && (grs % 2 == 0)) {
            grs++;
        }
        String sig = sig_grs.substring(0, 24); // 隐藏位+23位
        if (grs > 4) {
            sig = oneAdder(sig);
        } else if (grs == 4 && sig.endsWith("1")) {
            sig = oneAdder(sig);
        }

        if (Integer.parseInt(sig.substring(0, sig.length() - 23), 2) > 1) {
            sig = rightShift(sig, 1);
            exp = oneAdder(exp).substring(1);
        }
        if (exp.equals("11111111")) {
            return sign == '0' ? IEEE754Float.P_INF : IEEE754Float.N_INF;
        }

        return sign + exp + sig.substring(sig.length() - 23);
    }

    /**
     * add one to the operand
     *
     * @param operand the operand
     * @return result after adding, the first position means overflow (not equal to the carry to the next)
     *         and the remains means the result
     */
    private String oneAdder(String operand) {
        int len = operand.length();
        StringBuilder temp = new StringBuilder(operand);
        temp.reverse();
        int[] num = new int[len];
        for (int i = 0; i < len; i++) num[i] = temp.charAt(i) - '0';  //先转化为反转后对应的int数组
        int bit = 0x0;
        int carry = 0x1;
        char[] res = new char[len];
        for (int i = 0; i < len; i++) {
            bit = num[i] ^ carry;
            carry = num[i] & carry;
            res[i] = (char) ('0' + bit);  //显示转化为char
        }
        String result = new StringBuffer(new String(res)).reverse().toString();
        return "" + (result.charAt(0) == operand.charAt(0) ? '0' : '1') + result;  //注意有进位不等于溢出，溢出要另外判断
    }

    // Util tools.
    public static boolean isZero(String s) {
        for (char c : s.toCharArray()) {
            if (c == '1') return false;
        }
        return true;
    }

    public static String negation(String operand) {
        return operand.replace('0', '*').replace('1', '0').replace('*', '1');
    }

    public static String leftShift(String s, int n) {
        return s.substring(n) + "0".repeat(n);
    }

    public static String carry_adder(String add1, String add2, int carry, int length) {
        StringBuilder ans = new StringBuilder();
        int x, y;
        for (int i = length - 1; i >= 0; i--) {//顺序是从低位到高位
            x = add1.charAt(i) - '0';
            y = add2.charAt(i) - '0';
            ans.insert(0, x ^ y ^ carry);
            carry = x & carry | y & carry | x & y;
        }
        return carry + ans.toString();
    }

    public String floatAdd(Float a, Float b) {
        int exp = Math.max(a.exp, b.exp);
        if ( a.exp > b.exp ) {
            b.tail = rightShift(b.tail, a.exp - b.exp);
        }
        if ( b.exp > a.exp ) {
            a.tail = rightShift(a.tail, b.exp - a.exp);
        }

        String temp = signedAdd(a.sign + a.tail, b.sign + b.tail);
        char overflow = temp.charAt(0);
        char sign = temp.charAt(1);
        String tail = temp.substring(2);
        return normalize_1(overflow, sign, exp, tail);
    }

    public String floatMul(Float a, Float b) {
        int exp = a.exp + b.exp - 127;
        char sign = (char) ((a.sign - '0') ^ (b.sign - '0') + '0');
        if ( a.exp == 255 || b.exp == 255 ) {
            return "" + sign + "11111111" + ZERO;
        }
        String tail = unsignedMul(a.tail, b.tail, a.tail.length());
        exp++;
        return normalize_2(sign, exp, tail, a.tail.length());
    }

    public String floatDiv(Float a, Float b) {
        char sign = (char) ('0' + (a.sign - '0') ^ (b.sign - '0'));
        int exp = a.exp - b.exp + 127;
        if ( a.exp == 255 ) {
            return "" + sign + "11111111" + ZERO;
        }
        if ( b.exp == 255 ) {
            return "" + sign + "00000000" + ZERO;
        }
        String tail = unsignedDiv(a.tail, b.tail, a.tail.length());
        return normalize_2(sign, exp, tail, a.tail.length());
    }

    public String normalize_1(char overflow, char sign, int exp, String tail) {
        if ( overflow == '1' ) {
            exp++;
            tail = "1" + tail.substring(0, tail.length() - 1);
        }
        if ( exp >= 255 ) {
            return "" + sign + "11111111" + ZERO;
        }
        while ( tail.charAt(0) != '1' && exp > 0 ) {
            exp--;
            tail = leftShift(tail, 1);
        }
        if ( exp == 0 ) {
            tail = rightShift(tail, 1);
        }
        if ( isZero(tail) ) {
            return "000000000" + ZERO;
        }
        String exp_ = String.format("%8s", Integer.toBinaryString(exp)).replace(" ", "0");
        return round(sign, exp_, tail);
    }

    public String normalize_2(char sign, int exp, String tail, int length) {
        while ( tail.charAt(0) != '1' && exp > 0 ) {
            exp--;
            tail = leftShift(tail, 1);
        }
        while ( !isZero(tail.substring(0, length)) && exp < 0 ) {
            exp++;
            tail = rightShift(tail, 1);
        }
        if ( exp >= 255 ) {
            return "" + sign + "11111111" + ZERO;
        }
        else if ( exp == 0 ) {
            tail = rightShift(tail, 1);
        }
        else if ( exp < 0 ) {
            return "" + sign + "00000000" + ZERO;
        }
        String exp_ = String.format("%8s", Integer.toBinaryString(exp)).replace(" ", "0");
        return round(sign, exp_, tail);
    }

    public String signedAdd(String a, String b) {
        char sgn_a = a.charAt(0);
        char sgn_b = b.charAt(0);
        if ( isZero(a) ) return "0" + b;
        if ( isZero(b) ) return "0" + a;
        a = a.substring(1);
        b = b.substring(1);
        if ( sgn_a == sgn_b ) {
            String tmp = carry_adder(a, b, 0, a.length());
            return "" + tmp.charAt(0) + sgn_a + tmp.substring(1);
        }
        else {
            String tmp = carry_adder(a, negation(b), 1, a.length());
            return tmp.charAt(0) == '1' ? "0" + sgn_a + tmp.substring(1) :
                    "0" + negation("" + sgn_a) + oneAdder(negation(tmp.substring(1))).substring(1);
        }
    }

    String unsignedMul(String a, String b, int length) {
        String ans = String.join("", Collections.nCopies(length, "0")) + b;
        for ( int i = 0; i < length; i++ ) {
            char carry = '0';
            if ( ans.charAt(2 * length - 1) == '1' ) {
                String temp = carry_adder(a, ans.substring(0, length), 0, length);
                carry = temp.charAt(0);
                ans = temp.substring(1) + ans.substring(length);
            }
            ans = carry + ans.substring(0, 2 * length - 1);
        }
        return ans;
    }

    String unsignedDiv_(String a, String b, int length) {
        a += "0".repeat(length);
        for ( int i = 0; i < length; i++ ) {
            String temp = carry_adder(a.substring(0, length), negation(b), 1, length).substring(1);
            a = (temp.charAt(0) == '0') ? temp.substring(1) + a.substring(length) + "1" : leftShift(a, 1);
        }
        return a.substring(length);
    }

    String unsignedDiv(String a, String b, int length) {
        a += String.join("", Collections.nCopies(length, "0"));
        for ( int i = 0; i < length; i++ ) {
            String tmp = carry_adder(a.substring(0, length), negation(b), 1, length).substring(1);
            a = tmp.charAt(0) == '0' ? tmp.substring(1) + a.substring(length) + "1" : leftShift(a, 1);
        }
        return a.substring(length);
    }



    public static void main(String[] args) {
//        System.out.println(new FPU().signedAdd("1001", "0111"));
//        System.out.println(new FPU().oneAdder("1001"));
//        System.out.println(carry_adder("1001", "0101", 0, 4));
        System.out.println(new FPU().unsignedMul("1001", "1001", 4));
        System.out.println(new FPU().unsignedDiv("01001", "00011", 5));
    }

}