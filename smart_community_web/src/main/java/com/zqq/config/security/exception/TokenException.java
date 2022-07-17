package com.zqq.config.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 自定义异常
 */
public class TokenException extends AuthenticationException {
    public TokenException(String msg) {
        super(msg);
    }
}