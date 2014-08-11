package de.ctrlaltdel.cci;

import de.ctrlaltdel.cci.sample.SampleJmsProducer;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.spi.Injector;
import org.apache.camel.spi.Registry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * TestCamelContext
 * @author ds
 */
@RunWith(CdiTestRunner.class)
public class TestCamelContext {

	private static Logger LOG = LoggerFactory.getLogger(TestCamelContext.class);
	
	public static final String BROKER_URL = "vm://localhost";
	
	public static final String CONTEX_NAME = "test-context";
	
	@Inject
	private CamelContext camelContext;
	
	@Inject
	private SampleJmsProducer jmsProducer;

	@Inject
	private CamelContextProperties camelContextNameBean;

	public static CountDownLatch COUNTDOWN;



    @Test
	public void testCamelContext() throws Exception {
		LOG.info("CamelContext: " + camelContext);
		
		Assert.assertEquals(CONTEX_NAME, camelContext.getName());
		
		Injector injector = camelContext.getInjector();
		Assert.assertEquals(CamelBeanManagerIntegration.class, injector.getClass());

		Registry registry = camelContext.getRegistry();
		if (registry instanceof PropertyPlaceholderDelegateRegistry) {
			registry = ((PropertyPlaceholderDelegateRegistry) registry).getRegistry();
		}
		Assert.assertEquals(CamelBeanManagerIntegration.class, registry.getClass());
	
		// check objectname replacment
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		mBeanServer.getObjectInstance(getContextObjectName("context", CONTEX_NAME));
	}

	/**
	 * getContextObjectName
	 */
	private ObjectName getContextObjectName(String type, String name) throws MalformedObjectNameException {
		Hashtable<String, String> objectNameProperties = new Hashtable<String, String>();
		objectNameProperties.put("context", CONTEX_NAME);
		objectNameProperties.put("type", type);
		objectNameProperties.put("name", "\"" + name + "\"");
		return new ObjectName("org.apache.camel", objectNameProperties);
	}
	
	@Test
	public void testFileConsumer() throws Exception {
		String fileUri = null;
		for (Endpoint endpoint: camelContext.getEndpoints()) {
			LOG.info(endpoint.toString());
			String uri = endpoint.getEndpointUri();
			if (uri.startsWith("file") && uri.endsWith("input")) {
				fileUri = uri;
				break;
			}
		}
		
		Assert.assertNotNull(fileUri);
		
		COUNTDOWN = new CountDownLatch(1);
		
		fileUri = fileUri + "/" + System.currentTimeMillis();
		
		FileOutputStream fos = new FileOutputStream(new File(new URL(fileUri).toURI()));
		fos.write("hallo".getBytes());
		fos.close();

		COUNTDOWN.await(1, TimeUnit.MINUTES);
		
		Assert.assertEquals(0, COUNTDOWN.getCount());
	}
	

	@Test
	public void testFileRoute() throws Exception {
		String fileUri = null;
		Route sampleRoute = null;
		for (Route route: camelContext.getRoutes()) {
			LOG.info(route.getEndpoint().getEndpointUri());
			String from = route.getEndpoint().getEndpointUri();
			if (from.startsWith("file") && from.endsWith("start")) {
				fileUri = from;
				break;
			}
		}
		Assert.assertNotNull(fileUri);
		
		File endDir = new File(new URL(fileUri.replace("start", "end")).toURI());
		File outFile = new File(new URL(fileUri + "/" + System.currentTimeMillis()).toURI());
		
		FileOutputStream fos = new FileOutputStream(outFile);
		fos.write("hallo".getBytes());
		fos.close();

		synchronized (this) {
			wait(2000);
		}
		
		boolean found = false;
		File[] files = endDir.listFiles();
		Assert.assertNotNull(files);
		for (File file : files) {
			if (file.getName().equals(outFile.getName())) {
				found = true;
			}
			boolean rc = file.delete();
		}
		Assert.assertTrue(found);
		
	}
	
	@Test
	public void testComponent() throws Exception {
		
		RouteBuilder route = new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("helloworld://foo") 
				.id("hello")
				.to("helloworld://bar")
				;
			}
		};
		
		COUNTDOWN = new CountDownLatch(5);
		camelContext.addRoutes(route);
		
		COUNTDOWN.await(2, TimeUnit.MINUTES);
		
		Assert.assertEquals(0, COUNTDOWN.getCount());

		// test if jmx works
		ObjectName objectName = getContextObjectName("routes", "hello");
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		Long exchangesTotal = (Long) mBeanServer.getAttribute(objectName, "ExchangesTotal");
		Assert.assertTrue(5 <= exchangesTotal);
		
		mBeanServer.invoke(objectName, "stop", null, null);
	}
	
	
	@Test
	public void testJms() throws Exception {
        COUNTDOWN = new CountDownLatch(1);
        jmsProducer.send("hallo");
        COUNTDOWN.await(1, TimeUnit.MINUTES);
        Assert.assertEquals(0, COUNTDOWN.getCount());
	}


}
