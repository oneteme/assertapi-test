package org.usf.assertapi.test;

import static org.usf.assertapi.core.Module.registerAssertionFail;

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
	
	static {
		registerAssertionFail(TestAbortedException.class);
	}
	
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
	protected AssertionError failNotEqual(Object expected, Object actual, CompareStage stage) {
		var e = (ApiAssertionError) super.failNotEqual(expected, actual, stage); //super log error
		//throw the right junit exception (IDE plugin comparator)
		return  new AssertionFailedError(e.getMessage(), e.getExpected(), e.getActual()); 
	}
}
