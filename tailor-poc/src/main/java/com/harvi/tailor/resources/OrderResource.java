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
	public List<Order> getAll(@BeanParam OrderFilterBean bean) {
		if (bean.getMobileNum() > 0) {
//			return dao.getAllByMobileNum();
		}
		return dao.getAll();
	}

	@GET
	@Path("/{orderID}")
	public Order get(@PathParam("orderID") String orderID) {
		return dao.get(orderID);
	}
	
	/*@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Order create(Order order) {
		return dao.create(order);
	}
	
	@PUT
	@Path("/{orderID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Item update(@PathParam("orderID") String orderID, Order order) {
		orderID.setOrderID(orderID);
		return dao.update(order);
	}
	
	@DELETE
	@Path("/{orderID}")
	public void delete(@PathParam("orderID") String orderID) {
		return dao.delete(orderID);
	}*/
}
