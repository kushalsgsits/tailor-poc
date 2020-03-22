package com.harvi.tailor.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.harvi.tailor.entities.Item;
import com.harvi.tailor.entities.Order;
import com.harvi.tailor.enums.ItemCategoryType;

public class OrderDao {

	private Map<String, Order> nameToItemMap = new HashMap<>();
	private static final OrderDao INSTANCE = new OrderDao();

	private OrderDao() {

	}

	public static OrderDao getInstance() {
		return INSTANCE;
	}

	public List<Order> getAll() {
		return this.nameToItemMap.values().stream().collect(Collectors.toList());
	}

	public Order get(String name) {
		return this.nameToItemMap.get(name);
	}
}
