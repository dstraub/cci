package de.ctrlaltdel.cci.sample;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.activemq.camel.component.ActiveMQComponent;

import de.ctrlaltdel.cci.TestCamelContext;

/**
 * JmsComponent
 * @author ds
 */
public class JmsComponent {

	@Produces @Named("jms")
	public ActiveMQComponent producesActiveMQComponent() {
		ActiveMQComponent activeMQComponent = new ActiveMQComponent();
		activeMQComponent.setBrokerURL(TestCamelContext.BROKER_URL);
		return activeMQComponent;
	}
	
}
