package de.ctrlaltdel.cci.sample;

import de.ctrlaltdel.cci.TestCamelContext;

import org.apache.camel.Consume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SampleFileConsumer
 * @author ds
 */
public class SampleFileConsumer {

	private static Logger LOG = LoggerFactory.getLogger(SampleFileConsumer.class);
	

	@Consume(uri="file:///{{input}}")
	public void consume(String content) {
		LOG.info("consume: " + content);
        TestCamelContext.COUNTDOWN.countDown();
	}
}
