package machine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.lang.NumberHelper;

public class DyckWordTest extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
		Debug.setLogLevel("debug");
	}
	public void test_0() {
		DyckWord_0 dyck = new DyckWord_0("b", "c");
		for (int n = 10; 1 < n--;) {
			String[] words = dyck.get(n);
			System.out.print("[" + n + "] = ");
			for (int ii = 0, nn = words.length; ii < nn; ++ii) {
				if (ii != 0) {
					System.out.print(" + ");
				}
				System.out.print(dyck.trancateWord(words[ii]));
			}
			System.out.println();
		}
	}
	public void test_1() {
		DyckWord_1 dyck = new DyckWord_1("b", "c");
		for (int n = 10; 1 < n--;) {
			PolyWord[] words = dyck.get(n);
			System.out.print("[" + n + "] = ");
			for (int ii = 0, nn = words.length; ii < nn; ++ii) {
				if (ii != 0) {
					System.out.print(" + ");
				}
				System.out.print(dyck.trancateWord(words[ii]));
			}
			System.out.println();
		}
	}
	public void test_2() throws IOException {
		File file = new File("test_2.txt");
		PrintWriter writer = FileHelper.getPrintWriter(file);
		DyckWord_2 dyck = new DyckWord_2("b", "c");
		for (int n = 10; 1 < n--;) {
			PolyWord[] words = dyck.get(n);
			writer.write("[" + n + "] = ");
			for (int ii = 0, nn = words.length; ii < nn; ++ii) {
				if (ii != 0) {
					writer.write(" + ");
				}
				writer.write(dyck.trancateWord(words[ii]));
			}
			writer.write("\n");
		}
		for (int n = 10; 1 < n--;) {
			PolyWord[] words = dyck.get(n);
			writer.write("[" + n + "] = ");
			long[][] matrix = toMarix(words);
			boolean first = true;
			for (int ri = 0, rn = matrix.length; ri < rn; ++ri) {
				long sum = ArrayHelper.sum(matrix[ri]);
				if (sum < 1) {
					continue;
				}
				if (!first) {
					writer.write(" + ");
				}
				if (first || 1 < sum) {
					// writer.write(sum);
				}
				switch (ri) {
				case 0:
				break;
				case 1:
					writer.write("q");
				break;
				default:
					writer.write("q^" + ri);
				break;
				}
				writer.write("[");
				boolean notyet = true;
				for (int ci = 0, cn = words.length; ci < cn; ++ci) {
					long k = matrix[ri][ci];
					if (k < 1) {
						continue;
					}
					if (!notyet) {
						writer.write(" + ");
					}
					if (1 < k) {
						writer.write(Long.toString(k));
					}
					writer.write(dyck.trancateWord(words[ci].word));
					notyet = false;
				}
				writer.write("]");
				first = false;
			}
			writer.write("\n");
		}
		FileHelper.close(writer);
		Debug.log().debug("wrote " + file.getAbsolutePath());
	}
	private static long[][] toMarix(PolyWord[] words) {
		int rn = 0;
		for (int i = 0, n = words.length; i < n; ++i) {
			rn = Math.max(rn, words[i].weight.size());
		}
		long[][] matrix = new long[rn][words.length];
		for (int ci = 0, cn = words.length; ci < cn; ++ci) {
			Polynomial ks = words[ci].weight;
			for (int ki = 0, kn = ks.size(); ki < kn; ++ki) {
				matrix[ki][ci] = ks.get(ki);
			}
		}
		return matrix;
	}
}
