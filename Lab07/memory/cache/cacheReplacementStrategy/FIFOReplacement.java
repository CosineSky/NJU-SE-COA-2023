package COA2023.Lab07.memory.cache.cacheReplacementStrategy;

import COA2023.Lab07.memory.cache.Cache;

public class FIFOReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        //只有在添加进cache的时候需要重置timestamp
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        int replaceRowNO = 0;
        Cache cache = Cache.getCache();

        for ( int i = start; i <= end; i++ ) {
            if ( !cache.isValid(i) ) {
                replaceRowNO = i;
                break;
            }
            else if ( cache.getTimeStamp(i) == 0L ) {
                replaceRowNO = i;
            }
        }

        if ( Cache.isWriteBack ) {
            cache.WriteBack(replaceRowNO);
        }

        cache.update(replaceRowNO, addrTag, input);
        cache.setTimeStampFIFO(replaceRowNO);
        return replaceRowNO;
    }

}
