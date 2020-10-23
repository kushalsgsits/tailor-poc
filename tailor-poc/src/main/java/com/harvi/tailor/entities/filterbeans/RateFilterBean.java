package com.harvi.tailor.entities.filterbeans;

import javax.ws.rs.QueryParam;

public class RateFilterBean {
	private @QueryParam("dateMillis") long dateMillis;

	public long getDateMillis() {
		return dateMillis;
	}

	public void setDateMillis(long dateMillis) {
		this.dateMillis = dateMillis;
	}
}
