/*   0*/package org.mockito.internal.util.io;
/*   0*/
/*   0*/import java.io.BufferedReader;
/*   0*/import java.io.Closeable;
/*   0*/import java.io.File;
/*   0*/import java.io.FileWriter;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.InputStreamReader;
/*   0*/import java.io.PrintWriter;
/*   0*/import java.util.Collection;
/*   0*/import java.util.LinkedList;
/*   0*/import java.util.List;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/
/*   0*/public class IOUtil {
/*   0*/  public static void writeText(String text, File output) {
/*  19*/    PrintWriter pw = null;
/*   0*/    try {
/*  21*/      pw = new PrintWriter(new FileWriter(output));
/*  22*/      pw.write(text);
/*  23*/    } catch (Exception e) {
/*  24*/      throw new MockitoException("Problems writing text to file: " + output, e);
/*   0*/    } finally {
/*  26*/      close(pw);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public static Collection<String> readLines(InputStream is) {
/*  31*/    List<String> out = new LinkedList<String>();
/*  32*/    BufferedReader r = new BufferedReader(new InputStreamReader(is));
/*   0*/    try {
/*   0*/      String line;
/*  35*/      while ((line = r.readLine()) != null)
/*  36*/        out.add(line); 
/*  38*/    } catch (IOException e) {
/*  39*/      throw new MockitoException("Problems reading from: " + is, e);
/*   0*/    } 
/*  41*/    return out;
/*   0*/  }
/*   0*/  
/*   0*/  public static void closeQuietly(Closeable closeable) {
/*   0*/    try {
/*  51*/      close(closeable);
/*  52*/    } catch (MockitoException mockitoException) {}
/*   0*/  }
/*   0*/  
/*   0*/  public static void close(Closeable closeable) {
/*  63*/    if (closeable != null)
/*   0*/      try {
/*  65*/        closeable.close();
/*  66*/      } catch (IOException e) {
/*  67*/        throw new MockitoException("Problems closing stream: " + closeable, e);
/*   0*/      }  
/*   0*/  }
/*   0*/}
