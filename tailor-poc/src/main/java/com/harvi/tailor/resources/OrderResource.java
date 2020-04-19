package com.harvi.tailor.resources;

import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.harvi.tailor.dao.ItemDao;
import com.harvi.tailor.dao.OrderDao;
import com.harvi.tailor.entities.Item;
import com.harvi.tailor.entities.Order;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource implements RestMethods<Order> {

	private OrderDao dao = OrderDao.getInstance();

	@GET
	public List<Order> getAll(/* @BeanParam OrderFilterBean bean */) {
//		if (bean.getMobileNum() > 0) {
//			return dao.getAllByMobileNum();
//		}
		return dao.getRecentOrders();
	}

	@GET
	@Path("/{id}")
	public Order get(@PathParam("id") String id) {
		return dao.get(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Order save(Order order) {
		return dao.save(order);
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Order update(@PathParam("id") String id, Order order) {
		order.setId(id);
		return dao.update(order);
	}

	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") String id) {
		dao.delete(id);
	}
}
