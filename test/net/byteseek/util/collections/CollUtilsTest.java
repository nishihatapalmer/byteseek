package net.byteseek.util.collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

public class CollUtilsTest {

	@Test
	public final void testContainsAny() {
		try {
			CollUtils.containsAny(null, null);
			fail("Expected an Illegal Argument Exception for null collections");
		} catch (IllegalArgumentException expected) {}

		try {
			CollUtils.containsAny(new ArrayList<Integer>(), null);
			fail("Expected an Illegal Argument Exception for null collections");
		} catch (IllegalArgumentException expected) {}

		
		try {
			CollUtils.containsAny(null, new HashSet<Byte>());
			fail("Expected an Illegal Argument Exception for null collections");
		} catch (IllegalArgumentException expected) {}
		
		Collection<Integer> collection = new ArrayList<Integer>();
		Collection<Integer> values = new HashSet<Integer>();
		assertFalse("empty collections do not contain any of each other", CollUtils.containsAny(collection,values));
		assertFalse("empty collections do not contain any of each other", CollUtils.containsAny(values, collection));
		
		collection.add(1);
		assertFalse("empty values do not contain anything", CollUtils.containsAny(collection,values));
		collection.add(2);
		assertFalse("empty values do not contain anything", CollUtils.containsAny(collection,values));
		
		values.add(2);
		assertTrue("both collections share 2", CollUtils.containsAny(collection,values));
		assertTrue("both collections share 2", CollUtils.containsAny(values, collection));
	}

}
