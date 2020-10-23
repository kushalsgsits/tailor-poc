package com.harvi.tailor.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.harvi.tailor.entities.ApiError;
import com.harvi.tailor.entities.Rate;
import com.harvi.tailor.entities.filterbeans.RateFilterBean;
import com.harvi.tailor.utils.Utils;

public class RateDao {

	private static final Logger LOG = Logger.getLogger(RateDao.class.getName());

	private static final RateDao INSTANCE = new RateDao();

	private RateDao() {

	}

	public static RateDao getInstance() {
		return INSTANCE;
	}

	public Rate getFilteredRates(RateFilterBean rateFilterBean) {
		try {
			Query<Rate> query = ObjectifyService.ofy().load().type(Rate.class).orderKey(true).limit(1);
			List<Rate> result;

			long dateMillis = rateFilterBean.getDateMillis();
			if (dateMillis > 0) {
				Key<Rate> key = Key.create(Rate.class, dateMillis);
				query = query.filterKey("<=", key);
			}

			result = query.list();
			return result.isEmpty() ? null : result.get(0);
		} catch (Exception e) {
			String shortErrorMsg = "Could not fetch rate";
			ApiError apiError = Utils.createApiError(e, shortErrorMsg, LOG);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
			throw new WebApplicationException(response);
		}
	}

	public Rate save(Rate rate) {
		try {
			rate.setDateMillis(System.currentTimeMillis());
			saveRate(rate);
			return rate;
		} catch (WebApplicationException wae) {
			throw wae;
		} catch (Exception e) {
			String shortErrorMsg = "Could not save rate";
			ApiError apiError = Utils.createApiError(e, shortErrorMsg, LOG);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
			throw new WebApplicationException(response);
		}
	}

	public void delete(long dateMillis) {
		try {
			deleteRate(dateMillis);
		} catch (Exception e) {
			String shortErrorMsg = "Could not delete rate";
			ApiError apiError = Utils.createApiError(e, shortErrorMsg, LOG);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiError).build();
			throw new WebApplicationException(response);
		}
	}

	private static void saveRate(Rate rate) {
		ObjectifyService.ofy().save().entity(rate).now();
	}

	private static void deleteRate(long dateMillis) {
		ObjectifyService.ofy().delete().type(Rate.class).id(dateMillis).now();
	}
}
