package org.usf.assertapi.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.usf.assertapi.core.CsvDataComparator;
import org.usf.assertapi.core.DataMapper;
import org.usf.assertapi.core.DataTransformer;
import org.usf.assertapi.core.JsonDataComparator;
import org.usf.assertapi.core.JsonDataMapper;
import org.usf.assertapi.core.JsonKeyMapper;
import org.usf.assertapi.core.JsonPathFilter;
import org.usf.assertapi.core.JsonPathMover;
import org.usf.assertapi.core.ModelComparator;
import org.usf.assertapi.core.ModelTransformer;
import org.usf.assertapi.core.PolymorphicType;

import com.fasterxml.jackson.databind.json.JsonMapper;

class TestCaseProviderBuilderTest {

	@Test
	void testTestCaseProviderBuilder() {
		var mapper = new TestCaseProviderBuilder().mapper;
		assertNotNull(mapper.getSubtypeResolver());
		assertTrue(mapper.getRegisteredModuleIds().contains("assertapi"));
		assertTrue(mapper.getRegisteredModuleIds().contains("jackson-module-parameter-names"));
	}

	@ParameterizedTest
	@ValueSource(classes = {
			CsvDataComparator.class, JsonDataComparator.class, //comparators
			JsonDataMapper.class, JsonKeyMapper.class, JsonPathMover.class, JsonPathFilter.class, //modelTransformers
			DataMapper.class}) //dataTransformers
	void testRegister(Class<? extends ModelComparator<?>> classe) {
		var builder = new TestCaseProviderBuilder(new JsonMapper());
		assertSame(builder, builder.register(classe));
	}

	@Test
	void testRegister_comaparator() {
		class DummyClass implements ModelComparator<String> {
			public CompareResult compare(String expected, String actual) {return null;}
		}
		testRegisterPolymorphicType(DummyClass.class);
	}

	@Test
	void testRegister_model_transformer() {
		class DummyClass implements ModelTransformer<String> {
			public String transform(String resp) {return resp;}
		}
		testRegisterPolymorphicType(DummyClass.class);
	}

	@Test
	void testRegister_data_transformer() {
		class DummyClass implements DataTransformer {
			public Object transform(Object resp) {return resp;}
		}
		testRegisterPolymorphicType(DummyClass.class);
	}
	
	@Test
	void testBuild() {
		var builder = new TestCaseProviderBuilder();
		assertSame(builder.mapper, builder.build().mapper);
	}
	
	void testRegisterPolymorphicType(Class<? extends PolymorphicType> type) {
		var builder = new TestCaseProviderBuilder();
		assertSame(builder, builder.register("dummy", type));
		assertThrows(NullPointerException.class, ()-> builder.register("dummy", null));
		assertThrows(NullPointerException.class, ()-> builder.register(null, type));
		assertThrows(IllegalArgumentException.class, ()-> builder.register(type));
	}
	
}
