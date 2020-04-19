package com.harvi.tailor.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;
import com.harvi.tailor.auth.AuthUtil;
import com.harvi.tailor.entities.JwtToken;
import com.harvi.tailor.entities.UserCredentials;

@Path("login")
@Consumes(MediaType.APPLICATION_JSON)
public class JwtResource {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JwtToken create(UserCredentials user) {
		if (AuthUtil.validateUserCredentials(user)) {
			return AuthUtil.createJWT("ID", "Gujrati Tailors", user.getUname(), 1 * 60 * 60 * 1000);
		}
		// TODO login failure
		return null;
	}

}
