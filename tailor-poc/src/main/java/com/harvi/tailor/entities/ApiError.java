package com.harvi.tailor.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ApiError {

	private String shortErrorMsg;
	private String longErrorMsg;

	public ApiError() {

	}

	public ApiError(String shortErrorMsg, String longErrorMsg) {
		this.shortErrorMsg = shortErrorMsg;
		this.longErrorMsg = longErrorMsg;
	}

	public String getShortErrorMsg() {
		return shortErrorMsg;
	}

	public void setShortErrorMsg(String shortErrorMsg) {
		this.shortErrorMsg = shortErrorMsg;
	}

	public String getLongErrorMsg() {
		return longErrorMsg;
	}

	public void setLongErrorMsg(String longErrorMsg) {
		this.longErrorMsg = longErrorMsg;
	}

	@Override
	public String toString() {
		return "ApiError [shortErrorMsg=" + shortErrorMsg + ", longErrorMsg=" + longErrorMsg + "]";
	}
}
