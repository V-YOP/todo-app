package me.yuuki.todoapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 关于 key 的静态工具类，任何时候都应当使用该类提供的函数进行 key 的拼接操作
 */
public final class KeyUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyUtil.class);
    private KeyUtil() {
    }

    /**
     * 根据一系列路径元素构造相应 key，key将转换为全小写
     *
     * @param items 路径元素，不能包含Null
     * @return 结果 key，其将为所有路径元素使用/进行拼接的值，元素首部和中间的/将被妥善处理，\将被替换为/
     */
    public static String constructKey(String...items) {
        Assert.noNullElements(items, "入参不能包括null！");
        String key = Arrays.stream(items).flatMap(item -> Arrays.stream(item.split("[/\\\\]")).filter(StringUtils::hasText))
                .map(String::toLowerCase)
                .collect(Collectors.joining("/"));
        logger.info("构造KEY：{}", key);
        return key;
    }

    /**
     * 检查两个key是否相等，忽略大小写
     */
    public static boolean sameKey(String key1, String key2) {
        return Objects.equals(key1, key2) || Objects.equals(constructKey(key1), constructKey(key2));
    }
}
