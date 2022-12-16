package org.usf.assertapi.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.http.MediaType;
import org.usf.assertapi.core.ApiRequest;
import org.usf.assertapi.core.ResponseComparator;

public final class JunitResponseComparator implements ResponseComparator {
	
	@Override
	public void assumeEnabled(ApiRequest query) {
		assumeTrue(query.getConfiguration().isEnable(), "skipped test");
	}

	@Override
	public void assertStatusCode(int expectedStatusCode, int actualStatusCode) {
		assertEquals(expectedStatusCode, actualStatusCode, "Status code");
	}

	@Override
	public void assertContentType(MediaType expectedContentType, MediaType actualContentType) {
		assertEquals(expectedContentType, actualContentType, "Content Type");
	}
	
	@Override
	public void assertByteContent(byte[] expectedContent, byte[] actualContent) {
		assertArrayEquals(expectedContent, actualContent, "Response content");
	}
	
	@Override
	public void assertTextContent(String expectedContent, String actualContent) {
    	assertEquals(expectedContent, actualContent, "Response content");
	}

	@Override
	public void assertJsonCompareResut(JSONCompareResult res) {
		if (res.failed()) {
            throw new AssertionError(res.getMessage());
        }
	}
	
	@Override
	public void assertionFail(Throwable t) {
		throw new RuntimeException("Assertion error", t); //replace by other exception
	}
}
