package de.ctrlaltdel.cci.testapp.sample;

import org.apache.camel.builder.RouteBuilder;

/**
 * SampleJmsRoute
 * @author ds
 */
public class SampleJmsRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("jms:topic:data")
		.id("cci-sample-jms")
        .log("received: ${in.body}")
		.bean(SampleJmsProducer.class, "echo")
		;
	}
}
