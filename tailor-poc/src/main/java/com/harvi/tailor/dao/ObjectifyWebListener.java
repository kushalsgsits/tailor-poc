package com.harvi.tailor.dao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.harvi.tailor.entities.Item;
import com.harvi.tailor.entities.Order;
import com.harvi.tailor.entities.Rate;

@WebListener
public class ObjectifyWebListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			// Production server
			ObjectifyService.init();
		} else {
			// Local development server
			// which is: SystemProperty.Environment.Value.Development
			ObjectifyService.init(new ObjectifyFactory(DatastoreOptions.newBuilder().setHost("http://localhost:8081")
					.setProjectId("tailor-poc").build().getService()));
		}
		// This is a good place to register your POJO entity classes.
		ObjectifyService.register(Order.class);
		ObjectifyService.register(Item.class);
		ObjectifyService.register(Rate.class);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
}