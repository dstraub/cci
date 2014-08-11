package de.ctrlaltdel.cci.testapp.sample;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.sjms.SjmsComponent;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.jms.JMSException;


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
        sjmsComponent.setConnectionFactory(new ActiveMQConnectionFactory("vm://localhost"));
        return sjmsComponent;
    }
	
}
