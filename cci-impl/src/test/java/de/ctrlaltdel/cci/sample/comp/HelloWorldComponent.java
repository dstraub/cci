package de.ctrlaltdel.cci.sample.comp;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import javax.inject.Named;

import java.util.Map;

/**
 * Represents the component that manages {@link HelloWorldEndpoint}.
 */
@Named("helloworld")
public class HelloWorldComponent extends DefaultComponent {

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new HelloWorldEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
