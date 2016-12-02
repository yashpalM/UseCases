package com.zeyad.generic.usecase.dataaccesslayer.components.exceptions;

/**
 * @author zeyad on 11/30/16.
 */

public class ErrorBundle implements IErrorBundle {

    private Exception exception;

    public ErrorBundle(Exception exception) {
        this.exception = exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String getMessage() {
        return exception.getLocalizedMessage();
    }

    @Override
    public Exception getException() {
        return exception;
    }
}