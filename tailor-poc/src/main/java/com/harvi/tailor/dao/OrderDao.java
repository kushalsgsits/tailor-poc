package com.harvi.tailor.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

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

	private static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private OrderDao() {

	}

	public static OrderDao getInstance() {
		return INSTANCE;
	}

	public List<Order> getFilteredOrders(OrderFilterBean orderFilterBean) {
		try {
			Query<Order> baseLoadQuery = ObjectifyService.ofy().load().type(Order.class).order("deliveryDate")
					.limit(30);
			Filter filter = createFilterFromOrderFilterBean(orderFilterBean);
			if (filter != null) {
				return baseLoadQuery.filter(filter).list();
			}
			return baseLoadQuery.list();
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

		String shortErrorMsg = "Could not find orders";
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
		String orderDate = sdf.format(toDateFromISO8601UTC(order.getOrderDate()));

		StringBuilder idBuilder = new StringBuilder();
		idBuilder.append(order.getOrderType().charAt(0)).append("-").append(order.getOrderNumber()).append("-")
				.append(orderDate);
		String id = idBuilder.toString();
		return id;
	}

	private static Filter createFilterFromOrderFilterBean(OrderFilterBean orderFilterBean) throws ParseException {
		Filter compositeFilter = null;

		boolean hasDeliveryStartDate = orderFilterBean.getDeliveryStartDate() != null
				&& orderFilterBean.getDeliveryStartDate().length() > 0;
		boolean hasDeliveryEndDate = orderFilterBean.getDeliveryEndDate() != null
				&& orderFilterBean.getDeliveryEndDate().length() > 0;

		Date deliveryStartDate = null;
		if (hasDeliveryStartDate) {
			deliveryStartDate = toDateFromISO8601UTC(orderFilterBean.getDeliveryStartDate());
		} else if (!hasDeliveryEndDate) {
			deliveryStartDate = new Date();
		}
		deliveryStartDate = getUpdatedDeliveryStartDate(deliveryStartDate);
		compositeFilter = PropertyFilter.ge("deliveryDate", toISO8601UTCFromDate(deliveryStartDate));

		if (hasDeliveryEndDate) {
			Date deliveryEndDate = toDateFromISO8601UTC(orderFilterBean.getDeliveryEndDate());
			deliveryEndDate = getUpdatedDeliveryEndTimestamp(deliveryEndDate);
			PropertyFilter deliveryEndDateFilter = PropertyFilter.le("deliveryDate",
					toISO8601UTCFromDate(deliveryEndDate));
			compositeFilter = null == compositeFilter ? deliveryEndDateFilter
					: CompositeFilter.and(compositeFilter, deliveryEndDateFilter);
		}

		if (orderFilterBean.getOrderNumber() > 0) {
			PropertyFilter orderNumberFilter = PropertyFilter.eq("orderNumber", orderFilterBean.getOrderNumber());
			compositeFilter = null == compositeFilter ? orderNumberFilter
					: CompositeFilter.and(compositeFilter, orderNumberFilter);
		}

		if (orderFilterBean.getMobile() > 0) {
			PropertyFilter mobileFilter = PropertyFilter.eq("mobile", orderFilterBean.getMobile());
			compositeFilter = null == compositeFilter ? mobileFilter
					: CompositeFilter.and(compositeFilter, mobileFilter);
		}

		if (orderFilterBean.getName() != null && orderFilterBean.getName().trim().length() > 0) {
			PropertyFilter nameStartFilter = PropertyFilter.ge("name", orderFilterBean.getName());
			PropertyFilter nameEndFilter = PropertyFilter.lt("name", orderFilterBean.getName() + "\uFFFD");
			compositeFilter = null == compositeFilter ? CompositeFilter.and(nameStartFilter, nameEndFilter)
					: CompositeFilter.and(compositeFilter, nameStartFilter, nameEndFilter);
		}

		return compositeFilter;
	}

	private static Date getUpdatedDeliveryStartDate(Date deliveryStartDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(deliveryStartDate);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	private static Date getUpdatedDeliveryEndTimestamp(Date deliveryEndDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(deliveryEndDate);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

//	private static Date toDateFromISO8601UTC(String iso8601date) {
//		DateTimeFormatter jodaParser = ISODateTimeFormat.dateTime();
//		return jodaParser.parseDateTime(iso8601date).toDate();
//	}

	private static DateFormat getISO8601UTCDateFormat() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat(ISO_DATE_FORMAT);
		df.setTimeZone(tz);
		return df;
	}

	private static String toISO8601UTCFromDate(Date date) {
		DateFormat df = getISO8601UTCDateFormat();
		return df.format(date);
	}

	private static Date toDateFromISO8601UTC(String dateStr) throws ParseException {
		DateFormat df = getISO8601UTCDateFormat();
		return df.parse(dateStr);
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
