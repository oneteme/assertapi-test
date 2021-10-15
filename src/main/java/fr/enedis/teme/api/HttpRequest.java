package fr.enedis.teme.api;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.nio.charset.Charset;

import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class HttpRequest {

	private String uri;
	private String method = "GET";
	private String charset = "UTF-8";
	private String[] excludePaths;
	
	public String url() {
		return requireNonNull(uri).startsWith("/") ? uri : "/" + uri;
	}

	public HttpMethod httpMethod() {
		
		return HttpMethod.valueOf(requireNonNull(method).toUpperCase());
	}
	
	public Charset charset(){
		
		switch(requireNonNull(charset).toUpperCase().replace('-', '_')) {
			case "ISO_8859_1": return ISO_8859_1;
			case "UTF_8": return UTF_8;
			case "UTF_16": return UTF_16;
			default : throw new IllegalArgumentException("Unsupported charset " + charset);
		}
	}

	@Override
	public String toString() {
		return "["+method+"]" + " " + uri;
	}
}