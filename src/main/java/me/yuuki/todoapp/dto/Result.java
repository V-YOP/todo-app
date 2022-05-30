package me.yuuki.todoapp.dto;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

public class Result<T> {

    public static final String DEFAULT_SUCCESS_MSG = "done!";

    private final HttpStatus status;
    private final String message;
    private final T data;

    public Result(HttpStatus status, String message, T data) {
        Assert.notNull(message, "message 不能为 Null！");
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功的返回结果，状态码为 200
     */
    public static <T> Result<T> ok(T data, String message) {
        return new Result<>(
                HttpStatus.OK,
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
                HttpStatus.BAD_REQUEST,
                message,
                data
        );
    }

    /**
     * 服务端发生错误时的返回结果，状态码为 500,
     */
    public static <T> Result<T> error(T data, String message) {
        return new Result<>(
                HttpStatus.INTERNAL_SERVER_ERROR,
                message,
                data
        );
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
