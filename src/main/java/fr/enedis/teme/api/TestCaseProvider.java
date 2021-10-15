package fr.enedis.teme.api;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestCaseProvider {
	
	public static Stream<HttpQuery> resouces(URI uri) {
		
		return resouces(uri, (Predicate<File>)null);
	}
	
	public static Stream<HttpQuery> resouces(URI uri, String filenamePattern) {
		
		return resouces(uri, filenamePattern == null ? null : p-> p.getName().matches(filenamePattern));
	}

	public static Stream<HttpQuery> resouces(URI uri, Predicate<File> predicate) {
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
		var f = new File(uri);
		if(f.isFile()) {
			return filter.test(f) ? Stream.of(f) : Stream.empty();
		}
		return Stream.of(f.listFiles()).filter(filter);
	}
}
