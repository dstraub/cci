package de.ctrlaltdel.cci;

import org.apache.camel.spi.CamelContextNameStrategy;

/**
 * CamelContextNameBean
 * @author ds
 */
public class CamelContextNameBean implements CamelContextNameStrategy {

	@Override
	public String getName() {
		return "test-context";
	}
	
	@Override
	public String getNextName() {
		return null;
	}
	
	@Override
	public boolean isFixedName() {
		return true;
	}
}
