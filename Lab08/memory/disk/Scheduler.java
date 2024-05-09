package COA2023.Lab08.memory.disk;

import COA2023.Lab08.memory.disk.*;
import COA2023.Lab08.memory.disk.Disk;

import java.util.Arrays;

public class Scheduler {

    /**
     * 先来先服务算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double FCFS(int start, int[] request) {
        //TODO
        int total = Math.abs(start - request[0]);
        for ( int i = 0; i < request.length - 1; i++ ) {
            total += Math.abs(request[i + 1] - request[i]);
        }
        return (double)total / request.length;
    }

    /**
     * 最短寻道时间优先算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double SSTF(int start, int[] request) {
        //TODO
        int len = 0, index = 0, min;
        boolean[] handled = new boolean[request.length];

        for ( int i = 0; i < request.length; i++ ) {
            min = Integer.MAX_VALUE;
            for (int j = 0; j < request.length; j++) {
                if ( !handled[j] && Math.abs(start - request[j]) < min ) {
                    index = j;
                    min = Math.abs(start - request[j]);
                }
            }
            len += min;
            start = request[index];
            handled[index] = true;
        }
        return (double)len / request.length;
    }

    /**
     * 扫描算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double SCAN(int start, int[] request, boolean direction) {
        //TODO
        Arrays.sort(request);
        int M = -1, m = Integer.MAX_VALUE;
        for ( int i : request ) {
            M = (i > start && M == -1) ? i : M;
            m = (i < start) ? i : m;
        }
        if ( direction ) {
            if ( m > start ) {
                return (double)(request[request.length - 1] - start) / request.length;
            }
            else {
                return (double)(2 * COA2023.Lab06.memory.disk.Disk.TRACK_NUM - 2 - start - request[0]) / request.length;
            }
        }
        else {
            if ( M < start ) {
                return (double)(start - request[0]) / request.length;
            }
            else {
                return (double)(request[request.length - 1] + start) / request.length;
            }
        }
    }

    /**
     * C-SCAN算法：默认磁头向磁道号增大方向移动
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @return 平均寻道长度
     */
    public double CSCAN(int start,int[] request){
        //TODO
        Arrays.sort(request);
        int nearest = Integer.MAX_VALUE;
        for ( int i : request ) {
            nearest = (i < start) ? i : nearest;
        }
        return nearest != Integer.MAX_VALUE ? (double)(2 * Disk.TRACK_NUM - 2 - start + nearest) / request.length :
                (double)(request[request.length - 1] - start) / request.length;
    }

    /**
     * LOOK算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double LOOK(int start,int[] request,boolean direction){
        //TODO
        Arrays.sort(request);
        int M = -1, m = Integer.MAX_VALUE;
        for ( int i : request ) {
            M = (i > start && M == -1) ? i : M;
            m = (i < start) ? i : m;
        }
        if ( m == Integer.MAX_VALUE && M != -1 ) {
            return (double)(request[request.length - 1] - start) / request.length;
        }
        else if ( m != Integer.MAX_VALUE && M == -1 ) {
            return (double)(start - request[0]) / request.length;
        }

        if ( direction ) {
            return (double)(2 * request[request.length - 1] - start - request[0]) / request.length;
        }
        else {
            return (double)(request[request.length - 1] + start - 2 * request[0]) / request.length;
        }
    }

    /**
     * C-LOOK算法：默认磁头向磁道号增大方向移动
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @return 平均寻道长度
     */
    public double CLOOK(int start,int[] request){
        //TODO
        Arrays.sort(request);
        int M = -1, m = Integer.MAX_VALUE;
        for ( int i : request ) {
            M = (i > start && M == -1) ? i : M;
            m = (i < start) ? i : m;
        }
        if ( m == Integer.MAX_VALUE && M != -1 ) {
            return (double)(request[request.length - 1] - start) / request.length;
        }
        else if ( m != Integer.MAX_VALUE && M == -1 ) {
            return (double)(start + m - 2 * request[0]) / request.length;
        }
        else {
            return (request.length == 1 && request[0] == start) ? 0 : (double)(2 * request[request.length - 1] - 2 * request[0] - start + m) / request.length;
        }
    }

}
