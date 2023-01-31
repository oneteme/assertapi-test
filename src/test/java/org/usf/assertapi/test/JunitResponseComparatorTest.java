package org.usf.assertapi.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.usf.assertapi.core.ApiAssertionError.skippedAssertionError;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;
import org.usf.assertapi.core.ApiAssertionError;
import org.usf.assertapi.core.ApiAssertionRuntimeException;

class JunitResponseComparatorTest {

	@Test
	void testAssertionFail() {
		var rc = new JunitResponseComparator();
		var ex = assertThrows(ApiAssertionRuntimeException.class, ()-> rc.assertionFail(null));
		assertNull(ex.getCause());
	}

	@Test
	void testAssertionFail_AssertionFailedError() {
		var rc = new JunitResponseComparator();
		var ae = new ApiAssertionError("exp", "act", "msg");
		var ex = assertThrows(AssertionFailedError.class, ()-> rc.assertionFail(ae));
		assertEquals("exp", ex.getExpected().getValue());
		assertEquals("act", ex.getActual().getValue());
		assertEquals("msg", ex.getMessage());
	}

	@Test
	void testAssertionFail_TestAbortedException() {
		var rc = new JunitResponseComparator();
		var ae = skippedAssertionError("msg");
		var ex = assertThrows(TestAbortedException.class, ()-> rc.assertionFail(ae));
		assertEquals("msg", ex.getMessage());
	}

	@Test
	void testAssertionFail_RuntimeException() {
		var rc = new JunitResponseComparator();
		var re = new RuntimeException("msg");
		var ex = assertThrows(RuntimeException.class, ()-> rc.assertionFail(re));
		assertSame(re, ex);
	}
}
