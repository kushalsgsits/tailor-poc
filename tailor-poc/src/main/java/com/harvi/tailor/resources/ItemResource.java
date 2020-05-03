package com.harvi.tailor.resources;

import java.util.List;

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
import com.harvi.tailor.entities.Item;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
public class ItemResource {

	private ItemDao dao = ItemDao.getInstance();

	@GET
	public List<Item> getAll() {
		return dao.getAll();
	}

	@GET
	@Path("/{name}")
	public Item get(@PathParam("name") String name) {
		return dao.get(name);
	}
	
	/*@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Item create(Item item) {
		return dao.create(item);
	}
	
	@PUT
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Item update(@PathParam("name") String name, Item item) {
		item.setName(name);
		return dao.update(item);
	}
	
	@DELETE
	@Path("/{name}")
	public void delete(@PathParam("name") String name) {
		return dao.delete(name);
	}*/
}
