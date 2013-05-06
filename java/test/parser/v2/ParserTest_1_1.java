package parser.v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tiny.function.Function;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.lang.Messages;
import tiny.lang.StringHelper;

public class ParserTest_1_1 extends ParserTest_1_0 {
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
		public static final Comparator<Variable> NAME_COMPARATOR = new Comparator<Variable>() {
			@Override
			public int compare(Variable o1, Variable o2) {
				if (o1 == o2) {
					return 0;
				} else if (o2 == null) {
					return -1;
				} else if (o1 == null) {
					return 1;
				}
				return o1.name().compareTo(o2.name());
			}
		};

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

		private final String name;
		private final int index;
		private final int type;
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
			return this.compareTo(x) == 0;
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

	static class Term implements Comparable<Term> {
		public static final Term[] EMPTY_ARRAY = {};
		public static final Term ONE = new Term(Variable.EMPTY_ARRAY);
		public static final Function<Term, String> TO_INFIX = new Function<Term, String>() {
			@Override
			public String evaluate(Term source) {
				return source.toInfix();
			}
		};

		public static Term singleton(Variable x) {
			if (x == null) {
				return null;
			}
			switch (x.type()) {
			case Variable.ZERO:
				return null;
			case Variable.ONE:
				return Term.ONE;
			default:
				return new Term(new Variable[] { x });
			}
		}

		private final Variable[] variables;
		private int countBounded;

		public Term(Variable[] variables) {
			this.variables = variables;
			this.countBounded = -1;
		}
		public Variable[] variables() {
			return this.variables;
		}
		public int size() {
			return this.variables.length;
		}
		public Variable get(int i) {
			return this.variables[i];
		}
		public boolean isOne() {
			return this.variables.length < 1;
		}
		public int countBounded() {
			if (this.countBounded < 0) {
				this.countBounded = this.newCountBounded();
			}
			return this.countBounded;
		}
		private int newCountBounded() {
			int count = 0;
			Variable[] xs = this.variables;
			for (int i = 0, n = xs.length; i < n; ++i) {
				if (xs[i].isBounded()) {
					++count;
				}
			}
			return count;
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
			return this.compareTo(x) == 0;
		}
		@Override
		public int compareTo(Term x) {
			if (this == x) {
				return 0;
			} else if (x == null) {
				return -1;
			}
			Variable[] ts = this.variables;
			Variable[] xs = x.variables;
			if (ts.length < xs.length) {
				return -1;
			} else if (ts.length > xs.length) {
				return 1;
			}
			for (int i = 0, n = ts.length; i < n; ++i) {
				int sign = ts[i].compareTo(xs[i]);
				if (sign != 0) {
					return sign;
				}
			}
			return 0;
		}
		public ScaledTerm toScaledTerm() {
			return new ScaledTerm(this.variables());
		}
		public String toInfix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeInfix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public Appendable writeInfix(Appendable output) throws IOException {
			Variable[] xs = this.variables;
			switch (xs.length) {
			case 0:
				return output.append("1");
			case 1:
				return output.append(xs[0].name());
			default:
				for (int i = 0, n = xs.length; i < n; ++i) {
					if (i != 0) {
						output.append(' ');
					}
					output.append(xs[i].name());
				}
				return output;
			}
		}
	}

	static class TermArray implements Comparable<TermArray> {
		public static final TermArray[] EMPTY_ARRAY = {};
		public static final TermArray ZERO = new TermArray(Term.EMPTY_ARRAY);
		public static final Function<TermArray, String> TO_INFIX = new Function<TermArray, String>() {
			@Override
			public String evaluate(TermArray source) {
				return source.toInfix();
			}
		};

		private final Term[] terms;

		public TermArray(Term term) {
			if (term == null) {
				String msg = "term must not be a null";
				throw new IllegalArgumentException(msg);
			}
			this.terms = new Term[] { term };
		}
		public TermArray(Term[] terms) {
			if (terms == null) {
				String msg = "terms must not be a null";
				throw new IllegalArgumentException(msg);
			} else if (0 <= ArrayHelper.indexOf(terms, null)) {
				String msg = "terms must not contains a null";
				throw new IllegalArgumentException(msg);
			}
			this.terms = terms;
		}
		public Term[] terms() {
			return this.terms;
		}
		public int size() {
			return this.terms.length;
		}
		public Term get(int i) {
			return this.terms[i];
		}
		public boolean isZero() {
			return this.terms.length < 1;
		}
		public int hashCode() {
			return Arrays.hashCode(this.terms);
		}
		@Override
		public boolean equals(Object x) {
			try {
				return this.equalTermArray((TermArray) x);
			} catch (Exception ex) {
			}
			return false;
		}
		public boolean equalTermArray(TermArray x) {
			return this.compareTo(x) == 0;
		}
		@Override
		public int compareTo(TermArray x) {
			if (this == x) {
				return 0;
			} else if (x == null) {
				return -1;
			}
			Term[] ts = this.terms;
			Term[] xs = x.terms;
			if (ts.length < xs.length) {
				return -1;
			} else if (ts.length > xs.length) {
				return 1;
			}
			for (int i = 0, n = ts.length; i < n; ++i) {
				int sign = ts[i].compareTo(xs[i]);
				if (sign != 0) {
					return sign;
				}
			}
			return 0;
		}
		public TermArray toScaledTermArray() {
			switch (this.size()) {
			case 0:
				return TermArray.ZERO;
			case 1:
				return new TermArray(this.get(0).toScaledTerm());
			case 2:
				if (this.get(0).equalTerm(this.get(1))) {
					ScaledTerm x = this.get(0).toScaledTerm();
					ScaledTerm y = this.get(1).toScaledTerm();
					x.scale += y.scale;
					return new TermArray(x);
				}
				ScaledTerm x = this.get(0).toScaledTerm();
				ScaledTerm y = this.get(1).toScaledTerm();
				return new TermArray(new Term[] { x, y });
			default:
			break;
			}
			Map<Term, ScaledTerm> map = new TreeMap<Term, ScaledTerm>();
			for(int i=0,n=this.size();i<n;++i){
				Term term = this.get(i);
				ScaledTerm scaled = map.get(term);
				if(scaled==null){
					scaled=term.toScaledTerm();
					map.put(term, scaled);
				}else{
					++scaled.scale;
				}
			}
			return new TermArray(map.values().toArray(ScaledTerm.EMPTY_ARRAY));
		}
		public String toInfix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeInfix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public Appendable writeInfix(Appendable output) throws IOException {
			Term[] xs = this.terms;
			switch (xs.length) {
			case 0:
				return output.append("0");
			case 1:
				return xs[0].writeInfix(output);
			default:
				for (int i = 0, n = xs.length; i < n; ++i) {
					if (i != 0) {
						output.append(" + ");
					}
					xs[i].writeInfix(output);
				}
				return output;
			}
		}
	}

