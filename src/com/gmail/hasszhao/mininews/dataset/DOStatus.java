package com.gmail.hasszhao.mininews.dataset;

public final class DOStatus {

	private final int Code;
	private final String Message;
	private final String Data;


	public DOStatus(int _code, String _message, String _data) {
		super();
		Code = _code;
		Message = _message;
		Data = _data;
	}


	public int getCode() {
		return Code;
	}


	public String getMessage() {
		return Message;
	}


	public String getData() {
		return Data;
	}
}
