package com.harvi.tailor.entities;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@XmlRootElement
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Order {
	@Id
	private String id;

	private String orderType;

	private String orderStatus;

	private long orderDateMillis;

	@Index
	private long deliveryDateMillis;

	@Index
	private int orderNumber;

	@Index
	private String name;

	@Index
	private long mobile;

	private String[] itemIds;

	private int[] itemRates;

	private int[] itemCounts;

	private String notes;

}
