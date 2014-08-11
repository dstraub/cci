package de.ctrlaltdel.cci;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * CamelContextProducer
 * @author ds
 */
@ApplicationScoped
public class CamelContextProducer  {

	private static Logger LOG = LoggerFactory.getLogger(CamelContextProducer.class);
	
	@Inject
	private BeanManager beanManager;
	
	private CamelContext camelContext;
	
	@PostConstruct
	public void postConstruct() {
		
		DefaultCamelContext defaultContext = new DefaultCamelContext();

		CamelBeanManagerIntegration.add(defaultContext, beanManager);
		
		defaultContext.addComponent("properties", new PropertiesComponent());
		
		initProperties(defaultContext);
		
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
	
	/**
	 * initProperties
	 */
	private void initProperties(DefaultCamelContext camelContext) {
		Annotation any = new AnnotationLiteral<Any>() {};
		for (Method method: camelContext.getClass().getMethods()) {
			if (!method.getName().startsWith("set") || 1 != method.getParameterTypes().length) {
				continue;
			}
			
			Class<?> type = method.getParameterTypes()[0];
			// don't use primitive types, list, maps ... could more improved ...,
			if (!type.getName().startsWith("org.apache.camel")) {
				continue;
			}
			
			Set<Bean<?>> beans = beanManager.getBeans(type, any);
			if (beans.isEmpty()) {
				continue;
			}
			try {
				Bean<?> bean = beanManager.resolve(beans);
				Object parameter = beanManager.getReference(bean, type, beanManager.createCreationalContext(bean));
				method.invoke(camelContext, parameter);
				LOG.info("CamelContext." + method.getName().substring(3) + "=" + parameter.getClass().getName());
			} catch (Exception x) {
				// LOG.shithappens
			}
			
		}
	}
}
