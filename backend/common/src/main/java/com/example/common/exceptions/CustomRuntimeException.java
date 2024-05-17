package com.example.common.exceptions;

import com.example.common.enums.CommonStatusEnum;
import com.example.common.interfaces.IStatusCode;

public class CustomRuntimeException extends RuntimeException {

    private final IStatusCode statusCode;

    public CustomRuntimeException(IStatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
    }

    public CustomRuntimeException(IStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public CustomRuntimeException(String message) {
        super(message);
        this.statusCode = CommonStatusEnum.ERROR;
    }

    public IStatusCode getStatusCode() {
        return statusCode;
    }
}
