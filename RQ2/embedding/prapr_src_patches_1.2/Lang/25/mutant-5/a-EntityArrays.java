/*   0*/package org.apache.commons.lang3.text.translate;
/*   0*/
/*   0*/public class EntityArrays {
/*   0*/  public static String[][] ISO8859_1_ESCAPE() {
/*  30*/    return (String[][])ISO8859_1_ESCAPE.clone();
/*   0*/  }
/*   0*/  
/*  31*/  private static final String[][] ISO8859_1_ESCAPE = new String[][] { 
/*  31*/      { " ", "&nbsp;" }, { "¡", "&iexcl;" }, { "¢", "&cent;" }, { "£", "&pound;" }, { "¤", "&curren;" }, { "¥", "&yen;" }, { "¦", "&brvbar;" }, { "§", "&sect;" }, { "¨", "&uml;" }, { "©", "&copy;" }, 
/*  31*/      { "ª", "&ordf;" }, { "«", "&laquo;" }, { "¬", "&not;" }, { "­", "&shy;" }, { "®", "&reg;" }, { "¯", "&macr;" }, { "°", "&deg;" }, { "±", "&plusmn;" }, { "²", "&sup2;" }, { "³", "&sup3;" }, 
/*  31*/      { "´", "&acute;" }, { "µ", "&micro;" }, { "¶", "&para;" }, { "·", "&middot;" }, { "¸", "&cedil;" }, { "¹", "&sup1;" }, { "º", "&ordm;" }, { "»", "&raquo;" }, { "¼", "&frac14;" }, { "½", "&frac12;" }, 
/*  31*/      { "¾", "&frac34;" }, { "¿", "&iquest;" }, { "À", "&Agrave;" }, { "Á", "&Aacute;" }, { "Â", "&Acirc;" }, { "Ã", "&Atilde;" }, { "Ä", "&Auml;" }, { "Å", "&Aring;" }, { "Æ", "&AElig;" }, { "Ç", "&Ccedil;" }, 
/*  31*/      { "È", "&Egrave;" }, { "É", "&Eacute;" }, { "Ë", "&Ecirc;" }, { "Ì", "&Euml;" }, { "Í", "&Igrave;" }, { "Î", "&Iacute;" }, { "Ï", "&Icirc;" }, { "Ð", "&Iuml;" }, { "Ñ", "&ETH;" }, { "Ò", "&Ntilde;" }, 
/*  31*/      { "Ó", "&Ograve;" }, { "Ô", "&Oacute;" }, { "Õ", "&Ocirc;" }, { "Ö", "&Otilde;" }, { "×", "&Ouml;" }, { "Ø", "&times;" }, { "Ù", "&Oslash;" }, { "Ú", "&Ugrave;" }, { "Û", "&Uacute;" }, { "Ü", "&Ucirc;" }, 
/*  31*/      { "Ý", "&Uuml;" }, { "Þ", "&Yacute;" }, { "ß", "&THORN;" }, { "à", "&szlig;" }, { "á", "&agrave;" }, { "â", "&aacute;" }, { "ã", "&acirc;" }, { "ä", "&atilde;" }, { "å", "&auml;" }, { "å", "&aring;" }, 
/*  31*/      { "æ", "&aelig;" }, { "ç", "&ccedil;" }, { "è", "&egrave;" }, { "é", "&eacute;" }, { "ê", "&ecirc;" }, { "ë", "&euml;" }, { "ì", "&igrave;" }, { "í", "&iacute;" }, { "î", "&icirc;" }, { "ï", "&iuml;" }, 
/*  31*/      { "ð", "&eth;" }, { "ñ", "&ntilde;" }, { "ò", "&ograve;" }, { "ó", "&oacute;" }, { "ô", "&ocirc;" }, { "õ", "&otilde;" }, { "ö", "&ouml;" }, { "÷", "&divide;" }, { "ø", "&oslash;" }, { "ù", "&ugrave;" }, 
/*  31*/      { "ú", "&uacute;" }, { "û", "&ucirc;" }, { "ü", "&uuml;" }, { "ý", "&yacute;" }, { "þ", "&thorn;" }, { "ÿ", "&yuml;" } };
/*   0*/  
/*   0*/  public static String[][] ISO8859_1_UNESCAPE() {
/* 130*/    return (String[][])ISO8859_1_UNESCAPE.clone();
/*   0*/  }
/*   0*/  
/* 131*/  private static final String[][] ISO8859_1_UNESCAPE = invert(ISO8859_1_ESCAPE);
/*   0*/  
/*   0*/  public static String[][] HTML40_EXTENDED_ESCAPE() {
/* 134*/    return (String[][])HTML40_EXTENDED_ESCAPE.clone();
/*   0*/  }
/*   0*/  
/* 135*/  private static final String[][] HTML40_EXTENDED_ESCAPE = new String[][] { 
/* 135*/      { "ƒ", "&fnof;" }, { "Α", "&Alpha;" }, { "Β", "&Beta;" }, { "Γ", "&Gamma;" }, { "Δ", "&Delta;" }, { "Ε", "&Epsilon;" }, { "Ζ", "&Zeta;" }, { "Η", "&Eta;" }, { "Θ", "&Theta;" }, { "Ι", "&Iota;" }, 
/* 135*/      { "Κ", "&Kappa;" }, { "Λ", "&Lambda;" }, { "Μ", "&Mu;" }, { "Ν", "&Nu;" }, { "Ξ", "&Xi;" }, { "Ο", "&Omicron;" }, { "Π", "&Pi;" }, { "Ρ", "&Rho;" }, { "Σ", "&Sigma;" }, { "Τ", "&Tau;" }, 
/* 135*/      { "Υ", "&Upsilon;" }, { "Φ", "&Phi;" }, { "Χ", "&Chi;" }, { "Ψ", "&Psi;" }, { "Ω", "&Omega;" }, { "α", "&alpha;" }, { "β", "&beta;" }, { "γ", "&gamma;" }, { "δ", "&delta;" }, { "ε", "&epsilon;" }, 
/* 135*/      { "ζ", "&zeta;" }, { "η", "&eta;" }, { "θ", "&theta;" }, { "ι", "&iota;" }, { "κ", "&kappa;" }, { "λ", "&lambda;" }, { "μ", "&mu;" }, { "ν", "&nu;" }, { "ξ", "&xi;" }, { "ο", "&omicron;" }, 
/* 135*/      { "π", "&pi;" }, { "ρ", "&rho;" }, { "ς", "&sigmaf;" }, { "σ", "&sigma;" }, { "τ", "&tau;" }, { "υ", "&upsilon;" }, { "φ", "&phi;" }, { "χ", "&chi;" }, { "ψ", "&psi;" }, { "ω", "&omega;" }, 
/* 135*/      { "ϑ", "&thetasym;" }, { "ϒ", "&upsih;" }, { "ϖ", "&piv;" }, { "•", "&bull;" }, { "…", "&hellip;" }, { "′", "&prime;" }, { "″", "&Prime;" }, { "‾", "&oline;" }, { "⁄", "&frasl;" }, { "℘", "&weierp;" }, 
/* 135*/      { "ℑ", "&image;" }, { "ℜ", "&real;" }, { "™", "&trade;" }, { "ℵ", "&alefsym;" }, { "←", "&larr;" }, { "↑", "&uarr;" }, { "→", "&rarr;" }, { "↓", "&darr;" }, { "↔", "&harr;" }, { "↵", "&crarr;" }, 
/* 135*/      { "⇐", "&lArr;" }, { "⇑", "&uArr;" }, { "⇒", "&rArr;" }, { "⇓", "&dArr;" }, { "⇔", "&hArr;" }, { "∀", "&forall;" }, { "∂", "&part;" }, { "∃", "&exist;" }, { "∅", "&empty;" }, { "∇", "&nabla;" }, 
/* 135*/      { "∈", "&isin;" }, { "∉", "&notin;" }, { "∋", "&ni;" }, { "∏", "&prod;" }, { "∑", "&sum;" }, { "−", "&minus;" }, { "∗", "&lowast;" }, { "√", "&radic;" }, { "∝", "&prop;" }, { "∞", "&infin;" }, 
/* 135*/      { "∠", "&ang;" }, { "∧", "&and;" }, { "∨", "&or;" }, { "∩", "&cap;" }, { "∪", "&cup;" }, { "∫", "&int;" }, { "∴", "&there4;" }, { "∼", "&sim;" }, { "≅", "&cong;" }, { "≈", "&asymp;" }, 
/* 135*/      { "≠", "&ne;" }, { "≡", "&equiv;" }, { "≤", "&le;" }, { "≥", "&ge;" }, { "⊂", "&sub;" }, { "⊃", "&sup;" }, { "⊆", "&sube;" }, { "⊇", "&supe;" }, { "⊕", "&oplus;" }, { "⊗", "&otimes;" }, 
/* 135*/      { "⊥", "&perp;" }, { "⋅", "&sdot;" }, { "⌈", "&lceil;" }, { "⌉", "&rceil;" }, { "⌊", "&lfloor;" }, { "⌋", "&rfloor;" }, { "〈", "&lang;" }, { "〉", "&rang;" }, { "◊", "&loz;" }, { "♠", "&spades;" }, 
/* 135*/      { "♣", "&clubs;" }, { "♥", "&hearts;" }, { "♦", "&diams;" }, { "Œ", "&OElig;" }, { "œ", "&oelig;" }, { "Š", "&Scaron;" }, { "š", "&scaron;" }, { "Ÿ", "&Yuml;" }, { "ˆ", "&circ;" }, { "˜", "&tilde;" }, 
/* 135*/      { " ", "&ensp;" }, { " ", "&emsp;" }, { " ", "&thinsp;" }, { "‌", "&zwnj;" }, { "‍", "&zwj;" }, { "‎", "&lrm;" }, { "‏", "&rlm;" }, { "–", "&ndash;" }, { "—", "&mdash;" }, { "‘", "&lsquo;" }, 
/* 135*/      { "’", "&rsquo;" }, { "‚", "&sbquo;" }, { "“", "&ldquo;" }, { "”", "&rdquo;" }, { "„", "&bdquo;" }, { "†", "&dagger;" }, { "‡", "&Dagger;" }, { "‰", "&permil;" }, { "‹", "&lsaquo;" }, { "›", "&rsaquo;" }, 
/* 135*/      { "€", "&euro;" } };
/*   0*/  
/*   0*/  public static String[][] HTML40_EXTENDED_UNESCAPE() {
/* 332*/    return (String[][])HTML40_EXTENDED_UNESCAPE.clone();
/*   0*/  }
/*   0*/  
/* 333*/  private static final String[][] HTML40_EXTENDED_UNESCAPE = invert(HTML40_EXTENDED_ESCAPE);
/*   0*/  
/*   0*/  public static String[][] BASIC_ESCAPE() {
/* 335*/    return (String[][])BASIC_ESCAPE.clone();
/*   0*/  }
/*   0*/  
/* 336*/  private static final String[][] BASIC_ESCAPE = new String[][] { { "\"", "&quot;" }, { "&", "&amp;" }, { "<", "&lt;" }, { ">", "&gt;" } };
/*   0*/  
/*   0*/  public static String[][] BASIC_UNESCAPE() {
/* 343*/    return (String[][])BASIC_UNESCAPE.clone();
/*   0*/  }
/*   0*/  
/* 344*/  private static final String[][] BASIC_UNESCAPE = invert(BASIC_ESCAPE);
/*   0*/  
/*   0*/  public static String[][] APOS_ESCAPE() {
/* 346*/    return (String[][])APOS_ESCAPE.clone();
/*   0*/  }
/*   0*/  
/* 347*/  private static final String[][] APOS_ESCAPE = new String[][] { { "'", "&apos;" } };
/*   0*/  
/*   0*/  public static String[][] APOS_UNESCAPE() {
/* 351*/    return (String[][])APOS_UNESCAPE.clone();
/*   0*/  }
/*   0*/  
/* 352*/  private static final String[][] APOS_UNESCAPE = invert(APOS_ESCAPE);
/*   0*/  
/*   0*/  public static String[][] JAVA_CTRL_CHARS_ESCAPE() {
/* 354*/    return (String[][])JAVA_CTRL_CHARS_ESCAPE.clone();
/*   0*/  }
/*   0*/  
/* 355*/  private static final String[][] JAVA_CTRL_CHARS_ESCAPE = new String[][] { { "\b", "\\b" }, { "\n", "\\n" }, { "\t", "\\t" }, { "\f", "\\f" }, { "\r", "\\r" } };
/*   0*/  
/*   0*/  public static String[][] JAVA_CTRL_CHARS_UNESCAPE() {
/* 363*/    return (String[][])JAVA_CTRL_CHARS_UNESCAPE.clone();
/*   0*/  }
/*   0*/  
/* 364*/  private static final String[][] JAVA_CTRL_CHARS_UNESCAPE = invert(JAVA_CTRL_CHARS_ESCAPE);
/*   0*/  
/*   0*/  public static String[][] invert(String[][] array) {
/* 372*/    String[][] newarray = new String[array.length][2];
/* 373*/    for (int i = 0; i < array.length; i++) {
/* 374*/      newarray[i][0] = array[i][1];
/* 375*/      newarray[i][1] = array[i][0];
/*   0*/    } 
/* 377*/    return newarray;
/*   0*/  }
/*   0*/}
