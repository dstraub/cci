package de.ctrlaltdel.cci;

import org.apache.camel.IsSingleton;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Injector;
import org.apache.camel.spi.Registry;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * CamelBeanManagerIntegration
 * @author ds
 */
class CamelBeanManagerIntegration implements Registry, Injector {

	private final BeanManager beanManager;
	private final Registry contextRegistry;
	private final Injector contexInjector;
	
	static void add(DefaultCamelContext camelContext, BeanManager beanManager) {
		CamelBeanManagerIntegration helper = new CamelBeanManagerIntegration(beanManager, camelContext.getRegistry(), camelContext.getInjector());
		camelContext.setRegistry(helper);
		camelContext.setInjector(helper);
	}
	
	private CamelBeanManagerIntegration(BeanManager beanManager, Registry contextRegistry, Injector contexInjector) {
		this.beanManager = beanManager;
		this.contextRegistry = contextRegistry;
		this.contexInjector = contexInjector;
	}

	@Override
	public <T> Set<T> findByType(Class<T> type) {
		Map<String, T> beans = findByTypeWithName(type);
		return new HashSet<T>(beans.values());
	}

	@Override
	public <T> Map<String, T> findByTypeWithName(Class<T> type) {
		Map<String, T> beans = new HashMap<String, T>();

		Set<Bean<?>> definitions = beanManager.getBeans(type, new AnnotationLiteral<Any>() {});
		if (definitions == null) {
			return beans;
		}

		for (Bean bean : definitions) {
			if (bean.getName() != null) {
				beans.put(bean.getName(), getContextualReference(type, bean));
			}
		}
		return beans;
	}

	@Override
	public Object lookupByName(String name) {
		return lookupByNameAndType(name, Object.class);
	}

	@Override
	public <T> T lookupByNameAndType(String name, Class<T> type) {
		Set<Bean<?>> beans = beanManager.getBeans(name);
		if (beans == null || beans.isEmpty()) {
			return contextRegistry.lookupByNameAndType(name, type);
		}
		
		Bean<?> bean = beanManager.resolve(beans);
		return getContextualReference(type, bean);
	}

	@Override @Deprecated
	public Object lookup(String name) {
		return lookupByNameAndType(name, null);
	}

	@Override @Deprecated
	public <T> T lookup(String name, Class<T> type) {
		return lookupByNameAndType(name, type);
	}
 
	@Override @Deprecated
	public <T> Map<String, T> lookupByType(Class<T> type) {
		return findByTypeWithName(type);
	}

	
	@Override
	public <T> T newInstance(Class<T> type) {
		Set<Bean<?>> beans = beanManager.getBeans(type);
		if (beans == null || beans.isEmpty()) {
			return contexInjector.newInstance(type);
		}

		Bean<?> bean = beanManager.resolve(beans);		
		return getContextualReference(type, bean);
	}

	@Override
	public <T> T newInstance(Class<T> type, Object instance) {
		if (instance instanceof IsSingleton) {
			boolean singleton = ((IsSingleton) instance).isSingleton();
			if (singleton) {
				return type.cast(instance);
			}
		}
		return newInstance(type);
	}

	private <T> T getContextualReference(Class<T> type, Bean<?> bean) {
		CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, type, creationalContext);
	}

}
