package org.usf.assertapi.test;

import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpMethod.GET;
import static org.usf.assertapi.core.RestTemplateBuilder.build;
import static org.usf.assertapi.core.RuntimeEnvironement.build;
import static org.usf.assertapi.test.TestContext.setLocalContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.usf.assertapi.core.ApiRequest;
import org.usf.assertapi.core.ServerConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */

@RequiredArgsConstructor
public final class TestCaseProvider {
	
	private final ObjectMapper mapper;
	
	public Stream<ApiRequest> fromRepository(ServerConfig config, Map<String, String> map){
		return fromRepository(config, "/v1/assert/api/load", map); //default endpoint
	}

	public Stream<ApiRequest> fromRepository(ServerConfig config, String uri, Map<String, String> map){
		var template = build(config);
		injectMapper(template);
		if(map == null) {
			map = emptyMap();
		}
		else if(!map.isEmpty()) {
			uri += "?" + map.keySet().stream().map(s-> s+"={"+s+"}").collect(joining("&"));
		}
		var headers = new HttpHeaders();
		build().push(headers::add);
		var cases = template.exchange(uri, GET, new HttpEntity<>(headers), ApiRequest[].class, map);
		setLocalContext(template, cases.getHeaders().getFirst("trace"));
		return Stream.of(cases.getBody()).sorted(comparing(ApiRequest::getUri)); //sort by api
	}

	public Stream<ApiRequest> jsonRessources(Class<?> testClass) throws URISyntaxException {
		return jsonRessources(testClass, null);
	}

	public Stream<ApiRequest> jsonRessources(Class<?> testClass, String filenamePattern) throws URISyntaxException {
		return jsonRessources(testClass.getResource(".").toURI(), filenamePattern);
	}
	
	public Stream<ApiRequest> jsonRessources(URI uri) {
		return jsonRessources(uri, p-> p.getName().toLowerCase().endsWith(".json"));
	}
	
	public Stream<ApiRequest> jsonRessources(URI uri, String filenamePattern) {
		return jsonRessources(uri, filenamePattern == null ? null : p-> p.getName().matches(filenamePattern));
	}

	public Stream<ApiRequest> jsonRessources(URI uri, Predicate<File> predicate) {
		return searchIn(uri, predicate).flatMap(f-> {
			try {
				return Stream.of(mapper.readValue(f, ApiRequest[].class))
						.map(r-> r.withLocation(f.toURI()));
			} catch (IOException e) {
				throw new IllegalArgumentException("cannot read file : " + f, e);
			}
		});
	}
	
	private static Stream<File> searchIn(URI uri, Predicate<File> predicate) {
		Predicate<File> filter = Predicate.not(File::isDirectory);
		if(predicate != null) {
			filter = filter.and(predicate);
		}
		var f = new File(requireNonNull(uri));
		if(f.isFile()) {
			return filter.test(f) ? Stream.of(f) : Stream.empty();
		}
		return Stream.of(f.listFiles()).filter(filter);
	}

	private void injectMapper(RestTemplate template) {
        for(HttpMessageConverter<?> mc : template.getMessageConverters()) {
        	if(mc instanceof MappingJackson2HttpMessageConverter) {
        		((MappingJackson2HttpMessageConverter)mc).setObjectMapper(mapper);
        	}
        }
	}	
}
