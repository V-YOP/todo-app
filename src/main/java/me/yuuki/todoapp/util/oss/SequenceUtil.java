package me.yuuki.todoapp.util.oss;

import me.yuuki.todoapp.util.oss.OSSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p />一个线程安全的序列号工具，依赖OSS，保证在单JVM下获得递增数据，使用号段模式以保证性能和尽量少的对OSS的交互
 * <p>
 * <p />当前的实现无法保证集群的线程安全，如果需要保证，则需要把锁换成分布式锁，但当前且长期都没有分布式的需要
 * <p>
 * <p />存储路径将为 system/seq/{key}
 */
public class SequenceUtil {

    private static final long STEP = 500;
    private final ConcurrentHashMap<String, SeqBox> locks = new ConcurrentHashMap<>();
    private OSSUtil ossUtil;

    @Autowired
    void setOssUtil(OSSUtil ossUtil) {
        this.ossUtil = ossUtil;
    }

    public long getSeq(String key) {
        key = StringUtils.applyRelativePath("system/seq", key);
        if (key.startsWith("/")) {
            throw new IllegalArgumentException(String.format("不合法的 Key：%s", key));
        }

        locks.putIfAbsent(key, new SeqBox());
        SeqBox box = locks.get(key);

        synchronized (box) {
            if (box.currentSeq >= box.lastSeq) {
                Long ossSeq = ossUtil.get(key).map(Long::parseLong).orElse(box.currentSeq);
                ossUtil.put(key, String.valueOf(ossSeq + STEP));
                box.currentSeq = ossSeq;
                box.lastSeq = ossSeq + STEP;
            }
            return box.currentSeq++;
        }
    }

    // 既用来存储当前序列，也用来当锁
    private static class SeqBox {
        public volatile Long currentSeq = 1L;
        public volatile Long lastSeq = 0L;
    }
}
