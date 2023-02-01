package org.usf.assertapi.test;

import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpMethod.GET;
import static org.usf.assertapi.core.RestTemplateBuilder.build;
import static org.usf.assertapi.core.RuntimeEnvironement.build;
import static org.usf.assertapi.test.TestContext.setLocalContext;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.usf.assertapi.core.ApiRequest;
import org.usf.assertapi.core.ServerConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */

@RequiredArgsConstructor
public final class TestCaseProvider {
	
	final ObjectMapper mapper;
	
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

	public Stream<ApiRequest> fromLocal(@NonNull Class<?> testClass) throws URISyntaxException {
		return fromLocal(uri(testClass), defaultFilter());
	}

	public Stream<ApiRequest> fromLocal(@NonNull Class<?> testClass, @NonNull String regex) throws URISyntaxException {
		return fromLocal(uri(testClass), asFilter(regex));
	}
	
	public Stream<ApiRequest> fromLocal(@NonNull Class<?> testClass, @NonNull FileFilter filter) throws URISyntaxException {
		return fromLocal(uri(testClass), filter);
	}
	
	public Stream<ApiRequest> fromLocal(@NonNull URI uri) {
		return fromLocal(uri, defaultFilter());
	}
	
	public Stream<ApiRequest> fromLocal(@NonNull URI uri, @NonNull String regex) {
		return fromLocal(uri, asFilter(regex));
	}

	public Stream<ApiRequest> fromLocal(@NonNull URI uri, @NonNull FileFilter filter) {
		return listFiles(Path.of(uri), filter).flatMap(f-> {
			try {
				return Stream.of(mapper.readValue(f, ApiRequest[].class))
						.map(r-> r.withLocation(f.toURI()));
			} catch (IOException e) {
				throw new IllegalArgumentException("cannot read file : " + f, e);
			}
		});
	}
	
	private static Stream<File> listFiles(Path path, FileFilter filter) {
		var f = path.toFile();
		if(f.isDirectory()) {
			return Stream.of(f.listFiles(filter));
		}
		return filter.accept(f) ? Stream.of(f) : Stream.empty();
	}
	
	static URI uri(Class<?> testClass) throws URISyntaxException {
		return testClass.getResource(".").toURI();
	}
	
	static FileFilter asFilter(String regex) {
		return f-> f.getName().matches(regex);
	}
	
	static FileFilter defaultFilter() {
		return f-> f.isFile() && !f.getName().endsWith(".class"); //exclude .class
	}

	private void injectMapper(RestTemplate template) {
        for(HttpMessageConverter<?> mc : template.getMessageConverters()) {
        	if(mc instanceof MappingJackson2HttpMessageConverter) {
        		((MappingJackson2HttpMessageConverter)mc).setObjectMapper(mapper); //!important use default mapper
        	}
        }
	}	
}
