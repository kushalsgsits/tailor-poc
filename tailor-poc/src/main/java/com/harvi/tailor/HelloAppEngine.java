package com.harvi.tailor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.ObjectifyService;
import com.harvi.tailor.entities.Person;

@WebServlet(
    name = "HelloAppEngine",
    urlPatterns = {"/hello"}
)
public class HelloAppEngine extends HttpServlet {

	private static int counter = 0;
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

	  counter++;
    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");

    response.getWriter().print("Hello App Engine from Git!\r\n");

//    testObjectify(response); 
  }
  
	private void testObjectify(HttpServletResponse response) throws IOException {
		try {
//    	List<Long> ids = new ArrayList<Long>();
//    	LongStream.range(1, 51).forEach(ids::add);
//		ObjectifyService.ofy().delete().type(Person.class).ids(ids).now();

			Person person = new Person("Person_" + counter, counter);
			ObjectifyService.ofy().save().entity(person).now();
			response.getWriter().print("Person saved; Person=" + person + "\r\n");
			List<Person> list = ObjectifyService.ofy().load().type(Person.class).order("-name").list();
			response.getWriter().print("Persons=" + list + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print("Failed to save Person; msg=" + e.getMessage() + ", trace="
					+ Arrays.toString(e.getStackTrace()) + "\r\n");
		}
}
}