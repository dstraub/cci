package de.ctrlaltdel.cci.sample.comp;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Represents a HelloWorld endpoint.
 */
public class HelloWorldEndpoint extends DefaultEndpoint {
    
	public HelloWorldEndpoint() {
    }

    public HelloWorldEndpoint(String uri, HelloWorldComponent component) {
        super(uri, component);
    }

    public HelloWorldEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new HelloWorldProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new HelloWorldConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }
}
