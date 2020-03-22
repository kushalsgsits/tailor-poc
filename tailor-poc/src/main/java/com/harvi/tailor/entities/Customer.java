package com.harvi.tailor.entities;

import java.util.Map;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Customer {
	@Id
	private long mobileNum;
	private String fullName;
	private String emailID;
	/**
	 * For storing user preferences
	 */
	private String notes;
}
