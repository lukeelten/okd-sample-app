package de.codecentric;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GreetingResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(
	    name = "hello_timer",
	    description = "description"
    )
    @Counted(
	    name = "hello_counter",
    	description = "description"
    )
    @Metered(
	    name = "hello_meter",
	    description = "description"
    )
    public String hello() {
        return "Hello OpenShift";
    }
}