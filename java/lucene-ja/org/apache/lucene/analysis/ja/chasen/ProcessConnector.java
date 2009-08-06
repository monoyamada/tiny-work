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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * This is a sample code and don't work yet.
 * 
 * @author Kazuhiro Kazama
 */
public class ProcessConnector extends ChasenConnector {
  /* class variables */
  private final static String CHASEN_COMMAND = "/usr/local/bin/chasen";
  private final static String EOF = "\u0004";

  /* instance variables */
  private BufferedReader in;
  private PrintWriter out;
  private Process process;

  public ProcessConnector() {
  }

  public void open() throws IOException {
    Process process = Runtime.getRuntime().exec(CHASEN_COMMAND);

    in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    out = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));
  }

  public synchronized String readLine() throws IOException {
    try {
      return in.readLine();
    } catch (NullPointerException e) {
    }
    return null;
  }

  public synchronized void println(String s) throws IOException {
    out.println(s);
    out.flush();
  }
  public void quit() {
    out.flush();
    out.close();
  }

  public void close() throws IOException {
    //in.close();
    //process.destroy();
  }
}
