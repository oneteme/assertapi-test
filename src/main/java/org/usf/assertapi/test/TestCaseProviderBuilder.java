package org.usf.assertapi.test;

import static org.usf.assertapi.core.Utils.defaultMapper;

import org.usf.assertapi.core.DataComparator;
import org.usf.assertapi.core.DataTransformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import lombok.NonNull;

public final class TestCaseProviderBuilder {
	
	final ObjectMapper mapper;
	
	public TestCaseProviderBuilder() {
		this.mapper = defaultMapper();
	}
	
	public TestCaseProviderBuilder registerComaparator(@NonNull String name, @NonNull Class<? extends DataComparator<?>> c) {
		mapper.registerSubtypes(new NamedType(c, name));
		return this;
	}

	public TestCaseProviderBuilder registerTransformer(@NonNull String name, @NonNull Class<? extends DataTransformer<?,?>> c) {
		mapper.registerSubtypes(new NamedType(c, name));
		return this;
	}
	
	public TestCaseProvider build() {
		return new TestCaseProvider(mapper);
	}

}
