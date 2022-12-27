package org.usf.assertapi.test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.usf.assertapi.core.ApiAssertionError;
import org.usf.assertapi.core.ResponseComparator;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public final class JunitResponseComparator extends ResponseComparator {
	
	@Override
	public void assumeEnabled(boolean enabled) {
		try {
			super.assumeEnabled(enabled); //log error
		}
		catch(ApiAssertionError e) {
			assumeTrue(false, "api assertion skipped"); //specific JUnit exception for skipping test
		}
	}
	
}
