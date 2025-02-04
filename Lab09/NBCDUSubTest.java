package COA2023.Lab09;

import org.junit.Test;
import COA2023.Lab08.util.DataType;
import COA2023.Lab08.util.Transformer;

import static org.junit.Assert.assertEquals;

public class NBCDUSubTest {

    private final NBCDU nbcdu = new NBCDU();
    private DataType src;
    private DataType dest;
    private DataType result;

    @Test
    public void SubTest1() {
        src = new DataType("11000000000000000000000100100101");
        dest = new DataType("11000000000000000000001100001001");
        result = nbcdu.sub(src, dest);
        assertEquals("11000000000000000000000110000100", result.toString());
    }

}
