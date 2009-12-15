package study.oricon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OriconData implements Cloneable {
	public static final OriconData[] EMPTY_ARRAY = {};
	private Map<String, Object> auxMap;

	public Map<String, Object> getAuxMap() {
		return this.getAuxMap(true);
	}
	protected Map<String, Object> getAuxMap(boolean anyway) {
		if (this.auxMap == null && anyway) {
			this.auxMap = this.newAuxMap();
		}
		return this.auxMap;
	}
	protected Map<String, Object> newAuxMap() {
		return new HashMap<String, Object>();
	}
	protected void setAuxMap(Map<String, Object> auxMap) {
		this.auxMap = auxMap;
	}

	protected OriconData clone() {
		try {
			final OriconData that = (OriconData) super.clone();
			that.auxMap = null;
			return that;
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException(ex);
		}
	}
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		try {
			this.toString(buffer);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public void toString(Appendable output) throws IOException {
	}
}
