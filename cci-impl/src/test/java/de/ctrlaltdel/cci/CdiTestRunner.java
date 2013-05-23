package de.ctrlaltdel.cci;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * CdiTestRunner
 * @author ds
 */
public class CdiTestRunner extends BlockJUnit4ClassRunner {

    private final Class<?> clazz;
    private final Weld weld;
    private final WeldContainer container;

	static {
		System.setProperty("input", System.getProperty("user.dir") + "/target/tmp/input");
		System.setProperty("start", System.getProperty("user.dir") + "/target/tmp/start");
		System.setProperty("end", System.getProperty("user.dir") + "/target/tmp/end");
	}

    
    public CdiTestRunner(final Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
        this.weld = new Weld();
       	this.container = weld.initialize();
       	
    }

    @Override
    protected Object createTest() throws Exception {
    	return container.instance().select(clazz).get();
    }

    @Override
    public void run(RunNotifier notifier) {
    	try {
    		super.run(notifier);
    	} finally {
    		weld.shutdown();
    	}
    }

}

