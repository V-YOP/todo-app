package me.yuuki.todoapp.util.oss;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 操作 OSS 的工具类，抽象出接口以便进行 mock <br />
 * <p>
 * 该工具类提供的方法应当都是线程安全但非原子的，线程安全由 KVTable 去维护 <br />
 * <p>
 * KVTable 应当去按用户去划分数据，使不同用户之间能并发操作
 * <p/>
 * TODO 应当提供批量操作接口，避免在批量操作时重复进行连接等操作
 */
public abstract class OSSUtil {
    public List<String> listAll() {
        return listAsStream().collect(Collectors.toList());
    }

    public List<String> listAll(String prefix) {
        return listAsStream(prefix).collect(Collectors.toList());
    }

    public Stream<String> listAsStream() {
        return listAsStream("");
    }

    public abstract Stream<String> listAsStream(String prefix);

    public abstract boolean exists(String key);

    public boolean notExists(String key) {
        return !exists(key);
    }

    public abstract void put(String key, String value);

    public boolean putIfAbsent(String key, String value) {
        if (notExists(key)) {
            put(key, value);
            return true;
        }
        return false;
    }

    public abstract Optional<String> get(String key);

    public String getOrDefault(String key, String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    public void update(String key, Function<Optional<String>, String> fn) {
        put(key, fn.apply(get(key)));
    }

    public void update(String key, String defaultValue, Function<String, String> fn) {
        put(key, fn.apply(getOrDefault(key, defaultValue)));
    }

    public abstract boolean delete(String key);
}
