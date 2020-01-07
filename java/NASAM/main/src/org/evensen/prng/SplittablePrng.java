package org.evensen.prng;

public interface SplittablePrng<T extends SplittablePrng<T>> {
	T split();
}
