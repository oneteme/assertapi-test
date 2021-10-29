package fr.enedis.teme.assertapi.test;

import static fr.enedis.teme.assertapi.core.RestTemplateBuilder.build;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.enedis.teme.assertapi.core.HttpQuery;
import fr.enedis.teme.assertapi.core.ServerConfig;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestCaseProvider {
	
	public static final Stream<HttpQuery> fromRepository(String url){
		
		return fromRepository(url, null);
	}
	
	public static final Stream<HttpQuery> fromRepository(String uri, ServerConfig config){
		
		var template = config == null ? new RestTemplate() : build(config);
		return Stream.of(template.getForObject(uri, HttpQuery[].class));
	}
	
	public static Stream<HttpQuery> jsonRessources(URI uri) {
		
		return jsonRessources(uri, p-> p.getName().toLowerCase().endsWith(".json"));
	}
	
	public static Stream<HttpQuery> jsonRessources(URI uri, String filenamePattern) {
		
		return jsonRessources(uri, filenamePattern == null ? null : p-> p.getName().matches(filenamePattern));
	}

	public static Stream<HttpQuery> jsonRessources(URI uri, Predicate<File> predicate) {
		var mapper = new ObjectMapper();
		return searchIn(uri, predicate).flatMap(f-> {
			try {
				return Stream.of(mapper.readValue(f, HttpQuery[].class)).map(HttpQuery::build);
			} catch (IOException e) {
				throw new IllegalArgumentException("Canot parse this file " + f.getName(), e);
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
}
