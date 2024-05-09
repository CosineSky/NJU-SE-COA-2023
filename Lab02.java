package COA2023;

import java.util.Arrays;
import java.util.Collections;

class DataType {

    private final byte[] data = new byte[4];

    public DataType(String dataStr) {
        // 目前是大端实现，高位字节存放在低地址
        int length = dataStr.length();
        if (length == 8 || length == 16 || length == 32) {
            dataStr = String.join("", Collections.nCopies(32 - length, "0")) + dataStr;
            for (int i = 0; i < 32; i++) {
                char temp = dataStr.charAt(i);
                if (temp == '0' || temp == '1') {
                    data[i / 8] |= ((dataStr.charAt(i) - '0') << (7 - i % 8));
                } else {
                    throw new NumberFormatException("Illegal dataStr: " + dataStr);
                }
            }
        } else {
            throw new NumberFormatException("Illegal dataStr: " + dataStr);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            stringBuilder.append(fill32bit(Integer.toBinaryString((int) data[i])).substring(24));
        }
        return stringBuilder.toString();
    }

    public String fill32bit(String s){
        StringBuilder sb = new StringBuilder(s);
        if(s.length() < 32){
            int l = s.length();
            for(int i = 0;i < 32-l;i++){
                sb.insert(0,"0");
            }
        }
        return sb.toString();
    }
}


public class Lab02 {
    public static String ZERO = "00000000000000000000000000000000";
    public static String ONE = "00000000000000000000000000000001";

    public static String inverse(String s) {
        return s.replace('1', '*').replace('0', '1').replace('*', '0');
    }

    public static char[] addBit(char a, char b, char c) {
        char[] ret = new char[2];
        ret[0] = ((a - '0') ^ (b - '0') ^ (c - '0')) == 1 ? '1' : '0';
        ret[1] = (a - '0') + (b - '0') + (c - '0') > 1 ? '1' : '0';
        return ret;
    }

    public DataType add(DataType src, DataType dest) {
        String s1 = src.toString(), s2 = dest.toString();
        char[] ans = new char[32];
        char cin = '0';
        for ( int i = s1.length() - 1; i >= 0; i-- ) {
            ans[i] = addBit(s1.charAt(i), s2.charAt(i), cin)[0];
            cin = addBit(s1.charAt(i), s2.charAt(i), cin)[1];
        }
        return new DataType(new String(ans));
    }

    public DataType addTotal(DataType... dt) {
        DataType result = new DataType(ZERO);
        for ( DataType d : dt ) result = add(result, d);
        return result;
    }

    public DataType sub(DataType src, DataType dest) {
        return addTotal(dest, new DataType(inverse(src.toString())), new DataType(ONE));
    }

    public static void main(String[] args) {
        Lab02 lab = new Lab02();
        DataType dt1 = new DataType("00000000000000000000000000001100");
        DataType dt2 = new DataType("00000000000000000000000000000000");
        System.out.println(lab.sub(dt1, dt2));

    }


}
