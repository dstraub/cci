package de.ctrlaltdel.cci.testapp.sample;

import org.apache.camel.builder.RouteBuilder;

/**
 * SampleFileRoute
 * @author ds
 */
public class SampleFileRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("file://{{jboss.server.temp.dir}}/start")
		.id("cci-sample-file")
		.log("received: ${in.body}")
		.to("file://{{jboss.server.temp.dir}}/end")
		;
	}

}
