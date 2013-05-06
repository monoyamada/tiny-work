package parser.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.lang.Messages;

public class ParserTest_1_1 extends ParserTest_1_0 {
	static class Variable implements Comparable<Variable> {
		public static final Variable[] EMPTY_ARRAY = {};
		static final int FREE = 0;
		static final int BOUNDED_START = FREE + 1;
		static final int BOUNDED_END = BOUNDED_START + 1;
		static final int NUMBER_OF_TYPES = BOUNDED_END + 1;

		static interface Info {
			String typeName();
		}

		static final Info[] INFOS = Variable.newInfos();

		static Info[] newInfos() {
			Info[] out = new Info[NUMBER_OF_TYPES];
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
		int type;
		Variable opposite;

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

	/**
	 * represents direct product of free monoid on {@link Variable} and Dyck
	 * monoid.
	 * 
	 */
	static class Label {
		public static final Label[] EMPTY_ARRAY = {};

		public static int countFree(Variable[] variables) {
			if (variables == null) {
				return 0;
			}
			int count = 0;
			for (int i = 0, n = variables.length; i < n; ++i) {
				Variable x = variables[i];
				if (x != null && x.isType(Variable.FREE)) {
					++count;
				}
			}
			return count;
		}
		/**
		 * @param xs
		 *          assuming normal ordered.
		 * @param ys
		 *          assuming normal ordered.
		 * @return the number of contractions.
		 */
		static int countContranction(int[] xs, int[] ys) {
			if (xs == null || ys == null) {
				return -1;
			} else if (xs.length == 0 || ys.length == 0) {
				return 0;
			}
			int n = xs.length;
			if (ys.length < n) {
				n = ys.length;
			}
			for (int i = 0; i < n; ++i) {
				int x = xs[xs.length - i - 1];
				int y = ys[i];
				if (y < 0 && 0 < x) {
					if (x != -y) {
						return -1;
					}
				} else {
					return i;
				}
			}
			return n;
		}

		final Variable[] variables;
		final int[] dycks;
		int freeCount;

		Label(Variable[] variables) {
			this(variables, null);
		}
		Label(Variable[] variables, int[] dycks) {
			if (variables == null) {
				variables = Variable.EMPTY_ARRAY;
			}
			if (dycks == null) {
				dycks = ArrayHelper.EMPTY_INT_ARRAY;
			}
			this.variables = variables;
			this.dycks = dycks;
			this.freeCount = -1;
		}
		/**
		 * @return the number of free variables that this object holds. returning 0
		 *         means this object represents empty transition.
		 */
		int freeVariableSize() {
			if (this.freeCount < 0) {
				this.freeCount = countFree(this.variables);
			}
			return this.freeCount;
		}
		int boundedVariableSize() {
			return this.variables.length - this.freeVariableSize();
		}
		public boolean equalLabel(Label x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			} else if (Variable.equalArray(this.variables, x.variables)
					&& ArrayHelper.equalArray(this.dycks, x.dycks)) {
				return true;
			}
			return false;
		}
		/**
		 * indicates whether the result of {@link #multiplies} is not null. this
		 * method may be used for reducing the cost that {@link #multiplies}
		 * allocate memory.
		 * 
		 * @param x
		 * @return
		 */
		public boolean multiplyable(Label x) {
			if (x == null) {
				return false;
			} else if (this.dycks.length == 0 || x.dycks.length == 0) {
				return true;
			}
			int cn = Label.countContranction(this.dycks, x.dycks);
			if (cn < 0) {
				return false;
			}
			return true;
		}
		/**
		 * 
		 * @param x
		 * @return null represents zero.
		 */
		public Label multiplies(Label x) {
			int[] dycks = this.multipliesDyck(x.dycks);
			if (dycks == null) {
				return null;
			}
			Variable[] vars = this.multipliesVariable(x.variables);
			return this.newLabel(vars, dycks);
		}
		protected Label newLabel(Variable[] vars, int[] dycks) {
			return new Label(vars, dycks);
		}
		private int[] multipliesDyck(int[] dycks) {
			if (this.dycks.length == 0) {
				if (dycks.length == 0) {
					return ArrayHelper.EMPTY_INT_ARRAY;
				}
				return dycks;
			} else if (dycks.length == 0) {
				return this.dycks;
			}
			int cn = Label.countContranction(this.dycks, dycks);
			if (cn < 0) {
				return null;
			} else if (0 < cn) {
				if (this.dycks.length == cn) {
					if (dycks.length == cn) {
						return ArrayHelper.EMPTY_INT_ARRAY;
					}
					return dycks;
				} else if (dycks.length == cn) {
					return this.dycks;
				}
			}
			int xn = this.dycks.length - cn + dycks.length - cn;
			int[] xs = new int[xn];
			xn = this.dycks.length - cn;
			System.arraycopy(this.dycks, 0, xs, 0, xn);
			System.arraycopy(dycks, cn, xs, xn, dycks.length - cn);
			return xs;
		}
		private Variable[] multipliesVariable(Variable[] variables) {
			if (this.variables.length == 0) {
				if (variables.length == 0) {
					return Variable.EMPTY_ARRAY;
				}
				return variables;
			} else if (variables.length == 0) {
				return this.variables;
			}
			int n = this.variables.length + variables.length;
			Variable[] xs = new Variable[n];
			n = this.variables.length;
			System.arraycopy(this.variables, 0, xs, 0, n);
			System.arraycopy(variables, 0, xs, n, variables.length);
			return xs;
		}
		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.toString(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		protected Appendable toString(Appendable output) throws IOException {
			return this.toStringDycks(this.toStringVariables(output));
		}
		protected Appendable toStringVariables(Appendable output)
				throws IOException {
			switch (this.variables.length) {
			case 0:
				return output;
			case 1:
				return output.append(this.variables[0].name());
			default:
				// output.append('(');
				for (int i = 0, n = this.variables.length; i < n; ++i) {
					if (i != 0) {
						output.append(" ");
					}
					output.append(this.variables[i].name());
				}
				return output;
				// return output.append(')');
			}
		}
		protected Appendable toStringDycks(Appendable output) throws IOException {
			output.append('[');
			for (int i = 0, n = this.dycks.length; i < n; ++i) {
				if (i != 0) {
					output.append(", ");
				}
				output.append(Integer.toString(this.dycks[i]));
			}
			return output.append(']');
		}
	}

	static class LabeledArrow {
		public static final LabeledArrow[] EMPTY_ARRAY = {};

		final Variable source;
		final Variable target;
		final Label label;
		private int duplication;

		LabeledArrow(Variable source, Variable target, Label label) {
			if (source == null) {
				String msg = "source must not be null";
				throw new IllegalArgumentException(msg);
			} else if (target == null) {
				String msg = "target must not be null";
				throw new IllegalArgumentException(msg);
			} else if (label == null) {
				String msg = "label must not be null";
				throw new IllegalArgumentException(msg);
			}
			this.source = source;
			this.target = target;
			this.label = label;
		}
		int duplication() {
			return this.duplication;
		}
		/**
		 * needed at construction.
		 * 
		 * @see TreeParser#newArrows
		 */
		@Override
		public boolean equals(Object x) {
			try {
				return this.equalArrow((LabeledArrow) x);
			} catch (Exception ex) {
			}
			return false;
		}
		public boolean equalArrow(LabeledArrow x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			} else if (this.source != x.source || this.target != x.target) {
				return false;
			}
			return this.label.equalLabel(x.label);
		}
		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.toString(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		protected Appendable toString(Appendable output) throws IOException {
			return this.label.toString(output).append(": ").append(this.source.name)
					.append(" -> ").append(this.target.name);
		}
		public boolean multiplyable(LabeledArrow x) {
			if (x == null) {
				return false;
			} else if (this.target != x.source) {
				return false;
			}
			return this.label.multiplyable(x.label);
		}
		public LabeledArrow multiplies(LabeledArrow x) {
			if (x == null) {
				return null;
			} else if (this.target != x.source) {
				return null;
			}
			Label label = this.label.multiplies(x.label);
			return this.newArrow(this.source, x.target, label);
		}
		protected LabeledArrow newArrow(Variable source, Variable target,
				Label label) {
			return new LabeledArrow(source, target, label);
		}
	}

	static class LabeledGraph {
		final LabeledArrow[] arrows;
		final Variable[] variables;
		private AdVertex[] adVertices;

		LabeledGraph(LabeledArrow[] arrows, Variable[] variables) {
			this.arrows = arrows;
			this.variables = variables;
		}
		public AdVertex[] getAdVertices() {
			return this.getAdVertices(true);
		}
		AdVertex[] getAdVertices(boolean anyway) {
			if (this.adVertices == null && anyway) {
				this.adVertices = this.newAdVertices();
			}
			return this.adVertices;
		}
		AdVertex[] newAdVertices() {
			final Variable[] vars = this.variables;
			AdVertex[] vertices = new AdVertex[vars.length];
			for (int i = 0, n = vars.length; i < n; ++i) {
				vertices[i] = new AdVertex(vars[i], AdEdge.EMPTY_ARRAY);
			}
			for (int i = 0, n = vars.length; i < n; ++i) {
				vertices[i].opposite = vertices[vertices[i].variable.opposite().index];
			}
			final LabeledArrow[] arrows = this.arrows;
			List<?>[] edges = new List<?>[vars.length];
			for (int i = 0, n = arrows.length; i < n; ++i) {
				LabeledArrow arrow = arrows[i];
				AdEdge edge = new AdEdge(vertices[arrow.target.index], arrow.label);
				@SuppressWarnings("unchecked")
				List<AdEdge> list = (List<AdEdge>) edges[arrow.source.index];
				if (list == null) {
					list = Collections.singletonList(edge);
					edges[arrow.source.index] = list;
				} else if (list.size() == 1) {
					list = new ArrayList<AdEdge>(list);
					list.add(edge);
					edges[arrow.source.index] = list;
				} else {
					list.add(edge);
				}
			}
			for (int i = 0, n = edges.length; i < n; ++i) {
				@SuppressWarnings("unchecked")
				List<AdEdge> list = (List<AdEdge>) edges[i];
				if (list != null) {
					vertices[i].targets = list.toArray(AdEdge.EMPTY_ARRAY);
				}
			}
			return vertices;
		}
	}

	static class AdVertex {
		final Variable variable;
		AdEdge[] targets;
		AdVertex opposite;
		private AdSummaryEdge[] summaryTargets;

		public AdVertex(Variable variable, AdEdge[] targets) {
			this.variable = variable;
			this.targets = targets;
		}
		@Override
		public String toString() {
			return this.name();
		}
		/**
		 * an order of edges is lost.
		 * 
		 * @return
		 */
		AdSummaryEdge[] summaryTargets() {
			return this.summaryTargets(true);
		}
		AdSummaryEdge[] summaryTargets(boolean anyway) {
			if (this.summaryTargets == null && anyway) {
				this.summaryTargets = this.newSummaryTarget();
			}
			return this.summaryTargets;
		}
		private AdSummaryEdge[] newSummaryTarget() {
			AdEdge[] xs = this.targets;
			switch (xs.length) {
			case 0:
				return AdSummaryEdge.EMPTY_ARRAY;
			case 1:
				return new AdSummaryEdge[] { new AdSummaryEdge(xs[0].target,
						new Label[] { xs[0].label }) };
			default:
			break;
			}
			Map<AdVertex, List<Label>> map = new HashMap<AdVertex, List<Label>>();
			for (int i = 0, n = xs.length; i < n; ++i) {
				AdEdge x = xs[i];
				List<Label> list = map.get(x.target);
				if (list == null) {
					list = Collections.singletonList(x.label);
					map.put(x.target, list);
				} else if (list.size() == 1) {
					list = new ArrayList<Label>(list);
					list.add(x.label);
					map.put(x.target, list);
				} else {
					list.add(x.label);
				}
			}
			AdSummaryEdge[] out = new AdSummaryEdge[map.size()];
			Iterator<Entry<AdVertex, List<Label>>> p = map.entrySet().iterator();
			for (int i = 0; p.hasNext(); ++i) {
				Entry<AdVertex, List<Label>> ent = p.next();
				AdVertex target = ent.getKey();
				List<Label> labels = ent.getValue();
				out[i] = new AdSummaryEdge(target, labels.toArray(Label.EMPTY_ARRAY));
			}
			return out;
		}
		public int type() {
			return this.variable.type();
		}
		public boolean isType(int type) {
			return this.variable.isType(type);
		}
		public String name() {
			return this.variable.name();
		}
		public AdVertex opposite() {
			return this.opposite;
		}
		public int index() {
			return this.variable.index;
		}
	}

	static class AdEdge {
		public static final AdEdge[] EMPTY_ARRAY = {};
		final AdVertex target;
		final Label label;

		AdEdge(AdVertex target, Label label) {
			this.target = target;
			this.label = label;
		}
	}

	static class AdSummaryEdge {
		public static final AdSummaryEdge[] EMPTY_ARRAY = {};
		final AdVertex target;
		final Label[] labels;

		AdSummaryEdge(AdVertex target, Label[] labels) {
			this.target = target;
			this.labels = labels;
		}
	}

	/**
	 * adapts distributive law for free algebra on {@link Variable}. the method
	 * {@link FlattenEauation#multiplies(FlattenEauation)} is valid even when plus
	 * is defined by PEG's choice.
	 */
	static class FlattenEauation {
		static final FlattenEauation ZERO = newZero();
		static final FlattenEauation ONE = newOne();

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
	}

	/**
	 * essentially string pair.
	 */
	static class VariablePair implements Cloneable, Comparable<VariablePair> {
		Variable x0;
		Variable x1;

		VariablePair() {
			this(null, null);
		}
		VariablePair(Variable x0, Variable x1) {
			this.x0 = x0;
			this.x1 = x1;
		}
		@Override
		public int hashCode() {
			return this.x0.hashCode() * 3 + this.x1.hashCode();
		}
		@Override
		public boolean equals(Object x) {
			try {
				return this.equalPair((VariablePair) x);
			} catch (Exception ex) {
			}
			return false;
		}
		public boolean equalPair(VariablePair x) {
			if (this == x) {
				return true;
			} else if (x == null) {
				return false;
			}
			return (this.x0 == x.x0) && (this.x1 == x.x1);
		}
		VariablePair set(Variable x0, Variable x1) {
			this.x0 = x0;
			this.x1 = x1;
			return this;
		}
		@Override
		public VariablePair clone() {
			try {
				return (VariablePair) super.clone();
			} catch (CloneNotSupportedException ex) {
				ex.printStackTrace();
			}
			return new VariablePair(this.x0, this.x1);
		}
		@Override
		public String toString() {
			return "(" + this.x0 + ", " + this.x1 + ")";
		}
		@Override
		public int compareTo(VariablePair x) {
			if (this == x) {
				return 0;
			} else if (x == null) {
				return -1;
			} else if (this.x0.index < x.x0.index) {
				return -1;
			} else if (this.x0.index == x.x0.index) {
				if (this.x1.index < x.x1.index) {
					return -1;
				} else if (x1.index == x.x1.index) {
					return 1;
				}
				return 0;
			}
			return 1;
		}
	}

	static class Counter {
		final int index;
		int count;

		Counter(int index) {
			this.index = index;
		}
		@Override
		public String toString() {
			return "(" + Integer.toString(this.index) + ", "
					+ Integer.toString(this.count) + ")";
		}
	}

	static class TreeParser {
		Map<String, Variable> variableMap;
		int arrowCount;
		int equationCount;

		TreeParser initializeParse() {
			this.variableMap = null;
			this.arrowCount = 0;
			this.equationCount = 0;
			return this;
		}
		Map<String, Variable> variableMap(boolean anyway) {
			if (this.variableMap == null) {
				this.variableMap = new HashMap<String, Variable>();
			}
			return this.variableMap;
		}
		Variable variable(String name, boolean anyway) {
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

		public LabeledGraph parse(ParseNode nodes) throws ParserException {
			this.initializeParse();
			return this.newGraph(nodes);
		}
		public LabeledGraph newGraph(ParseNode nodes) throws ParserException {
			LabeledArrow[] arrows = this.newArrows(nodes);
			Variable[] xs = new Variable[this.equationCount << 1];
			Iterator<Variable> p = this.variableMap(true).values().iterator();
			while (p.hasNext()) {
				Variable x = p.next();
				int index = x.index;
				if (index < xs.length) {
					xs[index] = x;
				}
			}
			return new LabeledGraph(arrows, xs);
		}
		LabeledArrow[] newArrows(ParseNode nodes) throws ParserException {
			ParseNode node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				this.fixBoundedVariables(node);
				++this.equationCount;
			}
			List<LabeledArrow> xs = new ArrayList<LabeledArrow>();
			node = nodes.firstNode();
			for (; node != null; node = node.nextNode()) {
				FlattenEauation eqs = this.flattenEquation(node);
				this.newArrows(xs, node, eqs);
			}
			return xs.toArray(LabeledArrow.EMPTY_ARRAY);
		}
		private List<LabeledArrow> newArrows(List<LabeledArrow> output,
				ParseNode node, FlattenEauation eqs) {
			final ParseNode lhs = node.firstNode();
			final Variable variable = this.variable(lhs.value(), true);
			final List<List<Variable>> terms = eqs.terms;
			final VariablePair pair = new VariablePair();
			final Map<VariablePair, Counter> pairs = new HashMap<VariablePair, Counter>();

			for (int i = 0, n = terms.size(); i < n; ++i) {
				List<Variable> term = terms.get(i);
				Variable x = variable;
				int ix = -1;
				int lastIndex = -1;
				for (int iy = 0, ny = term.size(); iy < ny; ++iy) {
					Variable y = term.get(iy);
					if (y.isBounded()) {
						Counter memo = pairs.get(pair.set(x, y));
						{
							if (memo == null) {
								memo = new Counter(this.arrowCount++);
								pairs.put(pair.clone(), memo);
							}
							memo.count += 1;
						}
						{
							Variable[] eq = FlattenEauation.array(term, ix + 1, iy + 1);
							Variable source = x;
							int[] dycks = ArrayHelper.EMPTY_INT_ARRAY;
							if (0 <= lastIndex) {
								source = x.opposite();
								dycks = new int[] { -(lastIndex + 1), memo.index + 1 };
							} else {
								dycks = new int[] { memo.index + 1 };
							}
							Label label = new Label(eq, dycks);
							LabeledArrow arrow = new LabeledArrow(source, y, label);
							int old = output.indexOf(arrow);
							if (old < 0) {
								output.add(arrow);
							} else {
								Debug.log().debug("duplicated arrow=" + arrow);
								output.get(old).duplication += 1;
							}
						}
						{
							ix = iy;
							x = y;
							lastIndex = memo.index;
						}
					}
				}
				{
					Variable[] eq = new Variable[term.size() - ix - 1 + 1];
					FlattenEauation.array(eq, term, ix + 1, term.size());
					eq[eq.length - 1] = variable.opposite();
					int[] dycks = ArrayHelper.EMPTY_INT_ARRAY;
					Variable source = x;
					if (0 <= lastIndex) {
						source = x.opposite();
						dycks = new int[] { -(lastIndex + 1) };
					} else {
						source = variable;
					}
					Label label = new Label(eq, dycks);
					Variable target = variable.opposite();
					LabeledArrow arrow = new LabeledArrow(source, target, label);
					if (output.contains(arrow)) {
						Debug.log().debug("duplicated arrow=" + arrow);
					} else {
						output.add(arrow);
					}
				}
			}
			return output;
		}
		private FlattenEauation flattenEquation(ParseNode node)
				throws ParserException {
			return this.flattenRhs(node.lastNode());
		}
		private FlattenEauation flattenRhs(ParseNode node) throws ParserException {
			switch (node.type()) {
			case ParseNode.ZERO:
				return FlattenEauation.ZERO;
			case ParseNode.ONE:
				return FlattenEauation.ONE;
			case ParseNode.VARIABLE: {
				String name = node.value();
				if (name == null) {
					String msg = "value of variable must not be null";
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
				ParseNode child = node.firstNode();
				if (child == null) {
					String msg = "multiplies must have children";
					throw new ParserException(msg);
				}
				FlattenEauation x = this.flattenRhs(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					FlattenEauation y = this.flattenRhs(child);
					x = x.multiplies(y);
				}
				return x;
			}
			case ParseNode.PLUS: {
				ParseNode child = node.firstNode();
				if (child == null) {
					String msg = "plus must have children";
					throw new ParserException(msg);
				}
				FlattenEauation x = this.flattenRhs(child);
				for (child = child.nextNode(); child != null; child = child.nextNode()) {
					FlattenEauation y = this.flattenRhs(child);
					x = x.plus(y);
				}
				return x;
			}
			default: {
				String msg = "unexpected type of node=" + node.typeName();
				throw new ParserException(msg);
			}
			}
		}
		private void fixBoundedVariables(ParseNode node) throws ParserException {
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
			Variable start = this.variable(name, true);
			Variable end = this.variable(name + "$", true);
			start.type = Variable.BOUNDED_START;
			end.type = Variable.BOUNDED_END;
			start.makeOpposite(end);
		}
	}

	static class XyWriter {
		private static String op(String name) {
			name = name.replaceAll("_", "\\_");
			return "\\op{" + name + "}";
		}
		public static Appendable writeVertex(Appendable output, AdVertex x)
				throws IOException {
			if (x.isType(Variable.BOUNDED_END)) {
				return output.append("\\bar{").append(op(x.opposite().name()))
						.append("}");
			}
			return output.append(op(x.name()));
		}
		public static Appendable writeArrowLabel(Appendable output, Label label)
				throws IOException {
			Variable[] xs = label.variables;
			for (int xi = 0, xn = xs.length; xi < xn; ++xi) {
				Variable x = xs[xi];
				if (x.isType(Variable.BOUNDED_START)) {
					output.append(op(x.name())).append("^{\\ini}");
				} else if (x.isType(Variable.BOUNDED_END)) {
					output.append(op(x.opposite().name())).append("^{\\fin}");
				} else {
					output.append(op(x.name()));
				}
			}
			int[] ys = label.dycks;
			for (int yi = 0, yn = ys.length; yi < yn; ++yi) {
				int y = ys[yi];
				if (0 < y) {
					output.append('[').append(Integer.toString(y)).append(']');
				} else if (y < 0) {
					output.append('[').append(Integer.toString(-y)).append("]^\\dag");
				}
			}
			return output;
		}
		private static Appendable writeArrowLabels(Appendable writer, Label[] labels)
				throws IOException {
			for (int zi = 0, zn = labels.length; zi < zn; ++zi) {
				if (zi != 0) {
					writer.append(" + ");
				} else {
					writer.append(" ");
				}
				XyWriter.writeArrowLabel(writer, labels[zi]);
			}
			return writer;
		}
		public static Appendable writeArrowOffset(Appendable output,
				AdVertex source, AdVertex target) throws IOException {
			if (source.isType(Variable.BOUNDED_START)) {
				if (target.isType(Variable.BOUNDED_START)) {
					int col = (target.index() - source.index()) / 2;
					if (1 < col || col < -1) {
						output.append("@(u,u)");
					}
					return XyWriter.writeArrowOffset(output.append('['), 0, col).append(
							']');
				} else {
					int col = (target.index() - source.index() - 1) / 2;
					return XyWriter.writeArrowOffset(output.append('['), 1, col).append(
							']');
				}
			} else {
				if (target.isType(Variable.BOUNDED_START)) {
					int col = (target.index() - source.index() + 1) / 2;
					return XyWriter.writeArrowOffset(output.append('['), -1, col).append(
							']');
				} else {
					int col = (target.index() - source.index()) / 2;
					if (1 < col || col < -1) {
						output.append("@(d,d)");
					}
					return XyWriter.writeArrowOffset(output.append('['), 0, col).append(
							']');
				}
			}
		}
		private static Appendable writeArrowOffset(Appendable output, int row,
				int col) throws IOException {
			if (0 < row) {
				while (0 < row--) {
					output.append('d');
				}
			} else if (row < 0) {
				while (row++ < 0) {
					output.append('u');
				}
			}
			if (0 < col) {
				while (0 < col--) {
					output.append('r');
				}
			} else if (col < 0) {
				while (col++ < 0) {
					output.append('l');
				}
			}
			return output;
		}
		public static Appendable write(Appendable writer, AdVertex[] vertices)
				throws IOException {
			Set<VariablePair> arrows = new HashSet<VariablePair>();
			VariablePair arrow = new VariablePair();

			writer.append("\\xymatrix{\n\t");
			for (int xi = 0, xn = vertices.length; xi < xn; ++xi) {
				if ((xi & 1) == 1) {
					continue;
				} else if (xi != 0) {
					writer.append(" & ");
				}
				AdVertex source = vertices[xi];
				AdSummaryEdge[] targets = source.summaryTargets();
				XyWriter.writeVertex(writer, source);
				for (int yi = 0, yn = targets.length; yi < yn; ++yi) {
					AdVertex target = targets[yi].target;
					Label[] labels = targets[yi].labels;
					if (source == target) {
						writer.append("\\ar@(ul,ur)[]^{");
						XyWriter.writeArrowLabels(writer, labels).append("}");
					} else {
						writer.append("\\ar");
						if (arrows.contains(arrow.set(target.variable, source.variable))) {
							writer.append("@<1ex>");
						}
						XyWriter.writeArrowOffset(writer, source, target).append("^{");
						XyWriter.writeArrowLabels(writer, labels).append("}");
					}
				}
			}
			writer.append(" \\\\\n\t");
			for (int xi = 0, xn = vertices.length; xi < xn; ++xi) {
				if ((xi & 1) == 0) {
					continue;
				} else if (xi != 1) {
					writer.append(" & ");
				}
				AdVertex source = vertices[xi];
				AdSummaryEdge[] targets = source.summaryTargets();
				XyWriter.writeVertex(writer, source);
				for (int yi = 0, yn = targets.length; yi < yn; ++yi) {
					AdVertex target = targets[yi].target;
					Label[] labels = targets[yi].labels;
					if (source == target) {
						writer.append("\\ar@(dr,dl)[]^{");
						XyWriter.writeArrowLabels(writer, labels).append("}");
					} else {
						writer.append("\\ar");
						if (arrows.contains(arrow.set(target.variable, source.variable))) {
							writer.append("@<1ex>");
						}
						XyWriter.writeArrowOffset(writer, source, target).append("^{");
						XyWriter.writeArrowLabels(writer, labels).append("}");
					}
				}
			}
			return writer.append("\n}\n");
		}
	}

	static class CodeWriter {
		String nodeType = "ParseNode";
		String inputType = "AsciiInput";
		String inputVariable = "input";

		Appendable write(Appendable output, AdVertex[] vertices) throws IOException {
			for (int xi = 0, xn = vertices.length; xi < xn; ++xi) {
				AdVertex source = vertices[xi];
				if (!source.isType(Variable.BOUNDED_START)) {
					continue;
				}
				output.append(this.nodeType).append(" parse").append(source.name())
						.append('(').append(this.inputType).append(' ')
						.append(this.inputVariable).append(") {\n");
				output.append("}\n");
			}
			return output;
		}
	}

	public void testWriters() throws IOException {
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
		LabeledGraph graph = parser.parse(node);

		Writer writer = new PrintWriter(System.out);
		{
			XyWriter.write(writer, graph.getAdVertices());
			writer.flush();
		}
		{
			CodeWriter dumper = new CodeWriter();
			dumper.write(writer, graph.getAdVertices());
			writer.flush();
		}
	}
}
