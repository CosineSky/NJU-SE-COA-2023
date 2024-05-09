package COA2023.Lab05.memory.cache.cacheReplacementStrategy;

import COA2023.Lab05.memory.cache.Cache;


/**
 * TODO 最近最少用算法
 */
public class LRUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache.getCache().setTimeStamp(rowNO);
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        int replaceRowNO = 0;
        Cache cache = Cache.getCache();
        long longestTimeSpan = 0;
        long timeNow = System.currentTimeMillis();

        for (int i = start; i <= end; i++) {
            if (!cache.isValid(i)) {
                replaceRowNO = i;
                break;
            }
            if (timeNow - cache.getTimeStamp(i) > longestTimeSpan) {
                replaceRowNO = i;
                longestTimeSpan = timeNow - cache.getTimeStamp(i);
            }
        }

        if (Cache.isWriteBack) {
            cache.WriteBack(replaceRowNO);
        }

        cache.update(replaceRowNO, addrTag, input);
        cache.setTimeStamp(replaceRowNO);
        return replaceRowNO;
//        return -1;
    }

}




































