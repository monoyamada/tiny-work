package group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import tiny.lang.Debug;
import tiny.lang.Messages;
import tiny.lang.NumberHelper;
import tiny.lang.StringHelper;
import tiny.number.LongArrayAdapter;
import tiny.primitive.Euclid;
import tiny.primitive.LongArrayList;
import tiny.primitive.LongList;
import tiny.primitive.LongPushable;

public class NumberTest extends TestCase {
	static void doit(LongList output, int num, int del, int mod) {
		num *= mod;
		while (num < del) {
			output.addLast(0);
			num *= mod;
		}
		for (int i = 0; i < mod; ++i) {
			int q = num / del;
			num -= q * del;
			num *= mod;
			output.addLast(q);
		}
	}
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testDoit() {
		LongArrayList output = new LongArrayList(1024);
		{
			output.removeAll();
			doit(output, 1, 3, 10);
			Debug.log().debug(output);
		}
		{
			output.removeAll();
			doit(output, 1, 6, 10);
			Debug.log().debug(output);
		}
		{
			output.removeAll();
			doit(output, 1, 7, 10);
			Debug.log().debug(output);
		}
	}
	long euclid(long a0, long a1) {
		if (a0 < a1) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue("params",
					"a0 >= a1", "a0<a1"));
		}
		long r = a0 % a1;
		while (0 < r) {
			a0 = a1;
			a1 = r;
			r = a0 % a1;
		}
		return a1;
	}
	long euclid(LongPushable output, long a0, long a1) {
		if (a0 < a1) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue("params",
					"a0 >= a1", "a0<a1"));
		}
		long r = a0 % a1;
		while (0 < r) {
			output.push(r);
			a0 = a1;
			a1 = r;
			r = a0 % a1;
		}
		return a1;
	}
	long euclid(LongPushable rs, LongPushable xs0, LongPushable xs1, long a0,
			long a1) {
		if (a0 < a1) {
			throw new IllegalArgumentException(Messages.getUnexpectedValue("params",
					"a0 >= a1", "a0<a1"));
		}
		//
		long x00 = 1;
		long x10 = 0;
		rs.push(a0);
		xs0.push(x00);
		xs1.push(x10);
		//
		long x01 = 0;
		long x11 = 1;
		rs.push(a1);
		xs0.push(x01);
		xs1.push(x11);
		//
		long q = a0 / a1;
		long r = a0 % a1;
		long x0 = x00 - q * x01;
		long x1 = x10 - q * x11;
		x00 = x01;
		x10 = x11;
		x01 = x0;
		x11 = x1;
		while (0 < r) {
			xs0.push(x01);
			xs1.push(x11);
			rs.push(r);
			a0 = a1;
			a1 = r;
			q = a0 / a1;
			r = a0 % a1;
			x0 = x00 - q * x01;
			x1 = x10 - q * x11;
			x00 = x01;
			x10 = x11;
			x01 = x0;
			x11 = x1;
		}
		return a1;
	}
	public void testEuclid() {
		List<List<long[]>> all = new ArrayList<List<long[]>>(128);
		if (true) {
			LongArrayList x0 = new LongArrayList();
			LongArrayList x1 = new LongArrayList();
			LongArrayList rs = new LongArrayList();
			long a0 = 387;
			long a1 = 109;
			long gcd = euclid(rs, x0, x1, a0, a1);
			Debug.log().debug("gcd(" + a0 + ", " + a1 + ")=" + gcd);
			List<long[]> list = new ArrayList<long[]>(rs.getLength());
			System.out.println("r, x0, x1");
			for (int i = 0, n = rs.getLength(); i < n; ++i) {
				list.add(new long[] { rs.get(i), x0.get(i), x1.get(i) });
				System.out.println(rs.get(i) + ", " + x0.get(i) + ", " + x1.get(i));
			}
			all.add(list);
		}
		if (true) {
			LongArrayList x0 = new LongArrayList();
			LongArrayList x1 = new LongArrayList();
			LongArrayList rs = new LongArrayList();
			long a0 = 120;
			long a1 = 23;
			long gcd = euclid(rs, x0, x1, a0, a1);
			Debug.log().debug("gcd(" + a0 + ", " + a1 + ")=" + gcd);
			List<long[]> list = new ArrayList<long[]>(rs.getLength());
			System.out.println("r, x0, x1");
			for (int i = 0, n = rs.getLength(); i < n; ++i) {
				list.add(new long[] { rs.get(i), x0.get(i), x1.get(i) });
				System.out.println(rs.get(i) + ", " + x0.get(i) + ", " + x1.get(i));
			}
			all.add(list);
		}
		if (true) {
			LongArrayList x0 = new LongArrayList();
			LongArrayList x1 = new LongArrayList();
			LongArrayList rs = new LongArrayList();
			long a0 = 35742 / 42;
			long a1 = 13566 / 42;
			long gcd = euclid(rs, x0, x1, a0, a1);
			Debug.log().debug("gcd(" + a0 + ", " + a1 + ")=" + gcd);
			List<long[]> list = new ArrayList<long[]>(rs.getLength());
			System.out.println("r, x0, x1");
			for (int i = 0, n = rs.getLength(); i < n; ++i) {
				list.add(new long[] { rs.get(i), x0.get(i), x1.get(i) });
				System.out.println(rs.get(i) + ", " + x0.get(i) + ", " + x1.get(i));
			}
			all.add(list);
		}
		Collections.sort(all, new Comparator<List<long[]>>() {
			@Override
			public int compare(List<long[]> o1, List<long[]> o2) {
				return NumberHelper.compare(o1.size(), o2.size());
			}
		});
		Collections.reverse(all);
		for (int i = 0, n = all.size(); i < n; ++i) {
			System.out.print(" & r & x_0 & x_1");
		}
		System.out.println(" \\\\\\hline");
		for (int i = 0, n = all.get(0).size(); i < n; ++i) {
			for (int ii = 0, nn = all.size(); ii < nn; ++ii) {
				List<long[]> list = all.get(ii);
				if (list.size() <= i) {
					break;
				} else if (ii == 0) {
					System.out.print("n_" + i);
				}
				long[] x = list.get(i);
				System.out.print(" & " + x[0] + " & " + x[1] + " & " + x[2]);
			}
			System.out.println(" \\\\");
		}
	}
	public void testGcd() {
		Debug.log().debug(Euclid.gcd(10, -15));
		Debug.log().debug(Euclid.gcd(-15, 10));
		Debug.log().debug(Euclid.gcd(851, 437));
		Debug.log().debug(Euclid.gcd(-851, 437));
		Debug.log().debug(Euclid.gcd(851, -437));
		Debug.log().debug(Euclid.gcd(-851, -437));

		long[] xs = new long[2];
		long g = 0;
		long a = 120;
		long b = 23;
		g = Euclid.gcd(xs, a, b);
		Debug.log().debug(g + ", " + xs[0] + ", " + xs[1]);
		g = Euclid.gcd(xs, -a, b);
		Debug.log().debug(g + ", " + xs[0] + ", " + xs[1]);
		g = Euclid.gcd(xs, a, -b);
		Debug.log().debug(g + ", " + xs[0] + ", " + xs[1]);
		g = Euclid.gcd(xs, -a, -b);
		Debug.log().debug(g + ", " + xs[0] + ", " + xs[1]);
		g = Euclid.gcd(xs, b, a);
		Debug.log().debug(g + ", " + xs[0] + ", " + xs[1]);
		g = Euclid.gcd(xs, -b, a);
		Debug.log().debug(g + ", " + xs[0] + ", " + xs[1]);
		g = Euclid.gcd(xs, b, -a);
		Debug.log().debug(g + ", " + xs[0] + ", " + xs[1]);
		g = Euclid.gcd(xs, -b, -a);
		Debug.log().debug(g + ", " + xs[0] + ", " + xs[1]);

		g = Euclid.gcd(xs, 6, 9);
		Debug.log().debug(g + ", " + xs[0] + ", " + xs[1]);
	}
	public void testEuclid_1() {
		long[] base = { 15, 10 };
		long[] x0 = { 1, 0 };
		long[] x1 = { 0, 1 };
		long n0 = base[0];
		long n1 = base[1];
		LongArrayAdapter y = new LongArrayAdapter(x1);
		y.multiplies(n0 / n1);
		y.plus(x0);
		long m = y.inner(base);
		while (0 < m) {
			Debug.log().debug(
					m + ", " + StringHelper.join(x0) + ", " + StringHelper.join(x1));
			n0 = n1;
			n1 = m;
			x0 = x1;
			x1 = y.get();
			y.set(x1);
			y.multiplies(n0 / n1);
			y.minus();
			y.plus(x0);
			m = y.inner(base);
		}
		Debug.log().debug(
				m + ", " + StringHelper.join(x0) + ", " + StringHelper.join(x1));
	}
}
