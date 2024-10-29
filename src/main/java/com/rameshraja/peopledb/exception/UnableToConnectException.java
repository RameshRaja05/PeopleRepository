package com.rameshraja.peopledb.exception;

public class UnableToConnectException extends RuntimeException{

    public UnableToConnectException(String msg) {
        super(msg);
    }

    public UnableToConnectException(String msg,Throwable cause){
        super(msg,cause);
    }
}
