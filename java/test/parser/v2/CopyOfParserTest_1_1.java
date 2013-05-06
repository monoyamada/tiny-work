package parser.v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parser.v2.ParserTest_1_0.ParseNode;

import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.lang.Messages;

public class CopyOfParserTest_1_1 extends ParserTest_1_0 {
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
		public int hashCode() {
			return this.index;
		}
		@Override
		public boolean equals(Object x) {
			try {
				return this.equalVariable((Variable) x);
			} catch (Exception ex) {
			}
			return false;
		}
		public boolean equalVariable(Variable x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			}
			return this.index == x.index;
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
		public static final FlattenEauation[] EMPTY_ARRAY = {};

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
			if (this.isZero()) {
				return x;
			} else if (x.isZero()) {
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
			if (this.isZero()) {
				return this;
			} else if (x.isZero()) {
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

	static class Term {
		static Term newTerm(List<Variable> xs) {
			int n = xs.size();
			if (n == 0) {
				return new Term(Variable.EMPTY_ARRAY);
			}
			Variable[] ys = xs.toArray(Variable.EMPTY_ARRAY);
			return new Term(ys);
		}

		final Variable[] variables;

		Term(Variable[] variables) {
			this.variables = variables;
		}
		public int hashCode() {
			return Arrays.hashCode(this.variables);
		}
		@Override
		public boolean equals(Object x) {
			try {
				return this.equalTerm((Term) x);
			} catch (Exception ex) {
			}
			return false;
		}
		public boolean equalTerm(Term x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			}
			return Arrays.equals(this.variables, x.variables);
		}
	}

	static class ConstantEliminator extends ParseNodeRecycler {
		final TreeParser parser;

		ConstantEliminator(TreeParser parser) {
			this.parser = parser;
		}
		private ParseNode eliminate(ParseNode eqs) throws ParserException {
			ParseNode newEqs = eqs.clone().clearChildren();
			Map<Variable, ParseNode> constMap = new HashMap<Variable, ParseNode>();
			ParseNode zero = new ParseNode(ParseNode.ZERO);
			ParseNode one = new ParseNode(ParseNode.ONE);
			int newConst = 0;
			for (ParseNode eq = eqs.firstNode(); eq != null; eq = eq.nextNode()) {
				ParseNode newEq = eq.clone();
				ParseNode lhs = eq.firstNode().clone();
				ParseNode rhs = this.eliminate(eq.lastNode(), constMap);
				newEq.add(lhs).add(rhs);
				Variable x = this.variable(lhs.value());
				if (rhs.isType(ParseNode.ZERO)) {
					constMap.put(x, zero);
					++newConst;
				} else if (rhs.isType(ParseNode.ONE)) {
					constMap.put(x, one);
					++newConst;
				}
				newEqs.add(newEq);
			}
			while (0 < newConst) {
				newConst = 0;
				for (ParseNode eq = newEqs.firstNode(); eq != null; eq = eq.nextNode()) {
					ParseNode lhs = eq.firstNode();
					ParseNode oldRhs = eq.lastNode();
					ParseNode newRhs = this.eliminate(oldRhs, constMap);
					eq.clearChildren().add(lhs.clearSibling()).add(newRhs);
					this.backNode(oldRhs);
					Variable x = this.variable(lhs.value());
					if (constMap.get(x) == null) {
						if (newRhs.isType(ParseNode.ZERO)) {
							constMap.put(x, zero);
							++newConst;
						} else if (newRhs.isType(ParseNode.ONE)) {
							constMap.put(x, one);
							++newConst;
						}
					}
				}
			}
			return newEqs;
		}
		private ParseNode eliminate(ParseNode eq, Map<Variable, ParseNode> constMap)
				throws ParserException {
			switch (eq.type()) {
			case ParseNode.ZERO:
			case ParseNode.ONE:
				if (!eq.isLeaf()) {
					String msg = "zero/one must be a leaf";
					throw new ParserException(msg);
				}
				return this.cloneNode(eq);
			case ParseNode.VARIABLE: {
				if (!eq.isLeaf()) {
					String msg = "variable must be a leaf";
					throw new ParserException(msg);
				}
				ParseNode newEq = constMap.get(this.variable(eq.value()));
				if (newEq != null) {
					return this.cloneNode(newEq);
				}
				return this.cloneNode(eq);
			}
			case ParseNode.MULTIPLIES: {
				ParseNode newEq = this.cloneNode(eq);
				int nChild = 0;
				for (ParseNode child = eq.firstNode(); child != null; child = child
						.nextNode()) {
					ParseNode newChild = eliminate(child, constMap);
					if (newChild.isType(ParseNode.ZERO)) {
						return newChild;
					} else if (newChild.isType(ParseNode.ONE)) {
					} else {
						newEq.add(newChild);
						++nChild;
					}
				}
				switch (nChild) {
				case 0:
					return newEq.type(ParseNode.ONE);
				case 1: {
					ParseNode x = newEq.firstNode();
					this.backNode(newEq.clearChildren());
					return x;
				}
				default:
					return newEq;
				}
			}
			case ParseNode.PLUS: {
				ParseNode newEq = this.cloneNode(eq);
				int nChild = 0;
				for (ParseNode child = eq.firstNode(); child != null; child = child
						.nextNode()) {
					ParseNode newChild = eliminate(child, constMap);
					if (newChild.isType(ParseNode.ZERO)) {
					} else {
						newEq.add(newChild);
						++nChild;
					}
				}
				switch (nChild) {
				case 0:
					return newEq.type(ParseNode.ZERO);
				case 1: {
					ParseNode x = newEq.firstNode();
					this.backNode(newEq.clearChildren());
					return x;
				}
				default:
					return newEq;
				}
			}
			default: {
				String msg = "unexpected type of node=" + eq.typeName();
				throw new ParserException(msg);
			}
			}
		}
		private Variable variable(String name) {
			return this.parser.variable(name, true);
		}
	}

	static class TreeParser {
		private Map<String, Variable> variableMap;
		private Variable zero;
		private Variable one;
		private Variable none;
		private Variable[] nonFreeVariables;
		private FlattenEauation[] eauations;

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
			ConstantEliminator task = new ConstantEliminator(this);
			ParseNode newEqs = task.eliminate(eqs);
			Debug.log().debug(newEqs.toInfix());
			this.eauations = this.flattenEquations(eqs).toArray(
					FlattenEauation.EMPTY_ARRAY);
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
			this.eliminateConstants(out);
			return this.checkDuplication(out);
		}
		private List<FlattenEauation> checkDuplication(List<FlattenEauation> out) {
			for (int i = 0, n = out.size(); i < n; ++i) {
				FlattenEauation eq = out.get(i);
				List<List<Variable>> terms = eq.terms;
				Set<List<Variable>> termMap = new HashSet<List<Variable>>();
				for (int ii = 0, nn = terms.size(); ii < nn; ++ii) {
					List<Variable> term = terms.get(ii);
					if (termMap.contains(term)) {
						Debug.log().debug("duplicated term=" + term + " in " + eq.variable);
					} else {
						termMap.add(term);
					}
				}
			}
			return out;
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

	static class CodeWriter {
		static final String METHOD_BEGIN = "Output parse%var%(Input input) {\n";
		static final String METHOD_END = "}\n";
		static final String PREPARE_BACKTRACK = "long position = input.position();\n";
		static final String DO_BACKTRACK = "input.position(position);\n";
		static final String NEW_OUTPUT = "this.newOutput(VAR_%var%, %param%);\n";
		static final String RETURN_ZERO = "return null;\n";
		static final String RETURN_ONE = "return " + NEW_OUTPUT;
		static final String NEW_OUTPUT_VARIABLE = "Output output = " + NEW_OUTPUT;
		static final String ADD_VARIABLE = "this.addOutput(output, %var%_%n%);\n";
		static final String RETURN_VARIABLE = "return output;\n";
		static final String VARIABLE_OUTPUT = "Output %var%_%n% = this.parse(VAR_%var%, input);\n";
		static final String VARIABLE_BEGIN = "if (%var%_%n% != null) {\n";
		static final String VARIABLE_FAIL = "} else {\n";
		static final String VARIABLE_END = "}\n";
		static final String BACK_OUTPUT = "this.backOutput(%var%_%n%);\n";

		static Appendable writeEquation(Appendable output, FlattenEauation eq,
				int indent) throws IOException {
			CodeWriter.indent(output, indent).append(eq.variable.name).append(" = ");
			if (eq.isZero()) {
				return output.append("0\n");
			} else if (eq.isOne()) {
				return output.append("1\n");
			}
			List<List<Variable>> terms = eq.terms;
			int n = terms.size();
			for (int i = 0; i < n; ++i) {
				if (i != 0) {
					output.append(" + ");
				}
				List<Variable> term = terms.get(i);
				int nn = term.size();
				if (nn < 1) {
					output.append('1');
					continue;
				}
				for (int ii = 0; ii < nn; ++ii) {
					Variable x = term.get(ii);
					if (ii != 0) {
						output.append(' ');
					}
					output.append(x.name());
				}
			}
			return output.append("\n");
		}
		static Appendable writeParse(Appendable output, FlattenEauation eq,
				int indent) throws IOException {
			final Variable var = eq.variable;
			String text = METHOD_BEGIN.replaceAll("%var%", var.name);
			CodeWriter.indent(output, indent).append(text);
			if (eq.isZero()) {
				CodeWriter.indent(output, indent + 1).append(RETURN_ZERO);
			} else if (eq.isOne()) {
				text = RETURN_ONE.replaceAll("%var%", var.name()).replaceAll("%param%",
						"input");
				CodeWriter.indent(output, indent + 1).append(text);
			} else {
				final List<List<Variable>> terms = eq.terms;
				final int n = terms.size();
				boolean hasOne = false;
				for (int i = 0; i < n; ++i) {
					if (i == 0) {
						CodeWriter.indent(output, indent + 1).append(PREPARE_BACKTRACK);
					} else {
						CodeWriter.indent(output, indent + 1).append(DO_BACKTRACK);
					}
					List<Variable> term = terms.get(i);
					int nn = term.size();
					if (nn < 1) {
						hasOne = true;
						text = RETURN_ONE.replaceAll("%var%", var.name()).replaceAll(
								"%param%", "input");
						CodeWriter.indent(output, indent + 1).append(text);
						continue;
					}
					for (int ii = 0; ii < nn; ++ii) {
						Variable x = term.get(ii);
						text = VARIABLE_OUTPUT.replaceAll("%var%", x.name()).replaceAll(
								"%n%", Integer.toString(ii + 1));
						CodeWriter.indent(output, indent + ii + 1).append(text);
						text = VARIABLE_BEGIN.replaceAll("%var%", x.name()).replaceAll(
								"%n%", Integer.toString(ii + 1));
						CodeWriter.indent(output, indent + ii + 1).append(text);
					}
					text = NEW_OUTPUT_VARIABLE.replaceAll("%var%", var.name())
							.replaceAll("%param%", "position");
					CodeWriter.indent(output, indent + nn + 1).append(text);
					for (int ii = 0; ii < nn; ++ii) {
						Variable x = term.get(ii);
						text = ADD_VARIABLE.replaceAll("%var%", x.name).replaceAll("%n%",
								Integer.toString(ii + 1));
						CodeWriter.indent(output, indent + nn + 1).append(text);
					}
					CodeWriter.indent(output, indent + nn + 1).append(RETURN_VARIABLE);
					while (0 < nn--) {
						if (0 < nn) {
							CodeWriter.indent(output, nn + 1).append(VARIABLE_FAIL);
							for (int kk = nn; 0 < kk--;) {
								Variable x = term.get(kk);
								text = BACK_OUTPUT.replaceAll("%var%", x.name()).replaceAll(
										"%n%", Integer.toString(kk + 1));
								CodeWriter.indent(output, indent + nn + 2).append(text);
							}
						}
						CodeWriter.indent(output, indent + nn + 1).append(VARIABLE_END);
					}
				}
				if (!hasOne) {
					CodeWriter.indent(output, indent + 1).append(RETURN_ZERO);
				}
			}
			return CodeWriter.indent(output, indent).append(METHOD_END);
		}
		static Appendable indent(Appendable output, int depth) throws IOException {
			while (0 < depth--) {
				output.append('\t');
			}
			return output;
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
		for (FlattenEauation eq : parser.eauations) {
			Writer writer = new PrintWriter(System.out);
			writer.append("\t// ");
			CodeWriter.writeEquation(writer, eq, 0);
			CodeWriter.writeParse(writer, eq, 1);
			writer.flush();
		}
	}
}
