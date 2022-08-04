package me.yuuki.todoapp.exception;

import me.yuuki.todoapp.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

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
        Result<Void> result =
                Result.fail(null, e.getSupportedMethods() == null ?
                    "HTTP请求方法错误！" :
                    "HTTP请求方法错误！合法方法：" + String.join(", ", e.getSupportedMethods()));
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
                .body(Result.fail(null, "入参错误！" + e.getMessage()));
    }

    /**
     * 入参格式非法
     */
    @ExceptionHandler({
            HttpMessageNotReadableException.class
    })
    ResponseEntity<Object> badRequest(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(Result.fail(null, "入参格式非法！" + e.getMessage()));
    }


    @ExceptionHandler({
            ConstraintViolationException.class
    })
    ResponseEntity<Object> badRequest(ConstraintViolationException e) {
        return ResponseEntity.badRequest()
                .body(Result.fail(null, "入参校验失败！" + e.getLocalizedMessage()));
    }
/*
    // 鉴权相关异常

    @ExceptionHandler({
            AuthenticationException.class
    })
    ResponseEntity<Object> loginFail(AuthenticationException e) {
        return ResponseEntity.badRequest()
                .body(Result.fail(null, "用户名或密码错误！"));
    }

    @ExceptionHandler({
            UnauthenticatedException.class
    })
    ResponseEntity<Object> notLogin(UnauthenticatedException e) {
        return ResponseEntity.badRequest()
                .body(new Result<Void>("NOT_LOGIN",  "你没有登陆或者令牌已过期。", null));
    }

    @ExceptionHandler({
            UnauthorizedException.class
    })
    ResponseEntity<Object> noPermission(UnauthorizedException e) {
        return ResponseEntity.badRequest()
                .body(Result.fail(null, "你没有权限访问此接口！"));
    }

*/
    /**
     * 客户端的异常，抛40x
     */
    @ExceptionHandler({
            ClientException.class
    })
    ResponseEntity<Object> clientException(ClientException e) {
        return ResponseEntity.badRequest()
                .body(Result.fail(null, e.getMessage()));
    }

    /**
     * 可预期的服务端的异常，抛50x
     */
    @ExceptionHandler({
            ServerException.class
    })
    ResponseEntity<Object> serverException(ServerException e) {
        logger.error("服务器内部错误", e);
        return ResponseEntity.badRequest()
                .body(Result.error(null, e.getMessage()));
    }


    /**
     * 其它异常，这类异常基本代表着编码错误，或者有遗漏，如果是后者应当及时补全
     * <p>
     * TODO 考虑把这种错误持久化到数据库里？
     */
    @ExceptionHandler
    ResponseEntity<Object> handlerAllException(Throwable e) {
        logger.error("未被捕获到的异常：", e);
        if (debug) {
            return ResponseEntity
                    .internalServerError()
                    .body(Result.error(e, e.toString()));
        }

        return ResponseEntity
                .internalServerError()
                .body(Result.error(null, "服务器内部错误！如果您能够复现该问题的话，请发送电子邮件至 V-YOP@pm.me 联系开发者并附上复现的步骤，非常感谢！"));
    }
}
