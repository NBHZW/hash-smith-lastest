package com.donghyungko.swisstable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

class SwissMapSimdTest {

	private static long callSimdEq(SwissMap<?, ?> map, byte[] array, int base, byte value) throws Exception {
		Method m = SwissMap.class.getDeclaredMethod("simdEq", byte[].class, int.class, byte.class);
		m.setAccessible(true);
		return (long) m.invoke(map, array, base, value);
	}

	@Test
	void simdEqReturnsMaskForMatches() throws Exception {
		SwissMap<Integer, Integer> map = new SwissMap<>(SwissMap.Path.SIMD);
		byte[] arr = {
			9, 1, 9, 0, 0, 9, 7, 9,
			1, 1, 9, 3, 4, 9, 9, 8,
		};
		long expectedMask =
			(1L << 0)  | (1L << 2)  | (1L << 5) |
			(1L << 7)  | (1L << 10) | (1L << 13) |
			(1L << 14); // 0110010010100101

		long mask = callSimdEq(map, arr, 0, (byte) 9);
        System.out.println(mask);
		assertEquals(expectedMask, mask);
	}

	@Test
	void simdEqReturnsZeroWhenBeyondArray() throws Exception {
		SwissMap<Integer, Integer> map = new SwissMap<>(SwissMap.Path.SIMD);
		byte[] arr = new byte[30];

		long mask = callSimdEq(map, arr, 20, (byte) 0);
		assertEquals(0L, mask);
	}

	@Test
	void simdEqReturnsZeroWhenSimdDisabled() throws Exception {
		SwissMap<Integer, Integer> map = new SwissMap<>(SwissMap.Path.SCALAR);
		byte[] arr = {
			9, 9, 9, 9, 9, 9, 9, 9,
			9, 9, 9, 9, 9, 9, 9, 9
		};

		long mask = callSimdEq(map, arr, 0, (byte) 9);
		assertEquals(0L, mask);
	}
}
