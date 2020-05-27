package self.lcw01.miaosha.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import self.lcw01.miaosha.result.CodeMsg;
import self.lcw01.miaosha.result.Result;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

//该注解的作用主要要有三个：全局异常处理 全局数据绑定  全局数据预处理
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    //该注解的作用是定义要拦截的exception
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
        if (e instanceof GlobalException){
            GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCodeMsg());
        }
        if (e instanceof BindException){
            BindException ex = (BindException)e;
            //BindException里边可能会有多个error，这个方法可以全部都拿到
            List<ObjectError> errors = ex.getAllErrors();
            //取到错误列表中的第一个
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
