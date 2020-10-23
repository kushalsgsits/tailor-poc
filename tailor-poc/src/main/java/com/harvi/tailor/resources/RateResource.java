package com.harvi.tailor.resources;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.harvi.tailor.dao.RateDao;
import com.harvi.tailor.entities.Rate;
import com.harvi.tailor.entities.filterbeans.RateFilterBean;

@Path("/rates")
@Produces(MediaType.APPLICATION_JSON)
public class RateResource {

	private RateDao dao = RateDao.getInstance();

	@GET
	public Rate getAll(@BeanParam RateFilterBean rateFilterBean) {
		return dao.getFilteredRates(rateFilterBean);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Rate save(Rate rate) {
		return dao.save(rate);
	}

	@DELETE
	@Path("/{dateMillis}")
	public void delete(@PathParam("dateMillis") long dateMillis) {
		dao.delete(dateMillis);
	}
}
