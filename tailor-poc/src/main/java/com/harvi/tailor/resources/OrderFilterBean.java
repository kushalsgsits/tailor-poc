package com.harvi.tailor.resources;

import javax.ws.rs.QueryParam;

public class OrderFilterBean {

	private @QueryParam("mobileNum") long mobileNum;

	public long getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(long mobileNum) {
		this.mobileNum = mobileNum;
	}
}
