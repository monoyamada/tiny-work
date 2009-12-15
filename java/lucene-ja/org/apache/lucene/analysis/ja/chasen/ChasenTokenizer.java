package org.apache.lucene.analysis.ja.chasen;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

/**
 * A grammar-based tokenizer which using a Japanese morphological analyzer.
 * 
 * <p>
 * This is a standard tokenizer for Japanese documents.
 * 
 * @author Kazuhiro Kazama
 */
public class ChasenTokenizer extends Tokenizer implements Runnable {
	/* instance variables */
	BufferedReader reader;
	ChasenConnector connector;
	Thread thread;
	int position;

	/**
	 * Constructs a tokenizer for this Reader.
	 * @param reader reader for input characters.
	 * @param configFile this is dummy argument. It is not used anywhere.
	 */
	public ChasenTokenizer(Reader reader,  String configFile) {
		try {
			this.reader = new BufferedReader(reader);
			connector = ChasenConnector.getInstance();
			connector.open();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
		thread = new Thread(this);
		thread.run();
		position = 0;
	}

	/**
	 * Returns the next token in the stream, or null at EOS.
	 * <p>
	 * The returned token's type is set to an element of {@link
	 * StandardTokenizerConstants#tokenImage}.
	 */
	public Token next() throws IOException {
		Token token = null;
		String data = readData();
		if (data == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(data, "\t");
		String term = st.nextToken();
		String reading = st.nextToken();
		String original = st.nextToken();
		String type = st.nextToken();
		int start = position;
		position += term.length();
		return new Token(term, start, position, type);
	}

	String readData() throws IOException {
		String line;
		while (((line = connector.readLine()) != null) && line.equals("EOS")) // Ignore
																			  // "EOS"
																			  // of
																			  // Chasen
		;
		//System.out.println(line); // For debug
		return line;
	}

	public void close() throws IOException {
		connector.close();
		reader.close();
	}

	/**
	 * Send inputs to a Japanese morphological analyzer.
	 */
	public void run() {
		String input;
		try {
			while ((input = reader.readLine()) != null) {
				connector.println(input);
			}
			connector.quit();
		} catch (IOException e) {
		}
	}
}
