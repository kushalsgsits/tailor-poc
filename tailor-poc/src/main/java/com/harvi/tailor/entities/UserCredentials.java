package com.harvi.tailor.entities;

import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@XmlRootElement
public class UserCredentials {
	@Id
	private String uname;
	private String pwd;

	public UserCredentials() {

	}

	public UserCredentials(String uname, String pwd) {
		this.uname = uname;
		this.pwd = pwd;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
