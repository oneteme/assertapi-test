package org.usf.assertapi.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.usf.assertapi.core.DataComparator;
import org.usf.assertapi.core.DataTransformer;
import org.usf.assertapi.core.ReleaseTarget;

class TestCaseProviderBuilderTest {

	@Test
	void testTestCaseProviderBuilder() {
		var builder = new TestCaseProviderBuilder();
		assertNotNull(builder.mapper);
		assertTrue(builder.mapper.getRegisteredModuleIds().contains("assertapi"));
	}

	@Test
	void testRegisterComaparator() {
		var builder = new TestCaseProviderBuilder();
		assertSame(builder, builder.registerComaparator("dummy", DummyComparator.class));
	}
	
	@Test
	void testRegisterComaparator_null() {
		var builder = new TestCaseProviderBuilder();
		assertThrows(NullPointerException.class, ()-> builder.registerComaparator("dummy", null));
		assertThrows(NullPointerException.class, ()-> builder.registerComaparator(null, DummyComparator.class));
		//TODO parse
	}
	
	@Test
	void testRegisterTransformer() {
		var builder = new TestCaseProviderBuilder();
		assertSame(builder, builder.registerTransformer("dummy", DummyTranasformer.class));
		//TODO parse
	}
	
	@Test
	void testRegisterTransformer_null() {
		var builder = new TestCaseProviderBuilder();
		assertThrows(NullPointerException.class, ()-> builder.registerTransformer("dummy", null));
		assertThrows(NullPointerException.class, ()-> builder.registerTransformer(null, DummyTranasformer.class));
	}
	
	@Test
	void testBuild() {
		var builder = new TestCaseProviderBuilder();
		assertSame(builder.mapper, builder.build().mapper);
	}
	
	class DummyComparator implements DataComparator<String> {

		@Override
		public String getType() {return null;}

		@Override
		public CompareResult compare(String expected, String actual) {return null;}

		@Override
		public DataTransformer[] getTransformers() {return null;}
	}
	
	class DummyTranasformer extends DataTransformer<String, String> {

		protected DummyTranasformer(ReleaseTarget[] applyOn) {
			super(applyOn);
		}

		@Override
		public String getType() {return null;}

		@Override
		protected String transform(String resp) {return null;}
	}
}
