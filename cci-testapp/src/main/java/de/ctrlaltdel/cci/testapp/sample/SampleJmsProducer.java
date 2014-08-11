package de.ctrlaltdel.cci.testapp.sample;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * SampleJmsProducer
 * @author ds
 */
@ApplicationScoped
public class SampleJmsProducer {

	private static Logger LOG = LoggerFactory.getLogger(SampleJmsProducer.class);
	
	private ProducerTemplate messageProducer;
	
	private String msg;
	
	@Produce(uri="jms:topic:data")
	public void setMessageProducer(ProducerTemplate messageProducer) {
		this.messageProducer = messageProducer;
	}
	
	public void send(String msg) {
		messageProducer.sendBody(msg);
	}

	public void echo(String msg) {
		LOG.info("echo: " + msg);
	}

}
