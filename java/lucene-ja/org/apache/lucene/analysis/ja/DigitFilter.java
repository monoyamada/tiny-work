package org.apache.lucene.analysis.ja;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * Merge digits extracted with a Japanese tokenizer.
 * 
 * @author Kazuhiro Kazama
 */
public final class DigitFilter extends TokenFilter {

	/* Instance variables */
	boolean preRead;

	Token preReadToken;

	/**
	 * Construct filtering <i>in </i>.
	 */

	protected DigitFilter(TokenStream in) {
		super(in);
		preRead = false;
		preReadToken = null;
		input = in;
	}

	/**
	 * Returns the next token in the stream, or null at EOS.
	 * <p>
	 * Merge consecutive digits.
	 */
	public final Token next() throws IOException {
		if (preRead) {
			preRead = false;
			return preReadToken;
		}
		Token t = input.next();
		if (t == null)
			return null;
		String term = t.termText();
		if (term.length() == 1 && Character.isDigit(term.charAt(0))) {
			int start = t.startOffset();
			int end = t.endOffset();
			String type = t.type();
			StringBuffer st = new StringBuffer();
			st.append(t.termText());
			while (true) {
				t = input.next();
				if (t == null
						|| (t.termText().length() != 1 || !Character.isDigit(t
								.termText().charAt(0)))) {
					preRead = true;
					preReadToken = t;
					return new Token(new String(st), start, end, type);
				}
				st.append(t.termText());
				end = t.endOffset();
			}
		}
		return t;
	}
}
