package COA2023.Lab09;

import COA2023.Lab08.util.DataType;
import java.util.Collections;

//考虑0的情况
public class NBCDU {

    //src + dest
    public DataType add(DataType src, DataType dest) {
        String b = src.toString(); String a = dest.toString();
        if (isZero(a.substring(4)) && isZero(b.substring(4))) {
            return new DataType("1100" + a.substring(4));
        } else if (isZero(a.substring(4))) {
            return new DataType(b);
        } else if (isZero(b.substring(4))) {
            return new DataType(a);
        }

        // 1. 异号 -> 同正
        String bSign = b.substring(0, 4);
        String aSign = a.substring(0, 4);
        if (!bSign.equals(aSign)) {
            if (bSign.equals("1101")) {
                b = reverseNumber(b);
            } else {
                a = reverseNumber(a);
            }
        }

        // 2. 判断进位
        String res = addi(a, b);
        boolean[] hasCarry = new boolean[8];
        for ( int i = 0; i < 7; i++ ) {
            int l = 28 - 4 * i;
            int r = 32 - 4 * i;
            hasCarry[i] = (isFromCarry(a.substring(l, r), b.substring(l, r), res.substring(l, r)) ||
                    Integer.parseInt(res.substring(l, r), 2) >= 10);
        }


        // 3. "+0110"处理进位
        for ( int i = 0; i < 7; i++ ) {
            int l = 28 - 4 * i;
            int r = 32 - 4 * i;
            if ( hasCarry[i] ) {
                res = addi("0".repeat(l) + "0110" + "0".repeat(32 - r), res);
                if ( Integer.parseInt(res.substring(l - 4, r - 4), 2) >= 10 ) {
                    hasCarry[i + 1] = true;
                }
            }
        }

        if (aSign.equals(bSign)) {
            res = aSign + res.substring(4);
        }
        else {
            res = "1100" + res.substring(4);
            if (!hasCarry[6]) {
                // 4. 无溢出，取反
                res = "1101" + reverseNumber(res).substring(4);
                hasCarry = new boolean[8];
                for (int i = 0; i < 7; i++) {
                    int l = 28 - i * 4;
                    int r = 32 - i * 4;
                    if (Integer.parseInt(res.substring(l, r), 2) >= 10) hasCarry[i] = true;
                }
                for (int i = 0; i < 7; i++) {
                    int l = 28 - i * 4;
                    int r = 32 - i * 4;
                    if (hasCarry[i]) {
                        res = addi(String.join("", Collections.nCopies(l, "0"))
                                        + "0110" + String.join("", Collections.nCopies(28 - l, "0")), res);
                        if (Integer.parseInt(res.substring(l - 4, r - 4), 2) >= 10) {
                            hasCarry[i + 1] = true;
                        }
                    }
                }
            }
        }

        return new DataType(res);
    }


    //dest - drc
    public DataType sub(DataType src, DataType dest) {
        String b = src.toString();

        if (b.startsWith("1101")) {
            b = "1100" + b.substring(4);
        } else {
            b = "1101" + b.substring(4);
        }
        return add(new DataType(b), dest);
    }

    public boolean isFromCarry(String a, String b, String res) {
        int aInt = Integer.parseInt(a, 2);
        int bInt = Integer.parseInt(b, 2);
        int resInt = Integer.parseInt(res, 2);
        return aInt > resInt && bInt > resInt;
    }

    public String reverseDigit(String s) {
        int num = 9 - Integer.parseInt(s, 2);
        return String.format("%4s", Integer.toBinaryString(num)).replaceAll(" ", "0");
    }
    public String reverseNumber(String s) {
        for (int i = 0; i < 7; i++) {
            int l = 28 - i * 4;
            int r = 32 - i * 4;
            s = s.substring(0, l) + reverseDigit(s.substring(l, r)) + s.substring(r);
        }
        return s.substring(0, 4) + oneAdder(s.substring(4)).substring(1);
    }

    boolean isZero(String s) {
        for (char c : s.toCharArray()) {
            if (c != '0') return false;
        }
        return true;
    }

    public static String oneAdder(String operand) {
        int len = operand.length();
        StringBuffer temp = new StringBuffer(operand);
        temp = temp.reverse();
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

    private static char[] addBit(char a, char b, char c) {
        return new char[] {
                (a - '0') + (b - '0') + (c - '0') > 1 ? '1' : '0',
                ((a - '0') ^ (b - '0') ^ (c - '0')) == 1 ? '1' : '0'
        };
    }
    private static String addc(String a, String b, char c0) {
        StringBuilder result = new StringBuilder();
        char cin = c0;
        for ( int i = a.length() - 1; i >= 0; i-- ) {
            result.insert(0, addBit(a.charAt(i), b.charAt(i), cin)[1]);
            cin = addBit(a.charAt(i), b.charAt(i), cin)[0];
        }
        return cin + result.toString();
    }
    private static String addi(String a, String b) {
        return addc(a, b, '0').substring(1);
    }

    public static void main(String[] args) {
        NBCDU nbcdu = new NBCDU();
        System.out.println(nbcdu.add(
                new DataType("11000000000000000000000010011000"),
                new DataType("11000000000000000000000001111001")
        ));
    }

}
