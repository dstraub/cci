package de.ctrlaltdel.cci.sample;

import org.apache.camel.builder.RouteBuilder;

/**
 * SampleFileRoute
 * @author ds
 */
public class SampleFileRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("file://{{start}}")
		.log("received: ${in.body}")
		.to("file://{{end}}")
		;
	}

}
