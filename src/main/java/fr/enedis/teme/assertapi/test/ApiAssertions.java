package fr.enedis.teme.assertapi.test;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_XML;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import fr.enedis.teme.assertapi.core.HttpQuery;
import fr.enedis.teme.assertapi.core.HttpRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class ApiAssertions {
	
	public static void assertResponseEquals(HttpQuery query, RestTemplate exTemp, RestTemplate acTemp) throws Exception  {
		
		assumeFalse(query.isDisabled(), "skipped test"); //not working with mvn test
    	String aUrl = query.getActual().url();
    	CompletableFuture<ResponseEntity<byte[]>> af = query.isParallel() 
    			? supplyAsync(()-> acTemp.exchange(aUrl, HttpMethod.valueOf(query.getActual().httpMethod()), null, byte[].class), commonPool())
    			: completedFuture(acTemp.exchange(aUrl, HttpMethod.valueOf(query.getActual().httpMethod()), null, byte[].class));
    	try {
        	var eRes = exTemp.exchange(query.getExpected().url(), HttpMethod.valueOf(query.getExpected().httpMethod()), null, byte[].class);
        	
        	var aRes = assertDoesNotThrow(()-> af.get(), "error");
        	assertEquals(eRes.getStatusCodeValue(), aRes.getStatusCodeValue(), "Status code");
        	assertEquals(eRes.getHeaders().getContentType(), aRes.getHeaders().getContentType(), "Content Type");
			if(isTextContent(eRes.getHeaders().getContentType())) {
		    	var eCont = new String(eRes.getBody(), query.getExpected().charset());
		    	var aCont = new String(aRes.getBody(), query.getActual().charset());
		    	if(APPLICATION_JSON.isCompatibleWith(eRes.getHeaders().getContentType())) {
					assertEquals(
							excludePaths(eCont, query.getExpected()), 
							excludePaths(aCont, query.getActual()), 
							query.isStrict());
		    	}
		    	else {
		    		assertEquals(
		    				new String(eRes.getBody(), query.getExpected().charset()), 
		    				new String(aRes.getBody(), query.getActual().charset()), "Response content");
		    	}
			}
			else {
				assertArrayEquals(eRes.getBody(), aRes.getBody(), "Response content");
			}
    	}
    	catch(RestClientResponseException ee) {
        	var ae = assertThrows(RestClientResponseException.class, execute(af), "error");
        	assertEquals(ee.getRawStatusCode(), ae.getRawStatusCode(), "Status code");
//        	assertEquals("response body", ee.getResponseBodyAsString(), ae.getResponseBodyAsString());
    	}
    	catch(Exception e) {
    		waitFor(af);
    		throw e;
    	}
	}
	
	private static boolean isTextContent(MediaType media){
		
		return Stream.of(
				APPLICATION_JSON, APPLICATION_XML,
				TEXT_PLAIN, TEXT_HTML, TEXT_XML)
				.anyMatch(media::isCompatibleWith);
	}

    private static String excludePaths(String v, HttpRequest hr) {
		if(hr.getExcludePaths() != null) {
			var json = JsonPath.parse(v);
			Stream.of(hr.getExcludePaths()).forEach(json::delete);
	    	v = json.jsonString();
		}
		return v;
    }
    
    private static Executable execute(CompletableFuture<?> cf){
    	return ()-> {
    		try {
				cf.get();
			} catch (ExecutionException e) {
				throw e.getCause() instanceof RestClientResponseException ? e.getCause() : e;
			}
    	};
    }

    private static void waitFor(CompletableFuture<?> cf){
		try {
			cf.join();
		}
		catch(Exception ex) {
			log.warn(ex.getMessage());
		}
    }
}
