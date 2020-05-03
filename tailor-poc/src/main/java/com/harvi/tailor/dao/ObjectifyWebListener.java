package com.harvi.tailor.dao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.harvi.tailor.entities.Order;

@WebListener
public class ObjectifyWebListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ObjectifyService.init(new ObjectifyFactory(DatastoreOptions.newBuilder().setHost("http://localhost:8081")
				.setProjectId("tailor-poc").build().getService()));
//		ObjectifyService.init();
		// This is a good place to register your POJO entity classes.
		ObjectifyService.register(Order.class);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
}