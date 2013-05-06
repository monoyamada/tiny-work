package parser.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.lang.Messages;
import tiny.lang.StringHelper;

public class ParserTest_1_2 extends ParserTest_1_0 {
	static class Variable implements Comparable<Variable> {
		public static final Variable[] EMPTY_ARRAY = {};
		static final int ZERO = 0;
		static final int ONE = ZERO + 1;
		static final int NONE = ONE + 1;
		static final int BOUNDED_START = NONE + 1;
		static final int BOUNDED_END = BOUNDED_START + 1;
		static final int FREE = BOUNDED_END + 1;
		static final int NUMBER_OF_TYPES = FREE + 1;

		static interface Info {
			String typeName();
		}

		static final Info[] INFOS = Variable.newInfos();

		static Info[] newInfos() {
			Info[] out = new Info[NUMBER_OF_TYPES];
			out[ZERO] = new Info() {
				public String typeName() {
					return "zero";
				}
			};
			out[ONE] = new Info() {
				public String typeName() {
					return "one";
				}
			};
			out[NONE] = new Info() {
				public String typeName() {
					return "none";
				}
			};
			out[FREE] = new Info() {
				public String typeName() {
					return "free";
				}
			};
			out[BOUNDED_START] = new Info() {
				public String typeName() {
					return "start";
				}
			};
			out[BOUNDED_END] = new Info() {
				public String typeName() {
					return "end";
				}
			};
			return out;
		};

		/**
		 * assumes {@link Variable} is unique instance for {@link Variable#name}.
		 * 
		 * @param xs
		 * @param ys
		 * @return
		 */
		public static boolean equalArray(Variable[] xs, Variable[] ys) {
			if (xs == ys) {
				return true;
			} else if (xs == null || ys == null) {
				return false;
			} else if (xs.length != ys.length) {
				return false;
			}
			for (int i = 0, n = xs.length; i < n; ++i) {
				if (xs[i] != ys[i]) {
					return false;
				}
			}
			return true;
		}

		final String name;
		final int index;
		final int type;
		private Variable opposite;

