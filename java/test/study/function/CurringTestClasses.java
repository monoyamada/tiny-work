package study.function;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import study.io.FileHelper;

public class CurringTestClasses {
	public static class Prototype_0 {
		public String readLine(File file, long line) throws IOException {
			BufferedReader reader = null;
			try {
				reader = this.open(file);
				this.skipLines(reader, line);
				return reader.readLine();
			} finally {
				this.close(reader);
			}
		}
		protected void skipLines(BufferedReader reader, long line)
				throws IOException {
			while (0 < line--) {
				reader.readLine();
			}
		}
		protected BufferedReader open(File file) throws IOException {
			return FileHelper.getBufferedReader(file, "UTF-8");
		}
		protected void close(Closeable stream) {
			FileHelper.close(stream);
		}
	}

	protected static class Prototype_1 extends Prototype_0 {
		@Override
		public String readLine(File file, long line) throws IOException {
			Cursor cursor = null;
			try {
				cursor = this.getCursor(file);
				return cursor.readLine(line);
			} finally {
				this.close(cursor);
			}
		}
		public Cursor getCursor(final File file) {
			return new Cursor() {
				@Override
				protected BufferedReader open() throws IOException {
					return this.getPrototype().open(file);
				}
				@Override
				protected Prototype_1 getPrototype() {
					return Prototype_1.this;
				}
			};
		}
	}

	public static abstract class Cursor implements Closeable {
		private long nextLine;
		private BufferedReader reader;

		protected long getNextLine() {
			return this.nextLine;
		}
		protected void setNextLine(long lastLine) {
			this.nextLine = lastLine;
		}
		public String readLine() throws IOException {
			return this.readLine(this.getNextLine());
		}
		public String readLine(long line) throws IOException {
			if (line < this.getNextLine()) {
				this.close();
			} else {
				line -= this.getNextLine();
			}
			final BufferedReader reader = this.getReader(true);
			this.getPrototype().skipLines(reader, line);
			this.setNextLine(line + 1);
			return reader.readLine();
		}
		public void close() {
			try {
				final BufferedReader reader = this.getReader(false);
				if (reader != null) {
					this.getPrototype().close(reader);
					this.setReader(null);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		protected BufferedReader getReader(boolean anyway) throws IOException {
			if (this.reader == null && anyway) {
				this.reader = this.open();
			}
			return this.reader;
		}
		protected void setReader(BufferedReader reader) {
			this.reader = reader;
		}
		protected abstract Prototype_0 getPrototype();
		protected abstract BufferedReader open() throws IOException;
	}
}
