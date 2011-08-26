package group;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import tiny.lang.Debug;

public class PathAlgebraTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		Logger.getLogger(PathAlgebra.class).setLevel(Level.INFO);
		super.setUp();
	}
	public void testConstruct() throws IOException {
		/*
		 * construct group table of S3 from generators.
		 */
		PathAlgebra paths = new PathAlgebra(6);
		// paths.addBase("1", 1, 1, 2, 2, 3, 3);
		paths.addBase("t", 1, 2, 2, 3, 3, 1);
		// paths.addBase("", 1, 3, 2, 1, 3, 2);
		paths.addBase("s", 1, 2, 2, 1, 3, 3);
		paths.constructTable();
		Writer writer = new PrintWriter(System.out);
		PathAlgebra.writeAlgebra(writer, paths);
	}
	public void testAutomaton() throws IOException {
		/*
		 * construct group table of S3.
		 */
		PathAlgebra paths = new PathAlgebra(6);
		paths.addBase("0");
		paths.addBase("0:0", 0, 0);
		paths.addBase("0:1", 0, 1);
		paths.constructTable();
		Writer writer = new PrintWriter(System.out);
		PathAlgebra.writeAlgebra(writer, paths);
	}
	public void testS3() throws IOException {
		/*
		 * construct group table of S3.
		 */
		PathAlgebra paths = new PathAlgebra(6);
		paths.addBase("1", 1, 1, 2, 2, 3, 3);
		paths.addBase("t1", 1, 2, 2, 3, 3, 1);
		paths.addBase("t2", 1, 3, 2, 1, 3, 2);
		paths.addBase("s1", 1, 2, 2, 1, 3, 3);
		paths.addBase("s2", 2, 3, 3, 2, 1, 1);
		paths.addBase("s3", 3, 1, 1, 3, 2, 2);
		paths.constructTable();
		Writer writer = new PrintWriter(System.out);
		PathAlgebra.writeAlgebra(writer, paths);
		Debug.log().debug("t^-1 s1 t = " + paths.conjugates("t2", "s1"));
	}
	public void testS4() throws IOException {
		/*
		 * construct group table of S4 from generators.
		 */
		PathAlgebra paths = new PathAlgebra(6);
		paths.addBase("1", 1, 1, 2, 2, 3, 3, 4, 4);
		paths.addBase("u", 1, 2, 2, 3, 3, 4, 4, 1);
		paths.addBase("t", 1, 2, 2, 3, 3, 1, 4, 4);
		paths.addBase("s", 1, 2, 2, 1, 3, 3, 4, 4);
		paths.constructTable();
		Writer writer = new PrintWriter(System.out);
		PathAlgebra.writeAlgebra(writer, paths);
	}
	public void testS3_memo_8() throws IOException {
		if (true) {
			/*
			 * construct group table of S3 from 2 letters.
			 */
			PathAlgebra paths = new PathAlgebra(6);
			paths.addBase("s", 1, 2, 2, 1, 3, 3);
			paths.addBase("t", 2, 1, 3, 2, 1, 3);
			paths.constructTable();
			Writer writer = new PrintWriter(System.out);
			PathAlgebra.writeAlgebra(writer, paths);
		}
		if (true) {
			/*
			 * construct regular representation.
			 */
			PathAlgebra paths = new PathAlgebra(6);
			paths.addBase("1", 1, 1, 2, 2, 3, 3);
			paths.addBase("t1", 1, 2, 2, 3, 3, 1);
			paths.addBase("t2", 1, 3, 2, 1, 3, 2);
			paths.addBase("s1", 1, 2, 2, 1, 3, 3);
			paths.addBase("s2", 2, 3, 3, 2, 1, 1);
			paths.addBase("s3", 3, 1, 1, 3, 2, 2);
			paths.constructTable();
			List<String> names = paths.getNames();
			int[] invs = paths.getInverses();
			int[][] table = paths.getTable();
			int[][] reps = table.clone();
			for (int i = 0, n = invs.length; i < n; ++i) {
				reps[i] = table[invs[i]];
			}
			Writer writer = new PrintWriter(System.out);
			for (int g = 0, n = names.size(); g < n; ++g) {
				writer.write("------ ");
				writer.write(names.get(g));
				PathAlgebra.writeEol(writer);
				for (int i = 0; i < n; ++i) {
					for (int j = 0; j < n; ++j) {
						PathAlgebra.writeSpace(writer, 1);
						if (reps[j][i] == g) {
							writer.write("1");
						} else {
							writer.write("0");
						}
					}
					PathAlgebra.writeEol(writer);
				}
			}
		}
		if(true){
			/*
			 * Z/3Z
			 */
			PathAlgebra paths = new PathAlgebra(6);
			paths.addBase("0", 0, 0, 1, 1, 2, 2);
			paths.addBase("1", 0, 1, 1, 2, 2, 0);
			paths.addBase("2", 0, 2, 1, 0, 2, 1);
			paths.constructTable();
			Writer writer = new PrintWriter(System.out);
			PathAlgebra.writeAlgebra(writer, paths);
		}
	}
}
