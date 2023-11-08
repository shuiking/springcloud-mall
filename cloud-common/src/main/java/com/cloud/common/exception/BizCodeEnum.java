package com.cloud.common.exception;

public enum BizCodeEnum {

    UNKNOW_EXEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION( 10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    USER_EXIST_EXCEPTION(15001,"存在相同的用户"),
    PHONE_EXIST_EXCEPTION(15002,"存在相同的手机号"),
    READ_TIME_OUT_EXCEPTION(10004,"远程调用服务超时，请重新再试"),
    NO_STOCK_EXCEPTION(21000,"商品库存不足"),
    LOGINACCT_PASSWORD_EXCEPTION(15003,"账号或密码错误"),
    SMS_CODE_EXCEPTION(10003,"验证码获取频率太高，请稍后再试");
    private int code;
    private String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
