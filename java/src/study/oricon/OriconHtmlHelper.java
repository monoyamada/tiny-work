package study.oricon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import study.io.FileHelper;
import study.lang.ArrayHelper;
import study.lang.Debug;
import study.lang.Messages;
import study.lang.ObjectHelper;
import study.lang.StringHelper;

public class OriconHtmlHelper {
	protected static final boolean ELIMINATE_ZENKAKU = true;
	protected static final int HANKAKU_MINUS_ZENKAKU = 'A' - '‚`';
	protected static int[] HANKAKU_SYMBOLS = { '!', '#', '$', '%', '&', '(', ')',
			'*', '+', '<', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[',
			']', '^', '_', '{', '|', '}' };

	public static int getRankingData(OriconRanking[] output, File input)
			throws IOException {
		if (output.length < 1) {
			return 0;
		}
		final String table = OriconHtmlHelper.getRankingTable(input);
		return OriconHtmlHelper.getRankingData(output, table);
	}

	protected static int getRankingData(OriconRanking[] output, String table)
			throws IOException {
		if (output.length < 1) {
			return 0;
		}
		final String[] tokens = new String[3];
		final String[][] attrs = new String[2][100];
		final OriconRanking ranking = new OriconRanking();
		final int LOOK_ROW = 0;
		final int LOOK_COL = LOOK_ROW + 1;
		final int LOOK_BRAND = LOOK_COL + 1;
		final int LOOK_PRODUCT = LOOK_BRAND + 1;
		final int LOOK_SALES = LOOK_PRODUCT + 1;
		int got = 0;
		int rank = 0;
		int look = LOOK_ROW;
		int iRow = 0;
		OUTER: for (int begin = 0, end = table.length(); begin < end;) {
			begin = StringHelper.skipSpaces(table, begin, end);
			if (begin == end) {
				break;
			}
			begin = OriconHtmlHelper.readTag(tokens, table, begin, end);
			final String tag = tokens[0];
			if (ObjectHelper.equals(tag, "table")) {
			} else if (ObjectHelper.equals(tag, "/table")) {
			} else if (ObjectHelper.equals(tag, "tr")) {
				switch (look) {
				case LOOK_ROW:
					ranking.clear();
					look = LOOK_COL;
					break;
				default:
					throw new IOException(Messages.getUnexpectedValue("state", LOOK_ROW,
							look));
				}
			} else if (ObjectHelper.equals(tag, "/tr")) {
				switch (look) {
				case LOOK_COL:
					if (((got >> LOOK_BRAND) & (got >> LOOK_PRODUCT)
							& (got >> LOOK_SALES) & 1) == 1) {
						if (rank < output.length) {
							// Debug.log().debug(ranking);
							if (Debug.isDebug()) {
								if (ranking.getProduct().getIndex() == 105738) {
									Debug.log().debug(ranking.getProduct().getName());
								}
							}
							if (output[rank] == null) {
								output[rank++] = ranking.clone();
							} else {
								output[rank++].copy(ranking);
							}
							if (output.length <= rank) {
								break OUTER;
							}
						}
					}
					++iRow;
					got = 0;
					look = LOOK_ROW;
					break;
				default:
					throw new IOException(Messages.getUnexpectedValue("state", LOOK_COL,
							look));
				}
			} else if (ObjectHelper.equals(tag, "td")) {
				switch (look) {
				case LOOK_COL:
					break;
				default:
					throw new IOException(Messages.getUnexpectedValue("state", LOOK_COL,
							look));
				}
				final String val = OriconHtmlHelper.getAttribute(attrs, tokens[1],
						"class", null);
				if (val == null) {
				} else if (val.equals("artist")) {
					look = LOOK_BRAND;
				} else if (val.equals("title")) {
					look = LOOK_PRODUCT;
				} else if (val.equals("number")) {
					// Debug.log().debug("sales=" + tokens[2]);
					int value = Integer.parseInt(tokens[2].trim());
					ranking.setSales(value);
					got |= 1 << LOOK_SALES;
				} else {
				}
			} else if (ObjectHelper.equals(tag, "/td")) {
				look = LOOK_COL;
			} else if (ObjectHelper.equals(tag, "a")) {
				int id;
				String name;
				switch (look) {
				case LOOK_PRODUCT:
					name = getAttribute(attrs, tokens[1], "href", null);
					id = pathToProductId(name);
					name = tokens[2].trim();
					if (ELIMINATE_ZENKAKU) {
						name = zenkakuToHankaku(name);
					}
					ranking.setProduct(id, name);
					got |= 1 << LOOK_PRODUCT;
					look = LOOK_COL;
					// Debug.log().debug("product=(" + id + ", "+name+")");
					break;
				case LOOK_BRAND:
					name = getAttribute(attrs, tokens[1], "href", null);
					id = pathToBrandId(name);
					name = tokens[2].trim();
					if (ELIMINATE_ZENKAKU) {
						name = zenkakuToHankaku(name);
					}
					ranking.setBrand(id, name);
					got |= 1 << LOOK_BRAND;
					look = LOOK_COL;
					// Debug.log().debug("brand=(" + id + ", "+name+")");
					break;
				}
			}
		}
		return rank;
	}
	protected static String zenkakuToHankaku(String name) {
		final int n = name.length();
		int ind = 0;
		for (; ind < n; ++ind) {
			final char oldCh = name.charAt(ind);
			final char newCh = zenkakuToHankaku(oldCh);
			if (oldCh != newCh) {
				break;
			}
		}
		if (ind == n) {
			return name;
		}
		final char[] array = name.toCharArray();
		for (; ind < n; ++ind) {
			final char oldCh = name.charAt(ind);
			array[ind] = zenkakuToHankaku(oldCh);
		}
		return new String(array);
	}
	protected static char zenkakuToHankaku(char ch) {
		final int x = ch + HANKAKU_MINUS_ZENKAKU;
		if (('A' <= x && x <= 'Z') || ('a' <= x && x <= 'z')
				|| ('0' <= x && x <= '9')
				|| 0 <= ArrayHelper.indexOf(HANKAKU_SYMBOLS, x)) {
			return (char) x;
		} else if (ch == '\u3000') {
			return ' ';
		} else if (ch == 'f') {
			return '\'';
		}
		return ch;
	}

	protected static String getRankingTable(File file) throws IOException {
		final int LOOK_FOR_BEGIN = 1;
		final int LOOK_FOR_END = LOOK_FOR_BEGIN + 1;
		final String eol = "\n";
		final StringBuilder buffer = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = FileHelper.getBufferedReader(file,
					OriconConstant.ORICON_HTML_ENCODING);
			int lookFor = LOOK_FOR_BEGIN;
			String line = reader.readLine();
			int iLine = 1;
			OUTER: for (; line != null; ++iLine, line = reader.readLine()) {
				int ind = -1;
				switch (lookFor) {
				case LOOK_FOR_BEGIN:
					ind = line.indexOf("class=\"search_list\"");
					if (0 <= ind) {
						ind = line.indexOf("<table");
						if (0 <= ind) {
							lookFor = LOOK_FOR_END;
							buffer.append(line.substring(ind) + eol);
						} else {
							String msg = Messages.getUnexpectedLine(file, iLine, line);
							throw new IOException(msg);
						}
					}
					break;
				case LOOK_FOR_END:
					ind = line.indexOf("</table>");
					if (0 <= ind) {
						ind += "</table>".length();
						buffer.append(line.substring(0, ind) + eol);
						break OUTER;
					} else {
						buffer.append(line + eol);
					}
					break;
				default:
					throw new IOException("could not read table");
				}
			}
		} finally {
			FileHelper.close(reader);
		}
		return buffer.toString();
	}
	protected static int pathToBrandId(String path) {
		final String prefix = OriconConstant.BRAND_PATH_PREFIX;
		if (path == null || path.length() < prefix.length()) {
			return -1;
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.length() < prefix.length()) {
			return -1;
		}
		path = path.substring(prefix.length());
		try {
			final int id = Integer.parseInt(path);
			return 0 <= id ? id : -1;
		} catch (NumberFormatException ex) {
		}
		return -1;
	}
	protected static int pathToProductId(String path) {
		final String prefix = OriconConstant.PRODUCT_PATH_PREFIX;
		if (path == null || path.length() < prefix.length()) {
			return -1;
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.length() < prefix.length()) {
			return -1;
		}
		path = path.substring(prefix.length());
		final int ind = path.indexOf('/');
		if (ind < 0) {
			return -1;
		}
		path = path.substring(0, ind);
		try {
			final int id = Integer.parseInt(path);
			return 0 <= id ? id : -1;
		} catch (NumberFormatException ex) {
		}
		return -1;
	}
	protected static String getAttribute(String[][] buffer, String text,
			String name, String def) throws IOException {
		final int n = OriconHtmlHelper.getAttributes(buffer, text);
		return OriconHtmlHelper.getAttribute(buffer, 0, n, name, def);
	}
	protected static String getAttribute(String[][] attrs, int begin, int end,
			String name, String def) {
		int index = ArrayHelper.indexOf(attrs[0], begin, end, name);
		return 0 <= index ? attrs[1][index] : def;
	}
	protected static int getAttributes(String[][] output, String text)
			throws IOException {
		final int max = output.length;
		int n = 0;
		int begin = 0;
		final int end = text.length();
		for (; begin < end && n < max;) {
			begin = StringHelper.skipSpaces(text, begin, end);
			if (end <= begin) {
				break;
			}
			int index = text.indexOf('=', begin);
			if (index < 0 || end <= index + 2) {
				throw new IOException(text);
			}
			final String name = text.substring(begin, index);
			begin = index + 1;
			index = text.indexOf('"', begin);
			if (index < 0 || end <= index + 1) {
				throw new IOException(text);
			}
			begin = index + 1;
			index = text.indexOf('"', begin);
			if (index < 0 || end <= index) {
				throw new IOException(text);
			}
			final String value = text.substring(begin, index);
			begin = index + 1;
			output[0][n] = name;
			output[1][n] = value;
			++n;
		}
		return n;
	}

