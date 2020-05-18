package com.harvi.tailor.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.harvi.tailor.entities.ApiError;
import com.harvi.tailor.entities.Order;
import com.harvi.tailor.entities.filterbeans.OrderFilterBean;

public class OrderDao {

	private static final Logger LOG = Logger.getLogger(OrderDao.class.getName());

	private static final OrderDao INSTANCE = new OrderDao();

	private static Map<String, List<String>> ITEM_NAME_TO_ITEM_CATEGORIES_MAP = new HashMap<>();

	static {
		// All item categories:
		// 'Coat', 'Shirt', 'Pant', 'Kurta', 'Payjama', 'Jacket', 'Safari Shirt',
		// 'Others'
		// All item names:
		// '2 Piece Suit', '3 Piece Suit', 'Blazer', 'Achkan', 'Shirt', 'Pant', 'Jeans',
		// 'Kurta', 'Payjama', 'Pant Payjama', 'Pathani', 'Kurti', 'Jacket', 'Safari',
		// 'Waist Coat', 'Others'
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("2 Piece Suit", Arrays.asList("Coat", "Pant"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("3 Piece Suit", Arrays.asList("Coat", "Pant", "Jacket"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Blazer", Arrays.asList("Coat"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Achkan", Arrays.asList("Coat"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Shirt", Arrays.asList("Shirt"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Pant", Arrays.asList("Pant"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Jeans", Arrays.asList("Pant"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Kurti", Arrays.asList("Shirt"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Kurta", Arrays.asList("Kurta"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Payjama", Arrays.asList("Payjama"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Pant Payjama", Arrays.asList("Pant"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Pathani", Arrays.asList("Kurta"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Jacket", Arrays.asList("Jacket"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Waist Coat", Arrays.asList("Jacket"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Safari", Arrays.asList("Safari Shirt", "Pant"));
		ITEM_NAME_TO_ITEM_CATEGORIES_MAP.put("Others", Arrays.asList("Others"));
	}

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
			ApiError apiError = createApiError(e, shortErrorMsg);
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
		ApiError apiError = createApiError(exp, shortErrorMsg);
		Response response = Response.status(Status.NOT_FOUND).entity(apiError).build();
		throw new WebApplicationException(response);
	}

	public Order save(Order order, UriInfo uriInfo) {
		try {
			order.setId(createId(order));
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
			ApiError apiError = createApiError(e, shortErrorMsg);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
			throw new WebApplicationException(response);
		}
	}

	public void delete(String id) {
		try {
			deletedOrder(id);
		} catch (Exception e) {
			String shortErrorMsg = "Could not delete order";
			ApiError apiError = createApiError(e, shortErrorMsg);
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
			ApiError apiError = createApiError(e, shortErrorMsg);
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
		boolean hasMobile = orderFilterBean.getMobile() > 0;
		boolean hasName = orderFilterBean.getName() != null && orderFilterBean.getName().trim().length() > 0;
		boolean hasDeliveryStartDate = orderFilterBean.getDeliveryStartDate() > 0;
		boolean hasDeliveryEndDate = orderFilterBean.getDeliveryEndDate() > 0;
		boolean hasItemCategory = orderFilterBean.getItemCategory() != null
				&& orderFilterBean.getItemCategory().trim().length() > 0;

		String sortCondition;

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
			return finalQuery.list();

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
			List<Order> result = finalQuery.list();

			if (hasItemCategory) {
				List<String> itemCategories = Arrays.asList(orderFilterBean.getItemCategory().trim().split(","));
				result = result.stream().filter(order -> hasItemCategory(order, itemCategories))
						.collect(Collectors.toList());
			}

			return result;
		}
	}

	private static boolean hasItemCategory(Order order, List<String> itemCategories) {
		return Arrays.stream(order.getItemNames()).map(ITEM_NAME_TO_ITEM_CATEGORIES_MAP::get)
				.flatMap(catNames -> catNames.stream()).anyMatch(itemCategories::contains);
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

	private ApiError createApiError(Exception e, String shortErrorMsg) {
		String longErrorMsg = e == null ? ""
				: "ExceptionMsg: " + e.getMessage() + "\nStackTrace: " + Arrays.toString(e.getStackTrace());
		LOG.severe(shortErrorMsg + (e == null ? "" : (": " + longErrorMsg)));
		ApiError apiError = new ApiError(shortErrorMsg, longErrorMsg);
		return apiError;
	}

	private void throwExceptionForDuplicateOrder(UriInfo uriInfo, String id, Order duplicateOrder) {
		String shortErrorMsg = "Could not save order as order placed is duplicate";
		String longErrorMsg = "Duplicate " + duplicateOrder.toString();
		LOG.severe(shortErrorMsg + ": " + longErrorMsg);
		ApiError apiError = new ApiError(shortErrorMsg, longErrorMsg);
		String duplicateOrderURIString = uriInfo.getAbsolutePathBuilder().build(id).toString();
		Response response = Response.status(Status.CONFLICT).entity(apiError)
				.header(HttpHeaders.LOCATION, duplicateOrderURIString).build();
		throw new WebApplicationException(response);
	}

}
