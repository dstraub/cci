package de.ctrlaltdel.cci;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessManagedBean;

import org.apache.camel.CamelContext;
import org.apache.camel.Consume;
import org.apache.camel.Produce;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelBeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CdiCamelExtension
 * @author ds
 */
public class CdiCamelExtension implements Extension {

	private static Logger LOG = LoggerFactory.getLogger(CdiCamelExtension.class);
	
	private Set<Bean<?>> camelBeans;
	
	
	public CdiCamelExtension() {
		camelBeans = new HashSet<Bean<?>>();
	}

	/**
	 * processBean
	 */
	private <X> void processBean(@Observes ProcessManagedBean<X> processManagedBean) {
		for (AnnotatedMethod annotatedMethod: processManagedBean.getAnnotatedBeanClass().getMethods()) {
			if (annotatedMethod.isAnnotationPresent(Consume.class) || annotatedMethod.isAnnotationPresent(Produce.class)) {
				camelBeans.add(processManagedBean.getBean());
				break;
			}
		}
		// use annotated field only for dependent beans, otherwise weld uses a proxy ... 
		for (AnnotatedField annotatedField: processManagedBean.getAnnotatedBeanClass().getFields()) {
			if (annotatedField.isAnnotationPresent(Consume.class) || annotatedField.isAnnotationPresent(Produce.class)) {
				camelBeans.add(processManagedBean.getBean());
				break;
			}
		}
	}

	/**
	 * afterDeploymentValidation
	 */
	private void afterDeploymentValidation(@Observes AfterDeploymentValidation unused, BeanManager beanManager) {
		
		CamelContext camelContext = resolveCamelContext(beanManager);
		LOG.info("CamelContext created " + camelContext.getName());
		
		DefaultCamelBeanPostProcessor beanPostProcessor = new DefaultCamelBeanPostProcessor(camelContext);

		for (Bean<?> bean: camelBeans) {
			Object instance = beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
			String name = bean.getName();
			if (name == null) {
				name = bean.getBeanClass().getSimpleName();
			}
			try {
				beanPostProcessor.postProcessBeforeInitialization(instance, name);
				beanPostProcessor.postProcessAfterInitialization(instance, name);
			} catch (Exception x) {
				LOG.error(x.getClass().getSimpleName() + ": " + x.getMessage());
			}
		}
		
		Set<Bean<?>> rBeans = beanManager.getBeans(RouteBuilder.class);
		for (Bean<?> rBean : rBeans) {
			RouteBuilder routeBuilder = (RouteBuilder) beanManager.getReference(rBean, RouteBuilder.class, beanManager.createCreationalContext(rBean));
			try {
				camelContext.addRoutes(routeBuilder);
			} catch (Exception x) {
				LOG.error(x.getClass().getSimpleName() + ": " + x.getMessage());
			}
		}
		
		try {
			camelContext.start();
			LOG.info("CamelContext "  + camelContext.getName() + ": " + camelContext.getStatus());
		} catch (Exception x) {
			LOG.error(x.getClass().getSimpleName() + ": " + x.getMessage());
		}
	}
	
	
	/**
	 * resolveCamelContext
	 */
	private CamelContext resolveCamelContext(BeanManager beanManager) {
		Set<Bean<?>> beans = beanManager.getBeans(CamelContextProducer.class);
		Bean<?> bean = beanManager.resolve(beans);
		CamelContextProducer cp =  (CamelContextProducer) beanManager.getReference(bean, CamelContextProducer.class, beanManager.createCreationalContext(bean));
		return cp.produceCamelContext();
	}
}
