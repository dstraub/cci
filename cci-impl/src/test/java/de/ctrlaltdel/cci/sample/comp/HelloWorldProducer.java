package de.ctrlaltdel.cci.sample.comp;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ctrlaltdel.cci.TestCamelContext;

/**
 * The HelloWorld producer.
 */
public class HelloWorldProducer extends DefaultProducer {
	
	private static Logger LOG = LoggerFactory.getLogger(HelloWorldProducer.class);

	private HelloWorldEndpoint endpoint;

    public HelloWorldProducer(HelloWorldEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
    	LOG.info(exchange.getIn().getBody().toString());
    	TestCamelContext.COUNTDOWN.countDown();
    }

}
