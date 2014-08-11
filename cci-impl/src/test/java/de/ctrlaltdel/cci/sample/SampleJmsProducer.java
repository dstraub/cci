package de.ctrlaltdel.cci.sample;

import de.ctrlaltdel.cci.TestCamelContext;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;

import javax.enterprise.context.ApplicationScoped;


/**
 * SampleJmsProducer
 * @author ds
 */
@ApplicationScoped
public class SampleJmsProducer {

	private ProducerTemplate messageProducer;
	
	private String msg;
	
	@Produce(uri="jms:data")
	public void setMessageProducer(ProducerTemplate messageProducer) {
		this.messageProducer = messageProducer;
	}
	
	public void send(String msg) {
		this.msg = msg;
		messageProducer.sendBody(msg);
	}
	
	public void echo(String answer) {
		Assert.assertEquals(msg, answer);
		TestCamelContext.COUNTDOWN.countDown();
	}
}
