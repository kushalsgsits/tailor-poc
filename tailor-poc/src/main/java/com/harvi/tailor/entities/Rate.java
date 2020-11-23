package com.harvi.tailor.entities;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@XmlRootElement
@Data
@NoArgsConstructor
public class Rate {

	@Id
	@Index
	private long dateMillis;

	private String[] itemIds;

	private int[] itemRates;

}
