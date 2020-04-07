package exception;

import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice       //控制层全局异常处理器，只要在方法上加了一个@RequestMapping注解，所有的异常都会被捕获
public class BaseExceptionHandler {

    /***
     * 异常处理
     * @param e
     * @ExceptionHandler 异常处理对象
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }
}