	protected static int readTag(String[] output, String text, int begin, int end)
			throws IOException {
		Arrays.fill(output, null);
		for (; begin < end && text.charAt(begin++) != '<';) {
		}
		if (end <= begin) {
			return end;
		}
		int end1 = text.indexOf('>', begin);
		if (end1 < 0) {
			throw new IOException(Messages.getFailedOperation("find >"));
		} else if (end <= end1) {
			return end;
		}
		final String tag = text.substring(begin, end1);
		final String[] tokens = tag.split("\\s+", 2);
		switch (tokens.length) {
		case 0:
			throw new IOException(tag);
		case 1:
			output[0] = tokens[0].trim();
			break;
		case 2:
			output[0] = OriconHtmlHelper.trim(tokens[0]);
			output[1] = OriconHtmlHelper.trim(tokens[1]);
			break;
		default:
			throw new Error("bug");
		}
		begin = ++end1;
		end1 = text.indexOf('<', begin);
		end1 = 0 <= end1 ? end1 : end;
		output[2] = OriconHtmlHelper.trim(text.substring(begin, end1));
		return end1;
	}

	private static String trim(String text) {
		text = StringHelper.trim(text);
		if (text != null && text.length() < 1) {
			return null;
		}
		return text;
	}

	public static int getReleaseDay(File file) throws IOException {
		BufferedReader reader = null;
		try {
			final String sampleDate = "2000”N01ŒŽ26“ú”­”„";
			final String dateMarker = "“ú”­”„";
			reader = FileHelper.getBufferedReader(file,
					OriconConstant.ORICON_HTML_ENCODING);
			String line = reader.readLine();
			int iLine = 1;
			for (; line != null; line = reader.readLine(), ++iLine) {
				if (sampleDate.length() <= line.length()) {
					final int last = line.indexOf(dateMarker);
					if (sampleDate.length() - dateMarker.length() <= last) {
						String segment = line.substring(last - sampleDate.length()
								+ dateMarker.length(), last);
						final Matcher mather = Pattern.compile("\\d++”N\\d++ŒŽ\\d++")
								.matcher(segment);
						if (mather.find()) {
							segment = segment.substring(mather.start(), mather.end());
							final String[] tokens = segment.split("”N|ŒŽ");
							if (tokens.length == 3) {
								try {
									final int y = Integer.parseInt(tokens[0].trim());
									final int m = Integer.parseInt(tokens[1].trim());
									final int d = Integer.parseInt(tokens[2].trim());
									final long msec = OriconDateHelper.newCalendar(y, m, d)
											.getTimeInMillis();
									return OriconDateHelper.systemTimeToDay(msec);
								} catch (NumberFormatException ex) {
									ex.printStackTrace();
								}
							}
						}
					}
				}
			}
		} finally {
			FileHelper.close(reader);
		}
		return -1;
	}
}
