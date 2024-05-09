package COA2023.Lab05.memory;

import COA2023.Lab05.memory.cache.Cache;
import COA2023.Lab05.memory.cache.cacheReplacementStrategy.*;
import COA2023.Lab05.util.Transformer;

import java.util.Arrays;

/**
 * 内存抽象类
 */

public class Memory {

    private static final int MEM_SIZE_B = 16 * 1024 * 1024;      // 16 MB

    private static final byte[] memory = new byte[MEM_SIZE_B];

    private static final Memory memoryInstance = new Memory();

    private Memory() {
    }

    public static Memory getMemory() {
        return memoryInstance;
    }

    public byte[] read(String pAddr, int len) {
        byte[] data = new byte[len];
        for (int ptr = 0; ptr < len; ptr++) {
            data[ptr] = memory[Integer.parseInt(Transformer.binaryToInt(pAddr)) + ptr];
        }
        return data;
    }

    public void write(String pAddr, int len, byte[] data) {
        // 通知Cache缓存失效
        Cache.getCache().invalid(pAddr, len);
        // 更新数据
        for (int ptr = 0; ptr < len; ptr++) {
            memory[Integer.parseInt(Transformer.binaryToInt(pAddr)) + ptr] = data[ptr];
        }
    }

    public static void main(String[] args) {
        Memory memory = Memory.getMemory();
        Cache cache = Cache.getCache();
//        cache.setReplacementStrategy(new FIFOReplacement());
        cache.setSETS(Cache.CACHE_SIZE_B / Cache.LINE_SIZE_B);
        cache.setSetSize(1);


//        byte[] data = {0b00000001, 0b00000010, 0b000000011, 0b00000100};
//        String pAddr = "00000000000000001000000001000001";
////        00000000000000001-000000001-000001
//        memory.write(pAddr, data.length, data);
//        System.out.println(Arrays.toString(data).equals(Arrays.toString(cache.read(pAddr, data.length))));
//        System.out.println(Arrays.toString(cache.getData(1)));
//        System.out.println(cache.checkStatus(new int[]{Integer.parseInt(Transformer.binaryToInt("000000001"))}, new boolean[]{true}, new char[][]{"00000000000000000000000001".toCharArray()}));


//        Cache.getCache().setReplacementStrategy(new FIFOReplacement());
        byte[] input1 = new byte[32 * 1024];
//        byte[] input2 = new byte[64];
//        byte[] input3 = new byte[64];
        Arrays.fill(input1, (byte) 'd');
//        Arrays.fill(input2, (byte) 'e');
//        Arrays.fill(input3, (byte) 'f');
        String pAddr1 = "00000000000000000000000000000000";
//        String pAddr2 = "00000000000010100000000001000000";
//        String pAddr3 = "00000000000001110000000001000001";
//
//        // 将cache填满
        memory.write(pAddr1, input1.length, input1);
        byte[] dataRead = cache.read(pAddr1, input1.length);
        System.out.println(Arrays.toString(input1).equals(Arrays.toString(dataRead)));
        System.out.println(cache.checkStatus(new int[]{0, 1}, new boolean[]{true, true}, new char[][]{"00000000000000000000000000".toCharArray(), "00000000000000000000000001".toCharArray()}));
//
//        memory.write(pAddr2, input2.length, input2);
//        dataRead = cache.read(pAddr2, input2.length);
//        System.out.println(Arrays.toString(input2).equals(Arrays.toString(dataRead)));
//
//        memory.write(pAddr3, input3.length, input3);
//        dataRead = cache.read(pAddr3, input3.length);
//        System.out.println(Arrays.toString(input3).equals(Arrays.toString(dataRead)));



    }
}




















































