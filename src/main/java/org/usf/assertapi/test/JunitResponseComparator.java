package org.usf.assertapi.test;

import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;
import org.usf.assertapi.core.ApiAssertionError;
import org.usf.assertapi.core.CompareStage;
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
			super.assumeEnabled(enabled); //super log error
		}
		catch(ApiAssertionError e) {
			//throw the right junit exception (IDE plugin comparator)
			throw new TestAbortedException("api assertion skipped"); 
		}
	}
	
	@Override
	protected void failNotEqual(Object expected, Object actual, CompareStage stage) {
		try {
			super.failNotEqual(expected, actual, stage); //super log error
		}
		catch(ApiAssertionError e) {
			//throw the right junit exception (IDE plugin comparator)
			throw new AssertionFailedError(e.getMessage(), e.getExpected(), e.getActual()); 
		}
	}
}
