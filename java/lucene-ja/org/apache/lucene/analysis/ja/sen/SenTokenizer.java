package org.apache.lucene.analysis.ja.sen;

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
import java.io.Reader;
import java.util.Locale;

import net.java.sen.StreamTagger;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

/**
 * This is a Japanese tokenizer which uses "Sen" morphological
 * analyzer.
 *
 * @author Takashi Okamoto
 * @author Kazuhiro Kazama
 */
public class SenTokenizer extends Tokenizer {
    private StreamTagger tagger = null;

    public SenTokenizer(Reader in, String configFile) throws IOException {
        //        reader = new BufferedReader(in);
        input = in;
        tagger = new StreamTagger(input, configFile);
    }
    
    public Token next() throws IOException {
        if (!tagger.hasNext()) return null;
        net.java.sen.Token token = tagger.next();

        if (token == null) return next();

        return new Token
            (token.getBasicString(),
             token.start(),
             token.end(),
             token.getPos());
    }

  public void close() throws IOException {
    // TODO Auto-generated method stub
    super.close();
  }
}





