package me.yuuki.todoapp.dto;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

public class Result<T> {

    public static final String DEFAULT_SUCCESS_MSG = "done!";

    private final String code;
    private final String message;
    private final T data;

    public Result(String code, String message, T data) {
        Assert.notNull(message, "message 不能为 Null！");
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功的返回结果，状态码为 200
     */
    public static <T> Result<T> ok(T data, String message) {
        return new Result<>(
                HttpStatus.OK.name(),
                message,
                data);
    }

    /**
     * 请求成功的返回结果，状态码为 200
     */
    public static <T> Result<T> ok(T data) {
        return ok(data, DEFAULT_SUCCESS_MSG);
    }

    /**
     * 请求失败的返回结果，代表客户端的错误，状态码为 400,
     */
    public static <T> Result<T> fail(T data, String message) {
        return new Result<>(
                HttpStatus.BAD_REQUEST.name(),
                message,
                data
        );
    }

    /**
     * 服务端发生错误时的返回结果，状态码为 500,
     */
    public static <T> Result<T> error(T data, String message) {
        return new Result<>(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                message,
                data
        );
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
