package com.cloud.mall.cloudmember.exception;

public class UserExistException extends RuntimeException {
    public UserExistException() {
        super("该用户名已存在");
    }
}
