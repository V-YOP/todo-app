package me.yuuki.todoapp.config;

import me.yuuki.todoapp.dto.ClientException;
import me.yuuki.todoapp.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @Value("${debug:false}")
    private boolean debug;

    /**
     * 405 异常
     */
    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class
    })
    ResponseEntity<Object> invalidRequestMethod(HttpRequestMethodNotSupportedException e) {
        Result<Void> result = e.getSupportedMethods() == null ?
                Result.fail(null, "HTTP请求方法错误！") :
                Result.fail(null, "HTTP请求方法错误！合法方法：" +
                        String.join(", ", e.getSupportedMethods()));
        return ResponseEntity
                .badRequest()
                .body(result);
    }

    /**
     * 入参错误异常，应当抛40x而非50x
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class
    })
    ResponseEntity<Object> badRequest(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest()
                .body(Result.fail(e.getMessage(), "入参错误！"));
    }

    @ExceptionHandler({
            ClientException.class
    })
    ResponseEntity<Object> clientException(ClientException e) {
        return ResponseEntity.badRequest()
                .body(Result.fail(null, e.getMessage()));
    }


    /**
     * 其它异常
     */
    @ExceptionHandler
    ResponseEntity<Object> handlerAllException(Throwable e) {
        logger.error("未预料的异常", e);
        if (debug) {
            return ResponseEntity
                    .internalServerError()
                    .body(Result.error(e, e.toString()));
        }

        return ResponseEntity
                .internalServerError()
                .body(Result.error(null, "服务器内部错误!"));
    }
}
