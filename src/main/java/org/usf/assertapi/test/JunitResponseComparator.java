package org.usf.assertapi.test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.usf.assertapi.core.ApiRequest;
import org.usf.assertapi.core.ResponseComparator;

public final class JunitResponseComparator extends ResponseComparator {
	
	@Override
	public void assumeEnabled(ApiRequest query) {
		try {
			super.assumeEnabled(query); 
		}
		catch(AssertionError e) {
			assumeTrue(false, "api assertion skipped"); //specific JUnit exception skip test
		}
	}
	
}
