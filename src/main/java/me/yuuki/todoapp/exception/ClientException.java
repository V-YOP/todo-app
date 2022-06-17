package me.yuuki.todoapp.exception;

/**
 * 代表客户端的异常，将抛出40x错误
 */
public class ClientException extends RuntimeException {

    public ClientException(String s) {
        super(s);
    }
}
