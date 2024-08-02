package com.example.user.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Snowflake算法的分布式ID生成器
 * 由于javascript的Number类型最大支持53位，所以以下是53位的ID生成器
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，一般生成的ID为正数，所以默认为0
 * 41位时间戳（毫秒级），注意，41位时间戳不是存储当前时间的时间戳，而是存储时间戳的差值（当前时间戳 - 开始时间戳), 最高可以表示2^41 - 1个毫秒值，大概可以使用69年
 * 3位的数据机器位，可以部署在8个节点
 * 8位序列，毫秒内的计数，8位的计数顺序号支持每个节点每毫秒（同一机器，同一时间戳）产生256个ID序号
 */
@Component
public class IdGenerator {
    private final static long startTimestamp = 1704038400000L; // 2024-01-01 00:00:00
    private final static long workerIdBits = 3L;
    private final static long sequenceBits = 8L;
    private final static long maxWorkerId = ~(-1L << workerIdBits);
    private final static long workerIdShift = sequenceBits;
    private final static long timestampLeftShift = sequenceBits + workerIdBits;
    private final static long sequenceMask = ~(-1L << sequenceBits);
    private static long lastTimestamp = -1L;
    private static long sequence = 0L;

    @Value("${id-generator.worker-id}")
    private long workerId;
    private static IdGenerator idGenerator;

    @PostConstruct
    public void init() {
        System.err.println("workerId: " + workerId);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        idGenerator = this;
    }

    public IdGenerator() {}

    public static synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - startTimestamp) << timestampLeftShift) | (idGenerator.workerId << workerIdShift) | sequence;
    }

    private static long tilNextMillis(final long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static long timeGen() {
        return System.currentTimeMillis();
    }
}
