package me.yuuki.todoapp.util;


import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 一些关于函数的静态工具类
 */
public final class FunctionUtil {


    private FunctionUtil(){}

    public static <T> Function<Object, T> constant(T t) {
        return (a) -> t;
    }

    public static <T> T identity(T t) {
        return t;
    }

    @FunctionalInterface
    public interface RunnableEx {
        void run() throws Throwable;
    }

    @FunctionalInterface
    public interface SupplierEx<T> {
        T get() throws Throwable;
    }

    @FunctionalInterface
    public interface FunctionEx<A, B> {
        B apply(A a) throws Throwable;
    }

    public interface Consumer<T> {
        void put(T t) throws Throwable;
    }
}
