package me.yuuki.todoapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端出现的非编码错误，这种错误是“可预期的”，可能是数据库或集成第三方服务中出现的错误，仍抛50x异常
 */
public class ServerException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(ServerException.class);

    public ServerException(String msg) {
        super(msg);
    }
}
