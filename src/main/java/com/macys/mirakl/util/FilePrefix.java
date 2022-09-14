package com.macys.mirakl.util;

public enum FilePrefix {

	MCOM("12"), BCOM("13");

	private String opDiv;

	private FilePrefix(String opDiv) {
		this.opDiv = opDiv;
	}

	public String getOpDiv() {
		return opDiv;
	}

}
