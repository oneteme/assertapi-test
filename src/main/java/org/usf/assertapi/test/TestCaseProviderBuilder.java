package org.usf.assertapi.test;

import static org.usf.assertapi.core.Utils.defaultMapper;

import org.usf.assertapi.core.ContentComparator;
import org.usf.assertapi.core.ResponseTransformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import lombok.NonNull;

public final class TestCaseProviderBuilder {
	
	private final ObjectMapper mapper = defaultMapper(); //TODO inherite
	
	public void registerComaparator(@NonNull String name, @NonNull Class<? extends ContentComparator<?>> c) {
		mapper.registerSubtypes(new NamedType(c, name));
	}

	public void registerTransfomer(@NonNull String name, @NonNull Class<? extends ResponseTransformer<?>> c) {
		mapper.registerSubtypes(new NamedType(c, name));
	}
	
	public TestCaseProvider build() {
		return new TestCaseProvider(mapper);
	}
	

}
