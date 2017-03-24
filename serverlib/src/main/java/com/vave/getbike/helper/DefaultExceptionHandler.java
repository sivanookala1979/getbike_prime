package com.vave.getbike.helper;


import com.vave.getbike.exception.GetBikeException;

public class DefaultExceptionHandler implements ExceptionHandler {

	@Override
	public void handleException(Exception rootCause) {
		rootCause.printStackTrace();
		throw new GetBikeException(rootCause);
	}
}
