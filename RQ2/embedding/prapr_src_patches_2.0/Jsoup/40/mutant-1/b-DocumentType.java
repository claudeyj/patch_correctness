/*   0*/package org.jsoup.nodes;
/*   0*/
/*   0*/import org.jsoup.helper.StringUtil;
/*   0*/
/*   0*/public class DocumentType extends Node {
/*   0*/  public DocumentType(String name, String publicId, String systemId, String baseUri) {
/*  20*/    super(baseUri);
/*  23*/    attr("name", name);
/*  24*/    attr("publicId", publicId);
/*  25*/    attr("systemId", systemId);
/*   0*/  }
/*   0*/  
/*   0*/  public String nodeName() {
/*  30*/    return "#doctype";
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
/*  35*/    accum.append("<!DOCTYPE");
/*  36*/    if (!StringUtil.isBlank(attr("name"))) {
/*  37*/        accum.append(" ").append(attr("name")); 
/*   0*/       }
/*  38*/    if (!StringUtil.isBlank(attr("publicId"))) {
/*  39*/        accum.append(" PUBLIC \"").append(attr("publicId")).append('"'); 
/*   0*/       }
/*  40*/    if (!StringUtil.isBlank(attr("systemId"))) {
/*  41*/        accum.append(" \"").append(attr("systemId")).append('"'); 
/*   0*/       }
/*  42*/    accum.append('>');
/*   0*/  }
/*   0*/  
/*   0*/  void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {}
/*   0*/}
