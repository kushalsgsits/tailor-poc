package com.harvi.tailor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.harvi.tailor.entities.Item;
import com.harvi.tailor.enums.ItemCategoryType;

public class ItemDao {

	private Map<String, Item> nameToItemMap = new HashMap<String, Item>();
	private static final ItemDao INSTANCE = new ItemDao();

	private ItemDao() {
		Item shirt = new Item("Shirt", 200, ItemCategoryType.SHIRT);
		Item pant = new Item("Pant", 300, ItemCategoryType.PANT);
		this.nameToItemMap.put("Shirt", shirt);
		this.nameToItemMap.put("Pant", pant);
	}

	public static ItemDao getInstance() {
		return INSTANCE;
	}

	public List<Item> getAll() {
		return this.nameToItemMap.values().stream().collect(Collectors.toList());
	}

	public Item get(String name) {
		return this.nameToItemMap.get(name);
	}
}
