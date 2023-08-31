package org.usf.assertapi.test;

import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;
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
	public void assertionFail(AssertionError err) {
		try {
			super.assertionFail(err);
		}
		catch(ApiAssertionError e) {
			if(e.isSkipped()) {
				throw new TestAbortedException(e.getMessage());
			}
			throw new AssertionFailedError(e.getMessage(), e.getExpected(), e.getActual()); 
		}
	}	
}
