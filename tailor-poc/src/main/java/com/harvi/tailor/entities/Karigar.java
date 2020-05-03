package com.harvi.tailor.entities;

import java.util.Map;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Karigar {
	@Id
	private long mobile;
	private String name;
	private Map<String, Integer> itemToRateMap;
	
}
