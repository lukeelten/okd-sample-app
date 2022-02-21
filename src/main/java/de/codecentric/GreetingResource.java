package de.codecentric;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

@Path("/")
public class GreetingResource {

    private final AtomicReference<String> namespace = new AtomicReference<>("");

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


    @GET
    @Path("/id")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(
            name = "id_timer",
            description = "description"
    )
    @Counted(
            name = "id_counter",
            description = "description"
    )
    @Metered(
            name = "id_meter",
            description = "description"
    )
    public String id() {
        String hostname = System.getenv("HOSTNAME");
        String namespace = getNamespace();
        return namespace + '/' + hostname;
    }


    private String getNamespace() {
        String ns = namespace.get();

        if (ns == null || ns.isBlank()) {
            File nsFile = new File("/var/run/secrets/kubernetes.io/serviceaccount/namespace");
            try (Scanner scanner = new Scanner(nsFile)) {
                ns = scanner.nextLine();
                namespace.set(ns);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        return ns;
    }
}