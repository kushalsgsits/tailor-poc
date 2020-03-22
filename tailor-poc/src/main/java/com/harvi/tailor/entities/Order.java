package com.harvi.tailor.entities;

import java.util.Map;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.harvi.tailor.enums.OrderStatus;

@Entity
public class Order {
	@Id
	private long orderID;
	@Index
	private long mobileNum;
	@Index
	private long deliveryDateTime;
	private Map<String, Integer> itemToCountMap;
	private int discount;
	private int miscCharges;
	private int netBillAmount;
	/**
	 * For advance and partial payments
	 */
	private int paidAmount;
	@Index
	private OrderStatus status;
	private String notes;
	
}
