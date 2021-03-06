package de.codecentric;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Path("/")
public class GreetingResource {
    private static final Logger logger = Logger.getLogger(GreetingResource.class);
    private final AtomicReference<String> namespace = new AtomicReference<>("");

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry metricRegistry;

    private final String staticFilepath;

    public GreetingResource() {
        String path = System.getenv("STATIC_FILE_PATH");
        if (path == null || path.isBlank()) {
            staticFilepath = "/www";
        } else {
            staticFilepath = path;
        }
    }

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

    @GET
    @Path("/memory")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(
            name = "memory_timer",
            description = "description"
    )
    @Counted(
            name = "memory_counter",
            description = "description"
    )
    @Metered(
            name = "memory_meter",
            description = "description"
    )
    public String memory() {
        List<String> l = new LinkedList<>();
        for (int i = 0; i < 1000000; i++) {
            String t = "Test " + Integer.toString(i);
            l.add(t);
        }

        l.sort(Comparator.naturalOrder());

        return l.get(0);
    }

    @GET
    @Path("/compute")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(
            name = "compute_timer",
            description = "description"
    )
    @Counted(
            name = "compute_counter",
            description = "description"
    )
    @Metered(
            name = "compute_meter",
            description = "description"
    )
    public String compute() {
        Random random = new Random();
        int i = random.nextInt() * 278952125;
        i %= 1000000;

        List<Integer> primes = sieveOfEratosthenes(i);
        return "Prime " + primes.get(5);
    }

    private static List<Integer> sieveOfEratosthenes(int n) {
        boolean prime[] = new boolean[n + 1];
        Arrays.fill(prime, true);
        for (int p = 2; p * p <= n; p++) {
            if (prime[p]) {
                for (int i = p * 2; i <= n; i += p) {
                    prime[i] = false;
                }
            }
        }
        List<Integer> primeNumbers = new LinkedList<>();
        for (int i = 2; i <= n; i++) {
            if (prime[i]) {
                primeNumbers.add(i);
            }
        }
        return primeNumbers;
    }


    @GET()
    @Path("/files/")
    @Produces()
    @Timed(
            name = "file_timer",
            description = "description"
    )
    @Counted(
            name = "file_counter",
            description = "description"
    )
    @Metered(
            name = "file_meter",
            description = "description"
    )
    public Response fileIndex() {
        return files("index.html");
    }


    @GET()
    @Path("/files/{filename}")
    @Produces()
    @Timed(
            name = "files_timer",
            description = "description"
    )
    @Counted(
            name = "files_counter",
            description = "description"
    )
    @Metered(
            name = "files_meter",
            description = "description"
    )
    public Response files(@PathParam("filename") String filename) {
        if (filename.contains("..")) {
            return Response.serverError().build();
        }

        logger.info("Serving file: " + filename);
        metricRegistry.counter("files", new Tag("file", filename)).inc();

        File file = new File(staticFilepath + "/" + filename);
        if (!file.exists() || !file.isFile()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            String content = Files.readString(file.toPath());

            Response.ResponseBuilder res = Response.status(Response.Status.OK);

            if (filename.endsWith(".html")) {
                res.header("Content-Type", MediaType.TEXT_HTML);
            } else {
                res.header("Content-Type", MediaType.TEXT_PLAIN);
            }

            res.entity(content);
            return res.build();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    private String getNamespace() {
        String ns = namespace.get();

        if (ns == null || ns.isBlank()) {
            try {
                File nsFile = new File("/var/run/secrets/kubernetes.io/serviceaccount/namespace");
                if (!nsFile.exists()) {
                    ns = "not in cluster";
                } else {
                    ns = Files.readString(nsFile.toPath());
                }

                namespace.set(ns);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return ns;
    }
}