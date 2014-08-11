package de.ctrlaltdel.cci.sample;

import de.ctrlaltdel.cci.TestCamelContext;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.sjms.SjmsComponent;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.jms.JMSException;

/**
 * JmsComponent
 * @author ds
 */
public class JmsComponent {

    @PostConstruct
    public void postConstruct() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false");
            connectionFactory.createConnection();
        } catch (JMSException e) {
            throw new IllegalStateException(e);
        }
    }

	@Produces @Named("jms")
	public SjmsComponent producesSjmsComponent() {
        SjmsComponent sjmsComponent = new SjmsComponent();
        sjmsComponent.setConnectionFactory(new ActiveMQConnectionFactory(TestCamelContext.BROKER_URL));
        return sjmsComponent;
	}

}
