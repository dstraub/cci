package de.ctrlaltdel.cci.testapp.sample;

import org.apache.camel.Consume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SampleFileConsumer
 * @author ds
 */
public class SampleFileConsumer {

	private static Logger LOG = LoggerFactory.getLogger(SampleFileConsumer.class);
	
	@Consume(uri="file:///{{jboss.server.temp.dir}}/input")
	public void consume(String message) {
		LOG.info("consume: " +  message);
	}
}
