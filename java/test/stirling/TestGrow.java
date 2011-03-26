package stirling;

import graycode.CompositionHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.StringHelper;

public class TestGrow extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	static void grow(Collection<int[]> output, int[] value) {
		for (int i = 0, n = value.length; i < n; ++i) {
			int[] newValue = value.clone();
			newValue[i] += 1;
			output.add(newValue);
		}
		int[] newValue = new int[value.length + 1];
		System.arraycopy(value, 0, newValue, 0, value.length);
		newValue[value.length] = 1;
		output.add(newValue);
	}
	static String[] toStrings(Iterable<int[]> value) throws IOException {
		List<String> list = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();
		for (Iterator<int[]> p = value.iterator(); p.hasNext();) {
			StringHelper.clear(buffer);
			toString(buffer, p.next());
			list.add(buffer.toString());
		}
		String[] array = new String[list.size()];
		return list.toArray(array);
	}
	static void toString(Appendable output, int[] value) throws IOException {
		for (int i = 0, n = value.length; i < n; ++i) {
			output.append(Integer.toString(value[i]));
		}
	}

	static class GrowData {
		public static final GrowData[] EMPTY_ARRAY = {};
		final int[] value;
		final List<GrowData> grow;

		GrowData(int[] value) {
			this.value = value;
			this.grow = new ArrayList<GrowData>();
		}
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				this.toString(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		protected void toString(Appendable output) throws IOException {
			TestGrow.toString(output, this.value);
			output.append("(");
			for (int i = 0, n = this.grow.size(); i < n; ++i) {
				if (i != 0) {
					output.append(",");
				}
				TestGrow.toString(output, this.grow.get(i).value);
			}
			output.append(")");
		}
	}

	public void testGrow() throws IOException {
		List<int[]> buffer = new ArrayList<int[]>(1024);
		List<GrowData[]> allValues = new ArrayList<GrowData[]>();
		Map<int[], GrowData> values = new TreeMap<int[], GrowData>(
				new CompositionHelper.DictionaryOrder());
		GrowData data = new GrowData(new int[] { 1 });
		values.put(data.value, data);
		allValues.add(values.values().toArray(GrowData.EMPTY_ARRAY));
		for (int I = 0; I < 4; ++I) {
			Map<int[], GrowData> newValues = new TreeMap<int[], GrowData>(
					new CompositionHelper.DictionaryOrder());
			for (Iterator<Map.Entry<int[], GrowData>> p = values.entrySet()
					.iterator(); p.hasNext();) {
				data = p.next().getValue();
				buffer.clear();
				grow(buffer, data.value);
				for (int i = 0, n = buffer.size(); i < n; ++i) {
					int[] x = buffer.get(i);
					GrowData newData = newValues.get(x);
					if (newData == null) {
						newData = new GrowData(x);
						newValues.put(newData.value, newData);
					}
					newData.grow.add(data);
				}
			}
			values = newValues;
			allValues.add(values.values().toArray(GrowData.EMPTY_ARRAY));
		}
		for (int i = 0, n = allValues.size(); i < n; ++i) {
			Debug.log().debug(StringHelper.join(allValues.get(i)));
		}
	}
}
