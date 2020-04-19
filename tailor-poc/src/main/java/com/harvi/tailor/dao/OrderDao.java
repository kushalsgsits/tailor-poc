package com.harvi.tailor.dao;

import java.text.SimpleDateFormat;
import java.util.List;

import com.googlecode.objectify.ObjectifyService;
import com.harvi.tailor.entities.Order;

public class OrderDao {

	private static final OrderDao INSTANCE = new OrderDao();

	private OrderDao() {

	}

	public static OrderDao getInstance() {
		return INSTANCE;
	}

	public List<Order> getRecentOrders() {
		return ObjectifyService.ofy().load().type(Order.class).list();
	}

	public Order get(String id) {
		return loadOrderById(id);
	}

	public Order save(Order order) {
		order.setId(createId(order));
		Order duplicateOrder = loadOrderById(order.getId());
		if (null != duplicateOrder) {
			// TODO
			throw new IllegalArgumentException("Duplicate Order: " + order);
		}
		System.out.println("Saving Order: " + order);
		saveOrder(order);
		return order;
	}

	public void delete(String id) {
		deletedOrder(id);
	}

	private static String createId(Order order) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMMdd");
		String orderDate = sdf.format(order.getOrderDate());

		StringBuilder idBuilder = new StringBuilder();
		idBuilder.append(order.getOrderType()).append("-").append(orderDate).append("-").append(order.getOrderNumber());
		String id = idBuilder.toString();
		return id;
	}

	public Order update(Order order) {
		System.out.println("Updating Order: " + order);
		deletedOrder(order.getId());
		order.setId(createId(order));
		saveOrder(order);
		System.out.println("Updated Order: " + order);
		return order;
	}

	private static void saveOrder(Order order) {
		ObjectifyService.ofy().save().entity(order).now();
	}

	private static Order loadOrderById(String id) {
		return ObjectifyService.ofy().load().type(Order.class).id(id).now();
	}

	private static void deletedOrder(String id) {
		ObjectifyService.ofy().delete().type(Order.class).id(id).now();
	}

}
