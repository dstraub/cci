package de.ctrlaltdel.cci.testapp.sample;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.activemq.camel.component.ActiveMQComponent;


public class JmsComponent {

	@Produces @Named("jms")
	public ActiveMQComponent producesActiveMQComponent() {
		ActiveMQComponent activeMQComponent = new ActiveMQComponent();
		activeMQComponent.setBrokerURL("tcp://localhost:61616");
		return activeMQComponent;
	}
	
}
