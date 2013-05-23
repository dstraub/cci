package de.ctrlaltdel.cci.sample;

import java.io.File;

import javax.inject.Inject;

import org.apache.camel.Consume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ctrlaltdel.cci.TestCamelContext;

/**
 * SampleFileConsumer
 * @author ds
 */
public class SampleFileConsumer {

	private static Logger LOG = LoggerFactory.getLogger(SampleFileConsumer.class);
	
	@Inject
	private TestCamelContext test;
	
	@Consume(uri="file:///{{input}}")
	public void consume(String content) {
		LOG.info("consume: " + content);
		test.COUNTDOWN.countDown();
	}
}
