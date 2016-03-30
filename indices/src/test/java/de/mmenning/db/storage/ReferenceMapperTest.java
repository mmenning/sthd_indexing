package de.mmenning.db.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import de.mmenning.db.storage.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceMapperTest {

	@Test
	public void test() {

		Object a = new Object();
		Object b = new Object();

		assertNotEquals(a, b);
		assertEquals(a, a);
		assertEquals(b, b);

		Assert.assertNotEquals(IOUtils.getReferenceID(a), IOUtils.getReferenceID(b));
		assertEquals(IOUtils.getReferenceID(a), IOUtils.getReferenceID(a));

		int a_before = IOUtils.getReferenceID(a);

		System.gc();

		assertEquals(a_before, IOUtils.getReferenceID(a));
	}
}
