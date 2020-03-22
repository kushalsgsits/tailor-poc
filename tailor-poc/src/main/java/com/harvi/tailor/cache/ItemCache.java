package com.harvi.tailor.cache;

import com.harvi.tailor.entities.Item;

public class ItemCache {

	private static final Cache<String, Item> CACHE = new CacheImpl<>(20);
	
	public Item get(String name) {
		return null;
	}
}
