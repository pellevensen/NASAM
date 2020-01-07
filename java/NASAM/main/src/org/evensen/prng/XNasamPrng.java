package org.evensen.prng;

import java.security.SecureRandom;
import java.util.Objects;

public final class XNasamPrng implements SplittablePrng<XNasamPrng>, Prng, SkippablePrng {
	private final long x;
	private final long increment;
	private long ctr;

	public XNasamPrng(final long seed, final long streamIdx) {
		this.ctr = XNasamPrng.xnasam(seed, 0);
		final long tmpStreamIdx = XNasamPrng.xnasam(streamIdx, 1);
		this.increment = 0x5BE0CD19137E2179L ^ (tmpStreamIdx & 0xFFFFFFFF) << 2;
		this.x = XNasamPrng.expandWord((int) (this.increment >>> 32));
	}

	public XNasamPrng() {
		this(new SecureRandom().nextLong(), new SecureRandom().nextLong());
	}

	private static long xnasam(final long ctr, final long x) {
		long v = ctr;

		v ^= x;
		v ^= Long.rotateRight(v, 25) ^ Long.rotateRight(v, 47);
		v *= 0x9E6C63D0676A9A99L;
		v ^= v >>> 23 ^ v >>> 51;
		v *= 0x9E6D62D06F6A9A9BL;
		v ^= v >>> 23 ^ v >>> 51;

		return v;
	}

	// Gives a minimum hamming distance of 6 between
	// any output pair.
	private static int expandWord(final short w) {
		short w2 = (short) ~w;
		w2 ^= w2 >>> 12;
		int f = w | w2 << 16;
		f ^= f >>> 12;
		f ^= f << 13;
		f ^= f >>> 14;
		return f;
	}

	// Gives a minimum hamming distance of 6 between
	// any output pair.
	private static long expandWord(final int w) {
		return (long) XNasamPrng.expandWord((short) (w & 0xFFFF)) << 32
				| XNasamPrng.expandWord((short) (w >>> 16)) & 0xFFFFFFFFL;
	}

	@Override
	public long next() {
		final long oldCtr = this.ctr;
		this.ctr += this.increment;
		return XNasamPrng.xnasam(oldCtr, this.x);
	}

	@Override
	public XNasamPrng split() {
		return new XNasamPrng(next(), next());
	}

	@Override
	public void skip(final long distance) {
		this.ctr += distance * this.increment;
	}

	@Override
	public String toString() {
		return "XNasamPrng [x=" + this.x + ", increment=" + this.increment + ", ctr=" + this.ctr + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.ctr, this.increment, this.x);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final XNasamPrng other = (XNasamPrng) obj;
		return this.ctr == other.ctr && this.increment == other.increment && this.x == other.x;
	}

	@Override
	public XNasamPrng clone() {
		try {
			return (XNasamPrng) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new AssertionError(); // Can't happen.
		}
	}

}
