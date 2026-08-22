package com.troy.common.core.utils.bean;

/**
 * @author chenxl
 * @date 2023/8/9
 */
public class IdUtil {
    private static final long INIT_EPOCH = 1649059688068L;
    private long lastTimeMillis = -1L;
    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long MAX_DATA_CENTER_ID = 31L;
    private long datacenterId;
    private static final long WORKER_ID_BITS = 5L;
    private static final long MAX_WORKER_ID = 31L;
    private long workerId;
    private static final long SEQUENCE_BITS = 12L;
    private static final long SEQUENCE_MASK = 4095L;
    private long sequence;
    private static final long WORK_ID_SHIFT = 12L;
    private static final long DATA_CENTER_ID_SHIFT = 17L;
    private static final long TIMESTAMP_SHIFT = 22L;

    public IdUtil(long datacenterId, long workerId) {
        if (datacenterId >= 0L && datacenterId <= 31L) {
            if (workerId >= 0L && workerId <= 31L) {
                this.workerId = workerId;
                this.datacenterId = datacenterId;
            } else {
                throw new IllegalArgumentException(String.format("workId值必须大于0并且小于%d", 31L));
            }
        } else {
            throw new IllegalArgumentException(String.format("datacenterId值必须大于0并且小于%d", 31L));
        }
    }

    public synchronized long nextId() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < this.lastTimeMillis) {
            throw new RuntimeException(String.format("可能出现服务器时钟回拨问题，请检查服务器时间。当前服务器时间戳：%d，上一次使用时间戳：%d", currentTimeMillis, this.lastTimeMillis));
        } else {
            if (currentTimeMillis == this.lastTimeMillis) {
                this.sequence = this.sequence + 1L & 4095L;
                if (this.sequence == 0L) {
                    currentTimeMillis = this.tilNextMillis(this.lastTimeMillis);
                }
            } else {
                this.sequence = 0L;
            }

            this.lastTimeMillis = currentTimeMillis;
            return currentTimeMillis - 1649059688068L << 22 | this.datacenterId << 17 | this.workerId << 12 | this.sequence;
        }
    }

    private long tilNextMillis(long lastTimeMillis) {
        long currentTimeMillis;
        for(currentTimeMillis = System.currentTimeMillis(); currentTimeMillis <= lastTimeMillis; currentTimeMillis = System.currentTimeMillis()) {
        }

        return currentTimeMillis;
    }
}
