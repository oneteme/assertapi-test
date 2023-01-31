package org.usf.assertapi.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.usf.assertapi.test.TestContext.setLocalContext;
import static org.usf.assertapi.test.TestContext.testCaseOrigin;
import static org.usf.assertapi.test.TestContext.threadLocal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.usf.assertapi.core.ApiRequest;
import org.usf.assertapi.core.ComparisonResult;

class TestContextTest {
	
	@AfterEach
	void cleanup() {
		threadLocal.remove();
	}

	@Test
	void testSetLocalContext() {
		var rt = new RestTemplate();
		var tr = "uri";
		setLocalContext(rt, tr);
		var ac = threadLocal.get();
		assertNotNull(ac);
		assertSame(rt, ac.template);
		assertSame(tr, ac.traceUri);
	}
	
	@Test
	void testTestCaseOrigin_empty() {
		var api = dummyApi(0L, "uri");
		var res = dummyResult();
		assertDoesNotThrow(()-> testCaseOrigin(api, res));
	}
	
	@Test
	void testTestCaseOrigin() {
		var rt = new RestTemplate() {
			String url;
			Object request;
			Object[] uriVariables;
			
			@Override
			public void put(String url, Object request, Object... uriVariables) {
				this.url = url;
				this.request = request;
				this.uriVariables = uriVariables;
			}
		};
		var api = dummyApi(0L, "uri");
		var res = dummyResult();
		setLocalContext(rt, "uri");
		testCaseOrigin(api, res);
		assertEquals("uri", rt.url);
		assertSame(res, rt.request);
		assertNotNull(rt.uriVariables);
		assertEquals(api.getId(), rt.uriVariables[0]);
	}
	
	private static ApiRequest dummyApi(long id, String uri) {
		return new ApiRequest(id, null, null, null, uri, null, null, null, null, null, null, null, null, null);
	}

	private static ComparisonResult dummyResult() {
		return new ComparisonResult(null, null, null, null);
	}
	
}
