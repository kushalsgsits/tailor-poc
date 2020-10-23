package com.harvi.tailor.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.harvi.tailor.constants.ItemConstants.ItemType;
import com.harvi.tailor.constants.OrderStatus;
import com.harvi.tailor.entities.ApiError;
import com.harvi.tailor.entities.Item;
import com.harvi.tailor.entities.Order;
import com.harvi.tailor.entities.filterbeans.OrderFilterBean;
import com.harvi.tailor.utils.Utils;

public class OrderDao {

	private static final Logger LOG = Logger.getLogger(OrderDao.class.getName());

	private static final OrderDao INSTANCE = new OrderDao();

	private OrderDao() {

	}

	public static OrderDao getInstance() {
		return INSTANCE;
	}

	public List<Order> getFilteredOrders(OrderFilterBean orderFilterBean) {
		try {
			return getOrdersForFilterBean(orderFilterBean);
		} catch (Exception e) {
			String shortErrorMsg = "Could not fetch orders";
			ApiError apiError = Utils.createApiError(e, shortErrorMsg, LOG);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
			throw new WebApplicationException(response);
		}
	}

	public Order get(String id) {
		Exception exp = null;
		try {
			Order order = loadOrderById(id);
			if (null != order) {
				return order;
			}
		} catch (Exception e) {
			exp = e;
		}

		String shortErrorMsg = "Could not find order";
		ApiError apiError = Utils.createApiError(exp, shortErrorMsg, LOG);
		Response response = Response.status(Status.NOT_FOUND).entity(apiError).build();
		throw new WebApplicationException(response);
	}

	public Order save(Order order, UriInfo uriInfo) {
		try {
			order.setId(createId(order));
			order.setOrderStatus(OrderStatus.CREATED);
			LOG.fine("Saving Order: " + order);
			String id = order.getId();
			Order duplicateOrder = loadOrderById(id);
			if (null != duplicateOrder) {
				throwExceptionForDuplicateOrder(uriInfo, id, duplicateOrder);
			}
			saveOrder(order);
			return order;
		} catch (WebApplicationException wae) {
			throw wae;
		} catch (Exception e) {
			String shortErrorMsg = "Could not save order";
			ApiError apiError = Utils.createApiError(e, shortErrorMsg, LOG);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
			throw new WebApplicationException(response);
		}
	}

	public void delete(String id) {
		try {
			deletedOrder(id);
		} catch (Exception e) {
			String shortErrorMsg = "Could not delete order";
			ApiError apiError = Utils.createApiError(e, shortErrorMsg, LOG);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
			throw new WebApplicationException(response);
		}
	}

	public Order update(Order order) {
		try {
			LOG.fine("Updating Order: " + order);
			deletedOrder(order.getId());
			order.setId(createId(order));
			saveOrder(order);
			return order;
		} catch (Exception e) {
			String shortErrorMsg = "Could not update order";
			ApiError apiError = Utils.createApiError(e, shortErrorMsg, LOG);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
			throw new WebApplicationException(response);
		}
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

	private static String createId(Order order) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
		String orderDate = sdf.format(new Date(order.getOrderDateMillis()));

		StringBuilder idBuilder = new StringBuilder();
		idBuilder.append(order.getOrderType().charAt(0)).append("-").append(order.getOrderNumber()).append("-")
				.append(orderDate);
		String id = idBuilder.toString();
		return id;
	}

