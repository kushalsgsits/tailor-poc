package com.harvi.tailor.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ItemsGroup {

	private String groupName;
	private List<Item> groupItems;

	public ItemsGroup() {
	}

	public ItemsGroup(String groupName, List<Item> groupItems) {
		this.groupName = groupName;
		this.groupItems = groupItems;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<Item> getGroupItems() {
		return groupItems;
	}

	public void setGroupItems(List<Item> groupItems) {
		this.groupItems = groupItems;
	}

}
