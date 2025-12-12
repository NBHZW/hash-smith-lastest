package io.github.bluuewhale.hashsmith;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SwissMapBackshiftTest {

	record Collide(int v) {
		@Override public int hashCode() { return 0; }
	}

	@Test
	void removeWithoutTombstoneMaintainsCluster() {
		var m = new SwissMap<Collide, Integer>();
		for (int i = 0; i < 10; i++) {
			m.put(new Collide(i), i);
		}

		var removed = m.removeWithoutTombstone(new Collide(4));
		assertEquals(4, removed);
		assertEquals(9, m.size());

		for (int i = 0; i < 10; i++) {
			var k = new Collide(i);
			if (i == 4) {
				assertNull(m.get(k));
			} else {
				assertEquals(i, m.get(k));
			}
		}
	}

	@Test
	void removeWithoutTombstoneBridgesExistingTombstone() {
		var m = new SwissMap<Collide, Integer>();
		for (int i = 0; i < 7; i++) {
			m.put(new Collide(i), i);
		}

		// Create a tombstone first, then ensure backshift delete keeps the chain valid.
		m.remove(new Collide(1)); // create tombstone
		var removed = m.removeWithoutTombstone(new Collide(3));
		assertEquals(3, removed);

		for (int i = 0; i < 7; i++) {
			var k = new Collide(i);
			if (i == 1 || i == 3) {
				assertNull(m.get(k));
			} else {
				assertEquals(i, m.get(k));
			}
		}

		m.put(new Collide(100), 1000);
		assertEquals(1000, m.get(new Collide(100)));
		assertEquals(6, m.size());
	}
}

