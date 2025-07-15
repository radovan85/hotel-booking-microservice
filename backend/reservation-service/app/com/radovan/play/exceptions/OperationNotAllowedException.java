package com.radovan.play.exceptions;

public class OperationNotAllowedException extends IllegalStateException{

    public OperationNotAllowedException(String message) {
        super(message);
    }
}
