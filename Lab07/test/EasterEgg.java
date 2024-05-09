package COA2023.Lab07.test;

import COA2023.Lab07.cpu.mmu.MMU;
import COA2023.Lab07.memory.Memory;
import COA2023.Lab07.memory.cache.Cache;
import COA2023.Lab07.memory.cache.cacheReplacementStrategy.FIFOReplacement;
import COA2023.Lab07.memory.disk.Disk;
import COA2023.Lab07.memory.tlb.TLB;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class EasterEgg {

    private final MMU mmu = MMU.getMMU();

    private final Disk disk = Disk.getDisk();

    private final MemTestHelper helper = new MemTestHelper();

    @Before
    public void init() {
        Memory.PAGE = true;
        Memory.SEGMENT = true;
        MMU.getMMU().clear();
        Memory.timer = true;
        Cache.getCache().setSETS(128);
        Cache.getCache().setSetSize(4);
        Cache.getCache().setReplacementStrategy(new FIFOReplacement());
        Cache.isAvailable = true;
        TLB.isAvailable = true;
    }

    @Test
    public void EasterEgg1() {
        int len = 32;
        byte[] expect = disk.read("00000000000000000000000000000000", len);
        byte[] actual = mmu.read("000000000000000000000000000000000000000000000000", len);
        for (int i = 0; i < 300; i++) {
            actual = mmu.read("000000000000000000000000000000000000000000000000", len);
        }
        assertArrayEquals(expect, actual);
    }

}
