package group;

import java.io.IOException;

import base.TestBase;

import tiny.lang.Debug;
import junit.framework.Assert;

public class LongPolynomialTest extends TestBase {
	public void testConstants() {
		LongPolynomial x = LongPolynomial.create(0);
		Assert.assertEquals(x.isZero(), true);
		Assert.assertEquals(x.isOne(), false);
		Assert.assertEquals(x.isConstant(), true);

		x = LongPolynomial.create(1);
		Assert.assertEquals(x.isZero(), false);
		Assert.assertEquals(x.isOne(), true);
		Assert.assertEquals(x.isConstant(), true);

		x = LongPolynomial.create(2);
		Assert.assertEquals(x.isZero(), false);
		Assert.assertEquals(x.isOne(), false);
		Assert.assertEquals(x.isConstant(), true);

		x = LongPolynomial.create(2, 1);
		Assert.assertEquals(x.isZero(), false);
		Assert.assertEquals(x.isOne(), false);
		Assert.assertEquals(x.isConstant(), false);
	}
	public void testPlus() {
		LongPolynomial x = LongPolynomial.create(1, 2, 3);
		LongPolynomial y = LongPolynomial.create(3, 4, 5);

		LongPolynomial z = x.plus(y);
		Assert.assertEquals(z.get(0), 4);
		Assert.assertEquals(z.get(1), 6);
		Assert.assertEquals(z.get(2), 8);

		z = x.minus(y);
		Assert.assertEquals(z.get(0), -2);
		Assert.assertEquals(z.get(1), -2);
		Assert.assertEquals(z.get(2), -2);

		x = LongPolynomial.create(1, 2, 3);
		y = LongPolynomial.create(4, 5, -3);

		z = x.plus(y);
		Assert.assertEquals(z.size(), 2);
		Assert.assertEquals(z.get(0), 5);
		Assert.assertEquals(z.get(1), 7);

		x = LongPolynomial.create(1, 2, 3);
		y = LongPolynomial.create(1, 2, 3);

		z = x.minus(y);
		Assert.assertEquals(z, LongPolynomial.ZERO);
	}

	public void testTimes() {
		LongPolynomial x = LongPolynomial.create(1, 2);
		LongPolynomial y = LongPolynomial.create(3, 4);
		LongPolynomial z = x.times(y);
		Assert.assertEquals(z.size(), 3);
		Assert.assertEquals(z.get(0), 3);
		Assert.assertEquals(z.get(1), 10);
		Assert.assertEquals(z.get(2), 8);

		x = LongPolynomial.create(0, 0, 0, 0, 1);
		y = LongPolynomial.create(3, 4, 5, 6);
		z = LongPolynomial.create(7, 8, 9);
		Debug.log().debug(x.times(y).plus(z));
	}

	public void testZeroDivide() {
		try {
			Debug.log().debug(0.0 / 0.0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			Debug.log().debug(1.0 / 0.0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public void testRemainder() throws IOException {
		LongPolynomial x = LongPolynomial.create(4, 13, 23, 15);
		LongPolynomial y = LongPolynomial.create(4, 5);
		LongPolynomial r = LongPolynomial.create(4, 5, 1);
		LongPolynomial q = x.quotient(y);
		LongPolynomial z = x.remainder(y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));

		x = LongPolynomial.create(4, 13, 22, 15);
		y = LongPolynomial.create(4, 5);
		r = LongPolynomial.create();
		q = x.quotient(y);
		z = x.remainder(y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));

		x = LongPolynomial.create(4, 5);
		y = LongPolynomial.create(4, 13, 22, 15);
		r = x;
		q = x.quotient(y);
		z = x.remainder(y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));

		x = LongPolynomial.create(4, 13, 23, 15);
		y = LongPolynomial.create(1);
		r = LongPolynomial.create();
		q = x.quotient(y);
		z = x.remainder(y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));

		x = LongPolynomial.create(-3, 2, -1, 1);
		y = LongPolynomial.create(-1, 2, 1);
		r = LongPolynomial.create(-6, 9);
		q = LongPolynomial.quotient(x, y);
		z = LongPolynomial.remainder(x, y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));

		x = LongPolynomial.create(-3, 4, -2, 3);
		y = LongPolynomial.create(3, 3, 1);
		r = LongPolynomial.create(30, 28);
		q = x.quotient(y);
		z = x.remainder(y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));

		x = LongPolynomial.create(-5, 0, 3, 0, 1);
		y = LongPolynomial.create(0, 4, 1);
		r = LongPolynomial.create(-5, -76);
		q = x.quotient(y);
		z = x.remainder(y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));

		x = LongPolynomial.create(6, 14, 18, 16, 12);
		y = LongPolynomial.create(3, 4, 5, 6);
		r = LongPolynomial.create(3, 4, 5);
		q = LongPolynomial.quotient(x, y);
		z = LongPolynomial.remainder(x, y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));

		x = LongPolynomial.create(7, 8, 0, 0, 3, 4, 5, 6);
		y = LongPolynomial.create(3, 4, 5, 6);
		r = LongPolynomial.create(7, 8);
		q = x.quotient(y);
		z = x.remainder(y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));
		if (false) {
			q.times(y).plus(z).toPolynomial(System.out, "x", "0");
			System.out.println();
		}

		x = LongPolynomial.create(0, 0, 0, 0, 3, 4, 5, 6);
		y = LongPolynomial.create(3, 4, 5, 6);
		r = LongPolynomial.create();
		q = x.quotient(y);
		z = x.remainder(y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));
		if (false) {
			q.times(y).plus(z).toPolynomial(System.out, "x", "0");
			System.out.println();
		}

		x = LongPolynomial.create(21, 52, 94, 118, 93, 54);
		y = LongPolynomial.create(3, 4, 5, 6);
		r = LongPolynomial.create();
		q = x.quotient(y);
		z = x.remainder(y);
		Assert.assertEquals("remainder", r, z);
		Assert.assertEquals("numerator", x, q.times(y).plus(z));
		if (false) {
			q.times(y).plus(z).toPolynomial(System.out, "x", "0");
			System.out.println();
		}
	}
	public void testGcd() {
		Assert.assertEquals("gcd", 2, LongPolynomial.gcd(2, 4));
		Assert.assertEquals("gcd", 6, LongPolynomial.gcd(18, 30));
		if (true) {
			int n = 123;
			LongPolynomial x = LongPolynomial.create(3 * n, 5 * n, 7 * n, 11 * n,
					22 * n, 21 * n, 20 * n, 18 * n);
			Assert.assertEquals("gcd", n, x.gcd());
		}
		
		LongPolynomial x = LongPolynomial.create(1,2,3,4);
		LongPolynomial y = LongPolynomial.create(5,6,7);
		LongPolynomial z = LongPolynomial.create(8,9);
		LongPolynomial xz = x.times(z);
		LongPolynomial yz= y.times(z);
		Assert.assertEquals("gcd", z, xz.gcd(yz));
	}
}