	static class ScaledTerm extends Term {
		public static final ScaledTerm[] EMPTY_ARRAY = {};
		
		long scale;

		public ScaledTerm(Variable[] variables) {
			this(variables, 1);
		}
		public ScaledTerm(Variable[] variables, long scale) {
			super(variables);
			this.scale = scale;
		}
		public long scale() {
			return this.scale;
		}
		public ScaledTerm toScaledTerm() {
			return this;
		}
		public Appendable writeInfix(Appendable output) throws IOException {
			if (this.scale() != 1) {
				output.append(Long.toString(this.scale()));
			}
			if (this.variables().length < 1) {
				return output.append("[]");
			}
			return super.writeInfix(output.append('[')).append(']');
		}
	}

	static class TreeAnalyzer extends ParseNodeRecycler {
		private final ParseNode parseTree;
		private Map<String, Variable> variableMap;
		private Variable zero;
		private Variable one;
		private Variable none;
		private Variable[] boundedVariables;
		private Variable[] freeVariables;
		private Variable[] constantVariables;
		private Variable[] variables;
		private ParseEquation[] parseEquations;
		private AdjacencyEquation[] adjacencyEquations;
		private FlattenEquation[] flattenEquations;
		private boolean parseDone;

		public TreeAnalyzer(ParseNode equations) {
			if (equations == null) {
				String msg = "equations must not be a null";
				throw new IllegalArgumentException(msg);
			}
			this.parseTree = equations;
			this.variableMap = this.newVariableMap();
		}
		public ParseNode parseTree() {
			return this.parseTree;
		}
		private Map<String, Variable> newVariableMap() {
			Map<String, Variable> map = new HashMap<String, Variable>();
			this.zero = this.zero(0);
			this.one = this.one(1);
			this.none = this.none(2);
			map.put(this.zero.name(), this.zero);
			map.put(this.one.name(), this.one);
			map.put(this.none.name(), this.none);
			return map;
		}

		public Variable zero() {
			return this.zero(this.variableMap().size());
		}
		public Variable one() {
			return this.one(this.variableMap().size());
		}
		public Variable none() {
			return this.none(this.variableMap().size());
		}
		private Variable zero(int index) {
			if (this.zero == null) {
				this.zero = new Variable("0", index, Variable.ZERO);
			}
			return this.zero;
		}
		private Variable one(int index) {
			if (this.one == null) {
				this.one = new Variable("1", index, Variable.ONE);
			}
			return this.one;
		}
		private Variable none(int index) {
			if (this.none == null) {
				this.none = new Variable("$", index, Variable.NONE);
			}
			return this.none;
		}
		private Map<String, Variable> variableMap() {
			return this.variableMap;
		}
		private Variable variable(String name, boolean anyway) {
			if (name == null) {
				return null;
			}
			Map<String, Variable> map = this.variableMap();
			Variable x = map.get(name);
			if (x == null && anyway) {
				x = new Variable(name, map.size());
				map.put(name, x);
			}
			return x;
		}

		public AdjacencyEquation[] adjacencyEquations() throws ParserException {
			if (this.adjacencyEquations == null) {
				this.adjacencyEquations = AdjacencyEquation.build(this);
			}
			return this.adjacencyEquations;
		}
		public ParseEquation[] parseEquations() throws ParserException {
			if (this.parseEquations == null) {
				this.parseEquations = ParseEquation.build(this);
			}
			return this.parseEquations;
		}
		public FlattenEquation[] flattenEquations() throws ParserException {
			if (this.flattenEquations == null) {
				this.flattenEquations = FlattenEquation.build(this);
			}
			return this.flattenEquations;
		}

		private Variable[] boundedVariables() throws ParserException {
			if (this.boundedVariables == null) {
				this.boundedVariables = this.newBoundedVariables();
			}
			return this.boundedVariables;
		}
		private Variable[] newBoundedVariables() throws ParserException {
			List<Variable> out = new ArrayList<Variable>();
			ParseNode node = this.parseTree.firstNode();
			for (; node != null; node = node.nextNode()) {
				Variable x = this.fixBoundedVariables(node);
				out.add(x);
			}
			return out.toArray(Variable.EMPTY_ARRAY);
		}
		private Variable fixBoundedVariables(ParseNode node) throws ParserException {
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
			return this.makeBounded(name);
		}
		private Variable makeBounded(String name) throws ParserException {
			Map<String, Variable> map = this.variableMap();
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
		private Variable[] freeVariables() throws ParserException {
			if (this.freeVariables == null) {
				this.freeVariables = this.newFreeVariables();
			}
			return this.freeVariables;
		}
		private Variable[] newFreeVariables() throws ParserException {
			if (!this.parseDone()) {
				List<Variable> out = new ArrayList<Variable>();
				ParseNode node = this.parseTree.firstNode();
				for (; node != null; node = node.nextNode()) {
					this.getFreeVariables(out, node.lastNode());
				}
				this.parseDone(true);
				return out.toArray(Variable.EMPTY_ARRAY);
			}
			List<Variable> out = new ArrayList<Variable>();
			Map<String, Variable> map = this.variableMap();
			for (Variable x : map.values()) {
				if (x.isType(Variable.FREE)) {
					out.add(x);
				}
			}
			Variable[] array = out.toArray(Variable.EMPTY_ARRAY);
			Arrays.sort(array, Variable.NAME_COMPARATOR);
			return array;
		}
		private boolean getFreeVariables(Collection<Variable> output, ParseNode node)
				throws ParserException {
			switch (node.type()) {
			case ParseNode.ZERO:
			case ParseNode.ONE:
				if (!node.isLeaf()) {
					String msg = "zero/one must be a leaf";
					throw new ParserException(msg);
				}
				return false;
			case ParseNode.VARIABLE: {
				if (!node.isLeaf()) {
					String msg = "variable must be a leaf";
					throw new ParserException(msg);
				}
				Variable x = this.variable(node.value(), true);
				if (x.isType(Variable.FREE)) {
					output.add(x);
					return true;
				}
				return false;
			}
			case ParseNode.MULTIPLIES:
			case ParseNode.PLUS:
				for (ParseNode child = node.firstNode(); child != null; child = child
						.nextNode()) {
					this.getFreeVariables(output, child);
				}
				return false;
			default: {
				String msg = "unexpected type of node=" + node.typeName();
				throw new ParserException(msg);
			}
			}
		}
		public Variable[] constantVariables() {
			if (this.constantVariables == null) {
				this.constantVariables = this.newConstantVariables();
			}
			return this.constantVariables;
		}
		private Variable[] newConstantVariables() {
			return new Variable[] { this.zero, this.one(), this.none() };
		}
		public Variable[] variables() throws ParserException {
			if (this.variables == null) {
				this.variables = this.newVariable();
			}
			return this.variables;
		}
		private Variable[] newVariable() throws ParserException {
			Variable[] x0 = this.constantVariables();
			Variable[] x1 = this.boundedVariables();
			Variable[] x2 = this.freeVariables();
			int n = x0.length + x1.length + x2.length;
			Variable[] out = new Variable[n];
			System.arraycopy(x0, 0, out, 0, x0.length);
			System.arraycopy(x1, 0, out, x0.length, x1.length);
			System.arraycopy(x2, 0, out, x0.length + x1.length, x2.length);
			return out;
		}
		boolean parseDone() {
			return this.parseDone;
		}
		TreeAnalyzer parseDone(boolean set) {
			this.parseDone = set;
			return this;
		}
	}

