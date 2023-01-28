/*   0*/package org.apache.commons.jxpath.ri.parser;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.PrintStream;
/*   0*/
/*   0*/public class XPathParserTokenManager implements XPathParserConstants {
/*   8*/  public PrintStream debugStream = System.out;
/*   0*/  
/*   0*/  public void setDebugStream(PrintStream ds) {
/*   9*/    this.debugStream = ds;
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1) {
/*  12*/    switch (pos) {
/*   0*/      case 0:
/*  15*/        if ((active1 & 0x180000L) != 0L) {
/*  16*/            return 10; 
/*   0*/           }
/*  17*/        if ((active0 & 0xFFFFFFFFF8000000L) != 0L || (active1 & 0x7FFFL) != 0L) {
/*  19*/          this.jjmatchedKind = 79;
/*  20*/          return 12;
/*   0*/        } 
/*  22*/        return -1;
/*   0*/      case 1:
/*  24*/        if ((active0 & 0xFFF7FFFFF0000000L) != 0L || (active1 & 0x7FFFL) != 0L) {
/*  26*/          this.jjmatchedKind = 79;
/*  27*/          this.jjmatchedPos = 1;
/*  28*/          return 12;
/*   0*/        } 
/*  30*/        if ((active0 & 0x8000008000000L) != 0L) {
/*  31*/            return 12; 
/*   0*/           }
/*  32*/        return -1;
/*   0*/      case 2:
/*  34*/        if ((active0 & 0x10000070000000L) != 0L || (active1 & 0x410L) != 0L) {
/*  35*/            return 12; 
/*   0*/           }
/*  36*/        if ((active0 & 0xFFE7FFFF80000000L) != 0L || (active1 & 0x7BEFL) != 0L) {
/*  38*/          this.jjmatchedKind = 79;
/*  39*/          this.jjmatchedPos = 2;
/*  40*/          return 12;
/*   0*/        } 
/*  42*/        return -1;
/*   0*/      case 3:
/*  44*/        if ((active0 & 0xC1010180000000L) != 0L || (active1 & 0x1A0L) != 0L) {
/*  45*/            return 12; 
/*   0*/           }
/*  46*/        if ((active0 & 0xFF26FEFE00000000L) != 0L || (active1 & 0x7A4FL) != 0L) {
/*  48*/          if (this.jjmatchedPos != 3) {
/*  50*/            this.jjmatchedKind = 79;
/*  51*/            this.jjmatchedPos = 3;
/*   0*/          } 
/*  53*/          return 12;
/*   0*/        } 
/*  55*/        return -1;
/*   0*/      case 4:
/*  57*/        if ((active0 & 0xFF62FFF600000000L) != 0L || (active1 & 0x520FL) != 0L) {
/*  59*/          this.jjmatchedKind = 79;
/*  60*/          this.jjmatchedPos = 4;
/*  61*/          return 12;
/*   0*/        } 
/*  63*/        if ((active0 & 0x4000000000000L) != 0L || (active1 & 0x2840L) != 0L) {
/*  64*/            return 12; 
/*   0*/           }
/*  65*/        if ((active0 & 0x800000000L) != 0L) {
/*  67*/          if (this.jjmatchedPos < 3) {
/*  69*/            this.jjmatchedKind = 79;
/*  70*/            this.jjmatchedPos = 3;
/*   0*/          } 
/*  72*/          return -1;
/*   0*/        } 
/*  74*/        return -1;
/*   0*/      case 5:
/*  76*/        if ((active0 & 0x300000000000000L) != 0L || (active1 & 0x201L) != 0L) {
/*  77*/            return 12; 
/*   0*/           }
/*  78*/        if ((active0 & 0xFC62FFE600000000L) != 0L || (active1 & 0x500EL) != 0L) {
/*  80*/          if (this.jjmatchedPos != 5) {
/*  82*/            this.jjmatchedKind = 79;
/*  83*/            this.jjmatchedPos = 5;
/*   0*/          } 
/*  85*/          return 12;
/*   0*/        } 
/*  87*/        if ((active0 & 0x1000000000L) != 0L) {
/*  89*/          if (this.jjmatchedPos < 4) {
/*  91*/            this.jjmatchedKind = 79;
/*  92*/            this.jjmatchedPos = 4;
/*   0*/          } 
/*  94*/          return -1;
/*   0*/        } 
/*  96*/        if ((active0 & 0x800000000L) != 0L) {
/*  98*/          if (this.jjmatchedPos < 3) {
/* 100*/            this.jjmatchedKind = 79;
/* 101*/            this.jjmatchedPos = 3;
/*   0*/          } 
/* 103*/          return -1;
/*   0*/        } 
/* 105*/        return -1;
/*   0*/      case 6:
/* 107*/        if ((active0 & 0x200000000L) != 0L || (active1 & 0x1008L) != 0L) {
/* 108*/            return 12; 
/*   0*/           }
/* 109*/        if ((active0 & 0xFC62FFC400000000L) != 0L || (active1 & 0x4007L) != 0L) {
/* 111*/          this.jjmatchedKind = 79;
/* 112*/          this.jjmatchedPos = 6;
/* 113*/          return 12;
/*   0*/        } 
/* 115*/        if ((active0 & 0x2000000000L) != 0L) {
/* 117*/          if (this.jjmatchedPos < 5) {
/* 119*/            this.jjmatchedKind = 79;
/* 120*/            this.jjmatchedPos = 5;
/*   0*/          } 
/* 122*/          return -1;
/*   0*/        } 
/* 124*/        if ((active0 & 0x1000000000L) != 0L) {
/* 126*/          if (this.jjmatchedPos < 4) {
/* 128*/            this.jjmatchedKind = 79;
/* 129*/            this.jjmatchedPos = 4;
/*   0*/          } 
/* 131*/          return -1;
/*   0*/        } 
/* 133*/        return -1;
/*   0*/      case 7:
/* 135*/        if ((active0 & 0x1002000000000000L) != 0L) {
/* 136*/            return 12; 
/*   0*/           }
/* 137*/        if ((active0 & 0x2000000000L) != 0L) {
/* 139*/          if (this.jjmatchedPos < 5) {
/* 141*/            this.jjmatchedKind = 79;
/* 142*/            this.jjmatchedPos = 5;
/*   0*/          } 
/* 144*/          return -1;
/*   0*/        } 
/* 146*/        if ((active0 & 0xEC60FFC400000000L) != 0L || (active1 & 0x4007L) != 0L) {
/* 148*/          this.jjmatchedKind = 79;
/* 149*/          this.jjmatchedPos = 7;
/* 150*/          return 12;
/*   0*/        } 
/* 152*/        return -1;
/*   0*/      case 8:
/* 154*/        if ((active0 & 0xE800000000000000L) != 0L || (active1 & 0x4L) != 0L) {
/* 155*/            return 12; 
/*   0*/           }
/* 156*/        if ((active0 & 0x460FF8400000000L) != 0L || (active1 & 0x4003L) != 0L) {
/* 158*/          if (this.jjmatchedPos != 8) {
/* 160*/            this.jjmatchedKind = 79;
/* 161*/            this.jjmatchedPos = 8;
/*   0*/          } 
/* 163*/          return 12;
/*   0*/        } 
/* 165*/        if ((active0 & 0x4000000000L) != 0L) {
/* 167*/          if (this.jjmatchedPos < 7) {
/* 169*/            this.jjmatchedKind = 79;
/* 170*/            this.jjmatchedPos = 7;
/*   0*/          } 
/* 172*/          return -1;
/*   0*/        } 
/* 174*/        return -1;
/*   0*/      case 9:
/* 176*/        if ((active0 & 0x20000000000000L) != 0L) {
/* 177*/            return 12; 
/*   0*/           }
/* 178*/        if ((active0 & 0x78000000000L) != 0L) {
/* 180*/          if (this.jjmatchedPos < 8) {
/* 182*/            this.jjmatchedKind = 79;
/* 183*/            this.jjmatchedPos = 8;
/*   0*/          } 
/* 185*/          return -1;
/*   0*/        } 
/* 187*/        if ((active0 & 0x4000000000L) != 0L) {
/* 189*/          if (this.jjmatchedPos < 7) {
/* 191*/            this.jjmatchedKind = 79;
/* 192*/            this.jjmatchedPos = 7;
/*   0*/          } 
/* 194*/          return -1;
/*   0*/        } 
/* 196*/        if ((active0 & 0x6440F80400000000L) != 0L || (active1 & 0x4003L) != 0L) {
/* 198*/          this.jjmatchedKind = 79;
/* 199*/          this.jjmatchedPos = 9;
/* 200*/          return 12;
/*   0*/        } 
/* 202*/        return -1;
/*   0*/      case 10:
/* 204*/        if ((active0 & 0x400000000000000L) != 0L) {
/* 205*/            return 12; 
/*   0*/           }
/* 206*/        if ((active0 & 0x6040F00400000000L) != 0L || (active1 & 0x4003L) != 0L) {
/* 208*/          this.jjmatchedKind = 79;
/* 209*/          this.jjmatchedPos = 10;
/* 210*/          return 12;
/*   0*/        } 
/* 212*/        if ((active0 & 0x80000000000L) != 0L) {
/* 214*/          if (this.jjmatchedPos < 9) {
/* 216*/            this.jjmatchedKind = 79;
/* 217*/            this.jjmatchedPos = 9;
/*   0*/          } 
/* 219*/          return -1;
/*   0*/        } 
/* 221*/        if ((active0 & 0x78000000000L) != 0L) {
/* 223*/          if (this.jjmatchedPos < 8) {
/* 225*/            this.jjmatchedKind = 79;
/* 226*/            this.jjmatchedPos = 8;
/*   0*/          } 
/* 228*/          return -1;
/*   0*/        } 
/* 230*/        return -1;
/*   0*/      case 11:
/* 232*/        if ((active0 & 0x80000000000L) != 0L) {
/* 234*/          if (this.jjmatchedPos < 9) {
/* 236*/            this.jjmatchedKind = 79;
/* 237*/            this.jjmatchedPos = 9;
/*   0*/          } 
/* 239*/          return -1;
/*   0*/        } 
/* 241*/        if ((active0 & 0x6040F00400000000L) != 0L || (active1 & 0x4003L) != 0L) {
/* 243*/          this.jjmatchedKind = 79;
/* 244*/          this.jjmatchedPos = 11;
/* 245*/          return 12;
/*   0*/        } 
/* 247*/        return -1;
/*   0*/      case 12:
/* 249*/        if ((active0 & 0x6000F00400000000L) != 0L || (active1 & 0x2L) != 0L) {
/* 251*/          this.jjmatchedKind = 79;
/* 252*/          this.jjmatchedPos = 12;
/* 253*/          return 12;
/*   0*/        } 
/* 255*/        if ((active0 & 0x40000000000000L) != 0L || (active1 & 0x4001L) != 0L) {
/* 256*/            return 12; 
/*   0*/           }
/* 257*/        return -1;
/*   0*/      case 13:
/* 259*/        if ((active0 & 0x6000F00400000000L) != 0L || (active1 & 0x2L) != 0L) {
/* 261*/          this.jjmatchedKind = 79;
/* 262*/          this.jjmatchedPos = 13;
/* 263*/          return 12;
/*   0*/        } 
/* 265*/        return -1;
/*   0*/      case 14:
/* 267*/        if ((active0 & 0x4000000000000000L) != 0L || (active1 & 0x2L) != 0L) {
/* 268*/            return 12; 
/*   0*/           }
/* 269*/        if ((active0 & 0x2000F00400000000L) != 0L) {
/* 271*/          this.jjmatchedKind = 79;
/* 272*/          this.jjmatchedPos = 14;
/* 273*/          return 12;
/*   0*/        } 
/* 275*/        return -1;
/*   0*/      case 15:
/* 277*/        if ((active0 & 0x2000000000000000L) != 0L) {
/* 278*/            return 12; 
/*   0*/           }
/* 279*/        if ((active0 & 0xF00400000000L) != 0L) {
/* 281*/          this.jjmatchedKind = 79;
/* 282*/          this.jjmatchedPos = 15;
/* 283*/          return 12;
/*   0*/        } 
/* 285*/        return -1;
/*   0*/      case 16:
/* 287*/        if ((active0 & 0xE00400000000L) != 0L) {
/* 289*/          this.jjmatchedKind = 79;
/* 290*/          this.jjmatchedPos = 16;
/* 291*/          return 12;
/*   0*/        } 
/* 293*/        if ((active0 & 0x100000000000L) != 0L) {
/* 295*/          if (this.jjmatchedPos < 15) {
/* 297*/            this.jjmatchedKind = 79;
/* 298*/            this.jjmatchedPos = 15;
/*   0*/          } 
/* 300*/          return -1;
/*   0*/        } 
/* 302*/        return -1;
/*   0*/      case 17:
/* 304*/        if ((active0 & 0x600000000000L) != 0L) {
/* 306*/          if (this.jjmatchedPos < 16) {
/* 308*/            this.jjmatchedKind = 79;
/* 309*/            this.jjmatchedPos = 16;
/*   0*/          } 
/* 311*/          return -1;
/*   0*/        } 
/* 313*/        if ((active0 & 0x100000000000L) != 0L) {
/* 315*/          if (this.jjmatchedPos < 15) {
/* 317*/            this.jjmatchedKind = 79;
/* 318*/            this.jjmatchedPos = 15;
/*   0*/          } 
/* 320*/          return -1;
/*   0*/        } 
/* 322*/        if ((active0 & 0x800400000000L) != 0L) {
/* 324*/          this.jjmatchedKind = 79;
/* 325*/          this.jjmatchedPos = 17;
/* 326*/          return 12;
/*   0*/        } 
/* 328*/        return -1;
/*   0*/      case 18:
/* 330*/        if ((active0 & 0x800000000000L) != 0L) {
/* 332*/          if (this.jjmatchedPos < 17) {
/* 334*/            this.jjmatchedKind = 79;
/* 335*/            this.jjmatchedPos = 17;
/*   0*/          } 
/* 337*/          return -1;
/*   0*/        } 
/* 339*/        if ((active0 & 0x600000000000L) != 0L) {
/* 341*/          if (this.jjmatchedPos < 16) {
/* 343*/            this.jjmatchedKind = 79;
/* 344*/            this.jjmatchedPos = 16;
/*   0*/          } 
/* 346*/          return -1;
/*   0*/        } 
/* 348*/        if ((active0 & 0x400000000L) != 0L) {
/* 350*/          this.jjmatchedKind = 79;
/* 351*/          this.jjmatchedPos = 18;
/* 352*/          return 12;
/*   0*/        } 
/* 354*/        return -1;
/*   0*/      case 19:
/* 356*/        if ((active0 & 0x400000000L) != 0L) {
/* 358*/          this.jjmatchedKind = 79;
/* 359*/          this.jjmatchedPos = 19;
/* 360*/          return 12;
/*   0*/        } 
/* 362*/        if ((active0 & 0x800000000000L) != 0L) {
/* 364*/          if (this.jjmatchedPos < 17) {
/* 366*/            this.jjmatchedKind = 79;
/* 367*/            this.jjmatchedPos = 17;
/*   0*/          } 
/* 369*/          return -1;
/*   0*/        } 
/* 371*/        return -1;
/*   0*/      case 20:
/* 373*/        if ((active0 & 0x400000000L) != 0L) {
/* 375*/          this.jjmatchedKind = 79;
/* 376*/          this.jjmatchedPos = 20;
/* 377*/          return 12;
/*   0*/        } 
/* 379*/        return -1;
/*   0*/    } 
/* 381*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjStartNfa_0(int pos, long active0, long active1) {
/* 386*/    return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjStopAtPos(int pos, int kind) {
/* 390*/    this.jjmatchedKind = kind;
/* 391*/    this.jjmatchedPos = pos;
/* 392*/    return pos + 1;
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjStartNfaWithStates_0(int pos, int kind, int state) {
/* 396*/    this.jjmatchedKind = kind;
/* 397*/    this.jjmatchedPos = pos;
/*   0*/    try {
/* 398*/      this.curChar = this.input_stream.readChar();
/*   0*/    } catch (IOException e) {
/* 399*/      return pos + 1;
/*   0*/    } 
/* 400*/    return jjMoveNfa_0(state, pos + 1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa0_0() {
/* 404*/    switch (this.curChar) {
/*   0*/      case '!':
/* 407*/        return jjMoveStringLiteralDfa1_0(4096L, 0L);
/*   0*/      case '$':
/* 409*/        return jjStopAtPos(0, 17);
/*   0*/      case '(':
/* 411*/        return jjStopAtPos(0, 81);
/*   0*/      case ')':
/* 413*/        return jjStopAtPos(0, 82);
/*   0*/      case '*':
/* 415*/        return jjStopAtPos(0, 89);
/*   0*/      case '+':
/* 417*/        return jjStopAtPos(0, 9);
/*   0*/      case ',':
/* 419*/        return jjStopAtPos(0, 88);
/*   0*/      case '-':
/* 421*/        return jjStopAtPos(0, 10);
/*   0*/      case '.':
/* 423*/        this.jjmatchedKind = 83;
/* 424*/        return jjMoveStringLiteralDfa1_0(0L, 1048576L);
/*   0*/      case '/':
/* 426*/        this.jjmatchedKind = 6;
/* 427*/        return jjMoveStringLiteralDfa1_0(128L, 0L);
/*   0*/      case ':':
/* 429*/        return jjStopAtPos(0, 80);
/*   0*/      case '<':
/* 431*/        this.jjmatchedKind = 13;
/* 432*/        return jjMoveStringLiteralDfa1_0(16384L, 0L);
/*   0*/      case '=':
/* 434*/        return jjStopAtPos(0, 11);
/*   0*/      case '>':
/* 436*/        this.jjmatchedKind = 15;
/* 437*/        return jjMoveStringLiteralDfa1_0(65536L, 0L);
/*   0*/      case '@':
/* 439*/        return jjStopAtPos(0, 87);
/*   0*/      case '[':
/* 441*/        return jjStopAtPos(0, 85);
/*   0*/      case ']':
/* 443*/        return jjStopAtPos(0, 86);
/*   0*/      case 'a':
/* 445*/        return jjMoveStringLiteralDfa1_0(18417088200704L, 0L);
/*   0*/      case 'b':
/* 447*/        return jjMoveStringLiteralDfa1_0(0L, 8L);
/*   0*/      case 'c':
/* 449*/        return jjMoveStringLiteralDfa1_0(1298162669898956800L, 4096L);
/*   0*/      case 'd':
/* 451*/        return jjMoveStringLiteralDfa1_0(149534655119360L, 0L);
/*   0*/      case 'e':
/* 453*/        return jjMoveStringLiteralDfa1_0(576460752303423488L, 0L);
/*   0*/      case 'f':
/* 455*/        return jjMoveStringLiteralDfa1_0(39582418599936L, 18496L);
/*   0*/      case 'i':
/* 457*/        return jjMoveStringLiteralDfa1_0(2251799813685248L, 0L);
/*   0*/      case 'k':
/* 459*/        return jjMoveStringLiteralDfa1_0(4503599627370496L, 0L);
/*   0*/      case 'l':
/* 461*/        return jjMoveStringLiteralDfa1_0(9288674231451648L, 256L);
/*   0*/      case 'm':
/* 463*/        return jjMoveStringLiteralDfa1_0(536870912L, 0L);
/*   0*/      case 'n':
/* 465*/        return jjMoveStringLiteralDfa1_0(54044297187557376L, 658L);
/*   0*/      case 'o':
/* 467*/        return jjMoveStringLiteralDfa1_0(134217728L, 0L);
/*   0*/      case 'p':
/* 469*/        return jjMoveStringLiteralDfa1_0(635672339677184L, 0L);
/*   0*/      case 'r':
/* 471*/        return jjMoveStringLiteralDfa1_0(0L, 8192L);
/*   0*/      case 's':
/* 473*/        return jjMoveStringLiteralDfa1_0(-1945555004664315904L, 1025L);
/*   0*/      case 't':
/* 475*/        return jjMoveStringLiteralDfa1_0(4294967296L, 36L);
/*   0*/      case '|':
/* 477*/        return jjStopAtPos(0, 8);
/*   0*/    } 
/* 479*/    return jjMoveNfa_0(0, 0);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa1_0(long active0, long active1) {
/*   0*/    try {
/* 484*/      this.curChar = this.input_stream.readChar();
/* 485*/    } catch (IOException e) {
/* 486*/      jjStopStringLiteralDfa_0(0, active0, active1);
/* 487*/      return 1;
/*   0*/    } 
/* 489*/    switch (this.curChar) {
/*   0*/      case '.':
/* 492*/        if ((active1 & 0x100000L) != 0L) {
/* 493*/            return jjStopAtPos(1, 84); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case '/':
/* 496*/        if ((active0 & 0x80L) != 0L) {
/* 497*/            return jjStopAtPos(1, 7); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case '=':
/* 500*/        if ((active0 & 0x1000L) != 0L) {
/* 501*/            return jjStopAtPos(1, 12); 
/*   0*/           }
/* 502*/        if ((active0 & 0x4000L) != 0L) {
/* 503*/            return jjStopAtPos(1, 14); 
/*   0*/           }
/* 504*/        if ((active0 & 0x10000L) != 0L) {
/* 505*/            return jjStopAtPos(1, 16); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'a':
/* 508*/        return jjMoveStringLiteralDfa2_0(active0, 54325907455737856L, active1, 320L);
/*   0*/      case 'd':
/* 510*/        if ((active0 & 0x8000000000000L) != 0L) {
/* 511*/            return jjStartNfaWithStates_0(1, 51, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'e':
/* 514*/        return jjMoveStringLiteralDfa2_0(active0, 4653171863453696L, active1, 4096L);
/*   0*/      case 'h':
/* 516*/        return jjMoveStringLiteralDfa2_0(active0, 68719476736L, active1, 0L);
/*   0*/      case 'i':
/* 518*/        return jjMoveStringLiteralDfa2_0(active0, 1073741824L, active1, 0L);
/*   0*/      case 'l':
/* 520*/        return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 2048L);
/*   0*/      case 'n':
/* 522*/        return jjMoveStringLiteralDfa2_0(active0, 576478619635810304L, active1, 0L);
/*   0*/      case 'o':
/* 524*/        return jjMoveStringLiteralDfa2_0(active0, 1307772335490596864L, active1, 24602L);
/*   0*/      case 'r':
/* 526*/        if ((active0 & 0x8000000L) != 0L) {
/* 527*/            return jjStartNfaWithStates_0(1, 27, 12); 
/*   0*/           }
/* 528*/        return jjMoveStringLiteralDfa2_0(active0, 72584947302400L, active1, 36L);
/*   0*/      case 't':
/* 530*/        return jjMoveStringLiteralDfa2_0(active0, 360288519945453568L, active1, 1L);
/*   0*/      case 'u':
/* 532*/        return jjMoveStringLiteralDfa2_0(active0, -2305843009213693952L, active1, 1664L);
/*   0*/    } 
/* 536*/    return jjStartNfa_0(0, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1) {
/* 540*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 541*/        return jjStartNfa_0(0, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 542*/      this.curChar = this.input_stream.readChar();
/* 543*/    } catch (IOException e) {
/* 544*/      jjStopStringLiteralDfa_0(1, active0, active1);
/* 545*/      return 2;
/*   0*/    } 
/* 547*/    switch (this.curChar) {
/*   0*/      case 'a':
/* 550*/        return jjMoveStringLiteralDfa3_0(active0, 288230376151711744L, active1, 4L);
/*   0*/      case 'b':
/* 552*/        return jjMoveStringLiteralDfa3_0(active0, -2305843009213693952L, active1, 0L);
/*   0*/      case 'c':
/* 554*/        return jjMoveStringLiteralDfa3_0(active0, 9025066318692352L, active1, 0L);
/*   0*/      case 'd':
/* 556*/        if ((active0 & 0x10000000L) != 0L) {
/* 557*/            return jjStartNfaWithStates_0(2, 28, 12); 
/*   0*/           }
/* 558*/        if ((active0 & 0x20000000L) != 0L) {
/* 559*/            return jjStartNfaWithStates_0(2, 29, 12); 
/*   0*/           }
/* 560*/        return jjMoveStringLiteralDfa3_0(active0, 576460754450907136L, active1, 0L);
/*   0*/      case 'e':
/* 562*/        return jjMoveStringLiteralDfa3_0(active0, 72567767433216L, active1, 0L);
/*   0*/      case 'i':
/* 564*/        return jjMoveStringLiteralDfa3_0(active0, 68719476736L, active1, 4096L);
/*   0*/      case 'l':
/* 566*/        return jjMoveStringLiteralDfa3_0(active0, 39616778338304L, active1, 192L);
/*   0*/      case 'm':
/* 568*/        if ((active1 & 0x400L) != 0L) {
/* 569*/            return jjStartNfaWithStates_0(2, 74, 12); 
/*   0*/           }
/* 570*/        return jjMoveStringLiteralDfa3_0(active0, 54044303630008320L, active1, 512L);
/*   0*/      case 'n':
/* 572*/        return jjMoveStringLiteralDfa3_0(active0, 1297036692682702848L, active1, 256L);
/*   0*/      case 'o':
/* 574*/        return jjMoveStringLiteralDfa3_0(active0, 17179869184L, active1, 2056L);
/*   0*/      case 'r':
/* 576*/        return jjMoveStringLiteralDfa3_0(active0, 72057731476881408L, active1, 16387L);
/*   0*/      case 's':
/* 578*/        return jjMoveStringLiteralDfa3_0(active0, 993958511509504L, active1, 0L);
/*   0*/      case 't':
/* 580*/        if ((active1 & 0x10L) != 0L) {
/* 581*/            return jjStartNfaWithStates_0(2, 68, 12); 
/*   0*/           }
/* 582*/        return jjMoveStringLiteralDfa3_0(active0, 549755813888L, active1, 0L);
/*   0*/      case 'u':
/* 584*/        return jjMoveStringLiteralDfa3_0(active0, 1125899906842624L, active1, 8224L);
/*   0*/      case 'v':
/* 586*/        if ((active0 & 0x40000000L) != 0L) {
/* 587*/            return jjStartNfaWithStates_0(2, 30, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'x':
/* 590*/        return jjMoveStringLiteralDfa3_0(active0, 4294967296L, active1, 0L);
/*   0*/      case 'y':
/* 592*/        if ((active0 & 0x10000000000000L) != 0L) {
/* 593*/            return jjStartNfaWithStates_0(2, 52, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/    } 
/* 598*/    return jjStartNfa_0(1, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa3_0(long old0, long active0, long old1, long active1) {
/* 602*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 603*/        return jjStartNfa_0(1, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 604*/      this.curChar = this.input_stream.readChar();
/* 605*/    } catch (IOException e) {
/* 606*/      jjStopStringLiteralDfa_0(2, active0, active1);
/* 607*/      return 3;
/*   0*/    } 
/* 609*/    switch (this.curChar) {
/*   0*/      case 'a':
/* 612*/        return jjMoveStringLiteralDfa4_0(active0, 9007199254740992L, active1, 0L);
/*   0*/      case 'b':
/* 614*/        return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 512L);
/*   0*/      case 'c':
/* 616*/        return jjMoveStringLiteralDfa4_0(active0, 144337306604535808L, active1, 0L);
/*   0*/      case 'e':
/* 618*/        if ((active0 & 0x80000000L) != 0L) {
/* 619*/            return jjStartNfaWithStates_0(3, 31, 12); 
/*   0*/           }
/* 620*/        if ((active0 & 0x80000000000000L) != 0L) {
/* 622*/          this.jjmatchedKind = 55;
/* 623*/          this.jjmatchedPos = 3;
/* 625*/        } else if ((active1 & 0x20L) != 0L) {
/* 626*/          return jjStartNfaWithStates_0(3, 69, 12);
/*   0*/        } 
/* 627*/        return jjMoveStringLiteralDfa4_0(active0, 18033502524014592L, active1, 0L);
/*   0*/      case 'f':
/* 629*/        return jjMoveStringLiteralDfa4_0(active0, 34359738368L, active1, 0L);
/*   0*/      case 'g':
/* 631*/        if ((active1 & 0x100L) != 0L) {
/* 632*/            return jjStartNfaWithStates_0(3, 72, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'i':
/* 635*/        return jjMoveStringLiteralDfa4_0(active0, 72620543991349248L, active1, 1L);
/*   0*/      case 'l':
/* 637*/        if ((active1 & 0x80L) != 0L) {
/* 638*/            return jjStartNfaWithStates_0(3, 71, 12); 
/*   0*/           }
/* 639*/        return jjMoveStringLiteralDfa4_0(active0, 39651138076672L, active1, 4104L);
/*   0*/      case 'm':
/* 641*/        return jjMoveStringLiteralDfa4_0(active0, 8589934592L, active1, 16386L);
/*   0*/      case 'n':
/* 643*/        return jjMoveStringLiteralDfa4_0(active0, 1125899906842624L, active1, 8196L);
/*   0*/      case 'o':
/* 645*/        return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 2048L);
/*   0*/      case 'r':
/* 647*/        return jjMoveStringLiteralDfa4_0(active0, 288230925907525632L, active1, 0L);
/*   0*/      case 's':
/* 649*/        return jjMoveStringLiteralDfa4_0(active0, -1729382256910270464L, active1, 64L);
/*   0*/      case 't':
/* 651*/        if ((active0 & 0x100000000L) != 0L) {
/* 652*/            return jjStartNfaWithStates_0(3, 32, 12); 
/*   0*/           }
/* 653*/        if ((active0 & 0x1000000000000L) != 0L) {
/* 654*/            return jjStartNfaWithStates_0(3, 48, 12); 
/*   0*/           }
/* 655*/        return jjMoveStringLiteralDfa4_0(active0, 1152921504606846976L, active1, 0L);
/*   0*/    } 
/* 659*/    return jjStartNfa_0(2, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa4_0(long old0, long active0, long old1, long active1) {
/* 663*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 664*/        return jjStartNfa_0(2, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 665*/      this.curChar = this.input_stream.readChar();
/* 666*/    } catch (IOException e) {
/* 667*/      jjStopStringLiteralDfa_0(3, active0, active1);
/* 668*/      return 4;
/*   0*/    } 
/* 670*/    switch (this.curChar) {
/*   0*/      case '-':
/* 673*/        return jjMoveStringLiteralDfa5_0(active0, 576460752303423488L, active1, 0L);
/*   0*/      case ':':
/* 675*/        return jjMoveStringLiteralDfa5_0(active0, 34359738368L, active1, 0L);
/*   0*/      case 'a':
/* 677*/        return jjMoveStringLiteralDfa5_0(active0, 1297036692682702848L, active1, 16386L);
/*   0*/      case 'd':
/* 679*/        if ((active1 & 0x2000L) != 0L) {
/* 680*/            return jjStartNfaWithStates_0(4, 77, 12); 
/*   0*/           }
/* 681*/        return jjMoveStringLiteralDfa5_0(active0, 68719476736L, active1, 0L);
/*   0*/      case 'e':
/* 683*/        if ((active1 & 0x40L) != 0L) {
/* 684*/            return jjStartNfaWithStates_0(4, 70, 12); 
/*   0*/           }
/* 685*/        return jjMoveStringLiteralDfa5_0(active0, 222127118614528L, active1, 520L);
/*   0*/      case 'i':
/* 687*/        return jjMoveStringLiteralDfa5_0(active0, 549755813888L, active1, 4096L);
/*   0*/      case 'l':
/* 689*/        return jjMoveStringLiteralDfa5_0(active0, 9007199254740992L, active1, 0L);
/*   0*/      case 'n':
/* 691*/        return jjMoveStringLiteralDfa5_0(active0, 72057731476881408L, active1, 1L);
/*   0*/      case 'o':
/* 693*/        return jjMoveStringLiteralDfa5_0(active0, 39582418599936L, active1, 0L);
/*   0*/      case 'r':
/* 695*/        if ((active1 & 0x800L) != 0L) {
/* 696*/            return jjStartNfaWithStates_0(4, 75, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 's':
/* 699*/        return jjMoveStringLiteralDfa5_0(active0, 18033365085061120L, active1, 4L);
/*   0*/      case 't':
/* 701*/        if ((active0 & 0x4000000000000L) != 0L) {
/* 702*/            return jjStartNfaWithStates_0(4, 50, 12); 
/*   0*/           }
/* 703*/        return jjMoveStringLiteralDfa5_0(active0, -2017049683108560896L, active1, 0L);
/*   0*/    } 
/* 707*/    return jjStartNfa_0(3, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa5_0(long old0, long active0, long old1, long active1) {
/* 711*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 712*/        return jjStartNfa_0(3, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 713*/      this.curChar = this.input_stream.readChar();
/* 714*/    } catch (IOException e) {
/* 715*/      jjStopStringLiteralDfa_0(4, active0, active1);
/* 716*/      return 5;
/*   0*/    } 
/* 718*/    switch (this.curChar) {
/*   0*/      case '-':
/* 721*/        return jjMoveStringLiteralDfa6_0(active0, 9007199254740992L, active1, 0L);
/*   0*/      case ':':
/* 723*/        if ((active0 & 0x800000000L) != 0L) {
/* 724*/            return jjStopAtPos(5, 35); 
/*   0*/           }
/* 725*/        return jjMoveStringLiteralDfa6_0(active0, 68719476736L, active1, 0L);
/*   0*/      case 'a':
/* 727*/        return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 8L);
/*   0*/      case 'b':
/* 729*/        return jjMoveStringLiteralDfa6_0(active0, 549755813888L, active1, 0L);
/*   0*/      case 'd':
/* 731*/        return jjMoveStringLiteralDfa6_0(active0, 72567767433216L, active1, 0L);
/*   0*/      case 'g':
/* 733*/        if ((active0 & 0x100000000000000L) != 0L) {
/* 735*/          this.jjmatchedKind = 56;
/* 736*/          this.jjmatchedPos = 5;
/*   0*/        } 
/* 738*/        return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 1L);
/*   0*/      case 'i':
/* 740*/        return jjMoveStringLiteralDfa6_0(active0, 1153484454560268288L, active1, 0L);
/*   0*/      case 'l':
/* 742*/        return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 6L);
/*   0*/      case 'n':
/* 744*/        return jjMoveStringLiteralDfa6_0(active0, 149542171312128L, active1, 4096L);
/*   0*/      case 'p':
/* 746*/        return jjMoveStringLiteralDfa6_0(active0, 18015498021109760L, active1, 0L);
/*   0*/      case 'r':
/* 748*/        if ((active1 & 0x200L) != 0L) {
/* 749*/            return jjStartNfaWithStates_0(5, 73, 12); 
/*   0*/           }
/* 750*/        return jjMoveStringLiteralDfa6_0(active0, -2305843009213693952L, active1, 0L);
/*   0*/      case 's':
/* 752*/        return jjMoveStringLiteralDfa6_0(active0, 288230393331580928L, active1, 0L);
/*   0*/      case 't':
/* 754*/        if ((active0 & 0x200000000000000L) != 0L) {
/* 755*/            return jjStartNfaWithStates_0(5, 57, 12); 
/*   0*/           }
/* 756*/        return jjMoveStringLiteralDfa6_0(active0, 18004502904832L, active1, 16384L);
/*   0*/      case 'w':
/* 758*/        return jjMoveStringLiteralDfa6_0(active0, 576500334722023424L, active1, 0L);
/*   0*/    } 
/* 762*/    return jjStartNfa_0(4, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa6_0(long old0, long active0, long old1, long active1) {
/* 766*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 767*/        return jjStartNfa_0(4, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 768*/      this.curChar = this.input_stream.readChar();
/* 769*/    } catch (IOException e) {
/* 770*/      jjStopStringLiteralDfa_0(5, active0, active1);
/* 771*/      return 6;
/*   0*/    } 
/* 773*/    switch (this.curChar) {
/*   0*/      case '-':
/* 776*/        return jjMoveStringLiteralDfa7_0(active0, 288230376151711744L, active1, 16385L);
/*   0*/      case ':':
/* 778*/        if ((active0 & 0x1000000000L) != 0L) {
/* 779*/            return jjStopAtPos(6, 36); 
/*   0*/           }
/* 780*/        return jjMoveStringLiteralDfa7_0(active0, 137438953472L, active1, 0L);
/*   0*/      case 'a':
/* 782*/        return jjMoveStringLiteralDfa7_0(active0, 18015498021109760L, active1, 4L);
/*   0*/      case 'd':
/* 784*/        return jjMoveStringLiteralDfa7_0(active0, 149533581377536L, active1, 0L);
/*   0*/      case 'g':
/* 786*/        if ((active1 & 0x1000L) != 0L) {
/* 787*/            return jjStartNfaWithStates_0(6, 76, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'i':
/* 790*/        return jjMoveStringLiteralDfa7_0(active0, -1729270106724237312L, active1, 2L);
/*   0*/      case 'n':
/* 792*/        if ((active1 & 0x8L) != 0L) {
/* 793*/            return jjStartNfaWithStates_0(6, 67, 12); 
/*   0*/           }
/* 794*/        return jjMoveStringLiteralDfa7_0(active0, 1161928703861587968L, active1, 0L);
/*   0*/      case 'o':
/* 796*/        return jjMoveStringLiteralDfa7_0(active0, 580817017372672L, active1, 0L);
/*   0*/      case 's':
/* 798*/        return jjMoveStringLiteralDfa7_0(active0, 17179869184L, active1, 0L);
/*   0*/      case 't':
/* 800*/        if ((active0 & 0x200000000L) != 0L) {
/* 801*/            return jjStartNfaWithStates_0(6, 33, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'u':
/* 804*/        return jjMoveStringLiteralDfa7_0(active0, 549755813888L, active1, 0L);
/*   0*/    } 
/* 808*/    return jjStartNfa_0(5, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa7_0(long old0, long active0, long old1, long active1) {
/* 812*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 813*/        return jjStartNfa_0(5, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 814*/      this.curChar = this.input_stream.readChar();
/* 815*/    } catch (IOException e) {
/* 816*/      jjStopStringLiteralDfa_0(6, active0, active1);
/* 817*/      return 7;
/*   0*/    } 
/* 819*/    switch (this.curChar) {
/*   0*/      case ':':
/* 822*/        if ((active0 & 0x2000000000L) != 0L) {
/* 823*/            return jjStopAtPos(7, 37); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'a':
/* 826*/        return jjMoveStringLiteralDfa8_0(active0, 9156732836118528L, active1, 0L);
/*   0*/      case 'c':
/* 828*/        return jjMoveStringLiteralDfa8_0(active0, 18015498021109760L, active1, 0L);
/*   0*/      case 'i':
/* 830*/        return jjMoveStringLiteralDfa8_0(active0, 17179869184L, active1, 0L);
/*   0*/      case 'l':
/* 832*/        return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 1L);
/*   0*/      case 'n':
/* 834*/        if ((active0 & 0x2000000000000L) != 0L) {
/* 835*/            return jjStartNfaWithStates_0(7, 49, 12); 
/*   0*/           }
/* 836*/        return jjMoveStringLiteralDfa8_0(active0, -2305730859027660800L, active1, 16384L);
/*   0*/      case 'r':
/* 838*/        return jjMoveStringLiteralDfa8_0(active0, 17867063951360L, active1, 0L);
/*   0*/      case 's':
/* 840*/        if ((active0 & 0x1000000000000000L) != 0L) {
/* 841*/            return jjStartNfaWithStates_0(7, 60, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 't':
/* 844*/        return jjMoveStringLiteralDfa8_0(active0, 576461302059237376L, active1, 4L);
/*   0*/      case 'w':
/* 846*/        return jjMoveStringLiteralDfa8_0(active0, 288230376151711744L, active1, 0L);
/*   0*/      case 'z':
/* 848*/        return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 2L);
/*   0*/    } 
/* 852*/    return jjStartNfa_0(6, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa8_0(long old0, long active0, long old1, long active1) {
/* 856*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 857*/        return jjStartNfa_0(6, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 858*/      this.curChar = this.input_stream.readChar();
/* 859*/    } catch (IOException e) {
/* 860*/      jjStopStringLiteralDfa_0(7, active0, active1);
/* 861*/      return 8;
/*   0*/    } 
/* 863*/    switch (this.curChar) {
/*   0*/      case '-':
/* 866*/        return jjMoveStringLiteralDfa9_0(active0, 17592186044416L, active1, 0L);
/*   0*/      case ':':
/* 868*/        return jjMoveStringLiteralDfa9_0(active0, 274877906944L, active1, 0L);
/*   0*/      case 'e':
/* 870*/        if ((active1 & 0x4L) != 0L) {
/* 871*/            return jjStartNfaWithStates_0(8, 66, 12); 
/*   0*/           }
/* 872*/        return jjMoveStringLiteralDfa9_0(active0, 18016047776923648L, active1, 3L);
/*   0*/      case 'g':
/* 874*/        if ((active0 & Long.MIN_VALUE) != 0L) {
/* 876*/          this.jjmatchedKind = 63;
/* 877*/          this.jjmatchedPos = 8;
/*   0*/        } 
/* 879*/        return jjMoveStringLiteralDfa9_0(active0, 6917641177827115008L, active1, 0L);
/*   0*/      case 'h':
/* 881*/        if ((active0 & 0x800000000000000L) != 0L) {
/* 882*/            return jjStartNfaWithStates_0(8, 59, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'i':
/* 885*/        return jjMoveStringLiteralDfa9_0(active0, 288230376151711744L, active1, 0L);
/*   0*/      case 'm':
/* 887*/        return jjMoveStringLiteralDfa9_0(active0, 9007199254740992L, active1, 0L);
/*   0*/      case 'n':
/* 889*/        return jjMoveStringLiteralDfa9_0(active0, 149550761246720L, active1, 0L);
/*   0*/      case 'u':
/* 891*/        return jjMoveStringLiteralDfa9_0(active0, 0L, active1, 16384L);
/*   0*/    } 
/* 895*/    return jjStartNfa_0(7, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa9_0(long old0, long active0, long old1, long active1) {
/* 899*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 900*/        return jjStartNfa_0(7, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 901*/      this.curChar = this.input_stream.readChar();
/* 902*/    } catch (IOException e) {
/* 903*/      jjStopStringLiteralDfa_0(8, active0, active1);
/* 904*/      return 9;
/*   0*/    } 
/* 906*/    switch (this.curChar) {
/*   0*/      case '-':
/* 909*/        return jjMoveStringLiteralDfa10_0(active0, 6935648979266830336L, active1, 2L);
/*   0*/      case ':':
/* 911*/        if ((active0 & 0x4000000000L) != 0L) {
/* 912*/            return jjStopAtPos(9, 38); 
/*   0*/           }
/* 913*/        return jjMoveStringLiteralDfa10_0(active0, 8246337208320L, active1, 0L);
/*   0*/      case 'e':
/* 915*/        if ((active0 & 0x20000000000000L) != 0L) {
/* 916*/            return jjStartNfaWithStates_0(9, 53, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'g':
/* 919*/        return jjMoveStringLiteralDfa10_0(active0, 17179869184L, active1, 0L);
/*   0*/      case 'm':
/* 921*/        return jjMoveStringLiteralDfa10_0(active0, 0L, active1, 16384L);
/*   0*/      case 'n':
/* 923*/        return jjMoveStringLiteralDfa10_0(active0, 0L, active1, 1L);
/*   0*/      case 'o':
/* 925*/        return jjMoveStringLiteralDfa10_0(active0, 17592186044416L, active1, 0L);
/*   0*/      case 't':
/* 927*/        return jjMoveStringLiteralDfa10_0(active0, 288379909733089280L, active1, 0L);
/*   0*/    } 
/* 931*/    return jjStartNfa_0(8, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa10_0(long old0, long active0, long old1, long active1) {
/* 935*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 936*/        return jjStartNfa_0(8, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 937*/      this.curChar = this.input_stream.readChar();
/* 938*/    } catch (IOException e) {
/* 939*/      jjStopStringLiteralDfa_0(9, active0, active1);
/* 940*/      return 10;
/*   0*/    } 
/* 942*/    switch (this.curChar) {
/*   0*/      case '-':
/* 945*/        return jjMoveStringLiteralDfa11_0(active0, 140754668224512L, active1, 0L);
/*   0*/      case ':':
/* 947*/        if ((active0 & 0x8000000000L) != 0L) {
/* 948*/            return jjStopAtPos(10, 39); 
/*   0*/           }
/* 949*/        if ((active0 & 0x10000000000L) != 0L) {
/* 950*/            return jjStopAtPos(10, 40); 
/*   0*/           }
/* 951*/        if ((active0 & 0x20000000000L) != 0L) {
/* 952*/            return jjStopAtPos(10, 41); 
/*   0*/           }
/* 953*/        if ((active0 & 0x40000000000L) != 0L) {
/* 954*/            return jjStopAtPos(10, 42); 
/*   0*/           }
/* 955*/        return jjMoveStringLiteralDfa11_0(active0, 8796093022208L, active1, 0L);
/*   0*/      case 'a':
/* 957*/        return jjMoveStringLiteralDfa11_0(active0, 4611686018427387904L, active1, 0L);
/*   0*/      case 'b':
/* 959*/        return jjMoveStringLiteralDfa11_0(active0, 2305843009213693952L, active1, 16384L);
/*   0*/      case 'g':
/* 961*/        return jjMoveStringLiteralDfa11_0(active0, 0L, active1, 1L);
/*   0*/      case 'h':
/* 963*/        if ((active0 & 0x400000000000000L) != 0L) {
/* 964*/            return jjStartNfaWithStates_0(10, 58, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'r':
/* 967*/        return jjMoveStringLiteralDfa11_0(active0, 17592186044416L, active1, 0L);
/*   0*/      case 's':
/* 969*/        return jjMoveStringLiteralDfa11_0(active0, 105553116266496L, active1, 2L);
/*   0*/      case 'u':
/* 971*/        return jjMoveStringLiteralDfa11_0(active0, 18014398509481984L, active1, 0L);
/*   0*/    } 
/* 975*/    return jjStartNfa_0(9, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa11_0(long old0, long active0, long old1, long active1) {
/* 979*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/* 980*/        return jjStartNfa_0(9, old0, old1); 
/*   0*/       }
/*   0*/    try {
/* 981*/      this.curChar = this.input_stream.readChar();
/* 982*/    } catch (IOException e) {
/* 983*/      jjStopStringLiteralDfa_0(10, active0, active1);
/* 984*/      return 11;
/*   0*/    } 
/* 986*/    switch (this.curChar) {
/*   0*/      case '-':
/* 989*/        return jjMoveStringLiteralDfa12_0(active0, 17592186044416L, active1, 0L);
/*   0*/      case ':':
/* 991*/        if ((active0 & 0x80000000000L) != 0L) {
/* 992*/            return jjStopAtPos(11, 43); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'e':
/* 995*/        return jjMoveStringLiteralDfa12_0(active0, 2305843009213693952L, active1, 16384L);
/*   0*/      case 'f':
/* 997*/        return jjMoveStringLiteralDfa12_0(active0, 4611686018427387904L, active1, 0L);
/*   0*/      case 'i':
/* 999*/        return jjMoveStringLiteralDfa12_0(active0, 105570296135680L, active1, 0L);
/*   0*/      case 'o':
/*1001*/        return jjMoveStringLiteralDfa12_0(active0, 140737488355328L, active1, 0L);
/*   0*/      case 'p':
/*1003*/        return jjMoveStringLiteralDfa12_0(active0, 0L, active1, 2L);
/*   0*/      case 'r':
/*1005*/        return jjMoveStringLiteralDfa12_0(active0, 18014398509481984L, active1, 0L);
/*   0*/      case 't':
/*1007*/        return jjMoveStringLiteralDfa12_0(active0, 0L, active1, 1L);
/*   0*/    } 
/*1011*/    return jjStartNfa_0(10, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa12_0(long old0, long active0, long old1, long active1) {
/*1015*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/*1016*/        return jjStartNfa_0(10, old0, old1); 
/*   0*/       }
/*   0*/    try {
/*1017*/      this.curChar = this.input_stream.readChar();
/*1018*/    } catch (IOException e) {
/*1019*/      jjStopStringLiteralDfa_0(11, active0, active1);
/*1020*/      return 12;
/*   0*/    } 
/*1022*/    switch (this.curChar) {
/*   0*/      case 'a':
/*1025*/        return jjMoveStringLiteralDfa13_0(active0, 0L, active1, 2L);
/*   0*/      case 'b':
/*1027*/        return jjMoveStringLiteralDfa13_0(active0, 105553116266496L, active1, 0L);
/*   0*/      case 'f':
/*1029*/        return jjMoveStringLiteralDfa13_0(active0, 2305843009213693952L, active1, 0L);
/*   0*/      case 'h':
/*1031*/        if ((active1 & 0x1L) != 0L) {
/*1032*/            return jjStartNfaWithStates_0(12, 64, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'i':
/*1035*/        if ((active0 & 0x40000000000000L) != 0L) {
/*1036*/            return jjStartNfaWithStates_0(12, 54, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'n':
/*1039*/        return jjMoveStringLiteralDfa13_0(active0, 17179869184L, active1, 0L);
/*   0*/      case 'r':
/*1041*/        if ((active1 & 0x4000L) != 0L) {
/*1042*/            return jjStartNfaWithStates_0(12, 78, 12); 
/*   0*/           }
/*1043*/        return jjMoveStringLiteralDfa13_0(active0, 140737488355328L, active1, 0L);
/*   0*/      case 's':
/*1045*/        return jjMoveStringLiteralDfa13_0(active0, 17592186044416L, active1, 0L);
/*   0*/      case 't':
/*1047*/        return jjMoveStringLiteralDfa13_0(active0, 4611686018427387904L, active1, 0L);
/*   0*/    } 
/*1051*/    return jjStartNfa_0(11, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa13_0(long old0, long active0, long old1, long active1) {
/*1055*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/*1056*/        return jjStartNfa_0(11, old0, old1); 
/*   0*/       }
/*   0*/    try {
/*1057*/      this.curChar = this.input_stream.readChar();
/*1058*/    } catch (IOException e) {
/*1059*/      jjStopStringLiteralDfa_0(12, active0, active1);
/*1060*/      return 13;
/*   0*/    } 
/*1062*/    switch (this.curChar) {
/*   0*/      case '-':
/*1065*/        return jjMoveStringLiteralDfa14_0(active0, 140737488355328L, active1, 0L);
/*   0*/      case 'c':
/*1067*/        return jjMoveStringLiteralDfa14_0(active0, 0L, active1, 2L);
/*   0*/      case 'e':
/*1069*/        return jjMoveStringLiteralDfa14_0(active0, 4611703610613432320L, active1, 0L);
/*   0*/      case 'l':
/*1071*/        return jjMoveStringLiteralDfa14_0(active0, 105553116266496L, active1, 0L);
/*   0*/      case 'o':
/*1073*/        return jjMoveStringLiteralDfa14_0(active0, 2305843009213693952L, active1, 0L);
/*   0*/      case 's':
/*1075*/        return jjMoveStringLiteralDfa14_0(active0, 17179869184L, active1, 0L);
/*   0*/    } 
/*1079*/    return jjStartNfa_0(12, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa14_0(long old0, long active0, long old1, long active1) {
/*1083*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/*1084*/        return jjStartNfa_0(12, old0, old1); 
/*   0*/       }
/*   0*/    try {
/*1085*/      this.curChar = this.input_stream.readChar();
/*1086*/    } catch (IOException e) {
/*1087*/      jjStopStringLiteralDfa_0(13, active0, active1);
/*1088*/      return 14;
/*   0*/    } 
/*1090*/    switch (this.curChar) {
/*   0*/      case 'e':
/*1093*/        if ((active1 & 0x2L) != 0L) {
/*1094*/            return jjStartNfaWithStates_0(14, 65, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'i':
/*1097*/        return jjMoveStringLiteralDfa15_0(active0, 105553116266496L, active1, 0L);
/*   0*/      case 'l':
/*1099*/        return jjMoveStringLiteralDfa15_0(active0, 17592186044416L, active1, 0L);
/*   0*/      case 'r':
/*1101*/        if ((active0 & 0x4000000000000000L) != 0L) {
/*1102*/            return jjStartNfaWithStates_0(14, 62, 12); 
/*   0*/           }
/*1103*/        return jjMoveStringLiteralDfa15_0(active0, 2305843009213693952L, active1, 0L);
/*   0*/      case 's':
/*1105*/        return jjMoveStringLiteralDfa15_0(active0, 140737488355328L, active1, 0L);
/*   0*/      case 't':
/*1107*/        return jjMoveStringLiteralDfa15_0(active0, 17179869184L, active1, 0L);
/*   0*/    } 
/*1111*/    return jjStartNfa_0(13, active0, active1);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa15_0(long old0, long active0, long old1, long active1) {
/*1115*/    if (((active0 &= old0) | (active1 &= old1)) == 0L) {
/*1116*/        return jjStartNfa_0(13, old0, old1); 
/*   0*/       }
/*   0*/    try {
/*1117*/      this.curChar = this.input_stream.readChar();
/*1118*/    } catch (IOException e) {
/*1119*/      jjStopStringLiteralDfa_0(14, active0, 0L);
/*1120*/      return 15;
/*   0*/    } 
/*1122*/    switch (this.curChar) {
/*   0*/      case 'e':
/*1125*/        if ((active0 & 0x2000000000000000L) != 0L) {
/*1126*/            return jjStartNfaWithStates_0(15, 61, 12); 
/*   0*/           }
/*1127*/        return jjMoveStringLiteralDfa16_0(active0, 140737488355328L);
/*   0*/      case 'f':
/*1129*/        return jjMoveStringLiteralDfa16_0(active0, 17592186044416L);
/*   0*/      case 'n':
/*1131*/        return jjMoveStringLiteralDfa16_0(active0, 105553116266496L);
/*   0*/      case 'r':
/*1133*/        return jjMoveStringLiteralDfa16_0(active0, 17179869184L);
/*   0*/    } 
/*1137*/    return jjStartNfa_0(14, active0, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa16_0(long old0, long active0) {
/*1141*/    if ((active0 &= old0) == 0L) {
/*1142*/        return jjStartNfa_0(14, old0, 0L); 
/*   0*/       }
/*   0*/    try {
/*1143*/      this.curChar = this.input_stream.readChar();
/*1144*/    } catch (IOException e) {
/*1145*/      jjStopStringLiteralDfa_0(15, active0, 0L);
/*1146*/      return 16;
/*   0*/    } 
/*1148*/    switch (this.curChar) {
/*   0*/      case ':':
/*1151*/        return jjMoveStringLiteralDfa17_0(active0, 17592186044416L);
/*   0*/      case 'g':
/*1153*/        return jjMoveStringLiteralDfa17_0(active0, 105553116266496L);
/*   0*/      case 'l':
/*1155*/        return jjMoveStringLiteralDfa17_0(active0, 140737488355328L);
/*   0*/      case 'u':
/*1157*/        return jjMoveStringLiteralDfa17_0(active0, 17179869184L);
/*   0*/    } 
/*1161*/    return jjStartNfa_0(15, active0, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa17_0(long old0, long active0) {
/*1165*/    if ((active0 &= old0) == 0L) {
/*1166*/        return jjStartNfa_0(15, old0, 0L); 
/*   0*/       }
/*   0*/    try {
/*1167*/      this.curChar = this.input_stream.readChar();
/*1168*/    } catch (IOException e) {
/*1169*/      jjStopStringLiteralDfa_0(16, active0, 0L);
/*1170*/      return 17;
/*   0*/    } 
/*1172*/    switch (this.curChar) {
/*   0*/      case ':':
/*1175*/        if ((active0 & 0x100000000000L) != 0L) {
/*1176*/            return jjStopAtPos(17, 44); 
/*   0*/           }
/*1177*/        return jjMoveStringLiteralDfa18_0(active0, 105553116266496L);
/*   0*/      case 'c':
/*1179*/        return jjMoveStringLiteralDfa18_0(active0, 17179869184L);
/*   0*/      case 'f':
/*1181*/        return jjMoveStringLiteralDfa18_0(active0, 140737488355328L);
/*   0*/    } 
/*1185*/    return jjStartNfa_0(16, active0, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa18_0(long old0, long active0) {
/*1189*/    if ((active0 &= old0) == 0L) {
/*1190*/        return jjStartNfa_0(16, old0, 0L); 
/*   0*/       }
/*   0*/    try {
/*1191*/      this.curChar = this.input_stream.readChar();
/*1192*/    } catch (IOException e) {
/*1193*/      jjStopStringLiteralDfa_0(17, active0, 0L);
/*1194*/      return 18;
/*   0*/    } 
/*1196*/    switch (this.curChar) {
/*   0*/      case ':':
/*1199*/        if ((active0 & 0x200000000000L) != 0L) {
/*1200*/            return jjStopAtPos(18, 45); 
/*   0*/           }
/*1201*/        if ((active0 & 0x400000000000L) != 0L) {
/*1202*/            return jjStopAtPos(18, 46); 
/*   0*/           }
/*1203*/        return jjMoveStringLiteralDfa19_0(active0, 140737488355328L);
/*   0*/      case 't':
/*1205*/        return jjMoveStringLiteralDfa19_0(active0, 17179869184L);
/*   0*/    } 
/*1209*/    return jjStartNfa_0(17, active0, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa19_0(long old0, long active0) {
/*1213*/    if ((active0 &= old0) == 0L) {
/*1214*/        return jjStartNfa_0(17, old0, 0L); 
/*   0*/       }
/*   0*/    try {
/*1215*/      this.curChar = this.input_stream.readChar();
/*1216*/    } catch (IOException e) {
/*1217*/      jjStopStringLiteralDfa_0(18, active0, 0L);
/*1218*/      return 19;
/*   0*/    } 
/*1220*/    switch (this.curChar) {
/*   0*/      case ':':
/*1223*/        if ((active0 & 0x800000000000L) != 0L) {
/*1224*/            return jjStopAtPos(19, 47); 
/*   0*/           }
/*   0*/        break;
/*   0*/      case 'i':
/*1227*/        return jjMoveStringLiteralDfa20_0(active0, 17179869184L);
/*   0*/    } 
/*1231*/    return jjStartNfa_0(18, active0, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa20_0(long old0, long active0) {
/*1235*/    if ((active0 &= old0) == 0L) {
/*1236*/        return jjStartNfa_0(18, old0, 0L); 
/*   0*/       }
/*   0*/    try {
/*1237*/      this.curChar = this.input_stream.readChar();
/*1238*/    } catch (IOException e) {
/*1239*/      jjStopStringLiteralDfa_0(19, active0, 0L);
/*1240*/      return 20;
/*   0*/    } 
/*1242*/    switch (this.curChar) {
/*   0*/      case 'o':
/*1245*/        return jjMoveStringLiteralDfa21_0(active0, 17179869184L);
/*   0*/    } 
/*1249*/    return jjStartNfa_0(19, active0, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  private final int jjMoveStringLiteralDfa21_0(long old0, long active0) {
/*1253*/    if ((active0 &= old0) == 0L) {
/*1254*/        return jjStartNfa_0(19, old0, 0L); 
/*   0*/       }
/*   0*/    try {
/*1255*/      this.curChar = this.input_stream.readChar();
/*1256*/    } catch (IOException e) {
/*1257*/      jjStopStringLiteralDfa_0(20, active0, 0L);
/*1258*/      return 21;
/*   0*/    } 
/*1260*/    switch (this.curChar) {
/*   0*/      case 'n':
/*1263*/        if ((active0 & 0x400000000L) != 0L) {
/*1264*/            return jjStartNfaWithStates_0(21, 34, 12); 
/*   0*/           }
/*   0*/        break;
/*   0*/    } 
/*1269*/    return jjStartNfa_0(20, active0, 0L);
/*   0*/  }
/*   0*/  
/*   0*/  private final void jjCheckNAdd(int state) {
/*1273*/    if (this.jjrounds[state] != this.jjround) {
/*1275*/      this.jjstateSet[this.jjnewStateCnt++] = state;
/*1276*/      this.jjrounds[state] = this.jjround;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final void jjAddStates(int start, int end) {
/*   0*/    do {
/*1282*/      this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
/*1283*/    } while (start++ != end);
/*   0*/  }
/*   0*/  
/*   0*/  private final void jjCheckNAddTwoStates(int state1, int state2) {
/*1287*/    jjCheckNAdd(state1);
/*1288*/    jjCheckNAdd(state2);
/*   0*/  }
/*   0*/  
/*   0*/  private final void jjCheckNAddStates(int start, int end) {
/*   0*/    do {
/*1293*/      jjCheckNAdd(jjnextStates[start]);
/*1294*/    } while (start++ != end);
/*   0*/  }
/*   0*/  
/*   0*/  private final void jjCheckNAddStates(int start) {
/*1298*/    jjCheckNAdd(jjnextStates[start]);
/*1299*/    jjCheckNAdd(jjnextStates[start + 1]);
/*   0*/  }
/*   0*/  
/*1301*/  static final long[] jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
/*   0*/  
/*1304*/  static final long[] jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
/*   0*/  
/*1307*/  static final long[] jjbitVec3 = new long[] { 0L, -16384L, -17590038560769L, 8388607L };
/*   0*/  
/*1310*/  static final long[] jjbitVec4 = new long[] { 0L, 0L, 0L, -36028797027352577L };
/*   0*/  
/*1313*/  static final long[] jjbitVec5 = new long[] { 9219994337134247935L, 9223372036854775294L, -1L, -274156627316187121L };
/*   0*/  
/*1316*/  static final long[] jjbitVec6 = new long[] { 16777215L, -65536L, -576458553280167937L, 3L };
/*   0*/  
/*1319*/  static final long[] jjbitVec7 = new long[] { 0L, 0L, -17179879616L, 4503588160110591L };
/*   0*/  
/*1322*/  static final long[] jjbitVec8 = new long[] { -8194L, -536936449L, -65533L, 234134404065073567L };
/*   0*/  
/*1325*/  static final long[] jjbitVec9 = new long[] { -562949953421312L, -8547991553L, 127L, 1979120929931264L };
/*   0*/  
/*1328*/  static final long[] jjbitVec10 = new long[] { 576460743713488896L, -562949953419266L, 9007199254740991999L, 412319973375L };
/*   0*/  
/*1331*/  static final long[] jjbitVec11 = new long[] { 2594073385365405664L, 17163091968L, 271902628478820320L, 844440767823872L };
/*   0*/  
/*1334*/  static final long[] jjbitVec12 = new long[] { 247132830528276448L, 7881300924956672L, 2589004636761075680L, 4294967296L };
/*   0*/  
/*1337*/  static final long[] jjbitVec13 = new long[] { 2579997437506199520L, 15837691904L, 270153412153034720L, 0L };
/*   0*/  
/*1340*/  static final long[] jjbitVec14 = new long[] { 283724577500946400L, 12884901888L, 283724577500946400L, 13958643712L };
/*   0*/  
/*1343*/  static final long[] jjbitVec15 = new long[] { 288228177128316896L, 12884901888L, 0L, 0L };
/*   0*/  
/*1346*/  static final long[] jjbitVec16 = new long[] { 3799912185593854L, 63L, 2309621682768192918L, 31L };
/*   0*/  
/*1349*/  static final long[] jjbitVec17 = new long[] { 0L, 4398046510847L, 0L, 0L };
/*   0*/  
/*1352*/  static final long[] jjbitVec18 = new long[] { 0L, 0L, -4294967296L, 36028797018898495L };
/*   0*/  
/*1355*/  static final long[] jjbitVec19 = new long[] { 5764607523034749677L, 12493387738468353L, -756383734487318528L, 144405459145588743L };
/*   0*/  
/*1358*/  static final long[] jjbitVec20 = new long[] { -1L, -1L, -4026531841L, 288230376151711743L };
/*   0*/  
/*1361*/  static final long[] jjbitVec21 = new long[] { -3233808385L, 4611686017001275199L, 6908521828386340863L, 2295745090394464220L };
/*   0*/  
/*1364*/  static final long[] jjbitVec22 = new long[] { 83837761617920L, 0L, 7L, 0L };
/*   0*/  
/*1367*/  static final long[] jjbitVec23 = new long[] { 4389456576640L, -2L, -8587837441L, 576460752303423487L };
/*   0*/  
/*1370*/  static final long[] jjbitVec24 = new long[] { 35184372088800L, 0L, 0L, 0L };
/*   0*/  
/*1373*/  static final long[] jjbitVec25 = new long[] { -1L, -1L, 274877906943L, 0L };
/*   0*/  
/*1376*/  static final long[] jjbitVec26 = new long[] { -1L, -1L, 68719476735L, 0L };
/*   0*/  
/*1379*/  static final long[] jjbitVec27 = new long[] { 0L, 0L, 36028797018963968L, -36028797027352577L };
/*   0*/  
/*1382*/  static final long[] jjbitVec28 = new long[] { 16777215L, -65536L, -576458553280167937L, 196611L };
/*   0*/  
/*1385*/  static final long[] jjbitVec29 = new long[] { -1L, 12884901951L, -17179879488L, 4503588160110591L };
/*   0*/  
/*1388*/  static final long[] jjbitVec30 = new long[] { -8194L, -536936449L, -65413L, 234134404065073567L };
/*   0*/  
/*1391*/  static final long[] jjbitVec31 = new long[] { -562949953421312L, -8547991553L, -4899916411759099777L, 1979120929931286L };
/*   0*/  
/*1394*/  static final long[] jjbitVec32 = new long[] { 576460743713488896L, -277081224642561L, 9007199254740991999L, 288017070894841855L };
/*   0*/  
/*1397*/  static final long[] jjbitVec33 = new long[] { -864691128455135250L, 281268803485695L, -3186861885341720594L, 1125692414638495L };
/*   0*/  
/*1400*/  static final long[] jjbitVec34 = new long[] { -3211631683292264476L, 9006925953907079L, -869759877059465234L, 281204393786303L };
/*   0*/  
/*1403*/  static final long[] jjbitVec35 = new long[] { -878767076314341394L, 281215949093263L, -4341532606274353172L, 280925229301191L };
/*   0*/  
/*1406*/  static final long[] jjbitVec36 = new long[] { -4327961440926441490L, 281212990012895L, -4327961440926441492L, 281214063754719L };
/*   0*/  
/*1409*/  static final long[] jjbitVec37 = new long[] { -4323457841299070996L, 281212992110031L, 0L, 0L };
/*   0*/  
/*1412*/  static final long[] jjbitVec38 = new long[] { 576320014815068158L, 67076095L, 4323293666156225942L, 67059551L };
/*   0*/  
/*1415*/  static final long[] jjbitVec39 = new long[] { -4422530440275951616L, -558551906910465L, 215680200883507167L, 0L };
/*   0*/  
/*1418*/  static final long[] jjbitVec40 = new long[] { 0L, 0L, 0L, 9126739968L };
/*   0*/  
/*1421*/  static final long[] jjbitVec41 = new long[] { 17732914942836896L, -2L, -6876561409L, 8646911284551352319L };
/*   0*/  
/*   0*/  private final int jjMoveNfa_0(int startState, int curPos) {
/*1427*/    int startsAt = 0;
/*1428*/    this.jjnewStateCnt = 13;
/*1429*/    int i = 1;
/*1430*/    this.jjstateSet[0] = startState;
/*1431*/    int kind = Integer.MAX_VALUE;
/*   0*/    while (true) {
/*1434*/      if (++this.jjround == Integer.MAX_VALUE) {
/*1435*/          ReInitRounds(); 
/*   0*/         }
/*1436*/      if (this.curChar < '@') {
/*1438*/        long l = 1L << this.curChar;
/*   0*/        do {
/*1441*/          switch (this.jjstateSet[--i]) {
/*   0*/            case 0:
/*1444*/              if ((0x3FF000000000000L & l) != 0L) {
/*1446*/                if (kind > 20) {
/*1447*/                    kind = 20; 
/*   0*/                   }
/*1448*/                jjCheckNAddTwoStates(6, 7);
/*   0*/                break;
/*   0*/              } 
/*1450*/              if (this.curChar == '.') {
/*1451*/                jjCheckNAdd(10);
/*   0*/                break;
/*   0*/              } 
/*1452*/              if (this.curChar == '\'') {
/*1453*/                jjCheckNAddTwoStates(4, 5);
/*   0*/                break;
/*   0*/              } 
/*1454*/              if (this.curChar == '"') {
/*1455*/                  jjCheckNAddTwoStates(1, 2); 
/*   0*/                 }
/*   0*/              break;
/*   0*/            case 1:
/*1458*/              if ((0xFFFFFFFBFFFFFFFFL & l) != 0L) {
/*1459*/                  jjCheckNAddTwoStates(1, 2); 
/*   0*/                 }
/*   0*/              break;
/*   0*/            case 2:
/*1462*/              if (this.curChar == '"' && kind > 18) {
/*1463*/                  kind = 18; 
/*   0*/                 }
/*   0*/              break;
/*   0*/            case 3:
/*1466*/              if (this.curChar == '\'') {
/*1467*/                  jjCheckNAddTwoStates(4, 5); 
/*   0*/                 }
/*   0*/              break;
/*   0*/            case 4:
/*1470*/              if ((0xFFFFFF7FFFFFFFFFL & l) != 0L) {
/*1471*/                  jjCheckNAddTwoStates(4, 5); 
/*   0*/                 }
/*   0*/              break;
/*   0*/            case 5:
/*1474*/              if (this.curChar == '\'' && kind > 18) {
/*1475*/                  kind = 18; 
/*   0*/                 }
/*   0*/              break;
/*   0*/            case 6:
/*1478*/              if ((0x3FF000000000000L & l) == 0L) {
/*   0*/                  break; 
/*   0*/                 }
/*1480*/              if (kind > 20) {
/*1481*/                  kind = 20; 
/*   0*/                 }
/*1482*/              jjCheckNAddTwoStates(6, 7);
/*   0*/              break;
/*   0*/            case 7:
/*1485*/              if (this.curChar != '.') {
/*   0*/                  break; 
/*   0*/                 }
/*1487*/              if (kind > 20) {
/*1488*/                  kind = 20; 
/*   0*/                 }
/*1489*/              jjCheckNAdd(8);
/*   0*/              break;
/*   0*/            case 8:
/*1492*/              if ((0x3FF000000000000L & l) == 0L) {
/*   0*/                  break; 
/*   0*/                 }
/*1494*/              if (kind > 20) {
/*1495*/                  kind = 20; 
/*   0*/                 }
/*1496*/              jjCheckNAdd(8);
/*   0*/              break;
/*   0*/            case 9:
/*1499*/              if (this.curChar == '.') {
/*1500*/                  jjCheckNAdd(10); 
/*   0*/                 }
/*   0*/              break;
/*   0*/            case 10:
/*1503*/              if ((0x3FF000000000000L & l) == 0L) {
/*   0*/                  break; 
/*   0*/                 }
/*1505*/              if (kind > 20) {
/*1506*/                  kind = 20; 
/*   0*/                 }
/*1507*/              jjCheckNAdd(10);
/*   0*/              break;
/*   0*/            case 12:
/*1510*/              if ((0x3FF600000000000L & l) == 0L) {
/*   0*/                  break; 
/*   0*/                 }
/*1512*/              if (kind > 79) {
/*1513*/                  kind = 79; 
/*   0*/                 }
/*1514*/              this.jjstateSet[this.jjnewStateCnt++] = 12;
/*   0*/              break;
/*   0*/          } 
/*1518*/        } while (i != startsAt);
/*1520*/      } else if (this.curChar < '\u0080') {
/*1522*/        long l = 1L << (this.curChar & 0x3F);
/*   0*/        do {
/*1525*/          switch (this.jjstateSet[--i]) {
/*   0*/            case 0:
/*   0*/            case 12:
/*1529*/              if ((0x7FFFFFE87FFFFFEL & l) == 0L) {
/*   0*/                  break; 
/*   0*/                 }
/*1531*/              if (kind > 79) {
/*1532*/                  kind = 79; 
/*   0*/                 }
/*1533*/              jjCheckNAdd(12);
/*   0*/              break;
/*   0*/            case 1:
/*1536*/              jjAddStates(0, 1);
/*   0*/              break;
/*   0*/            case 4:
/*1539*/              jjAddStates(2, 3);
/*   0*/              break;
/*   0*/          } 
/*1543*/        } while (i != startsAt);
/*   0*/      } else {
/*1547*/        int hiByte = this.curChar >> 8;
/*1548*/        int i1 = hiByte >> 6;
/*1549*/        long l1 = 1L << (hiByte & 0x3F);
/*1550*/        int i2 = (this.curChar & 0xFF) >> 6;
/*1551*/        long l2 = 1L << (this.curChar & 0x3F);
/*   0*/        do {
/*1554*/          switch (this.jjstateSet[--i]) {
/*   0*/            case 0:
/*1557*/              if (!jjCanMove_1(hiByte, i1, i2, l1, l2)) {
/*   0*/                  break; 
/*   0*/                 }
/*1559*/              if (kind > 79) {
/*1560*/                  kind = 79; 
/*   0*/                 }
/*1561*/              jjCheckNAdd(12);
/*   0*/              break;
/*   0*/            case 1:
/*1564*/              if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
/*1565*/                  jjAddStates(0, 1); 
/*   0*/                 }
/*   0*/              break;
/*   0*/            case 4:
/*1568*/              if (jjCanMove_0(hiByte, i1, i2, l1, l2)) {
/*1569*/                  jjAddStates(2, 3); 
/*   0*/                 }
/*   0*/              break;
/*   0*/            case 12:
/*1572*/              if (!jjCanMove_2(hiByte, i1, i2, l1, l2)) {
/*   0*/                  break; 
/*   0*/                 }
/*1574*/              if (kind > 79) {
/*1575*/                  kind = 79; 
/*   0*/                 }
/*1576*/              jjCheckNAdd(12);
/*   0*/              break;
/*   0*/          } 
/*1580*/        } while (i != startsAt);
/*   0*/      } 
/*1582*/      if (kind != Integer.MAX_VALUE) {
/*1584*/        this.jjmatchedKind = kind;
/*1585*/        this.jjmatchedPos = curPos;
/*1586*/        kind = Integer.MAX_VALUE;
/*   0*/      } 
/*1588*/      curPos++;
/*1589*/      if ((i = this.jjnewStateCnt) == (startsAt = 13 - (this.jjnewStateCnt = startsAt))) {
/*1590*/          return curPos; 
/*   0*/         }
/*   0*/      try {
/*1591*/        this.curChar = this.input_stream.readChar();
/*   0*/      } catch (IOException e) {
/*1592*/        return curPos;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*1595*/  static final int[] jjnextStates = new int[] { 1, 2, 4, 5 };
/*   0*/  
/*   0*/  private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
/*1600*/    switch (hiByte) {
/*   0*/      case 0:
/*1603*/        return ((jjbitVec2[i2] & l2) != 0L);
/*   0*/    } 
/*1605*/    if ((jjbitVec0[i1] & l1) != 0L) {
/*1606*/        return true; 
/*   0*/       }
/*1607*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
/*1612*/    switch (hiByte) {
/*   0*/      case 0:
/*1615*/        return ((jjbitVec4[i2] & l2) != 0L);
/*   0*/      case 1:
/*1617*/        return ((jjbitVec5[i2] & l2) != 0L);
/*   0*/      case 2:
/*1619*/        return ((jjbitVec6[i2] & l2) != 0L);
/*   0*/      case 3:
/*1621*/        return ((jjbitVec7[i2] & l2) != 0L);
/*   0*/      case 4:
/*1623*/        return ((jjbitVec8[i2] & l2) != 0L);
/*   0*/      case 5:
/*1625*/        return ((jjbitVec9[i2] & l2) != 0L);
/*   0*/      case 6:
/*1627*/        return ((jjbitVec10[i2] & l2) != 0L);
/*   0*/      case 9:
/*1629*/        return ((jjbitVec11[i2] & l2) != 0L);
/*   0*/      case 10:
/*1631*/        return ((jjbitVec12[i2] & l2) != 0L);
/*   0*/      case 11:
/*1633*/        return ((jjbitVec13[i2] & l2) != 0L);
/*   0*/      case 12:
/*1635*/        return ((jjbitVec14[i2] & l2) != 0L);
/*   0*/      case 13:
/*1637*/        return ((jjbitVec15[i2] & l2) != 0L);
/*   0*/      case 14:
/*1639*/        return ((jjbitVec16[i2] & l2) != 0L);
/*   0*/      case 15:
/*1641*/        return ((jjbitVec17[i2] & l2) != 0L);
/*   0*/      case 16:
/*1643*/        return ((jjbitVec18[i2] & l2) != 0L);
/*   0*/      case 17:
/*1645*/        return ((jjbitVec19[i2] & l2) != 0L);
/*   0*/      case 30:
/*1647*/        return ((jjbitVec20[i2] & l2) != 0L);
/*   0*/      case 31:
/*1649*/        return ((jjbitVec21[i2] & l2) != 0L);
/*   0*/      case 33:
/*1651*/        return ((jjbitVec22[i2] & l2) != 0L);
/*   0*/      case 48:
/*1653*/        return ((jjbitVec23[i2] & l2) != 0L);
/*   0*/      case 49:
/*1655*/        return ((jjbitVec24[i2] & l2) != 0L);
/*   0*/      case 159:
/*1657*/        return ((jjbitVec25[i2] & l2) != 0L);
/*   0*/      case 215:
/*1659*/        return ((jjbitVec26[i2] & l2) != 0L);
/*   0*/    } 
/*1661*/    if ((jjbitVec3[i1] & l1) != 0L) {
/*1662*/        return true; 
/*   0*/       }
/*1663*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  private static final boolean jjCanMove_2(int hiByte, int i1, int i2, long l1, long l2) {
/*1668*/    switch (hiByte) {
/*   0*/      case 0:
/*1671*/        return ((jjbitVec27[i2] & l2) != 0L);
/*   0*/      case 1:
/*1673*/        return ((jjbitVec5[i2] & l2) != 0L);
/*   0*/      case 2:
/*1675*/        return ((jjbitVec28[i2] & l2) != 0L);
/*   0*/      case 3:
/*1677*/        return ((jjbitVec29[i2] & l2) != 0L);
/*   0*/      case 4:
/*1679*/        return ((jjbitVec30[i2] & l2) != 0L);
/*   0*/      case 5:
/*1681*/        return ((jjbitVec31[i2] & l2) != 0L);
/*   0*/      case 6:
/*1683*/        return ((jjbitVec32[i2] & l2) != 0L);
/*   0*/      case 9:
/*1685*/        return ((jjbitVec33[i2] & l2) != 0L);
/*   0*/      case 10:
/*1687*/        return ((jjbitVec34[i2] & l2) != 0L);
/*   0*/      case 11:
/*1689*/        return ((jjbitVec35[i2] & l2) != 0L);
/*   0*/      case 12:
/*1691*/        return ((jjbitVec36[i2] & l2) != 0L);
/*   0*/      case 13:
/*1693*/        return ((jjbitVec37[i2] & l2) != 0L);
/*   0*/      case 14:
/*1695*/        return ((jjbitVec38[i2] & l2) != 0L);
/*   0*/      case 15:
/*1697*/        return ((jjbitVec39[i2] & l2) != 0L);
/*   0*/      case 16:
/*1699*/        return ((jjbitVec18[i2] & l2) != 0L);
/*   0*/      case 17:
/*1701*/        return ((jjbitVec19[i2] & l2) != 0L);
/*   0*/      case 30:
/*1703*/        return ((jjbitVec20[i2] & l2) != 0L);
/*   0*/      case 31:
/*1705*/        return ((jjbitVec21[i2] & l2) != 0L);
/*   0*/      case 32:
/*1707*/        return ((jjbitVec40[i2] & l2) != 0L);
/*   0*/      case 33:
/*1709*/        return ((jjbitVec22[i2] & l2) != 0L);
/*   0*/      case 48:
/*1711*/        return ((jjbitVec41[i2] & l2) != 0L);
/*   0*/      case 49:
/*1713*/        return ((jjbitVec24[i2] & l2) != 0L);
/*   0*/      case 159:
/*1715*/        return ((jjbitVec25[i2] & l2) != 0L);
/*   0*/      case 215:
/*1717*/        return ((jjbitVec26[i2] & l2) != 0L);
/*   0*/    } 
/*1719*/    if ((jjbitVec3[i1] & l1) != 0L) {
/*1720*/        return true; 
/*   0*/       }
/*1721*/    return false;
/*   0*/  }
/*   0*/  
/*1724*/  public static final String[] jjstrLiteralImages = new String[] { 
/*1724*/      "", null, null, null, null, null, "/", "//", "|", "+", 
/*1724*/      "-", "=", "!=", "<", "<=", ">", ">=", "$", null, null, 
/*1724*/      null, null, null, null, null, null, null, "or", "and", "mod", 
/*1724*/      "div", "node", "text", "comment", "processing-instruction", "self::", "child::", "parent::", "ancestor::", "attribute::", 
/*1724*/      "namespace::", "preceding::", "following::", "descendant::", "ancestor-or-self::", "following-sibling::", "preceding-sibling::", "descendant-or-self::", "last", "position", 
/*1724*/      "count", "id", "key", "local-name", "namespace-uri", "name", "string", "concat", "starts-with", "ends-with", 
/*1724*/      "contains", "substring-before", "substring-after", "substring", "string-length", "normalize-space", "translate", "boolean", "not", "true", 
/*1724*/      "false", "null", "lang", "number", "sum", "floor", "ceiling", "round", "format-number", null, 
/*1724*/      ":", "(", ")", ".", "..", "[", "]", "@", ",", "*" };
/*   0*/  
/*1746*/  public static final String[] lexStateNames = new String[] { "DEFAULT" };
/*   0*/  
/*1749*/  static final long[] jjtoToken = new long[] { -132644927L, 67108863L };
/*   0*/  
/*1752*/  static final long[] jjtoSkip = new long[] { 62L, 0L };
/*   0*/  
/*   0*/  protected SimpleCharStream input_stream;
/*   0*/  
/*1756*/  private final int[] jjrounds = new int[13];
/*   0*/  
/*1757*/  private final int[] jjstateSet = new int[26];
/*   0*/  
/*   0*/  protected char curChar;
/*   0*/  
/*   0*/  int curLexState;
/*   0*/  
/*   0*/  int defaultLexState;
/*   0*/  
/*   0*/  int jjnewStateCnt;
/*   0*/  
/*   0*/  int jjround;
/*   0*/  
/*   0*/  int jjmatchedPos;
/*   0*/  
/*   0*/  int jjmatchedKind;
/*   0*/  
/*   0*/  public XPathParserTokenManager(SimpleCharStream stream, int lexState) {
/*1767*/    this(stream);
/*1768*/    SwitchTo(lexState);
/*   0*/  }
/*   0*/  
/*   0*/  public void ReInit(SimpleCharStream stream) {
/*1772*/    this.jjmatchedPos = this.jjnewStateCnt = 0;
/*1773*/    this.curLexState = this.defaultLexState;
/*1774*/    this.input_stream = stream;
/*1775*/    ReInitRounds();
/*   0*/  }
/*   0*/  
/*   0*/  private final void ReInitRounds() {
/*1780*/    this.jjround = -2147483647;
/*1781*/    for (int i = 13; i-- > 0;) {
/*1782*/        this.jjrounds[i] = Integer.MIN_VALUE; 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void ReInit(SimpleCharStream stream, int lexState) {
/*1786*/    ReInit(stream);
/*1787*/    SwitchTo(lexState);
/*   0*/  }
/*   0*/  
/*   0*/  public void SwitchTo(int lexState) {
/*1791*/    if (lexState >= 1 || lexState < 0) {
/*1792*/        throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2); 
/*   0*/       }
/*1794*/    this.curLexState = lexState;
/*   0*/  }
/*   0*/  
/*   0*/  protected Token jjFillToken() {
/*1799*/    Token t = Token.newToken(this.jjmatchedKind);
/*1800*/    t.kind = this.jjmatchedKind;
/*1801*/    String im = jjstrLiteralImages[this.jjmatchedKind];
/*1802*/    t.image = (im == null) ? this.input_stream.GetImage() : im;
/*1803*/    t.beginLine = this.input_stream.getBeginLine();
/*1804*/    t.beginColumn = this.input_stream.getBeginColumn();
/*1805*/    t.endLine = this.input_stream.getEndLine();
/*1806*/    t.endColumn = this.input_stream.getEndColumn();
/*1807*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  public XPathParserTokenManager(SimpleCharStream stream) {
/*1810*/    this.curLexState = 0;
/*1811*/    this.defaultLexState = 0;
/*   0*/    this.input_stream = stream;
/*   0*/  }
/*   0*/  
/*   0*/  public Token getNextToken() {
/*1820*/    Token specialToken = null;
/*1822*/    int curPos = 0;
/*   0*/    while (true) {
/*   0*/      try {
/*1829*/        this.curChar = this.input_stream.BeginToken();
/*1831*/      } catch (IOException e) {
/*1833*/        this.jjmatchedKind = 0;
/*1834*/        Token matchedToken = jjFillToken();
/*1835*/        return matchedToken;
/*   0*/      } 
/*   0*/      try {
/*1838*/        int i = 0;
/*1838*/        if (this.input_stream == null) {
/*   0*/        
/*   0*/        } else {
/*1838*/          this.input_stream.backup(i);
/*   0*/        } 
/*1839*/        while (this.curChar <= ' ' && (0x100003600L & 1L << this.curChar) != 0L) {
/*1840*/            this.curChar = this.input_stream.BeginToken(); 
/*   0*/           }
/*1842*/      } catch (IOException e1) {
/*   0*/        continue;
/*   0*/      } 
/*1843*/      this.jjmatchedKind = Integer.MAX_VALUE;
/*1844*/      this.jjmatchedPos = 0;
/*1845*/      curPos = jjMoveStringLiteralDfa0_0();
/*1846*/      if (this.jjmatchedKind != Integer.MAX_VALUE) {
/*1848*/        if (this.jjmatchedPos + 1 < curPos) {
/*1849*/            this.input_stream.backup(curPos - this.jjmatchedPos - 1); 
/*   0*/           }
/*1850*/        if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
/*1852*/          Token matchedToken = jjFillToken();
/*1853*/          return matchedToken;
/*   0*/        } 
/*   0*/        continue;
/*   0*/      } 
/*   0*/      break;
/*   0*/    } 
/*1860*/    int error_line = this.input_stream.getEndLine();
/*1861*/    int error_column = this.input_stream.getEndColumn();
/*1862*/    String error_after = null;
/*   0*/    boolean EOFSeen = false;
/*   0*/    try {
/*1864*/      this.input_stream.readChar();
/*1864*/      this.input_stream.backup(1);
/*1865*/    } catch (IOException e1) {
/*1866*/      EOFSeen = true;
/*1867*/      error_after = (curPos <= 1) ? "" : this.input_stream.GetImage();
/*1868*/      if (this.curChar == '\n' || this.curChar == '\r') {
/*1869*/        error_line++;
/*1870*/        error_column = 0;
/*   0*/      } else {
/*1873*/        error_column++;
/*   0*/      } 
/*   0*/    } 
/*1875*/    if (!EOFSeen) {
/*1876*/      this.input_stream.backup(1);
/*1877*/      error_after = (curPos <= 1) ? "" : this.input_stream.GetImage();
/*   0*/    } 
/*1879*/    throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
/*   0*/  }
/*   0*/}