		Variable(String name, int index) {
			this(name, index, FREE);
		}
		Variable(String name, int index, int type) {
			if (name == null) {
				String msg = Messages.getUnexpectedValue("name", "non-null", "null");
				throw new IllegalArgumentException(msg);
			}
			this.name = name;
			this.index = index;
			this.type = type;
		}
		/*
		 * must be unique instance.
		 * 
		 * @Override public boolean equals(Object x) { try { return
		 * this.equalVariable((Variable) x); } catch (Exception ex) { } return
		 * false; }
		 */
		public boolean equalVariable(Variable x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			}
			return this.name.equals(x.name) && this.type == x.type;
		}
		public String name() {
			return this.name;
		}
		public int type() {
			return this.type;
		}
		public boolean isType(int type) {
			return this.type == type;
		}
		public boolean isConstant() {
			switch (this.type) {
			case ZERO:
			case ONE:
				return true;
			default:
				return false;
			}
		}
		public boolean isVariable() {
			switch (this.type) {
			case FREE:
			case BOUNDED_START:
			case BOUNDED_END:
				return true;
			default:
				return false;
			}
		}
		public boolean isBounded() {
			switch (this.type) {
			case BOUNDED_START:
			case BOUNDED_END:
				return true;
			default:
				return false;
			}
		}
		Variable opposite() {
			return this.opposite;
		}
		@Override
		public String toString() {
			return this.name;
		}
		Variable makeOpposite(Variable x) {
			this.opposite = x;
			x.opposite = this;
			return this;
		}
		Info info() {
			return Variable.INFOS[this.type()];
		}
		String typeName() {
			return this.info().typeName();
		}
		/**
		 * compares by index not but name.
		 */
		@Override
		public int compareTo(Variable x) {
			if (this == x) {
				return 0;
			} else if (x == null) {
				return -1;
			} else if (this.index < x.index) {
				return -1;
			} else if (this.index == x.index) {
				return 0;
			}
			return 1;
		}
	}

	static class FlattenEauation {
		static FlattenEauation newZero() {
			FlattenEauation out = new FlattenEauation();
			out.terms = Collections.emptyList();
			return out;
		}
		static FlattenEauation newOne() {
			FlattenEauation out = new FlattenEauation();
			out.terms = Collections.singletonList(Collections.<Variable> emptyList());
			return out;
		}
		static FlattenEauation newVariable(Variable x) {
			switch (x.type()) {
			case Variable.ZERO:
				return FlattenEauation.newZero();
			case Variable.ONE:
				return FlattenEauation.newOne();
			default:
			break;
			}
			FlattenEauation out = new FlattenEauation();
			out.terms = Collections.singletonList(Collections.singletonList(x));
			return out;
		}

		static Variable[] array(List<Variable> vars, int begin, int end) {
			if (end < begin) {
				String msg = "invalid range=[" + begin + ", " + end + ")";
				throw new IllegalArgumentException(msg);
			} else if (begin == end) {
				return Variable.EMPTY_ARRAY;
			}
			Variable[] output = new Variable[end - begin];
			return array(output, 0, vars, begin, end);
		}
		static Variable[] array(Variable[] output, List<Variable> vars, int begin,
				int end) {
			return array(output, 0, vars, begin, end);
		}
		static Variable[] array(Variable[] output, int offset, List<Variable> vars,
				int begin, int end) {
			if (end < begin) {
				String msg = "invalid range=[" + begin + ", " + end + ")";
				throw new IllegalArgumentException(msg);
			}
			for (int i = 0, n = end - begin; i < n; ++i) {
				output[offset + i] = vars.get(begin + i);
			}
			return output;
		}

		List<List<Variable>> terms;
		Variable variable;

		boolean isZero() {
			return this.terms.size() < 1;
		}
		boolean isOne() {
			return this.terms.size() == 1 && this.terms.get(0).size() == 0;
		}
		FlattenEauation plus(FlattenEauation x) {
			if (this.isOne()) {
				return x;
			} else if (x.isOne()) {
				return this;
			}
			FlattenEauation out = new FlattenEauation();
			int n = this.terms.size();
			int xn = x.terms.size();
			out.terms = new ArrayList<List<Variable>>(n + xn);
			out.terms.addAll(this.terms);
			out.terms.addAll(x.terms);
			return out;
		}
		FlattenEauation multiplies(FlattenEauation x) {
			if (this.isOne()) {
				return this;
			} else if (x.isOne()) {
				return x;
			} else if (this.isOne()) {
				return x;
			} else if (x.isOne()) {
				return this;
			}
			FlattenEauation out = new FlattenEauation();
			int n = this.terms.size();
			int xn = x.terms.size();
			out.terms = new ArrayList<List<Variable>>(n * xn);
			for (int i = 0; i < n; ++i) {
				List<Variable> pre = this.terms.get(i);
				for (int xi = 0; xi < xn; ++xi) {
					ArrayList<Variable> term = new ArrayList<Variable>();
					term.addAll(pre);
					term.addAll(x.terms.get(xi));
					out.terms.add(term);
				}
			}
			return out;
		}
		public FlattenEauation setZero(Variable zero) {
			List<List<Variable>> terms = this.terms;
			int nz = 0;
			for (int i = 0, n = terms.size(); i < n; ++i) {
				if (terms.get(i).contains(zero)) {
					terms.set(i, null);
					++nz;
				}
			}
			if (nz == terms.size()) {
				this.terms = Collections.singletonList(Collections
						.<Variable> emptyList());
			} else if (0 < nz) {
				List<List<Variable>> newTerms = new ArrayList<List<Variable>>();
				for (int ii = 0, nn = terms.size(); ii < nn; ++ii) {
					if (terms.get(ii) != null) {
						newTerms.add(terms.get(ii));
					}
				}
				this.terms = newTerms;
			}
			return this;
		}
		public FlattenEauation setOne(Variable one) {
			List<List<Variable>> terms = this.terms;
			for (int i = 0, n = terms.size(); i < n; ++i) {
				List<Variable> term = terms.get(i);
				List<Variable> newTerm = null;
				for (int ii = 0, nn = term.size(); ii < nn; ++ii) {
					if (term.get(ii).equals(one)) {
						if (newTerm == null) {
							newTerm = new ArrayList<Variable>();
							for (int jj = 0; jj < ii; ++jj) {
								newTerm.add(term.get(jj));
							}
						}
					} else if (newTerm != null) {
						newTerm.add(term.get(ii));
					}
				}
				if (newTerm != null) {
					terms.set(i, newTerm);
				}
			}
			return this;
		}
	}

	static class TreeParser {
		private Map<String, Variable> variableMap;
		private Variable zero;
		private Variable one;
		private Variable none;
		private Variable[] nonFreeVariables;

		Variable zero() {
			return this.zero;
		}
		Variable one() {
			return this.one;
		}
		Variable none() {
			return this.none;
		}

		private TreeParser parse(ParseNode eqs) throws ParserException {
			this.initializeParse();
			this.fixNonFreeVariables(eqs);
			List<FlattenEauation> fes = this.flattenEquations(eqs);
			for (FlattenEauation fe : fes) {
				if (fe.isZero()) {
					Debug.log().debug(fe.variable + " = 0");
				} else if (fe.isOne()) {
					Debug.log().debug(fe.variable + " = 1");
				} else {
					Debug.log().debug(
							fe.variable + " = " + StringHelper.join(fe.terms, " + "));
				}
			}
			return this;
		}

		private TreeParser initializeParse() {
			this.variableMap = null;
			Map<String, Variable> map = this.variableMap(true);
			Variable zero = new Variable("#0", map.size(), Variable.ZERO);
			map.put(zero.name(), zero);
			Variable one = new Variable("#1", map.size(), Variable.ONE);
			this.variableMap(true).put(one.name(), one);
			Variable none = new Variable("$", map.size(), Variable.NONE);
			this.variableMap(true).put(none.name(), none);
			this.zero = zero;
			this.one = one;
			this.none = none;
			this.nonFreeVariables = null;
			return this;
		}
		private Variable[] nonFreeVariables(boolean anyway) {
			if (this.nonFreeVariables == null && anyway) {
				this.nonFreeVariables = this.newNonFreeVariables();
			}
			return this.nonFreeVariables;
		}
		private Variable[] newNonFreeVariables() {
			Map<String, Variable> map = this.variableMap(true);
			Variable[] vs = new Variable[map.size()];
			Iterator<Variable> p = map.values().iterator();
			int count = 0;
			while (p.hasNext()) {
				Variable v = p.next();
				if (v.isType(Variable.FREE)) {
					continue;
				}
				vs[v.index] = v;
				++count;
			}
			if (count < vs.length) {
				vs = ArrayHelper.sub(vs, 0, count);
			}
			return vs;
		}
		private TreeParser fixNonFreeVariables(ParseNode eqs)
				throws ParserException {
			ParseNode node = eqs.firstNode();
			for (; node != null; node = node.nextNode()) {
				this.fixBoundedVariables(node);
			}
			this.nonFreeVariables(true);
			return this;
		}
		private TreeParser fixBoundedVariables(ParseNode node)
				throws ParserException {
			if (!node.isType(ParseNode.ASSIGNS)) {
				String msg = "root of equation must be assigns but actual type="
						+ node.typeName();
				throw new ParserException(msg);
			} else if (!node.isBinary()) {
				String msg = "assigns must be a binary operator";
				throw new ParserException(msg);
			}
			ParseNode lhs = node.firstNode();
			if (!lhs.isType(ParseNode.VARIABLE)) {
				String msg = "lhs of assings must be a variable but actual type="
						+ node.typeName();
				throw new ParserException(msg);
			} else if (!lhs.isLeaf()) {
				String msg = "variable must be a binary leaf";
				throw new ParserException(msg);
			}
			String name = lhs.value();
			if (name == null) {
				String msg = "value of variable must not be null";
				throw new ParserException(msg);
			}
			this.makeBounded(name);
			return this;
		}
		private Variable makeBounded(String name) throws ParserException {
			Map<String, Variable> map = this.variableMap(true);
			String startName = name;
			String endName = "-" + name;
			Variable start = map.get(startName);
			if (start != null) {
				String msg = "bounded variable=" + startName + " already exists";
				throw new ParserException(msg);
			}
			Variable end = map.get(endName);
			if (end != null) {
				String msg = "bounded variable=" + endName + " already exists";
				throw new ParserException(msg);
			}
			start = new Variable(startName, map.size(), Variable.BOUNDED_START);
			map.put(start.name(), start);
			end = new Variable(endName, map.size(), Variable.BOUNDED_END);
			map.put(end.name(), end);
			start.makeOpposite(end);
			return start;
		}

		private List<FlattenEauation> flattenEquations(ParseNode eqs)
				throws ParserException {
			List<FlattenEauation> out = new ArrayList<FlattenEauation>();
			ParseNode eq = eqs.firstNode();
			for (; eq != null; eq = eq.nextNode()) {
				FlattenEauation x = this.flattenEquation(eq.lastNode());
				x.variable = this.variable(eq.firstNode().value(), true);
				out.add(x);
			}
			return this.eliminateConstants(out);
		}
		private List<FlattenEauation> eliminateConstants(List<FlattenEauation> out) {
			out = this.eliminateZeros(out);
			return this.eliminateOnes(out);
		}
		private List<FlattenEauation> eliminateZeros(List<FlattenEauation> out) {
			List<Variable> zeros = null;
			for (int i = 0, n = out.size(); i < n; ++i) {
				FlattenEauation x = out.get(i);
				if (x.isZero()) {
					if (zeros == null) {
						zeros = new ArrayList<Variable>();
					}
					zeros.add(x.variable);
				}
			}
			if (zeros == null) {
				return out;
			}
			while (0 < zeros.size()) {
				Variable zero = zeros.remove(zeros.size() - 1);
				for (int i = 0, n = out.size(); i < n; ++i) {
					FlattenEauation x = out.get(i);
					if (x.isZero()) {
						continue;
					} else if (x.setZero(zero).isZero()) {
						zeros.add(x.variable);
					}
				}
			}
			return out;
		}
		private List<FlattenEauation> eliminateOnes(List<FlattenEauation> out) {
			List<Variable> ones = null;
			for (int i = 0, n = out.size(); i < n; ++i) {
				FlattenEauation x = out.get(i);
				if (x.isOne()) {
					if (ones == null) {
						ones = new ArrayList<Variable>();
					}
					ones.add(x.variable);
				}
			}
			if (ones == null) {
				return out;
			}
			while (0 < ones.size()) {
				Variable one = ones.remove(ones.size() - 1);
				for (int i = 0, n = out.size(); i < n; ++i) {
					FlattenEauation x = out.get(i);
					if (x.isOne()) {
						continue;
					} else if (x.setOne(one).isOne()) {
						ones.add(x.variable);
					}
				}
			}
			return out;
		}

		private FlattenEauation flattenEquation(ParseNode eq)
				throws ParserException {
			switch (eq.type()) {
			case ParseNode.ZERO:
				if (!eq.isLeaf()) {
					String msg = "zero must be a leaf";
					throw new ParserException(msg);
				}
				return FlattenEauation.newVariable(this.zero());
			case ParseNode.ONE:
				if (!eq.isLeaf()) {
					String msg = "one must be a leaf";
					throw new ParserException(msg);
				}
				return FlattenEauation.newVariable(this.one());
			case ParseNode.VARIABLE: {
				String name = eq.value();
				if (name == null) {
					String msg = "value of variable must not be null";
					throw new ParserException(msg);
				} else if (!eq.isLeaf()) {
					String msg = "value must be a leaf";
					throw new ParserException(msg);
				}
				Variable x = this.variable(name, true);
				return FlattenEauation.newVariable(x);
			}
			case ParseNode.POWERS: {
				throw new Error("powers is not yet supported. it need more work.");
			}
			case ParseNode.NUMBER_VARIABLE: {
				throw new Error(
						"number variable is not yet supported. it need more work.");
			}
			case ParseNode.NUMBER: {
				throw new Error("number is not yet supported. it need more work.");
			}
			case ParseNode.MULTIPLIES: {
				ParseNode child = eq.firstNode();
				if (child == null) {
					String msg = "multiplies must have children";
					throw new ParserException(msg);
				}
				FlattenEauation x = this.flattenEquation(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					FlattenEauation y = this.flattenEquation(child);
					x = x.multiplies(y);
				}
				return x;
			}
			case ParseNode.PLUS: {
				ParseNode child = eq.firstNode();
				if (child == null) {
					String msg = "plus must have children";
					throw new ParserException(msg);
				}
				FlattenEauation x = this.flattenEquation(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					FlattenEauation y = this.flattenEquation(child);
					x = x.plus(y);
				}
				return x;
			}
			default: {
				String msg = "unexpected type of node=" + eq.typeName();
				throw new ParserException(msg);
			}
			}
		}

		private Map<String, Variable> variableMap(boolean anyway) {
			if (this.variableMap == null) {
				this.variableMap = new HashMap<String, Variable>();
			}
			return this.variableMap;
		}
		private Variable variable(String name, boolean anyway) {
			Map<String, Variable> map = this.variableMap(anyway);
			if (name == null && map == null) {
				return null;
			}
			Variable x = map.get(name);
			if (x == null && anyway) {
				x = new Variable(name, map.size());
				map.put(name, x);
			}
			return x;
		}
	}

	public void testParse() throws IOException {
		File file = new File("data/ParserTest_1.tiny");
		FileInputStream in = null;
		ParseNode node = null;
		try {
			in = new FileInputStream(file);
			AsciiParser parser = new AsciiParser();
			long[] lines = AsciiParser.newLines(in.getChannel());
			parser.setLines(lines);
			ByteInput input = new ByteFileInput(in.getChannel().position(0), 512);
			node = parser.parse(input);
			Debug.log().debug(node != null ? node.toInfix() : null);
		} finally {
			FileHelper.close(in);
		}
		TreeParser parser = new TreeParser();
		parser.parse(node);
	}
}
