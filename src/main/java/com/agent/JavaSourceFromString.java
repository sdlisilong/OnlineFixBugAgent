package com.agent;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class JavaSourceFromString extends SimpleJavaFileObject {
 
     private String content = null;
     public JavaSourceFromString(String name, String content) throws URISyntaxException

    {
        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = content;
     }
 
     public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException

    {
        return content;
     }
  }