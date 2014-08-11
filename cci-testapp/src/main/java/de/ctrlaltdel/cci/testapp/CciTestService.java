package de.ctrlaltdel.cci.testapp;

import de.ctrlaltdel.cci.testapp.sample.SampleJmsProducer;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Route;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

@Path("/")
@ManagedBean
public class CciTestService {

    @Inject
	private CamelContext camelContext;

	@Inject
	private SampleJmsProducer jmsProducer;



    @GET
    @Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String index() {
		StringWriter writer = new StringWriter();
		try {
			writer.append("CamelContext=").append(camelContext.getName());
			for (Route route: camelContext.getRoutes()) {
				writer.append("\n  Route ").append(route.getId()).append(' ').append(route.getEndpoint().getEndpointUri());
			}
			for (Endpoint endpoint: camelContext.getEndpoints()) {
				writer.append("\n  Endpoint ").append(' ').append(endpoint.getEndpointUri());
			}
		} catch (Exception x) {
			x.printStackTrace(new PrintWriter(writer));
		}
		
		return writer.append("\n").toString();
	}
	
	@GET 
    @Path("/jms/{msg}")
	@Produces(MediaType.TEXT_PLAIN)
	public String jms(@PathParam("msg") String msg) {
		StringWriter writer = new StringWriter();
		try {
			jmsProducer.send(msg);
			writer.append("message sent, check server log");
		} catch (Exception x) {
			x.printStackTrace(new PrintWriter(writer));
		}
		
		return writer.append("\n").toString();
	}

	@GET 
    @Path("/file/{msg}")
	@Produces(MediaType.TEXT_PLAIN)
	public String file(@PathParam("msg") String msg) {
		StringWriter writer = new StringWriter();
		String tmpDir = System.getProperty("jboss.server.temp.dir");
		String timeStamp = String.valueOf(System.currentTimeMillis());
		try {
			for (String destination: new String[] { "/input/", "/start/" }) {
				File file = new File(tmpDir + destination + timeStamp);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(msg.getBytes());
				fos.close();
			}
			writer.append("check: cat ").append(tmpDir).append("/end/").append(timeStamp);
			
		} catch (Exception x) {
			x.printStackTrace(new PrintWriter(writer));
		}
		
		return writer.append("\n").toString();
	}

	
}
