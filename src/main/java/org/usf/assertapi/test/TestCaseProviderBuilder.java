package org.usf.assertapi.test;

import static org.usf.assertapi.core.PolymorphicType.typeName;
import static org.usf.assertapi.core.Utils.defaultMapper;

import org.usf.assertapi.core.PolymorphicType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class TestCaseProviderBuilder {
	
	final ObjectMapper mapper;
	
	public TestCaseProviderBuilder() {
		this(defaultMapper());
	}

	@SuppressWarnings("unchecked")
	public TestCaseProviderBuilder register(@NonNull Class<? extends PolymorphicType>... classes) {
		for(var c : classes) {
			if(typeName(c) == null) {
				throw new IllegalArgumentException("cannot find annotation @JsonTypeName on " + c.getCanonicalName());
			}
		}
		mapper.registerSubtypes(classes);
		return this;
	}
	
	public TestCaseProviderBuilder register(@NonNull String name, @NonNull Class<? extends PolymorphicType> c) {
		mapper.registerSubtypes(new NamedType(c, name));
		return this;
	}
	
	public TestCaseProvider build() {
		return new TestCaseProvider(mapper);
	}
}
