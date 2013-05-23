package de.ctrlaltdel.cci;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.CamelContextNameStrategy;

/**
 * CamelContextProducer
 * @author ds
 */
@ApplicationScoped
public class CamelContextProducer  {

	@Inject
	private BeanManager beanManager;
	
	@Inject
	private Instance<CamelContextNameStrategy> contextNameStrategies;
	
	private CamelContext camelContext;
	
	@PostConstruct
	public void postConstruct() {
		DefaultCamelContext defaultContext = new DefaultCamelContext();
		CamelBeanManagerIntegration.add(defaultContext, beanManager);
		
		if (!contextNameStrategies.isUnsatisfied() && !contextNameStrategies.isAmbiguous()) {
			defaultContext.setNameStrategy(contextNameStrategies.get());
		}
		
		defaultContext.addComponent("properties", new PropertiesComponent());
		
		camelContext = defaultContext;
	}

	@Produces
	public CamelContext produceCamelContext() {
		return camelContext;
	}
	
	@PreDestroy
	public void preDestroy() {
		try {
			camelContext.stop();
		} catch (Exception x) {
			
		}
		camelContext = null;
	}
}
