package me.yuuki.todoapp.exception;

import me.yuuki.todoapp.util.FunctionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * 代表客户端的异常，将抛出40x错误
 */
public class ClientException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(ClientException.class);

    public ClientException(String msg) {
        super(msg);
    }

    public static <T> T tryMe(FunctionUtil.SupplierEx<T> body, Function<Throwable, String> msg) {
        try {
            return body.get();
        } catch (Throwable e) {
            throw new ClientException(msg.apply(e));
        }
    }

    public static void tryMe(FunctionUtil.RunnableEx body, Function<Throwable, String> msg) {
        try {
            body.run();
        } catch (Throwable e) {
            throw new ClientException(msg.apply(e));
        }
    }

    /**
     * 将参数函数中抛出的异常包装成为ClientException
     */
    public static void convertAssert(FunctionUtil.RunnableEx body) {
        tryMe(body, Throwable::getMessage);
    }
}
