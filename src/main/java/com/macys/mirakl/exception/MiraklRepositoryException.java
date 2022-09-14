package com.macys.mirakl.exception;

import lombok.Getter;


@Getter
public class MiraklRepositoryException extends Exception {

	private static final long serialVersionUID = 1L;
	
    private String detailMessage;

	public MiraklRepositoryException(String message) {
		super(message);
	}
}