	static class Equation {
		private final Variable variable;

		Equation(Variable variable) {
			this.variable = variable;
		}
		public Variable variable() {
			return this.variable;
		}
		public int variableType() {
			return this.variable.type();
		}
		public boolean isVariableType(int type) {
			return this.variable.isType(type);
		}
	}

	static class ParseEquation extends Equation {
		public static final ParseEquation[] EMPTY_ARRAY = {};
		public static final Function<ParseEquation, String> TO_PREFIX = new Function<ParseEquation, String>() {
			@Override
			public String evaluate(ParseEquation source) throws Exception {
				return source.toPrefix();
			}
		};

		static ParseEquation[] build(TreeAnalyzer analyzer) throws ParserException {
			final int nEq = analyzer.boundedVariables().length;
			final ParseEquation[] out = new ParseEquation[nEq];
			final Map<Variable, ParseNode> constMap = new HashMap<Variable, ParseNode>();
			final ParseNode zero = new ParseNode(ParseNode.ZERO);
			final ParseNode one = new ParseNode(ParseNode.ONE);
			final ParseNode parseTree = analyzer.parseTree();
			int newConst = 0;
			ParseNode node = parseTree.firstNode();
			for (int i = 0; node != null; node = node.nextNode(), ++i) {
				ParseNode lhs = node.firstNode();
				ParseNode rhs = node.lastNode();
				rhs = eliminateConstant(rhs, constMap, analyzer);
				Variable x = analyzer.variable(lhs.value(), true);
				if (rhs.isType(ParseNode.ZERO)) {
					constMap.put(x, zero);
					++newConst;
				} else if (rhs.isType(ParseNode.ONE)) {
					constMap.put(x, one);
					++newConst;
				}
				out[i] = new ParseEquation(x, rhs);
			}
			analyzer.parseDone(true);
			while (0 < newConst) {
				newConst = 0;
				for (int i = 0; i < nEq; ++i) {
					ParseNode oldRhs = out[i].parseTree;
					ParseNode newRhs = eliminateConstant(oldRhs, constMap, analyzer);
					out[i].parseTree = newRhs;
					analyzer.backNode(oldRhs);
					Variable x = out[i].variable();
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
			analyzer.clearNodes();
			return out;
		}
		private static ParseNode eliminateConstant(ParseNode eq,
				Map<Variable, ParseNode> constMap, TreeAnalyzer analyzer)
				throws ParserException {
			switch (eq.type()) {
			case ParseNode.ZERO:
			case ParseNode.ONE:
				if (!eq.isLeaf()) {
					String msg = "zero/one must be a leaf";
					throw new ParserException(msg);
				}
				return analyzer.cloneNode(eq);
			case ParseNode.VARIABLE: {
				if (!eq.isLeaf()) {
					String msg = "variable must be a leaf";
					throw new ParserException(msg);
				}
				ParseNode newEq = constMap.get(analyzer.variable(eq.value(), true));
				if (newEq != null) {
					return analyzer.cloneNode(newEq);
				}
				return analyzer.cloneNode(eq);
			}
			case ParseNode.MULTIPLIES: {
				ParseNode newEq = analyzer.cloneNode(eq);
				int nChild = 0;
				for (ParseNode child = eq.firstNode(); child != null; child = child
						.nextNode()) {
					if (child.isType(ParseNode.MULTIPLIES)) {
						for (ParseNode x = child.firstNode(); x != null; x = x.nextNode()) {
							ParseNode newX = eliminateConstant(x, constMap, analyzer);
							if (newX.isType(ParseNode.ZERO)) {
								return newX;
							} else if (newX.isType(ParseNode.ONE)) {
							} else {
								newEq.add(newX);
								++nChild;
							}
						}
					} else {
						ParseNode newChild = eliminateConstant(child, constMap, analyzer);
						if (newChild.isType(ParseNode.ZERO)) {
							analyzer.backNode(newEq);
							return newChild;
						} else if (newChild.isType(ParseNode.ONE)) {
						} else {
							newEq.add(newChild);
							++nChild;
						}
					}
				}
				switch (nChild) {
				case 0:
					return newEq.type(ParseNode.ONE);
				case 1: {
					ParseNode x = newEq.firstNode();
					analyzer.backNode(newEq.clearChildren());
					return x;
				}
				default:
					return newEq;
				}
			}
			case ParseNode.PLUS: {
				ParseNode newEq = analyzer.cloneNode(eq);
				int nChild = 0;
				for (ParseNode child = eq.firstNode(); child != null; child = child
						.nextNode()) {
					if (child.isType(ParseNode.PLUS)) {
						for (ParseNode x = child.firstNode(); x != null; x = x.nextNode()) {
							ParseNode newX = eliminateConstant(x, constMap, analyzer);
							if (newX.isType(ParseNode.ZERO)) {
								analyzer.backNode(newX);
							} else {
								newEq.add(newX);
								++nChild;
							}
						}
					} else {
						ParseNode newChild = eliminateConstant(child, constMap, analyzer);
						if (newChild.isType(ParseNode.ZERO)) {
							analyzer.backNode(newChild);
						} else {
							newEq.add(newChild);
							++nChild;
						}
					}
				}
				switch (nChild) {
				case 0:
					return newEq.type(ParseNode.ZERO);
				case 1: {
					ParseNode x = newEq.firstNode();
					analyzer.backNode(newEq.clearChildren());
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

		private ParseNode parseTree;

		ParseEquation(Variable variable, ParseNode parseTree) {
			super(variable);
			this.parseTree = parseTree;
		}

		@Override
		public String toString() {
			return this.toInfix();
		}
		public String toInfix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeInfix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		Appendable writeInfix(Appendable output) throws IOException {
			output.append(this.variable().name()).append(" = ");
			return this.parseTree().writeInfix(output);
		}
		public String toPrefix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writePrefix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		Appendable writePrefix(Appendable output) throws IOException {
			output.append(this.variable().name()).append(" = ");
			return this.parseTree().writePrefix(output);
		}
		public ParseNode parseTree() {
			return this.parseTree;
		}
	}

	static class FirstLast {
		List<AdjacencyNode> firsts;
		List<AdjacencyNode> lasts;
		boolean owingOne;

		public FirstLast clear() {
			if (this.firsts != null) {
				this.firsts.clear();
			}
			if (this.lasts != null) {
				this.lasts.clear();
			}
			this.owingOne = false;
			return this;
		}
		List<AdjacencyNode> firsts() {
			if (this.firsts == null) {
				this.firsts = new ArrayList<AdjacencyNode>();
			}
			return this.firsts;
		}
		List<AdjacencyNode> lasts() {
			if (this.lasts == null) {
				this.lasts = new ArrayList<AdjacencyNode>();
			}
			return this.lasts;
		}
	}

	static class FirstLastBuilder extends Recycler<FirstLastBuilder, FirstLast> {
		private List<AdjacencyNode> nodes;

		private List<AdjacencyNode> nodes() {
			if (this.nodes == null) {
				this.nodes = new ArrayList<AdjacencyNode>();
			}
			return nodes;
		}
		private FirstLastBuilder clearNodes() {
			if (this.nodes != null) {
				this.nodes.clear();
			}
			return this;
		}
		private FirstLast newFirstLast() {
			List<FirstLast> backed = this.backedValues(false);
			if (backed == null || backed.size() < 1) {
				return new FirstLast();
			}
			return backed.remove(backed.size() - 1).clear();
		}
		private FirstLast newFirstLast(AdjacencyNode node) {
			FirstLast out = this.newFirstLast();
			out.firsts().add(node);
			out.lasts().add(node);
			out.owingOne = node.variable().isType(Variable.ONE);
			return out;
		}
		private AdjacencyNode newNode(Variable x) {
			AdjacencyNode node = new AdjacencyNode(x, this.nodes().size());
			this.nodes().add(node);
			return node;
		}
		private FirstLast newFirstLast(Variable x) {
			return this.newFirstLast(this.newNode(x));
		}
		private FirstLast newFirstLast(String name, TreeAnalyzer analyzer) {
			return this.newFirstLast(analyzer.variable(name, true));
		}
		private FirstLastBuilder backFirstLast(FirstLast x) {
			return this.backValue(x);
		}

		private FirstLast build(ParseNode node, TreeAnalyzer analyzer)
				throws ParserException {
			switch (node.type()) {
			case ParseNode.ZERO:
				return this.newFirstLast();
			case ParseNode.ONE:
				return this.newFirstLast(analyzer.one());
			case ParseNode.VARIABLE:
				return this.newFirstLast(node.value(), analyzer);
			case ParseNode.MULTIPLIES: {
				ParseNode child = node.firstNode();
				if (child == null) {
					return this.newFirstLast(analyzer.one());
				}
				FirstLast out = this.build(child, analyzer);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					FirstLast y = this.build(child, analyzer);
					FirstLast z = this.multiplies(out, y);
					this.backFirstLast(out).backFirstLast(y);
					out = z;
				}
				return out;
			}
			case ParseNode.PLUS: {
				ParseNode child = node.firstNode();
				if (child == null) {
					return this.newFirstLast();
				}
				FirstLast out = this.build(child, analyzer);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					FirstLast y = this.build(child, analyzer);
					FirstLast z = this.plus(out, y);
					this.backFirstLast(out).backFirstLast(y);
					out = z;
				}
				return out;
			}
			default: {
				String msg = "unexpected type=" + node.typeName();
				throw new ParserException(msg);
			}
			}
		}
		private FirstLast plus(FirstLast x, FirstLast y) {
			FirstLast out = this.newFirstLast();
			out.firsts().addAll(x.firsts());
			out.firsts().addAll(y.firsts());
			out.lasts().addAll(y.lasts());
			out.lasts().addAll(x.lasts());
			out.owingOne = x.owingOne || y.owingOne;
			return out;
		}
		private FirstLast multiplies(FirstLast x, FirstLast y) {
			List<AdjacencyNode> xs = x.lasts();
			List<AdjacencyNode> ys = y.firsts();
			for (int ix = 0, nx = xs.size(); ix < nx; ++ix) {
				AdjacencyNode xx = xs.get(ix);
				for (int iy = 0, ny = xs.size(); iy < ny; ++iy) {
					xx.follows().add(ys.get(iy));
				}
			}
			FirstLast out = this.newFirstLast();
			out.firsts().addAll(x.firsts());
			if (x.owingOne) {
				out.firsts().addAll(y.firsts());
			}
			out.lasts().addAll(y.lasts());
			if (y.owingOne) {
				out.lasts().addAll(x.lasts());
			}
			out.owingOne = x.owingOne && y.owingOne;
			return out;
		}
	}

	static class AdjacencyEquation extends Equation {
		public static final Function<AdjacencyEquation, String> TO_PREFIX = new Function<AdjacencyEquation, String>() {
			@Override
			public String evaluate(AdjacencyEquation source) {
				return source.toInfix();
			}
		};

		static AdjacencyEquation[] build(TreeAnalyzer analyzer)
				throws ParserException {
			ParseEquation[] eqs = analyzer.parseEquations();
			AdjacencyEquation[] out = new AdjacencyEquation[eqs.length];
			FirstLastBuilder builder = new FirstLastBuilder();
			for (int i = 0, n = eqs.length; i < n; ++i) {
				Variable x = eqs[i].variable();
				ParseNode eq = eqs[i].parseTree();
				FirstLast adj = builder.clearNodes().build(eq, analyzer);
				out[i] = new AdjacencyEquation(x //
						, adj.firsts().toArray(AdjacencyNode.EMPTY_ARRAY) //
						, adj.lasts().toArray(AdjacencyNode.EMPTY_ARRAY) //
						, builder.nodes().toArray(AdjacencyNode.EMPTY_ARRAY));
			}
			return out;
		}

		final AdjacencyNode[] firstNodes;
		final AdjacencyNode[] lastNodes;
		final AdjacencyNode[] allNodes;

		AdjacencyEquation(Variable variable, AdjacencyNode[] firsts,
				AdjacencyNode[] lasts, AdjacencyNode[] all) {
			super(variable);
			this.firstNodes = firsts;
			this.lastNodes = lasts;
			this.allNodes = all;
		}
		public String toInfix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeInfix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		Appendable writeInfix(Appendable output) throws IOException {
			output.append(this.variable().name()).append(" = ");
			AdjacencyNode[] nodes = this.firstNodes;
			switch (nodes.length) {
			case 0:
				return output.append("0");
			case 1:
				return nodes[0].writeInfix(output);
			default:
				for (int i = 0, n = nodes.length; i < n; ++i) {
					if (i != 0) {
						output.append(" + ");
					}
					nodes[i].writeInfix(output);
				}
				return output;
			}
		}
	}

	static class AdjacencyNode extends Equation {
		public static final AdjacencyNode[] EMPTY_ARRAY = {};
		public static final Function<AdjacencyNode, String> TO_INFIX = new Function<AdjacencyNode, String>() {
			@Override
			public String evaluate(AdjacencyNode source) {
				return source.toInfix();
			}
		};

		final int index;
		private List<AdjacencyNode> follows;

		AdjacencyNode(Variable variable, int index) {
			super(variable);
			this.index = index;
		}
		List<AdjacencyNode> follows() {
			if (this.follows == null) {
				this.follows = new ArrayList<AdjacencyNode>();
			}
			return this.follows;
		}
		public String toInfix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeInfix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		Appendable writeInfix(Appendable output) throws IOException {
			Variable x = this.variable();
			List<AdjacencyNode> follows = this.follows();
			switch (x.type()) {
			case Variable.ZERO:
				return output.append("0");
			case Variable.ONE:
				switch (follows.size()) {
				case 0:
					return output.append("1");
				case 1:
					return follows.get(0).writeInfix(output);
				default:
					for (int i = 0, n = follows.size(); i < n; ++i) {
						if (i != 0) {
							output.append(" + ");
						}
						follows.get(i).writeInfix(output);
					}
				}
			default:
				output.append(x.name());
				switch (follows.size()) {
				case 0:
					return output;
				case 1:
					return follows.get(0).writeInfix(output.append(' '));
				default:
					output.append('(');
					for (int i = 0, n = follows.size(); i < n; ++i) {
						if (i != 0) {
							output.append(" + ");
						}
						follows.get(i).writeInfix(output);
					}
					return output.append(')');
				}
			}
		}
	}

	static class ListRecycler<T> extends Recycler<ListRecycler<T>, List<T>> {
		public List<T> newList() {
			List<List<T>> xs = this.backedValues(false);
			if (xs == null || xs.size() < 1) {
				return new ArrayList<T>();
			}
			List<T> x = xs.remove(xs.size() - 1);
			x.clear();
			return x;
		}
		public List<T> newList(Collection<T> x) {
			List<T> y = this.newList();
			y.addAll(x);
			return y;
		}
	}

	@SuppressWarnings("serial")
	static class TermArrayBuilder extends ArrayList<List<Variable>> {
		final ListRecycler<Variable> recycler;

		TermArrayBuilder(ListRecycler<Variable> recycler) {
			this.recycler = recycler;
		}
		private List<Variable> newList() {
			if (this.recycler == null) {
				return new ArrayList<Variable>();
			}
			return this.recycler.newList();
		}
		private List<Variable> newList(Collection<Variable> x) {
			if (this.recycler == null) {
				return new ArrayList<Variable>(x);
			}
			return this.recycler.newList(x);
		}
		private TermArrayBuilder backList(List<Variable> x) {
			if (this.recycler != null) {
				this.recycler.backValue(x);
			}
			return this;
		}
		@Override
		public void clear() {
			int n = this.size();
			while (0 < n--) {
				this.backList(this.get(n));
			}
			super.clear();
		}
		public TermArrayBuilder makeZero() {
			this.clear();
			return this;
		}
		public TermArrayBuilder makeOne() {
			this.clear();
			this.add(this.newList());
			return this;
		}
		public TermArrayBuilder duplicate() {
			TermArrayBuilder that = (TermArrayBuilder) this.clone();
			for (int i = 0, n = this.size(); i < n; ++i) {
				that.set(i, this.newList((this.get(i))));
			}
			return that;
		}
		public TermArrayBuilder plus(TermArrayBuilder x) {
			for (int i = 0, n = x.size(); i < n; ++i) {
				this.add(this.newList(x.get(i)));
			}
			return this;
		}
		public TermArrayBuilder plus(Term term) {
			if (term != null) {
				List<Variable> list = this.newList();
				for (int i = 0, n = term.size(); i < n; ++i) {
					list.add(term.get(i));
				}
				this.add(list);
			}
			return this;
		}
		public TermArrayBuilder multiplies(Variable x) {
			switch (x.type()) {
			case Variable.ZERO:
				return this.makeZero();
			case Variable.ONE:
				return this;
			default:
				for (int i = 0, n = this.size(); i < n; ++i) {
					this.get(i).add(x);
				}
				return this;
			}
		}
		public TermArrayBuilder multiplies(Term x) {
			if (x == null) {
				return this.makeZero();
			} else if (x.isOne()) {
				return this;
			}
			for (int i = 0, n = x.size(); i < n; ++i) {
				this.multiplies(x.get(i));
			}
			return this;
		}
		public TermArrayBuilder multiplies(TermArray x) {
			switch (x.size()) {
			case 0:
				return this.makeZero();
			case 1:
				return this.multiplies(x.get(0));
			default:
			break;
			}
			List<List<Variable>> out = new ArrayList<List<Variable>>();
			for (int i = 0, n = this.size(); i < n; ++i) {
				this.multiplies(out, this.get(i), x);
			}
			this.clear();
			this.addAll(out);
			return this;
		}
		private Collection<List<Variable>> multiplies(
				Collection<List<Variable>> output, List<Variable> term, TermArray x) {
			for (int i = 0, n = x.size(); i < n; ++i) {
				this.multiplies(output, term, x.get(i));
			}
			return output;
		}
		/**
		 * operates under the assumption that y did not contain zero or one.
		 * 
		 * @param output
		 * @param x
		 * @param y
		 * @return
		 */
		private Collection<List<Variable>> multiplies(
				Collection<List<Variable>> output, List<Variable> x, Term y) {
			List<Variable> z = this.newList(x);
			for (int i = 0, n = y.size(); i < n; ++i) {
				z.add(y.get(i));
			}
			output.add(z);
			return output;
		}
		public TermArrayBuilder multiplies(Collection<Variable> x) {
			for (int i = 0, n = this.size(); i < n; ++i) {
				this.get(i).addAll(x);
			}
			return this;
		}
		public TermArrayBuilder multiplies(TermArrayBuilder x) {
			switch (x.size()) {
			case 0:
				return this.makeZero();
			case 1:
				return this.multiplies(x.get(0));
			default:
			break;
			}
			List<List<Variable>> out = new ArrayList<List<Variable>>();
			for (int i = 0, n = this.size(); i < n; ++i) {
				this.multiplies(out, this.get(i), x);
			}
			this.clear();
			this.addAll(out);
			return this;
		}
		private Collection<List<Variable>> multiplies(
				Collection<List<Variable>> output, List<Variable> term,
				TermArrayBuilder x) {
			for (int i = 0, n = x.size(); i < n; ++i) {
				this.multiplies(output, term, x.get(i));
			}
			return output;
		}
		/**
		 * operates under the assumption that y did not contain zero or one.
		 * 
		 * @param output
		 * @param x
		 * @param y
		 * @return
		 */
		private Collection<List<Variable>> multiplies(
				Collection<List<Variable>> output, List<Variable> x, List<Variable> y) {
			List<Variable> z = this.newList(x);
			z.addAll(y);
			output.add(z);
			return output;
		}
		public TermArray toTermArray() {
			if (this.size() < 1) {
				return TermArray.ZERO;
			}
			Term[] terms = new Term[this.size()];
			for (int i = 0, n = this.size(); i < n; ++i) {
				terms[i] = TermArrayBuilder.toTerm(this.get(i));
			}
			return new TermArray(terms);
		}
		private static Term toTerm(List<Variable> xs) {
			if (xs.size() < 1) {
				return Term.ONE;
			}
			return new Term(xs.toArray(Variable.EMPTY_ARRAY));
		}
	}

	static class FlattenEquation extends Equation {
		public static final Function<FlattenEquation, String> TO_INFIX = new Function<FlattenEquation, String>() {
			@Override
			public String evaluate(FlattenEquation source) {
				return source.toInfix();
			}
		};

		static FlattenEquation[] build(TreeAnalyzer analyzer)
				throws ParserException {
			ParseEquation[] eqs = analyzer.parseEquations();
			FlattenEquation[] out = new FlattenEquation[eqs.length];
			for (int i = 0, n = eqs.length; i < n; ++i) {
				TermArray eq = build(eqs[i], analyzer);
				out[i] = new FlattenEquation(eqs[i].variable(), eq);
			}
			return out;
		}
		private static TermArray build(ParseEquation eq, TreeAnalyzer analyzer)
				throws ParserException {
			ListRecycler<Variable> recycler = new ListRecycler<Variable>();
			TermArrayBuilder buffer = new TermArrayBuilder(recycler).makeOne();
			build(buffer, eq.parseTree(), analyzer);
			return buffer.toTermArray();
		}
		private static List<List<Variable>> build(TermArrayBuilder output,
				ParseNode node, TreeAnalyzer analyzer) throws ParserException {
			switch (node.type()) {
			case ParseNode.ZERO: {
				Variable x = analyzer.zero();
				return output.multiplies(x);
			}
			case ParseNode.ONE: {
				Variable x = analyzer.one();
				return output.multiplies(x);
			}
			case ParseNode.VARIABLE: {
				Variable x = analyzer.variable(node.value(), true);
				return output.multiplies(x);
			}
			case ParseNode.MULTIPLIES: {
				for (ParseNode child = node.firstNode(); child != null; child = child
						.nextNode()) {
					build(output, child, analyzer);
				}
				return output;
			}
			case ParseNode.PLUS: {
				ParseNode child = node.firstNode();
				if (child == null) {
					return output.makeZero();
				} else if (child.nextNode() == null) {
					return build(output, child, analyzer);
				}
				TermArrayBuilder copy = output.duplicate();
				output.makeZero();
				for (; child != null; child = child.nextNode()) {
					if (child.nextNode() == null) {
						build(copy, child, analyzer);
						output.plus(copy);
						copy.makeZero();
					} else {
						TermArrayBuilder x = copy.duplicate();
						build(x, child, analyzer);
						output.plus(x);
						x.makeZero();
					}
				}
				return output;
			}
			default: {
				String msg = "unexpected type=" + node.typeName();
				throw new ParserException(msg);
			}
			}
		}

		static TermArray[][] enumerate(int degree, TreeAnalyzer analyzer)
				throws ParserException {
			FlattenEquation[] eqs = analyzer.flattenEquations();
			TermArray[][] output = new TermArray[eqs.length][];
			if (degree < 0) {
				return output;
			}
			ListRecycler<Variable> recycler = new ListRecycler<Variable>();
			TermArrayBuilder zero = new TermArrayBuilder(recycler).makeZero();
			TermArray[] gs = new TermArray[eqs.length];
			TermArray[][] xs = new TermArray[analyzer.variables().length][];
			for (int i = 0, n = eqs.length; i < n; ++i) {
				Variable x = eqs[i].variable();
				xs[x.index] = new TermArray[degree + 1];
				output[i] = new TermArray[degree + 1];
				gs[i] = enumerate_0(xs, eqs, i, zero, analyzer);
			}
			for (int di = 1, dn = degree + 1; di < dn; ++di) {
				for (int i = 0, n = eqs.length; i < n; ++i) {
					enumerate(xs, gs, eqs, i, di, zero, analyzer);
				}
			}
			for (int di = 0, dn = degree + 1; di < dn; ++di) {
				for (int i = 0, n = eqs.length; i < n; ++i) {
					Variable x = eqs[i].variable();
					output[i][di] = xs[x.index][di];
				}
			}
			return output;
		}
		private static TermArray[][] enumerate(TermArray[][] output,
				TermArray[] generator, FlattenEquation[] eqs, int index, int degree,
				TermArrayBuilder zero, TreeAnalyzer analyzer) {
			final FlattenEquation eq = eqs[index];
			final Variable x = eq.variable();
			final TermArray geq = generator[index];
			if (geq.isZero()) {
				output[x.index][degree] = zero.toTermArray();
				return output;
			}
			TermArrayBuilder out = zero.duplicate().makeZero();
			for (int i = 0, n = geq.size(); i < n; ++i) {
				Term term = geq.get(i);
				int nk = term.countBounded();
				WeakComposition inds = WeakComposition.build(degree - 1, nk);
				int[] ind = inds.get();
				while (ind != null) {
					TermArrayBuilder one = zero.duplicate().makeOne();
					int ik = 0;
					for (int ii = 0, nn = term.size(); ii < nn; ++ii) {
						Variable y = term.get(ii);
						if (y.isBounded()) {
							TermArray z = output[y.index][ind[ik++]];
							one.multiplies(z);
						} else {
							one.multiplies(y);
						}
					}
					out.plus(one);
					one.makeZero();
					ind = inds.next().get();
				}
			}
			output[x.index][degree] = out.toTermArray();
			out.makeZero();
			return output;
		}
		private static TermArray enumerate_0(TermArray[][] output,
				FlattenEquation[] eqs, int index, TermArrayBuilder zero,
				TreeAnalyzer analyzer) {
			FlattenEquation eq = eqs[index];
			Variable x = eq.variable();
			TermArray terms = eq.terms;
			TermArrayBuilder gens = zero.duplicate().makeZero();
			TermArrayBuilder cons = zero.duplicate().makeZero();
			for (int i = 0, n = terms.size(); i < n; ++i) {
				Term term = terms.get(i);
				if (0 < term.countBounded()) {
					gens.plus(term);
				} else {
					cons.plus(term);
				}
			}
			output[x.index][0] = cons.toTermArray();
			cons.makeZero();
			return gens.toTermArray();
		}

		final TermArray terms;

		FlattenEquation(Variable variable, TermArray terms) {
			super(variable);
			this.terms = terms;
		}
		public String toInfix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeInfix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		Appendable writeInfix(Appendable output) throws IOException {
			output.append(this.variable().name()).append(" = ");
			return terms.writeInfix(output);
		}
	}

	static class CodeWriter {
		private static final String READ_METHOD = "read";
		private static final String OUTPUT_TYPE = "Output";
		private static final String INPUT_TYPE = "Input";
		private static final String INPUT_VALUE = "input";
		private static final String INPUT_POSITION = "position";

		private String tag(Variable x) {
			return "VAR_" + x.name();
		}
		private String readMethod(Variable x) {
			return READ_METHOD + '_' + x.name();
		}
		private String callReadMethod(String name, Variable x) {
			return OUTPUT_TYPE + ' ' + name + " = this." + READ_METHOD + '(' + tag(x)
					+ ", " + INPUT_VALUE + ')';
		}
		private String callNewOutput(Variable x) {
			return "this.new" + OUTPUT_TYPE + '(' + tag(x) + ", " + INPUT_POSITION
					+ ')';
		}
		private String callAddOutput(String parent, String child) {
			return "this.add" + OUTPUT_TYPE + '(' + parent + ", " + child + ')';
		}
		private String callDiscardOutput(String name) {
			return "this.back" + OUTPUT_TYPE + '(' + name + ')';
		}
		private String outputVariable(Variable x, int n) {
			return x.name() + '_' + Integer.toString(n + 1);
		}
		private String callGetInputPosition() {
			return "long " + INPUT_POSITION + " = this.position(" + INPUT_VALUE + ")";
		}
		private String callSetInputPosition() {
			return "this.position(" + INPUT_VALUE + ", " + INPUT_POSITION + ")";
		}
		private String callGetInputPosition(int n) {
			return "long " + INPUT_POSITION + '_' + Integer.toString(n + 1)
					+ " = this.position(" + INPUT_VALUE + ")";
		}
		private String callSetInputPosition(int n) {
			return "this.position(" + INPUT_VALUE + ", " + INPUT_POSITION + '_'
					+ Integer.toString(n + 1) + ")";
		}

		Appendable write(Appendable output, String name, String exception,
				TreeAnalyzer analyzer) throws IOException {
			if (exception == null) {
				exception = "";
			} else {
				exception = " throws " + exception;
			}
			this.indent(output, 0).append("abstract class ").append(name).append('<')
					.append(INPUT_TYPE).append(", ").append(OUTPUT_TYPE).append("> {\n");
			this.writeTags(output, analyzer, 1);
			output.append("\n");
			this.writeReadMethod(output, exception, analyzer, 1);
			AdjacencyEquation[] eqs = analyzer.adjacencyEquations();
			for (AdjacencyEquation eq : eqs) {
				this.writeReadMethod(output, eq, exception, analyzer, 1);
			}
			output.append("\n");
			this.writeAbstractMethod(output, name, exception, analyzer, 1);
			return this.indent(output, 0).append("}\n");
		}
		private Appendable writeAbstractMethod(Appendable output, String that,
				String exception, TreeAnalyzer analyzer, int depth) throws IOException {
			this.indent(output, depth).append("protected abstract long position(")
					.append(INPUT_TYPE).append(' ').append(INPUT_VALUE).append(");\n");
			this.indent(output, depth).append("protected abstract ")
					.append(INPUT_TYPE).append(" position(").append(INPUT_TYPE)
					.append(' ').append(INPUT_VALUE).append(", long position")
					.append(");\n");
			this.indent(output, depth).append("protected abstract ")
					.append(OUTPUT_TYPE).append(" new").append(OUTPUT_TYPE)
					.append("(int tag, long position);\n");
			this.indent(output, depth).append("protected abstract ")
					.append(OUTPUT_TYPE).append(" add").append(OUTPUT_TYPE).append('(')
					.append(OUTPUT_TYPE).append(" parent, ").append(OUTPUT_TYPE)
					.append(" child);\n");
			this.indent(output, depth).append("protected abstract ").append(that)
					.append('<').append(INPUT_TYPE).append(", ").append(OUTPUT_TYPE)
					.append("> back").append(OUTPUT_TYPE).append('(').append(OUTPUT_TYPE)
					.append(" dicarding);\n");
			output.append("\n");
			final Variable[] ys = analyzer.freeVariables();
			final int yn = ys.length;
			for (int i = 0; i < yn; ++i) {
				Variable x = ys[i];
				this.indent(output, depth).append("protected abstract ")
						.append(OUTPUT_TYPE).append(' ').append(readMethod(x)).append('(')
						.append(INPUT_TYPE).append(' ').append(INPUT_VALUE).append(')')
						.append(exception).append(";\n");
			}
			return output;
		}
		private Appendable writeReadMethod(Appendable output, AdjacencyEquation eq,
				String exception, TreeAnalyzer analyzer, int depth) throws IOException {
			this.indent(output, depth).append("protected ").append(OUTPUT_TYPE)
					.append(' ').append(readMethod(eq.variable())).append('(')
					.append(INPUT_TYPE).append(' ').append(INPUT_VALUE).append(')')
					.append(exception).append(" {\n");
			{
				++depth;
				AdjacencyNode[] nodes = eq.firstNodes;
				if (nodes.length < 1) {
					this.indent(output, depth).append("return null;\n");
				} else {
					boolean hasOne = false;
					List<String> stack = new ArrayList<String>();
					this.indent(output, depth).append(callGetInputPosition())
							.append(";\n");
					for (int i = 0, n = nodes.length; i < n; ++i) {
						if (i != 0) {
							this.indent(output, depth).append(callSetInputPosition())
									.append(";\n");
						}
						writeReadMethod(output, eq.variable(), nodes[i], stack, analyzer,
								depth);
						if (nodes[i].isVariableType(Variable.ONE)) {
							hasOne = true;
							break;
						}
					}
					if (!hasOne) {
						this.indent(output, depth).append("return null;\n");
					}
				}
				--depth;
			}
			return this.indent(output, depth).append("}\n");
		}
		private Appendable writeReadMethod(Appendable output, Variable var,
				AdjacencyNode node, List<String> stack, TreeAnalyzer analyzer, int depth)
				throws IOException {
			Variable x = node.variable();
			if (x.isType(Variable.ONE)) {
				return this.indent(output, depth).append("return ")
						.append(callNewOutput(var)).append(";\n");
			}
			String name = outputVariable(x, stack.size());
			this.indent(output, depth).append(callReadMethod(name, x)).append(";\n");
			stack.add(name);
			this.indent(output, depth).append("if (").append(name)
					.append(" != null) {\n");
			List<AdjacencyNode> ys = node.follows();
			int yn = ys.size();
			switch (yn) {
			case 0:
				if (stack.size() < 1) {
					this.indent(output, depth + 1).append("return ")
							.append(callNewOutput(var)).append(";\n");
				} else {
					this.indent(output, depth + 1).append(OUTPUT_TYPE).append(" out = ")
							.append(callNewOutput(var)).append(";\n");
					for (int i = 0, n = stack.size(); i < n; ++i) {
						this.indent(output, depth + 1)
								.append(callAddOutput("out", stack.get(i))).append(";\n");
					}
					this.indent(output, depth + 1).append("return out;\n");
				}
			break;
			case 1:
				writeReadMethod(output, var, ys.get(0), stack, analyzer, depth + 1);
			break;
			default:
				this.indent(output, depth).append(callGetInputPosition(stack.size()))
						.append(";\n");
				for (int yi = 0; yi < yn; ++yi) {
					if (0 < yi) {
						this.indent(output, depth)
								.append(callSetInputPosition(stack.size())).append(";\n");
					}
					AdjacencyNode y = ys.get(yi);
					writeReadMethod(output, var, y, stack, analyzer, depth + 1);
					if (y.isVariableType(Variable.ONE)) {
						break;
					}
				}
			break;
			}
			stack.remove(stack.size() - 1);
			if (stack.size() == 0) {
				return this.indent(output, depth).append("}\n");
			}
			this.indent(output, depth).append("} else {\n");
			for (int i = 0, n = stack.size(); i < n; ++i) {
				this.indent(output, depth + 1).append(callDiscardOutput(stack.get(i)))
						.append(";\n");
			}
			return this.indent(output, depth).append("}\n");
		}

		private Appendable writeReadMethod(Appendable output, String exception,
				TreeAnalyzer analyzer, int depth) throws IOException {
			this.indent(output, depth).append("public ").append(OUTPUT_TYPE)
					.append(' ').append(READ_METHOD).append('(').append("int which, ")
					.append(INPUT_TYPE).append(' ').append(INPUT_VALUE).append(')')
					.append(exception).append(" {\n");
			{
				++depth;
				this.indent(output, depth).append("switch (which) {\n");
				final Variable[] xs = analyzer.boundedVariables();
				final int xn = xs.length;
				for (int i = 0; i < xn; ++i) {
					Variable x = xs[i];
					this.indent(output, depth).append("case ").append(this.tag(x))
							.append(":\n");
					this.indent(output, depth + 1).append("return this.")
							.append(readMethod(x)).append('(').append(INPUT_VALUE)
							.append(')').append(";\n");
				}
				final Variable[] ys = analyzer.freeVariables();
				final int yn = ys.length;
				for (int i = 0; i < yn; ++i) {
					Variable y = ys[i];
					this.indent(output, depth).append("case ").append(this.tag(y))
							.append(":\n");
					this.indent(output, depth + 1).append("return this.")
							.append(readMethod(y)).append('(').append(INPUT_VALUE)
							.append(')').append(";\n");
				}
				this.indent(output, depth).append("default:\n");
				this.indent(output, depth + 1).append(
						"throw new IllegalArgumentException(\"unknown tag=\" + which);\n");
				this.indent(output, depth).append("}\n");
				--depth;
			}
			return this.indent(output, depth).append("}\n");
		}
		private Appendable writeTags(Appendable output, TreeAnalyzer analyzer,
				int depth) throws IOException {
			final Variable[] xs = analyzer.boundedVariables();
			final int xn = xs.length;
			for (int i = 0; i < xn; ++i) {
				Variable x = xs[i];
				this.indent(output, depth).append("public static final int ")
						.append(this.tag(x)).append(" = ").append(Integer.toString(i))
						.append(";\n");
			}
			final Variable[] ys = analyzer.freeVariables();
			final int yn = ys.length;
			for (int i = 0; i < yn; ++i) {
				Variable x = ys[i];
				this.indent(output, depth).append("public static final int ")
						.append(this.tag(x)).append(" = ").append(Integer.toString(xn + i))
						.append(";\n");
			}
			return output;
		}
		Appendable indent(Appendable output, int depth) throws IOException {
			while (0 < depth--) {
				output.append('\t');
			}
			return output;
		}
	}

	public void testBuilders() throws IOException {
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
			parser.clearNodes();
			Debug.log().debug(node != null ? node.toInfix() : null);
		} finally {
			FileHelper.close(in);
		}
		TreeAnalyzer analyzer = new TreeAnalyzer(node);
		{
			ParseEquation[] eqs = analyzer.parseEquations();
			Debug.log().debug(StringHelper.join(eqs, ", "));
			Debug.log().debug(
					"\tbounded={" + StringHelper.join(analyzer.boundedVariables(), ", ")
							+ "}, free={" + StringHelper.join(analyzer.freeVariables(), ", ")
							+ "}");
			Debug.log().debug(StringHelper.join(eqs, ", ", ParseEquation.TO_PREFIX));
		}
		{
			AdjacencyEquation[] adj = analyzer.adjacencyEquations();
			Debug.log().debug(
					"adjacent: "
							+ StringHelper.join(adj, ", ", AdjacencyEquation.TO_PREFIX));
		}
		{
			File outFile = new File("data/Descendant.java");
			CodeWriter dumper = new CodeWriter();
			Writer writer = FileHelper.getWriter(outFile);
			dumper.write(writer, "Descendant", "IOException", analyzer);
			writer.flush();
			FileHelper.close(writer);
			Debug.log().debug("wrote=" + outFile.getAbsolutePath());
		}
		{
			FlattenEquation[] eqs = FlattenEquation.build(analyzer);
			Debug.log().debug(
					"flatten: " + StringHelper.join(eqs, ", ", FlattenEquation.TO_INFIX));
			FlattenEquation.enumerate(3, analyzer);
		}
	}

	public void testCatalan() throws IOException {
		AsciiParser parser = new AsciiParser();
		ByteInput input = new ByteArrayInput(
				"x = a + x b x;".getBytes(FileHelper.UTF_8));
		ParseNode node = parser.parse(input);
		TreeAnalyzer analyzer = new TreeAnalyzer(node);
		{
			FlattenEquation[] eqs = FlattenEquation.build(analyzer);
			Debug.log().debug(
					"flatten: " + StringHelper.join(eqs, ", ", FlattenEquation.TO_INFIX));
			int dn = 12;
			TermArray[][] xs = FlattenEquation.enumerate(dn, analyzer);
			for (int di = 0; di <= dn; ++di) {
				Debug.log().debug("x:" + di + " = " + xs[0][di].toScaledTermArray().toInfix());
			}
		}
	}
}
