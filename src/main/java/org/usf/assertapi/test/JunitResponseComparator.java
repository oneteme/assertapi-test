package org.usf.assertapi.test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

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
		assumeTrue(false, "api assertion skipped"); //specific JUnit exception skip test
	}
	
}
