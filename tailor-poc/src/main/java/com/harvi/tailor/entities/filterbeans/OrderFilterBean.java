package com.harvi.tailor.entities.filterbeans;

import javax.ws.rs.QueryParam;

public class OrderFilterBean {

	private @QueryParam("deliveryStartDateMillis") long deliveryStartDate;
	private @QueryParam("deliveryEndDateMillis") long deliveryEndDate;
	private @QueryParam("itemCategory") String itemCategory;
	private @QueryParam("orderNumber") long orderNumber;
	private @QueryParam("name") String name;
	private @QueryParam("mobile") long mobile;

	public long getDeliveryStartDate() {
		return deliveryStartDate;
	}

	public void setDeliveryStartDate(long deliveryStartDate) {
		this.deliveryStartDate = deliveryStartDate;
	}

	public long getDeliveryEndDate() {
		return deliveryEndDate;
	}

	public void setDeliveryEndDate(long deliveryEndDate) {
		this.deliveryEndDate = deliveryEndDate;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	public long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMobile() {
		return mobile;
	}

	public void setMobile(long mobile) {
		this.mobile = mobile;
	}

}
