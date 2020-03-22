package com.harvi.tailor.entities;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.harvi.tailor.enums.ItemCategoryType;

@Entity
@XmlRootElement
public class Item {
	@Id
	private String name;
	private int rate;
	private ItemCategoryType type;

	public Item() {
	}

	public Item(String name, int rate, ItemCategoryType type) {
		this.name = name;
		this.rate = rate;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public ItemCategoryType getType() {
		return type;
	}

	public void setType(ItemCategoryType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Item [name=" + name + ", rate=" + rate + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + rate;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (rate != other.rate)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
