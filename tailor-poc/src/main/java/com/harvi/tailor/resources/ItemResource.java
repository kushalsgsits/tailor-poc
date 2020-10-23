package com.harvi.tailor.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
		return dao.getAllItems();
	}

//	@GET
//	@Path("/{id}")
//	public Item get(@PathParam("id") String id) {
//		return dao.get(id);
//	}
//
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Item save(Item item, @Context UriInfo uriInfo) {
//		return dao.save(item, uriInfo);
//	}
//
//	@PUT
//	@Path("/{id}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Item update(@PathParam("id") String id, Item item) {
//		item.setId(id);
//		return dao.update(item);
//	}
//
//	@DELETE
//	@Path("/{id}")
//	public void delete(@PathParam("id") String id) {
//		dao.delete(id);
//	}
}
