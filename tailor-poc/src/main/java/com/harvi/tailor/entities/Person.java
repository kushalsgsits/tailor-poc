package com.harvi.tailor.entities;

import java.time.LocalDateTime;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;

@Entity
public class Person {
	@Id
	public Long id;
	@Index
	public String name;
	@Index
	@Serialize
	public LocalDateTime date;
	
	public Person() {
	}

	public Person(String name, long id) {
		this.name = name;
		date = LocalDateTime.now();
//		this.id = id;
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", name=" + name + ", date=" + date + "]";
	}
}
