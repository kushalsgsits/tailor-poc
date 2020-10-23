package com.harvi.tailor.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@XmlRootElement
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Item {

	@Id
	@Index
	private String id;
	private String name;
	private String groupName;
	private String type;
	private List<String> comboItemIds;
	@Ignore
	private int rate;

	public Item(String id, String name, String groupName, String type, List<String> comboItemIds) {
		this.id = id;
		this.name = name;
		this.groupName = groupName;
		this.type = type;
		this.comboItemIds = comboItemIds;
	}
}
