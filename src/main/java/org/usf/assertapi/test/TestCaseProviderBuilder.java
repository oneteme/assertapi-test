package org.usf.assertapi.test;

import static org.usf.assertapi.core.Utils.defaultMapper;

import org.usf.assertapi.core.ContentComparator;
import org.usf.assertapi.core.ResponseTransformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import lombok.NonNull;

public final class TestCaseProviderBuilder {
	
	private final ObjectMapper mapper;
	
	public TestCaseProviderBuilder() {
		this.mapper = defaultMapper();
	}
	
	public TestCaseProviderBuilder registerComaparator(@NonNull String name, @NonNull Class<? extends ContentComparator<?>> c) {
		mapper.registerSubtypes(new NamedType(c, name));
		return this;
	}

	public TestCaseProviderBuilder registerTransformer(@NonNull String name, @NonNull Class<? extends ResponseTransformer<?,?>> c) {
		mapper.registerSubtypes(new NamedType(c, name));
		return this;
	}
	
	public TestCaseProvider build() {
		return new TestCaseProvider(mapper);
	}

}
