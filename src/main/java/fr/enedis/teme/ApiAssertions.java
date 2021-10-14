package fr.enedis.teme;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class ApiAssertions {

	public static void assertResponseEquals(RestTemplate exTemp, RestTemplate acTemp, HttpQuery query) throws Exception {

    	String aUrl = query.getActual().url();
    	CompletableFuture<ResponseEntity<byte[]>> af = query.isParallel() 
    			? supplyAsync(()-> acTemp.exchange(aUrl, query.getActual().httpMethod(), null, byte[].class), commonPool())
    			: completedFuture(acTemp.exchange(aUrl, query.getActual().httpMethod(), null, byte[].class));
    	try {
        	var eRes = exTemp.exchange(query.getExpected().url(), query.getExpected().httpMethod(), null, byte[].class);
        	
        	var aRes = assertDoesNotThrow(()-> af.get(), "error");
        	assertEquals(eRes.getStatusCodeValue(), aRes.getStatusCodeValue(), "Status code");
        	assertEquals(eRes.getHeaders().getContentType(), aRes.getHeaders().getContentType(), "Content Type");
			if(APPLICATION_JSON.isCompatibleWith(eRes.getHeaders().getContentType())){
				JSONAssert.assertEquals(
						excludePaths(eRes.getBody(), query.getExpected()), 
						excludePaths(aRes.getBody(), query.getActual()), 
						query.isStrict());
			}
    	}
    	catch(RestClientResponseException ee) {
        	var ae = assertThrows(RestClientResponseException.class, ()->{ 
        		try {
            		af.get();
        		}
        		catch (ExecutionException e) {
        			throw e.getCause();
				}
        	}, "error");
        	assertEquals(ee.getRawStatusCode(), ae.getRawStatusCode(), "Status code");
//        	assertEquals("response body", ee.getResponseBodyAsString(), ae.getResponseBodyAsString());
    	}
	}

    private static String excludePaths(byte[] content, HttpRequest hr) {
    	var v = new String(content, hr.charset());
		if(hr.getExcludePaths() != null) {
			var json = JsonPath.parse(v);
	    	for(String p : hr.getExcludePaths()) {
	    		json.delete(p);
	    	}
	    	v = json.jsonString();
		}
		return v;
    }
	
}
