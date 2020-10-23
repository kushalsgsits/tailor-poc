package com.harvi.tailor.resources;

import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.harvi.tailor.dao.ItemDao;
import com.harvi.tailor.entities.ItemsGroup;
import com.harvi.tailor.entities.filterbeans.RateFilterBean;

@Path("/grouped-items")
@Produces(MediaType.APPLICATION_JSON)
public class GroupedItemsResource {

	private ItemDao dao = ItemDao.getInstance();

	@GET
	public List<ItemsGroup> getAll(@BeanParam RateFilterBean rateFilterBean) {
		return dao.getGroupedItemsWithRates(rateFilterBean);
	}
}
