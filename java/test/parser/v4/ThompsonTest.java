package parser.v4;

import java.util.List;

import parser.v4.ThompsonVm.Code;
import parser.v4.ThompsonVm.Node;
import parser.v4.ThompsonVm.Compiler;
import tiny.lang.Debug;
import base.TestBase;

import static parser.v4.ThompsonVm.newCharNode;

public class ThompsonTest extends TestBase {

	public void test_1() {
		Node a = newCharNode('a').oneMore();
		Node b = newCharNode('b').oneMore();
		Node eq = a.multiplies(b);
		Debug.log().debug(eq);

		Compiler compiler = new Compiler();
		List<Code> codes = compiler.compile(eq);
		Debug.log().debug("-- code --");
		for (int i = 0, n = codes.size(); i < n; ++i) {
			System.out.println(i + ": " + codes.get(i));
		}
	}
	public void test_2() {
		Node a = newCharNode('a').anyMore();
		Node b = newCharNode('b').oneMore();
		Node eq = a.multiplies(b);
		Debug.log().debug(eq);

		Compiler compiler = new Compiler();
		List<Code> codes = compiler.compile(eq);
		Debug.log().debug("-- code --");
		for (int i = 0, n = codes.size(); i < n; ++i) {
			System.out.println(i + ": " + codes.get(i));
		}
	}
	public void test_3() {
		Node a = newCharNode('a').oneOrZero();
		Node b = newCharNode('b').oneMore();
		Node eq = a.multiplies(b);
		Debug.log().debug(eq);

		Compiler compiler = new Compiler();
		List<Code> codes = compiler.compile(eq);
		Debug.log().debug("-- code --");
		for (int i = 0, n = codes.size(); i < n; ++i) {
			System.out.println(i + ": " + codes.get(i));
		}
	}
	public void test_4() {
		Node a = newCharNode('a').anyMore().anyMore();
		Node eq = a;
		Debug.log().debug(eq);

		Compiler compiler = new Compiler();
		List<Code> codes = compiler.compile(eq);
		Debug.log().debug("-- code --");
		for (int i = 0, n = codes.size(); i < n; ++i) {
			System.out.println(i + ": " + codes.get(i));
		}
	}
	public void test_5() {
		Node a = newCharNode('a');
		Node a2 = newCharNode('a').multiplies('a');
		Node eq = a.multiplies(a2).plus(a2.multiplies(a));
		Debug.log().debug(eq);

		Compiler compiler = new Compiler();
		List<Code> codes = compiler.compile(eq);
		Debug.log().debug("-- code --");
		for (int i = 0, n = codes.size(); i < n; ++i) {
			System.out.println(i + ": " + codes.get(i));
		}
	}

	public void testEliminateJump() {
		{
			Node a = newCharNode('a').anyMore().anyMore();
			Node eq = a;
			Debug.log().debug(eq);

			Compiler compiler = new Compiler();
			List<Code> codes = compiler.compile(eq);
			Debug.log().debug("-- code --");
			for (int i = 0, n = codes.size(); i < n; ++i) {
				System.out.println(i + ": " + codes.get(i));
			}
			int count = compiler.eliminateJump(codes);
			if (0 < count) {
				compiler.eliminateUnusedCode(codes);
			}
			Debug.log().debug("-- eliminated jump --");
			for (int i = 0, n = codes.size(); i < n; ++i) {
				System.out.println(i + ": " + codes.get(i));
			}
		}
		{
			Node a = newCharNode('a').anyMore();
			Node b = newCharNode('b').oneMore();
			Node eq = a.multiplies(b);
			Debug.log().debug(eq);

			Compiler compiler = new Compiler();
			List<Code> codes = compiler.compile(eq);
			Debug.log().debug("-- code --");
			for (int i = 0, n = codes.size(); i < n; ++i) {
				System.out.println(i + ": " + codes.get(i));
			}
			int count = compiler.eliminateJump(codes);
			if (0 < count) {
				compiler.eliminateUnusedCode(codes);
			}
			Debug.log().debug("-- eliminated jump --");
			for (int i = 0, n = codes.size(); i < n; ++i) {
				System.out.println(i + ": " + codes.get(i));
			}
		}
	}
}
