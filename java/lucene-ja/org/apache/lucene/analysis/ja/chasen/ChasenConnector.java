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

import java.io.IOException;

/**
 * ChasenConnector class is a abstract class for using Chasen.
 * 
 * @author Kazuhiro Kazama
 */
public abstract class ChasenConnector {
  static public ChasenConnector getInstance() {
    return (ChasenConnector) new NetworkConnector();
    //return (ChasenConnector)new ProcessConnector();
  }

  public abstract void open() throws IOException;

  public abstract void quit() throws IOException;

  public abstract void close() throws IOException;

  public abstract String readLine() throws IOException;

  public abstract void println(String s) throws IOException;
}
