package com.harvi.tailor.dao;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;
import com.google.appengine.repackaged.org.joda.time.format.ISODateTimeFormat;
import com.google.cloud.Timestamp;
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

	private OrderDao() {

	}

	public static OrderDao getInstance() {
		return INSTANCE;
	}

	public List<Order> getRecentOrders(OrderFilterBean orderFilterBean) {
		Query<Order> baseLoadQuery = ObjectifyService.ofy().load().type(Order.class).order("-deliveryDate").limit(30);
		Filter filter = createFilterFromOrderFilterBean(orderFilterBean);
		if (filter != null) {
			return baseLoadQuery.filter(filter).list();
		}
		return baseLoadQuery.list();
	}

	public Order get(String id) {
		Order order = null;
		String shortErrorMsg = null;
		String longErrorMsg = null;
		try {
			order = loadOrderById(id);
		} catch (Exception e) {
			shortErrorMsg = "Order with id=" + id + " not found";
			longErrorMsg = Arrays.toString(e.getStackTrace());
			LOG.severe(shortErrorMsg + ": " + longErrorMsg);
		}
		if (order == null) {
			ApiError apiError = new ApiError(shortErrorMsg, longErrorMsg);
			Response response = Response.status(Status.NOT_FOUND).entity(apiError).build();
			throw new WebApplicationException(response);
		}
		return order;
	}

	public Order save(Order order, UriInfo uriInfo) {
		order.setId(createId(order));
		LOG.fine("Saving Order: " + order);
		String id = order.getId();
		Order duplicateOrder = loadOrderById(id);
		if (null != duplicateOrder) {
			String shortErrorMsg = "Order with id=" + id + " already exists";
			String longErrorMsg = "Duplicate " + duplicateOrder.toString();
			LOG.severe(shortErrorMsg + ": " + longErrorMsg);
			ApiError apiError = new ApiError(shortErrorMsg, longErrorMsg);
			String duplicateOrderURIString = uriInfo.getAbsolutePathBuilder().build(id).toString();
			Response response = Response.status(Status.CONFLICT).entity(apiError)
					.header(HttpHeaders.LOCATION, duplicateOrderURIString).build();
			throw new WebApplicationException(response);
		}
		saveOrder(order);
		return order;
	}

	public void delete(String id) {
		deletedOrder(id);
	}

	private static String createId(Order order) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
		String orderDate = sdf.format(order.getOrderDate());

		StringBuilder idBuilder = new StringBuilder();
		idBuilder.append(order.getOrderType().charAt(0)).append("-").append(order.getOrderNumber()).append("-")
				.append(orderDate);
		String id = idBuilder.toString();
		return id;
	}

	public Order update(Order order) {
		LOG.fine("Updating Order: " + order);
		deletedOrder(order.getId());
		order.setId(createId(order));
		saveOrder(order);
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

	private static Filter createFilterFromOrderFilterBean(OrderFilterBean orderFilterBean) {
		Filter compositeFilter = null;

		if (orderFilterBean.getDeliveryStartDate() != null && orderFilterBean.getDeliveryStartDate().length() > 0) {
			Date deliveryStartDate = getDateFromIsoStr(orderFilterBean.getDeliveryStartDate());
			compositeFilter = PropertyFilter.ge("deliveryDate", getDeliveryStartTimestamp(deliveryStartDate));
		}

		if (orderFilterBean.getDeliveryEndDate() != null && orderFilterBean.getDeliveryEndDate().length() > 0) {
			Date deliveryEndDate = getDateFromIsoStr(orderFilterBean.getDeliveryEndDate());
			PropertyFilter deliveryEndDateFilter = PropertyFilter.le("deliveryDate",
					getDeliveryEndTimestamp(deliveryEndDate));
			compositeFilter = null == compositeFilter ? deliveryEndDateFilter
					: CompositeFilter.and(compositeFilter, deliveryEndDateFilter);
		}

//		if (orderFilterBean.getItemCategory() != null && orderFilterBean.getItemCategory().trim().length() > 0
//				&& !orderFilterBean.getItemCategory().contains("Coat")) {
//			PropertyFilter orderTypeFilter = PropertyFilter.eq("orderType", "Regular");
//			compositeFilter = null == compositeFilter ? orderTypeFilter
//					: CompositeFilter.and(compositeFilter, orderTypeFilter);
//		}

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

	private static Timestamp getDeliveryStartTimestamp(Date deliveryStartDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(deliveryStartDate);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return Timestamp.of(c.getTime());
	}

	private static Timestamp getDeliveryEndTimestamp(Date deliveryEndDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(deliveryEndDate);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return Timestamp.of(c.getTime());
	}

	public static void main(String[] args) {
		Date d = getDateFromIsoStr("2020-04-06T18:30:00.000Z");
		System.out.println("d=" + d);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
		System.out.println(sdf.format(d));

//		Timestamp st = getDeliveryStartTimestamp(deliveryStartDate);
//		System.out.println("t=" + st);
//
//		Timestamp et = getDeliveryEndTimestamp(deliveryStartDate);
//		System.out.println("t=" + et);
//
//		PropertyFilter deliveryStartDateFilter = PropertyFilter.ge("deliveryDate", st);
//		PropertyFilter deliveryEndDateFilter = PropertyFilter.le("deliveryDate", et);
//
//		CompositeFilter compositeFilter = CompositeFilter.and(deliveryStartDateFilter, deliveryEndDateFilter);
//		System.out.println(compositeFilter);
	}

	private static Date getDateFromIsoStr(String iso8601date) {
		DateTimeFormatter jodaParser = ISODateTimeFormat.dateTime();
		return jodaParser.parseDateTime(iso8601date).toDate();
	}
}
