package com.harvi.tailor.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ClientResponseFilter {

	private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		LOG.info("Header of request: " + requestContext.getHeaders());
	}

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		LOG.info("Header of request: " + responseContext.getHeaders());
	}

}
