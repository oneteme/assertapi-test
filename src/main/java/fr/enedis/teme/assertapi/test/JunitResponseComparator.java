package fr.enedis.teme.assertapi.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import fr.enedis.teme.assertapi.core.ResponseComparator;

public final class JunitResponseComparator implements ResponseComparator {
	
	@Override
	public void assumeEnabled(boolean enable) {
		assumeTrue(enable, "skipped test");
	}
	
	@Override
	public ResponseEntity<byte[]> assertNotResponseException(SafeSupplier<ResponseEntity<byte[]>> supp) {
		return assertDoesNotThrow(supp::get, "Actual response exception");
	}

	@Override
	public RestClientResponseException assertResponseException(SafeSupplier<?> supp) {
		return assertThrows(RestClientResponseException.class, supp::get, "Expected response exception");
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
}
