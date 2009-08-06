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
import java.net.Socket;

/**
 * The network connector connects to Chasen server and execute morphological
 * analysis.
 * 
 * @author Kazuhiro Kazama
 */
public class NetworkConnector extends ChasenConnector {
  /* class variables */
  public static final String DEFAULT_HOSTNAME = "localhost";
  public static final int DEFAULT_PORT = 31000; /* ¥µ¥»¥ó¢ª¥Á¥ã¥»¥? */
  public static final String DEFAULT_CHARSET = "EUC-JP";

  public static final String RUN_COMMAND = "RUN";
  public static final String RC_COMMAND = "RC";
  public static final String HELP_COMMAND = "HELP";
  public static final String QUIT_COMMAND = "QUIT";

  /* instance variables */
  private String hostname;
  private int port;
  private String charset = DEFAULT_CHARSET;
  private BufferedReader in;
  private PrintWriter out;
  private boolean needCheck = false;

  public NetworkConnector() {
    hostname = System.getProperty("chasen.host", DEFAULT_HOSTNAME);
    String s = System.getProperty("chasen.port");
    if (s == null)
      port = DEFAULT_PORT;
    else
      port = Integer.parseInt(s);
  }

  public void open() throws IOException {
    Socket s = new Socket(hostname, port);
    in = new BufferedReader(new InputStreamReader(s.getInputStream(), charset));
    out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), charset));
    checkStatus();
    out.print(RUN_COMMAND + "\r\n");
    out.flush();
    needCheck = true;
  }

  public String readLine() throws IOException {
    if (needCheck) {
      checkStatus();
      needCheck = false;
    }
    String line = in.readLine();
    if (line.equals("."))
      return null;
    if (line.length() > 1 && line.charAt(0) == '.')
      line = line.substring(1);
    return line;
  }

  public void println(String s) throws IOException {
    if (s.length() > 0 && s.charAt(0) == '.')
      out.print('.');
    out.print(s);
    out.print("\r\n");
    out.flush();
  }

  void checkStatus() throws IOException {
    String s = in.readLine();
    if (!s.startsWith("200 "))
      throw new IllegalStateException("Illegal return code: " + s);
  }

  public void quit() {
    out.print(".\r\n");
    out.print("QUIT\r\n");
    out.flush();
  }

  public void close() throws IOException {
    in.close();
    out.close();
  }
}
