package com.harvi.tailor.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.harvi.tailor.auth.AuthUtil;
import com.harvi.tailor.entities.ApiError;
import com.harvi.tailor.entities.JwtToken;
import com.harvi.tailor.entities.UserCredentials;

@Path("login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JwtResource {

	@POST
	public JwtToken create(UserCredentials user) {
		if (AuthUtil.validateUserCredentials(user)) {
			int millisFor24Hours = 24 * 60 * 60 * 1000;
			return AuthUtil.createJWT("Kushal", user.getUname(), millisFor24Hours);
		}
		ApiError apiError = new ApiError("Either username or password is incorrect", "");
		Response response = Response.status(Status.UNAUTHORIZED).entity(apiError).build();
		throw new WebApplicationException(response);
	}

}
