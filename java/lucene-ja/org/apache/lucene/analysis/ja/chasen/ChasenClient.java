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

/**
 * This is a sample Java client for Chasen server.
 * 
 * @author Kazuhiro Kazama
 */
public class ChasenClient {
  /* instance variables */
  ChasenConnector connector;
  BufferedReader stdin;

  public ChasenClient(String[] argv) throws IOException {
    connector = ChasenConnector.getInstance();
    connector.open();
    stdin = new BufferedReader(new InputStreamReader(System.in));
  }

  public void loop() throws IOException {
    String request;
    String reply;
    while ((request = stdin.readLine()) != null) {
      connector.println(request);
      while ((reply = connector.readLine()) != null) {
        System.out.println(reply);
        if (reply.equals("EOS"))
          break;
      }
    }
  }

  public void quit() throws IOException {
    connector.quit();
    connector.close();
  }

  public static void main(String[] argv) throws IOException {
    ChasenClient client = new ChasenClient(argv);
    client.loop();
    client.quit();
  }
}
