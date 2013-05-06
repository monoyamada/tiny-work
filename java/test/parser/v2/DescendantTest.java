package parser.v2;

import java.io.IOException;

import tiny.lang.Debug;
import tiny.lang.FileHelper;

public class DescendantTest extends ParserTest_1_0 {
	static class InputData {
		final ByteInput input;
		long position;

		InputData(ByteInput input) {
			this.input = input;
		}
		int get() throws IOException {
			return this.input.get(this.position);
		}
		InputData next() {
			++this.position;
			return this;
		}
	}

	static class TestParser extends Descendant<InputData, ParseNode> {
		@Override
		protected long position(InputData input) {
			return input.position;
		}
		@Override
		protected InputData position(InputData input, long position) {
			input.position = position;
			return input;
		}
		@Override
		protected ParseNode newOutput(int tag, long position) {
			switch(tag){
			case VAR_S:
				return new ParseNode(ParseNode.MULTIPLIES).begin(position).value("S");
			case VAR_A:
				return new ParseNode(ParseNode.MULTIPLIES).begin(position).value("A");
			case VAR_B:
				return new ParseNode(ParseNode.MULTIPLIES).begin(position).value("B");
			case VAR_C:
				return new ParseNode(ParseNode.MULTIPLIES).begin(position).value("C");
			case VAR_a:
				return new ParseNode(ParseNode.VARIABLE).begin(position).value("a");
			case VAR_b:
				return new ParseNode(ParseNode.VARIABLE).begin(position).value("b");
			case VAR_c:
				return new ParseNode(ParseNode.VARIABLE).begin(position).value("c");
			default:
				return new ParseNode(ParseNode.VARIABLE).begin(position).value("unknown");
			}
		}
		@Override
		protected ParseNode addOutput(ParseNode parent, ParseNode child) {
			return parent.add(child);
		}
		@Override
		protected TestParser backOutput(ParseNode dicarding) {
			return this;
		}
		@Override
		protected ParseNode read_a(InputData input) throws IOException {
			if (input.get() == 'a') {
				ParseNode out = this.newOutput(VAR_a, input.position);
				input.next();
				return out.add(1);
			}
			return null;
		}
		@Override
		protected ParseNode read_b(InputData input) throws IOException {
			if (input.get() == 'b') {
				ParseNode out = this.newOutput(VAR_b, input.position);
				input.next();
				return out.add(1);
			}
			return null;
		}
		@Override
		protected ParseNode read_c(InputData input) throws IOException {
			if (input.get() == 'c') {
				ParseNode out = this.newOutput(VAR_c, input.position);
				input.next();
				return out.add(1);
			}
			return null;
		}
		public ParseNode read(int which, ByteInput input) throws IOException {
			InputData data = new InputData(input);
			return this.read(which, data);
		}
	}

	public void test_read() throws IOException {
		ByteInput input = new ByteArrayInput("ac".getBytes(FileHelper.UTF_8));;
		TestParser parser = new TestParser();
		ParseNode node = parser.read(TestParser.VAR_S, input);
		Debug.log().debug(node != null ? node.toInfix() : null);
	}
}
