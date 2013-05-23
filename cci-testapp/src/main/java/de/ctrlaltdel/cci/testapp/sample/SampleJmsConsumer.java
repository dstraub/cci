package de.ctrlaltdel.cci.testapp.sample;

import org.apache.camel.Consume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SampleJmsConsumer
 * @author ds
 */
public class SampleJmsConsumer {

	private static Logger LOG = LoggerFactory.getLogger(SampleJmsConsumer.class);
	
	@Consume(uri="jms:topic:data")
	public void consume(String msg) {
		LOG.info("consume: " + msg);
	}

}
