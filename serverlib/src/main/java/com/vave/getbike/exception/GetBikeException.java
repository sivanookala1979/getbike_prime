package com.vave.getbike.exception;

/**
 * Created by sivanookala on 25/10/16.
 */

public class GetBikeException extends RuntimeException {
    Throwable rootCause;


    public GetBikeException(Throwable rootCause) {
        super(rootCause);
        this.rootCause = rootCause;
    }
}
