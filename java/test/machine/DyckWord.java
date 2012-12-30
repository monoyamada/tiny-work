package machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tiny.lang.ArrayHelper;

public abstract class DyckWord<Word> {
	final String left;
	final String right;
	String generator;
	List<Word[]> words;
	ArrayList<Word> wordBuffer;

	public DyckWord(String left, String right) {
		this.left = left;
		this.right = right;
	}
	public String getGenerator() {
		if (this.generator == null) {
			this.generator = this.left + this.right;
		}
		return this.generator;
	}
	public Word[] get(int n) {
		List<Word[]> xs = this.getWords(true);
		ArrayList<Word> buffer = null;
		for (int nn = xs.size(); nn <= n; ++nn) {
			if (buffer == null) {
				buffer = this.getWordBuffer(true);
			}
			buffer.clear();
			for (int kk = 0; kk < nn; ++kk) {
				Word[] x1 = this.get(kk);
				Word[] x2 = this.get(nn - kk - 1);
				buffer.ensureCapacity(buffer.size() + x1.length * x2.length);
				this.getProduct(buffer, nn - 1, kk, x1, x2);
			}
			xs.add(buffer.toArray(getEmptyWordArray()));
		}
		return xs.get(n);
	}
	protected abstract void getProduct(Collection<Word> output, int n, int k,
			Word[] x1, Word[] x2);
	protected abstract Word[] getEmptyWordArray();
	protected List<Word[]> getWords(boolean anyway) {
		if (this.words == null) {
			this.words = newWordList();
			this.words.add(this.getUnit());
		}
		return this.words;
	}
	protected abstract List<Word[]> newWordList();
	protected ArrayList<Word> getWordBuffer(boolean anyway) {
		if (this.wordBuffer == null) {
			this.wordBuffer = newWordBuffer();
		}
		return this.wordBuffer;
	}
	protected abstract ArrayList<Word> newWordBuffer();
	protected abstract Word[] getUnit();
	
	protected String trancateWord(String word) {
		if (word == null || word.length() < 1) {
			return word;
		}
		String u = this.getGenerator();
		int n = word.length() / u.length();
		for (int k = n + 1; 2 < k--;) {
			word = word.replaceAll("(" + u + "){" + k + "}", "(_)^" + k);
		}
		for (int k = n + 1; 2 < k--;) {
			word = word.replaceAll("(" + this.left + "){" + k + "}", this.left + "^"
					+ k);
			word = word.replaceAll("(" + this.right + "){" + k + "}", this.right
					+ "^" + k);
		}
		return word.replaceAll("_", u);
	}
}
