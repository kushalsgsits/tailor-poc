package com.harvi.tailor.entities;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@XmlRootElement
public class Order {
	@Id
	private String id;

	private String orderType;

	private long orderDateMillis;

	@Index
	private long deliveryDateMillis;

	@Index
	private int orderNumber;

	@Index
	private String name;

	@Index
	private long mobile;

	private String[] itemNames;

	private int[] itemCounts;

	private String notes;

	public Order() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public long getOrderDateMillis() {
		return orderDateMillis;
	}

	public void setOrderDateMillis(long orderDateMillis) {
		this.orderDateMillis = orderDateMillis;
	}

	public long getDeliveryDateMillis() {
		return deliveryDateMillis;
	}

	public void setDeliveryDateMillis(long deliveryDateMillis) {
		this.deliveryDateMillis = deliveryDateMillis;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
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

	public String[] getItemNames() {
		return itemNames;
	}

	public void setItemNames(String[] itemNames) {
		this.itemNames = itemNames;
	}

	public int[] getItemCounts() {
		return itemCounts;
	}

	public void setItemCounts(int[] itemCounts) {
		this.itemCounts = itemCounts;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", orderType=" + orderType + ", orderDateMillis=" + orderDateMillis
				+ ", deliveryDateMillis=" + deliveryDateMillis + ", orderNumber=" + orderNumber + ", name=" + name
				+ ", mobile=" + mobile + ", itemNames=" + Arrays.toString(itemNames) + ", itemCounts="
				+ Arrays.toString(itemCounts) + ", notes=" + notes + "]";
	}

}
