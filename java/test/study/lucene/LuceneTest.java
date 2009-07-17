package study.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.LockObtainFailedException;

import study.io.FileHelper;
import study.lang.Debug;

public class LuceneTest extends TestCase {
	private static final String SEN_HOME_KEY = "sen.home";
	private static final String SEN_HOME_VALUE = "sen";

	protected void setUp() throws Exception {
		super.setUp();
		this.configureLucene();
	}
	private void configureLucene() {
		System.setProperty(SEN_HOME_KEY, SEN_HOME_VALUE);
	}
	public void testIndexer() throws CorruptIndexException,
			LockObtainFailedException, IOException {
		boolean create = true;
		final String inputPath = "lucene-ja/docs-ja";
		final String outputPath = "data/lucene";
		final File inputDir = new File(inputPath);
		final String[] inputFiles = inputDir.list();
		final File outputDir = new File(outputPath, "test-index");
		{
			IndexWriter writer = null;
			try {
				writer = new IndexWriter(outputDir, new JapaneseAnalyzer(), create,
						new MaxFieldLength(IndexWriter.DEFAULT_MAX_FIELD_LENGTH));
				for (int i = 0; i < inputFiles.length; i++) {
					final Document doc = new Document();
					final String path = inputPath + '/' + inputFiles[i];
					doc.add(new Field("url", path, Store.NO, Index.NOT_ANALYZED));
					final Reader reader = new FileReader(path);
					doc.add(new Field("contents", reader));
					writer.addDocument(doc);
				}
				writer.optimize();
			} finally {
				writer.close();
			}
		}

		{
			IndexReader reader = null;
			PrintWriter writer = null;
			try {
				final String fieldName = "contents";
				final File outputFile = new File(outputPath, fieldName + ".txt");
				writer = FileHelper.getPrintWriter(outputFile, "UTF-8");
				reader = IndexReader.open(outputDir);
				final TermEnum terms = reader.terms(new Term(fieldName, "*"));
				do {
					final Term term = terms.term();
					if (!term.field().equals(fieldName))
						break;
					writer.println(term.text());
				} while (terms.next());
				terms.close();
				Debug.log().info("wrote=" + outputFile.getAbsolutePath());
			} finally {
				reader.close();
				writer.close();
			}
		}
	}
}
