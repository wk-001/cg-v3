package entity;

/**
 * 返回码
 */
public interface StatusCode {
    Integer OK = 20000;//成功
    Integer ERROR = 20001;//失败
    Integer LOGINERROR = 20002;//用户名或密码错误
    Integer ACCESSERROR = 20003;//权限不足
    Integer REMOTEERROR = 20004;//远程调用失败
    Integer REPERROR = 20005;//重复操作
    Integer NOTFOUNDERROR = 20006;//没有对应的抢购数据
}
