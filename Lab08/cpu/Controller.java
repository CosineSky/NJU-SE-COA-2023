package COA2023.Lab08.cpu;

import COA2023.Lab08.util.DataType;
import COA2023.Lab08.util.Transformer;
import COA2023.Lab08.cpu.alu.ALU;
import COA2023.Lab08.memory.Memory;

import java.util.Arrays;


public class Controller {
    private static final String ZERO12 = "000000000000";
    private static final String ZERO16 = "0000000000000000";
    private static final String ZERO20 = "00000000000000000000";

    // general purpose register
    char[][] GPR = new char[32][32];
    // program counter
    char[] PC = new char[32];
    // instruction register
    char[] IR = new char[32];
    // memory address register
    char[] MAR = new char[32];
    // memory buffer register
    char[] MBR =  new char[32];
    char[] ICC = new char[2];

    // 单例模式
    private static final Controller controller = new Controller();

    private Controller(){
        //规定第0个寄存器为zero寄存器
        GPR[0] = new char[]{'0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0'};
        ICC = new char[]{'0','0'}; // ICC初始化为00
    }

    public static Controller getController(){
        return controller;
    }

    public void reset(){
        PC = new char[32];
        IR = new char[32];
        MAR = new char[32];
        for ( int i = 0 ; i < 32; i++ ) {
            GPR[i] = new char[]{'0','0','0','0','0','0','0','0',
                    '0','0','0','0','0','0','0','0',
                    '0','0','0','0','0','0','0','0',
                    '0','0','0','0','0','0','0','0'};
        }
        ICC = new char[]{'0','0'}; // ICC初始化为00
        interruptController.reset();
    }

    public InterruptController interruptController = new InterruptController();
    public Memory memory = Memory.getMemory();
    public ALU alu = new ALU();

    private String convertCharArray(char[] chars) {
        StringBuilder s = new StringBuilder();
        for ( char c : chars ) s.append(c);
        return s.toString();
    }

    public void tick() {
        System.out.println("In: tick()");
        // TODO
        switch ("" + ICC[0] + ICC[1]) {
            case "00" -> getInstruct();
            case "01" -> findOperand();
            case "10" -> operate();
            case "11" -> interrupt();
        }

    }

    /** 执行取指操作 */
    private void getInstruct(){
        System.out.println("In: getInstruct()");
        // TODO
        MAR = PC.clone();
        byte[] ins = memory.read(convertCharArray(PC), 4);
        MBR = (Transformer.intToBinary(String.valueOf(ins[0])).substring(24) +
                Transformer.intToBinary(String.valueOf(ins[1])).substring(24) +
                Transformer.intToBinary(String.valueOf(ins[2])).substring(24) +
                Transformer.intToBinary(String.valueOf(ins[3])).substring(24)).toCharArray();
        PC = alu.add(new DataType(convertCharArray(PC)), new DataType(Transformer.intToBinary("4"))).toString().toCharArray();
        IR = MBR;
        ICC = (convertCharArray(IR).startsWith("1101110") ? "01" : "10").toCharArray();
    }

    /** 执行间址操作 */
    private void findOperand(){
        System.out.println("In: findOperand()");
        // TODO
        String instruction = convertCharArray(IR);
        MAR = GPR[Integer.parseInt(instruction.substring(20, 25), 2)];
        byte[] val = memory.read(convertCharArray(MAR), 4);
        GPR[Integer.parseInt(instruction.substring(20, 25), 2)] =
                (Transformer.intToBinary(String.valueOf(val[0])).substring(24) +
                        Transformer.intToBinary(String.valueOf(val[1])).substring(24) +
                        Transformer.intToBinary(String.valueOf(val[2])).substring(24) +
                        Transformer.intToBinary(String.valueOf(val[3])).substring(24)).toCharArray();
        ICC = "10".toCharArray();
    }

    /** 执行周期 */
    private void operate(){
        System.out.println("In: operate()");
        // TODO
        String instruction = convertCharArray(IR);
        if ( instruction.startsWith("1100100") ) {
            System.out.println("L = " + convertCharArray(GPR[2]));
            System.out.println("L = " + convertCharArray(GPR[Integer.parseInt(instruction.substring(15, 20), 2)]));
            GPR[Integer.parseInt(instruction.substring(7, 12), 2)] = alu.add(new DataType(convertCharArray(GPR[Integer.parseInt(instruction.substring(15, 20), 2)])), new DataType(ZERO20 + instruction.substring(20, 32))).toString().toCharArray();
        }
        else if ( instruction.startsWith("1100110") ) {
            GPR[Integer.parseInt(instruction.substring(7, 12), 2)] = alu.add(new DataType(convertCharArray(GPR[Integer.parseInt(instruction.substring(15, 20), 2)])), new DataType(convertCharArray(GPR[Integer.parseInt(instruction.substring(20, 25), 2)]))).toString().toCharArray();
        }
        else if ( instruction.startsWith("1110110") ) {
            for ( int i = 0; i < 20; i++ ) {
                GPR[Integer.parseInt(instruction.substring(7, 12), 2)][i] = instruction.charAt(i + 12);
            }
        }
        else if ( instruction.startsWith("1100000") ) {
            byte[] val = memory.read(convertCharArray(GPR[Integer.parseInt(instruction.substring(15, 20), 2)]), 4);
            GPR[Integer.parseInt(instruction.substring(7, 12), 2)] =
                    (Transformer.intToBinary(String.valueOf(val[0])).substring(24) +
                            Transformer.intToBinary(String.valueOf(val[1])).substring(24) +
                            Transformer.intToBinary(String.valueOf(val[2])).substring(24) +
                            Transformer.intToBinary(String.valueOf(val[3])).substring(24)).toCharArray();
        }
        else if ( instruction.startsWith("1110011") ) {
            GPR[Integer.parseInt(instruction.substring(7, 12), 2)] = PC;
            PC = alu.add(new DataType(convertCharArray(GPR[Integer.parseInt(instruction.substring(15, 20), 2)])), new DataType(ZERO20 + instruction.substring(20))).toString().toCharArray();
        }
        else if ( instruction.startsWith("1100111") ) {
            GPR[1] = PC;
        }
        ICC = (convertCharArray(IR).startsWith("1100111") ? "11" : "00").toCharArray();
    }

    /** 执行中断操作 */
    private void interrupt(){
        System.out.println("In: interrupt()");
        interruptController.handleInterrupt();
        // TODO
        ICC = "00".toCharArray();
    }

    public class InterruptController{
        // 中断信号：是否发生中断
        public boolean signal;
        public StringBuffer console = new StringBuffer();
        /** 处理中断 */
        public void handleInterrupt(){
            console.append("ecall ");
        }
        public void reset(){
            signal = false;
            console = new StringBuffer();
        }
    }

    // 以下一系列的get方法用于检查寄存器中的内容进行测试，请勿修改

    // 假定代码程序存储在主存起始位置，忽略系统程序空间
    public void loadPC(){
        PC = GPR[0];
    }

    public char[] getRA() {
        //规定第1个寄存器为返回地址寄存器
        return GPR[1];
    }

    public char[] getGPR(int i) {
        return GPR[i];
    }
}
