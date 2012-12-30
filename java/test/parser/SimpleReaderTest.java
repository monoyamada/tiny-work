package parser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import parser.SimpleReader.Token;
import parser.SimpleReader.Variable;
import tiny.lang.Debug;
import tiny.lang.FileHelper;

public class SimpleReaderTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void test1() throws Exception {
		class Test {
			private static final char P = 'P';
			private static final char T = 'T';
			private static final char V = 'V';
			private static final int EOF = -1;

			int doit(String input, int index, Appendable output, char state)
					throws IOException {
				switch (state) {
				case P: {
					index = this.doit(input, index, output.append("<" + state + ">"), T);
					int c = this.getc(input, index);
					switch (c) {
					case '+':
						index = this.doit(input, index + 1, output, P);
					break;
					default:
					break;
					}
					output.append("</" + state + ">");
					return index;
				}
				case T: {
					index = this.doit(input, index, output.append("<" + state + ">"), V);
					int c = this.getc(input, index);
					switch (c) {
					case '*':
						index = this.doit(input, index + 1, output, T);
					break;
					default:
					break;
					}
					output.append("</" + state + ">");
					return index;
				}
				case V: {
					int c = this.getc(input, index);
					switch (c) {
					case 'x':
						output.append((char) c);
						return index + 1;
					default:
						throw new IllegalArgumentException("unknown alphabet=" + (char) c);
					}
				}
				default:
					throw new IllegalArgumentException("unknown state=" + state);
				}
			}
			int getc(String input, int index) {
				return index < input.length() ? input.charAt(index) : Test.EOF;
			}
		}
		Test test = new Test();
		try {
			StringBuilder output = new StringBuilder();
			String input = "xy";
			int index = test.doit(input, 0, output, Test.V);
			String status = index == input.length() ? "success" : "fail";
			Debug.log().debug(status + ": " + output);
		} catch (Exception ex) {
			Debug.log().debug(ex);
			throw ex;
		}
		try {
			StringBuilder output = new StringBuilder();
			String input = "x*x*x";
			int index = test.doit(input, 0, output, Test.T);
			String status = index == input.length() ? "success" : "fail";
			Debug.log().debug(status + ": " + output);
		} catch (Exception ex) {
			Debug.log().debug(ex);
			throw ex;
		}
		try {
			StringBuilder output = new StringBuilder();
			String input = "x*x*x+x*x";
			int index = test.doit(input, 0, output, Test.P);
			String status = index == input.length() ? "success" : "fail";
			Debug.log().debug(status + ": " + output);
		} catch (Exception ex) {
			Debug.log().debug(ex);
			throw ex;
		}
	}
	public void testRead() throws IOException {
		// Debug.log().debug(FileHelper.getCurrentDirectory());
		SimpleReader test = new SimpleReader();
		Writer writer = new PrintWriter(System.out);
		if (true) {
			StringReader reader = new StringReader("	x_1 + x_2 x_3\n");
			List<Token> buffer = new ArrayList<Token>();
			test.tokenize(buffer, reader);
			printTokens(writer, buffer);
			writer.write('\n');
			writer.flush();
		}
		if (true) {
			File file = new File("data/SimpleReader.txt");
			List<Token> buffer = new ArrayList<Token>();
			Reader reader = null;
			try {
				reader = FileHelper.getReader(file);
				test.tokenize(buffer, reader);
			} catch (IOException ex) {
				FileHelper.close(reader);
				throw ex;
			}
			printTokens(writer, buffer);
			writer.write('\n');
			writer.flush();
		}
	}
	static void printTokens(Writer writer, List<Token> tokens) throws IOException {
		for (int i = 0, n = tokens.size(); i < n; ++i) {
			Token x = tokens.get(i);
			switch (x.what()) {
			case Token.VARIABLE: {
				Variable v = (Variable) x;
				writer.write(v.value);
			}
			break;
			case Token.EXP_DELIM: {
				writer.write(" ; ");
			}
			break;
			case Token.ASSINGS: {
				writer.write(" = ");
			}
			break;
			case Token.PLUS: {
				writer.write(" + ");
			}
			break;
			case Token.TIMES: {
				writer.write(' ');
			}
			break;
			case Token.POWERS: {
				writer.write("^");
			}
			break;
			case Token.GE_0: {
				writer.write("*");
			}
			break;
			case Token.GE_1: {
				writer.write("+");
			}
			break;
			case Token.LE_1: {
				writer.write("?");
			}
			break;
			case Token.PAREN_L: {
				writer.write('(');
			}
			break;
			case Token.PAREN_R: {
				writer.write(')');
			}
			break;
			default:
				throw new IllegalArgumentException("unknown type=" + x.what());
			}
		}
	}
}
