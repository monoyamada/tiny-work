package machine;

import java.io.IOException;

public class Polynomial {
	static final Polynomial one = new Polynomial(new long[] { 1 });

	protected static Polynomial getOne() {
		return Polynomial.one;
	}

	final long[] coef;

	public Polynomial(long[] coef) {
		this.coef = coef;
	}
	public int size() {
		return this.coef.length;
	}
	public boolean isOne() {
		return this.coef.length == 1 && this.coef[0] == 1;
	}
	public long get(int index) {
		return this.coef[index];
	}
	public long[] get(long[] output) {
		return this.get(output, 0);
	}
	public long[] get(long[] output, int begin) {
		System.arraycopy(this.coef, 0, output, begin, this.coef.length);
		return output;
	}
	public String toString() {
		return this.toString("x");
	}
	public String toString(String var) {
		StringBuilder buffer = new StringBuilder();
		try {
			return this.toString(buffer, var).toString();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return "?";
	}
	public Appendable toString(Appendable output, String var) throws IOException {
		long[] ks = this.coef;
		boolean first = true;
		for (int i = 0, n = ks.length; i < n; ++i) {
			if (ks[i] == 0) {
				continue;
			}
			if (ks[i] < 0) {
				output.append(" - ");
				if (Math.abs(ks[i]) != 1) {
					output.append(Long.toString(Math.abs(ks[i])));
				}
			} else if (first) {
				if (i == 0 || Math.abs(ks[i]) != 1) {
					output.append(Long.toString(Math.abs(ks[i])));
				}
			} else {
				output.append(" + ");
				if (Math.abs(ks[i]) != 1) {
					output.append(Long.toString(Math.abs(ks[i])));
				}
			}
			switch (i) {
			case 0:
			break;
			case 1:
				output.append(var);
			break;
			default:
				output.append(var);
				output.append('^');
				output.append(Integer.toString(i));
			break;
			}
			first = false;
		}
		return output;
	}
}
