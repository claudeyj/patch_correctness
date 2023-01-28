/*   0*/package org.apache.commons.compress.archivers;
/*   0*/
/*   0*/import java.io.ByteArrayInputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.OutputStream;
/*   0*/import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
/*   0*/import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
/*   0*/import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
/*   0*/import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
/*   0*/import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
/*   0*/import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
/*   0*/import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
/*   0*/import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
/*   0*/import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
/*   0*/import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
/*   0*/import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
/*   0*/
/*   0*/public class ArchiveStreamFactory {
/*   0*/  public static final String AR = "ar";
/*   0*/  
/*   0*/  public static final String CPIO = "cpio";
/*   0*/  
/*   0*/  public static final String DUMP = "dump";
/*   0*/  
/*   0*/  public static final String JAR = "jar";
/*   0*/  
/*   0*/  public static final String TAR = "tar";
/*   0*/  
/*   0*/  public static final String ZIP = "zip";
/*   0*/  
/*   0*/  public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in) throws ArchiveException {
/* 120*/    if (archiverName == null) {
/* 121*/        throw new IllegalArgumentException("Archivername must not be null."); 
/*   0*/       }
/* 124*/    if (in == null) {
/* 125*/        throw new IllegalArgumentException("InputStream must not be null."); 
/*   0*/       }
/* 128*/    if ("ar".equalsIgnoreCase(archiverName)) {
/* 129*/        return new ArArchiveInputStream(in); 
/*   0*/       }
/* 131*/    if ("zip".equalsIgnoreCase(archiverName)) {
/* 132*/        return new ZipArchiveInputStream(in); 
/*   0*/       }
/* 134*/    if ("tar".equalsIgnoreCase(archiverName)) {
/* 135*/        return new TarArchiveInputStream(in); 
/*   0*/       }
/* 137*/    if ("jar".equalsIgnoreCase(archiverName)) {
/* 138*/        return new JarArchiveInputStream(in); 
/*   0*/       }
/* 140*/    if ("cpio".equalsIgnoreCase(archiverName)) {
/* 141*/        return new CpioArchiveInputStream(in); 
/*   0*/       }
/* 143*/    if ("dump".equalsIgnoreCase(archiverName)) {
/* 144*/        return new DumpArchiveInputStream(in); 
/*   0*/       }
/* 147*/    throw new ArchiveException("Archiver: " + archiverName + " not found.");
/*   0*/  }
/*   0*/  
/*   0*/  public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out) throws ArchiveException {
/* 162*/    if (archiverName == null) {
/* 163*/        throw new IllegalArgumentException("Archivername must not be null."); 
/*   0*/       }
/* 165*/    if (out == null) {
/* 166*/        throw new IllegalArgumentException("OutputStream must not be null."); 
/*   0*/       }
/* 169*/    if ("ar".equalsIgnoreCase(archiverName)) {
/* 170*/        return new ArArchiveOutputStream(out); 
/*   0*/       }
/* 172*/    if ("zip".equalsIgnoreCase(archiverName)) {
/* 173*/        return new ZipArchiveOutputStream(out); 
/*   0*/       }
/* 175*/    if ("tar".equalsIgnoreCase(archiverName)) {
/* 176*/        return new TarArchiveOutputStream(out); 
/*   0*/       }
/* 178*/    if ("jar".equalsIgnoreCase(archiverName)) {
/* 179*/        return new JarArchiveOutputStream(out); 
/*   0*/       }
/* 181*/    if ("cpio".equalsIgnoreCase(archiverName)) {
/* 182*/        return new CpioArchiveOutputStream(out); 
/*   0*/       }
/* 184*/    throw new ArchiveException("Archiver: " + archiverName + " not found.");
/*   0*/  }
/*   0*/  
/*   0*/  public ArchiveInputStream createArchiveInputStream(InputStream in) throws ArchiveException {
/* 199*/    if (in == null) {
/* 200*/        throw new IllegalArgumentException("Stream must not be null."); 
/*   0*/       }
/* 203*/    if (!in.markSupported()) {
/* 204*/        throw new IllegalArgumentException("Mark is not supported."); 
/*   0*/       }
/* 207*/    byte[] signature = new byte[12];
/* 208*/    in.mark(signature.length);
/*   0*/    try {
/* 210*/      int signatureLength = in.read(signature);
/* 211*/      in.reset();
/* 212*/      if (ZipArchiveInputStream.matches(signature, signatureLength)) {
/* 213*/          return new ZipArchiveInputStream(in); 
/*   0*/         }
/* 214*/      if (JarArchiveInputStream.matches(signature, signatureLength)) {
/* 215*/          return new JarArchiveInputStream(in); 
/*   0*/         }
/* 216*/      if (ArArchiveInputStream.matches(signature, signatureLength)) {
/* 217*/          return new ArArchiveInputStream(in); 
/*   0*/         }
/* 218*/      if (CpioArchiveInputStream.matches(signature, signatureLength)) {
/* 219*/          return new CpioArchiveInputStream(in); 
/*   0*/         }
/* 223*/      byte[] dumpsig = new byte[32];
/* 224*/      in.mark(dumpsig.length);
/* 225*/      signatureLength = in.read(dumpsig);
/* 226*/      in.reset();
/* 227*/      if (DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
/* 228*/          return new DumpArchiveInputStream(in); 
/*   0*/         }
/* 232*/      byte[] tarheader = new byte[512];
/* 233*/      in.mark(tarheader.length);
/* 234*/      signatureLength = in.read(tarheader);
/* 235*/      in.reset();
/* 236*/      if (TarArchiveInputStream.matches(tarheader, signatureLength)) {
/* 237*/          return new TarArchiveInputStream(in); 
/*   0*/         }
/* 240*/      if (signatureLength >= 512) {
/*   0*/          try {
/* 242*/            TarArchiveInputStream tais = new TarArchiveInputStream(new ByteArrayInputStream(tarheader));
/* 244*/            new TarArchiveEntry((String)null, (byte)0);
/* 245*/            return new TarArchiveInputStream(in);
/* 246*/          } catch (Exception exception) {} 
/*   0*/         }
/* 253*/    } catch (IOException e) {
/* 254*/      throw new ArchiveException("Could not use reset and mark operations.", e);
/*   0*/    } 
/* 257*/    throw new ArchiveException("No Archiver found for the stream signature");
/*   0*/  }
/*   0*/}