	private static List<Order> getOrdersForFilterBean(OrderFilterBean orderFilterBean) throws ParseException {

		Query<Order> baseQuery = ObjectifyService.ofy().load().type(Order.class);

		Filter compositeFilter = null;

		boolean hasOrderNum = orderFilterBean.getOrderNumber() > 0;
		boolean hasOrderStatus = orderFilterBean.getOrderStatus() != null
				&& orderFilterBean.getOrderStatus().trim().length() > 0;
		boolean hasMobile = orderFilterBean.getMobile() > 0;
		boolean hasName = orderFilterBean.getName() != null && orderFilterBean.getName().trim().length() > 0;
		boolean hasDeliveryStartDate = orderFilterBean.getDeliveryStartDate() > 0;
		boolean hasDeliveryEndDate = orderFilterBean.getDeliveryEndDate() > 0;
		boolean hasItemCategory = orderFilterBean.getItemCategory() != null
				&& orderFilterBean.getItemCategory().trim().length() > 0;

		String sortCondition;
		List<Order> result;

		if (hasOrderNum || hasMobile || hasName) {

			sortCondition = "-deliveryDateMillis";

			if (hasOrderNum) {
				PropertyFilter orderNumberFilter = PropertyFilter.eq("orderNumber", orderFilterBean.getOrderNumber());
				compositeFilter = null == compositeFilter ? orderNumberFilter
						: CompositeFilter.and(compositeFilter, orderNumberFilter);
			}

			if (hasMobile) {
				PropertyFilter mobileFilter = PropertyFilter.eq("mobile", orderFilterBean.getMobile());
				compositeFilter = null == compositeFilter ? mobileFilter
						: CompositeFilter.and(compositeFilter, mobileFilter);
			}

			if (hasName) {
				PropertyFilter nameStartFilter = PropertyFilter.ge("name", orderFilterBean.getName());
				PropertyFilter nameEndFilter = PropertyFilter.lt("name", orderFilterBean.getName() + "\uFFFD");
				compositeFilter = null == compositeFilter ? CompositeFilter.and(nameStartFilter, nameEndFilter)
						: CompositeFilter.and(compositeFilter, nameStartFilter, nameEndFilter);
				sortCondition = "name";
			}

			Query<Order> finalQuery = baseQuery.order(sortCondition).filter(compositeFilter);
			LOG.info("Final Query: " + finalQuery);
			result = finalQuery.list();

		} else {

			sortCondition = "deliveryDateMillis";

			long deliveryStartDate = 0;
			if (hasDeliveryStartDate) {
				deliveryStartDate = orderFilterBean.getDeliveryStartDate();
			} else if (!hasDeliveryEndDate) {
				deliveryStartDate = System.currentTimeMillis();
			}
			deliveryStartDate = getUpdatedDeliveryStartDate(deliveryStartDate);
			compositeFilter = PropertyFilter.ge("deliveryDateMillis", deliveryStartDate);

			if (hasDeliveryEndDate) {
				long deliveryEndDate = orderFilterBean.getDeliveryEndDate();
				deliveryEndDate = getUpdatedDeliveryEndTimestamp(deliveryEndDate);
				PropertyFilter deliveryEndDateFilter = PropertyFilter.le("deliveryDateMillis", deliveryEndDate);
				compositeFilter = null == compositeFilter ? deliveryEndDateFilter
						: CompositeFilter.and(compositeFilter, deliveryEndDateFilter);
			}

			Query<Order> finalQuery = baseQuery.order(sortCondition).filter(compositeFilter);
			LOG.info("Final Query: " + finalQuery);
			result = finalQuery.list();

			if (hasItemCategory) {
				List<String> itemCategories = Arrays.asList(orderFilterBean.getItemCategory().trim().split(","));
				result = result.stream().filter(order -> hasItemCategory(order, itemCategories))
						.collect(Collectors.toList());
			}
		}

		if (hasOrderStatus) {
			List<String> orderStatuses = Arrays.asList(orderFilterBean.getOrderStatus().trim().split(","));
			result = result.stream().filter(order -> orderStatuses.contains(order.getOrderStatus()))
					.collect(Collectors.toList());
		}

		return result;
	}

	private static boolean hasItemCategory(Order order, List<String> itemTypes) {
		Map<String, Item> itemIdToItemMap = ItemDao.getInstance().getItemIdToItemMap();
		return Arrays.stream(order.getItemIds()).map(itemId -> itemIdToItemMap.get(itemId))
				.map(item -> item.getType() == ItemType.COMBO ? item.getComboItemIds() : Arrays.asList(item.getId()))
				.flatMap(comboItemIds -> comboItemIds.stream()).map(comboItemId -> itemIdToItemMap.get(comboItemId))
				.map(Item::getType).anyMatch(itemTypes::contains);
	}

	private static long getUpdatedDeliveryStartDate(long deliveryStartDateMillis) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
		c.setTime(new Date(deliveryStartDateMillis));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime().getTime();
	}

	private static long getUpdatedDeliveryEndTimestamp(long deliveryEndDateMillis) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
		c.setTime(new Date(deliveryEndDateMillis));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime().getTime();
	}

	private void throwExceptionForDuplicateOrder(UriInfo uriInfo, String id, Order duplicateOrder) {
		String shortErrorMsg = "Could not save order as order with type: '" + duplicateOrder.getOrderType()
				+ "' and no.: '" + duplicateOrder.getOrderNumber() + "' already exits";
		String longErrorMsg = "Duplicate " + duplicateOrder.toString();
		LOG.severe(shortErrorMsg + ": " + longErrorMsg);
		ApiError apiError = new ApiError(shortErrorMsg, longErrorMsg);
		String duplicateOrderURIString = uriInfo.getAbsolutePathBuilder().build(id).toString();
		Response response = Response.status(Status.CONFLICT).entity(apiError)
				.header(HttpHeaders.LOCATION, duplicateOrderURIString).build();
		throw new WebApplicationException(response);
	}

}
