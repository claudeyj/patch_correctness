/*   0*/package org.jsoup.helper;
/*   0*/
/*   0*/import java.io.BufferedInputStream;
/*   0*/import java.io.BufferedWriter;
/*   0*/import java.io.ByteArrayInputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.OutputStream;
/*   0*/import java.io.OutputStreamWriter;
/*   0*/import java.io.UnsupportedEncodingException;
/*   0*/import java.net.HttpURLConnection;
/*   0*/import java.net.InetSocketAddress;
/*   0*/import java.net.MalformedURLException;
/*   0*/import java.net.Proxy;
/*   0*/import java.net.URI;
/*   0*/import java.net.URL;
/*   0*/import java.net.URLEncoder;
/*   0*/import java.nio.ByteBuffer;
/*   0*/import java.nio.charset.Charset;
/*   0*/import java.nio.charset.IllegalCharsetNameException;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.LinkedHashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.regex.Pattern;
/*   0*/import java.util.zip.GZIPInputStream;
/*   0*/import java.util.zip.Inflater;
/*   0*/import java.util.zip.InflaterInputStream;
/*   0*/import javax.net.ssl.HttpsURLConnection;
/*   0*/import javax.net.ssl.SSLSocketFactory;
/*   0*/import org.jsoup.Connection;
/*   0*/import org.jsoup.HttpStatusException;
/*   0*/import org.jsoup.UncheckedIOException;
/*   0*/import org.jsoup.UnsupportedMimeTypeException;
/*   0*/import org.jsoup.internal.ConstrainableInputStream;
/*   0*/import org.jsoup.internal.Normalizer;
/*   0*/import org.jsoup.internal.StringUtil;
/*   0*/import org.jsoup.nodes.Document;
/*   0*/import org.jsoup.parser.Parser;
/*   0*/import org.jsoup.parser.TokenQueue;
/*   0*/
/*   0*/public class HttpConnection implements Connection {
/*   0*/  public static final String CONTENT_ENCODING = "Content-Encoding";
/*   0*/  
/*   0*/  public static final String DEFAULT_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";
/*   0*/  
/*   0*/  private static final String USER_AGENT = "User-Agent";
/*   0*/  
/*   0*/  public static final String CONTENT_TYPE = "Content-Type";
/*   0*/  
/*   0*/  public static final String MULTIPART_FORM_DATA = "multipart/form-data";
/*   0*/  
/*   0*/  public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
/*   0*/  
/*   0*/  private static final int HTTP_TEMP_REDIR = 307;
/*   0*/  
/*   0*/  private static final String DefaultUploadType = "application/octet-stream";
/*   0*/  
/*   0*/  private Connection.Request req;
/*   0*/  
/*   0*/  private Connection.Response res;
/*   0*/  
/*   0*/  public static Connection connect(String url) {
/*  68*/    Connection con = new HttpConnection();
/*  69*/    con.url(url);
/*  70*/    return con;
/*   0*/  }
/*   0*/  
/*   0*/  public static Connection connect(URL url) {
/*  74*/    Connection con = new HttpConnection();
/*  75*/    con.url(url);
/*  76*/    return con;
/*   0*/  }
/*   0*/  
/*   0*/  public HttpConnection() {
/*  80*/    this.req = new Request();
/*  81*/    this.res = new Response();
/*   0*/  }
/*   0*/  
/*   0*/  private static String encodeUrl(String url) {
/*   0*/    try {
/*  91*/      URL u = new URL(url);
/*  92*/      return encodeUrl(u).toExternalForm();
/*  93*/    } catch (Exception e) {
/*  94*/      return url;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  static URL encodeUrl(URL u) {
/*   0*/    try {
/* 101*/      String urlS = u.toExternalForm();
/* 102*/      urlS = urlS.replaceAll(" ", "%20");
/* 103*/      URI uri = new URI(urlS);
/* 104*/      return new URL(uri.toASCIIString());
/* 105*/    } catch (Exception e) {
/* 106*/      return u;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static String encodeMimeName(String val) {
/* 111*/    if (val == null) {
/* 112*/        return null; 
/*   0*/       }
/* 113*/    return val.replaceAll("\"", "%22");
/*   0*/  }
/*   0*/  
/*   0*/  public Connection url(URL url) {
/* 120*/    this.req.url(url);
/* 121*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection url(String url) {
/* 125*/    Validate.notEmpty(url, "Must supply a valid URL");
/*   0*/    try {
/* 127*/      this.req.url(new URL(encodeUrl(url)));
/* 128*/    } catch (MalformedURLException e) {
/* 129*/      throw new IllegalArgumentException("Malformed URL: " + url, e);
/*   0*/    } 
/* 131*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection proxy(Proxy proxy) {
/* 135*/    this.req.proxy(proxy);
/* 136*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection proxy(String host, int port) {
/* 140*/    this.req.proxy(host, port);
/* 141*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection userAgent(String userAgent) {
/* 145*/    Validate.notNull(userAgent, "User agent must not be null");
/* 146*/    this.req.header("User-Agent", userAgent);
/* 147*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection timeout(int millis) {
/* 151*/    this.req.timeout(millis);
/* 152*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection maxBodySize(int bytes) {
/* 156*/    this.req.maxBodySize(bytes);
/* 157*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection followRedirects(boolean followRedirects) {
/* 161*/    this.req.followRedirects(followRedirects);
/* 162*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection referrer(String referrer) {
/* 166*/    Validate.notNull(referrer, "Referrer must not be null");
/* 167*/    this.req.header("Referer", referrer);
/* 168*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection method(Connection.Method method) {
/* 172*/    this.req.method(method);
/* 173*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection ignoreHttpErrors(boolean ignoreHttpErrors) {
/* 177*/    this.req.ignoreHttpErrors(ignoreHttpErrors);
/* 178*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection ignoreContentType(boolean ignoreContentType) {
/* 182*/    this.req.ignoreContentType(ignoreContentType);
/* 183*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection data(String key, String value) {
/* 188*/    this.req.data(KeyVal.create(key, value));
/* 189*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection sslSocketFactory(SSLSocketFactory sslSocketFactory) {
/* 193*/    this.req.sslSocketFactory(sslSocketFactory);
/* 194*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection data(String key, String filename, InputStream inputStream) {
/* 198*/    this.req.data(KeyVal.create(key, filename, inputStream));
/* 199*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection data(String key, String filename, InputStream inputStream, String contentType) {
/* 204*/    this.req.data(KeyVal.create(key, filename, inputStream).contentType(contentType));
/* 205*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection data(Map<String, String> data) {
/* 209*/    Validate.notNull(data, "Data map must not be null");
/* 210*/    for (Map.Entry<String, String> entry : data.entrySet()) {
/* 211*/        this.req.data(KeyVal.create(entry.getKey(), entry.getValue())); 
/*   0*/       }
/* 213*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection data(String... keyvals) {
/* 217*/    Validate.notNull(keyvals, "Data key value pairs must not be null");
/* 218*/    Validate.isTrue((keyvals.length % 2 == 0), "Must supply an even number of key value pairs");
/* 219*/    for (int i = 0; i < keyvals.length; i += 2) {
/* 220*/      String key = keyvals[i];
/* 221*/      String value = keyvals[i + 1];
/* 222*/      Validate.notEmpty(key, "Data key must not be empty");
/* 223*/      Validate.notNull(value, "Data value must not be null");
/* 224*/      this.req.data(KeyVal.create(key, value));
/*   0*/    } 
/* 226*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection data(Collection<Connection.KeyVal> data) {
/* 230*/    Validate.notNull(data, "Data collection must not be null");
/* 231*/    for (Connection.KeyVal entry : data) {
/* 232*/        this.req.data(entry); 
/*   0*/       }
/* 234*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection.KeyVal data(String key) {
/* 238*/    Validate.notEmpty(key, "Data key must not be empty");
/* 239*/    for (Connection.KeyVal keyVal : request().data()) {
/* 240*/      if (keyVal.key().equals(key)) {
/* 241*/          return keyVal; 
/*   0*/         }
/*   0*/    } 
/* 243*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection requestBody(String body) {
/* 247*/    this.req.requestBody(body);
/* 248*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection header(String name, String value) {
/* 252*/    this.req.header(name, value);
/* 253*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection headers(Map<String, String> headers) {
/* 257*/    Validate.notNull(headers, "Header map must not be null");
/* 258*/    for (Map.Entry<String, String> entry : headers.entrySet()) {
/* 259*/        this.req.header(entry.getKey(), entry.getValue()); 
/*   0*/       }
/* 261*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection cookie(String name, String value) {
/* 265*/    this.req.cookie(name, value);
/* 266*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection cookies(Map<String, String> cookies) {
/* 270*/    Validate.notNull(cookies, "Cookie map must not be null");
/* 271*/    for (Map.Entry<String, String> entry : cookies.entrySet()) {
/* 272*/        this.req.cookie(entry.getKey(), entry.getValue()); 
/*   0*/       }
/* 274*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection parser(Parser parser) {
/* 278*/    this.req.parser(parser);
/* 279*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Document get() throws IOException {
/* 283*/    this.req.method(Connection.Method.GET);
/* 284*/    execute();
/* 285*/    return this.res.parse();
/*   0*/  }
/*   0*/  
/*   0*/  public Document post() throws IOException {
/* 289*/    this.req.method(Connection.Method.POST);
/* 290*/    execute();
/* 291*/    return this.res.parse();
/*   0*/  }
/*   0*/  
/*   0*/  public Connection.Response execute() throws IOException {
/* 295*/    this.res = Response.execute(this.req);
/* 296*/    return this.res;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection.Request request() {
/* 300*/    return this.req;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection request(Connection.Request request) {
/* 304*/    this.req = request;
/* 305*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection.Response response() {
/* 309*/    return this.res;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection response(Connection.Response response) {
/* 313*/    this.res = response;
/* 314*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Connection postDataCharset(String charset) {
/* 318*/    this.req.postDataCharset(charset);
/* 319*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  private static abstract class Base<T extends Connection.Base> implements Connection.Base<T> {
/*   0*/    URL url;
/*   0*/    
/*   0*/    Connection.Method method;
/*   0*/    
/* 330*/    Map<String, List<String>> headers = new LinkedHashMap<>();
/*   0*/    
/* 331*/    Map<String, String> cookies = new LinkedHashMap<>();
/*   0*/    
/*   0*/    public URL url() {
/* 335*/      return this.url;
/*   0*/    }
/*   0*/    
/*   0*/    public T url(URL url) {
/* 339*/      Validate.notNull(url, "URL must not be null");
/* 340*/      this.url = url;
/* 341*/      return (T)this;
/*   0*/    }
/*   0*/    
/*   0*/    public Connection.Method method() {
/* 345*/      return this.method;
/*   0*/    }
/*   0*/    
/*   0*/    public T method(Connection.Method method) {
/* 349*/      Validate.notNull(method, "Method must not be null");
/* 350*/      this.method = method;
/* 351*/      return (T)this;
/*   0*/    }
/*   0*/    
/*   0*/    public String header(String name) {
/* 355*/      Validate.notNull(name, "Header name must not be null");
/* 356*/      List<String> vals = getHeadersCaseInsensitive(name);
/* 357*/      if (vals.size() > 0) {
/* 359*/          return StringUtil.join(vals, ", "); 
/*   0*/         }
/* 362*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public T addHeader(String name, String value) {
/* 367*/      Validate.notEmpty(name);
/* 368*/      value = (value == null) ? "" : value;
/* 370*/      List<String> values = headers(name);
/* 371*/      if (values.isEmpty()) {
/* 372*/        values = new ArrayList<>();
/* 373*/        this.headers.put(name, values);
/*   0*/      } 
/* 375*/      values.add(value);
/* 377*/      return (T)this;
/*   0*/    }
/*   0*/    
/*   0*/    public List<String> headers(String name) {
/* 382*/      Validate.notEmpty(name);
/* 383*/      return getHeadersCaseInsensitive(name);
/*   0*/    }
/*   0*/    
/*   0*/    private static String fixHeaderEncoding(String val) {
/*   0*/      try {
/* 388*/        byte[] bytes = val.getBytes("ISO-8859-1");
/* 389*/        if (!looksLikeUtf8(bytes)) {
/* 390*/            return val; 
/*   0*/           }
/* 391*/        return new String(bytes, "UTF-8");
/* 392*/      } catch (UnsupportedEncodingException e) {
/* 394*/        return val;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private static boolean looksLikeUtf8(byte[] input) {
/* 399*/      int i = 0;
/* 401*/      if (input.length >= 3 && (input[0] & 0xFF) == 239 && (((input[1] & 0xFF) == 187) ? true : false) & (((input[2] & 0xFF) == 191) ? true : false)) {
/* 403*/          i = 3; 
/*   0*/         }
/* 407*/      for (int j = input.length; i < j; i++) {
/* 408*/        int o = input[i];
/* 409*/        if ((o & 0x80) != 0) {
/*   0*/          int end;
/* 414*/          if ((o & 0xE0) == 192) {
/* 415*/            end = i + 1;
/* 416*/          } else if ((o & 0xF0) == 224) {
/* 417*/            end = i + 2;
/* 418*/          } else if ((o & 0xF8) == 240) {
/* 419*/            end = i + 3;
/*   0*/          } else {
/* 421*/            return false;
/*   0*/          } 
/* 425*/          while (i < end) {
/* 426*/            i++;
/* 427*/            o = input[i];
/* 428*/            if ((o & 0xC0) != 128) {
/* 429*/                return false; 
/*   0*/               }
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/* 433*/      return true;
/*   0*/    }
/*   0*/    
/*   0*/    public T header(String name, String value) {
/* 437*/      Validate.notEmpty(name, "Header name must not be empty");
/* 438*/      removeHeader(name);
/* 439*/      addHeader(name, value);
/* 440*/      return (T)this;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasHeader(String name) {
/* 444*/      Validate.notEmpty(name, "Header name must not be empty");
/* 445*/      return (getHeadersCaseInsensitive(name).size() != 0);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasHeaderWithValue(String name, String value) {
/* 452*/      Validate.notEmpty(name);
/* 453*/      Validate.notEmpty(value);
/* 454*/      List<String> values = headers(name);
/* 455*/      for (String candidate : values) {
/* 456*/        if (value.equalsIgnoreCase(candidate)) {
/* 457*/            return true; 
/*   0*/           }
/*   0*/      } 
/* 459*/      return false;
/*   0*/    }
/*   0*/    
/*   0*/    public T removeHeader(String name) {
/* 463*/      Validate.notEmpty(name, "Header name must not be empty");
/* 464*/      Map.Entry<String, List<String>> entry = scanHeaders(name);
/* 465*/      if (entry != null) {
/* 466*/          this.headers.remove(entry.getKey()); 
/*   0*/         }
/* 467*/      return (T)this;
/*   0*/    }
/*   0*/    
/*   0*/    public Map<String, String> headers() {
/* 471*/      LinkedHashMap<String, String> map = new LinkedHashMap<>(this.headers.size());
/* 472*/      for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
/* 473*/        String header = entry.getKey();
/* 474*/        List<String> values = entry.getValue();
/* 475*/        if (values.size() > 0) {
/* 476*/            map.put(header, values.get(0)); 
/*   0*/           }
/*   0*/      } 
/* 478*/      return map;
/*   0*/    }
/*   0*/    
/*   0*/    public Map<String, List<String>> multiHeaders() {
/* 483*/      return this.headers;
/*   0*/    }
/*   0*/    
/*   0*/    private List<String> getHeadersCaseInsensitive(String name) {
/* 487*/      Validate.notNull(name);
/* 489*/      for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
/* 490*/        if (name.equalsIgnoreCase(entry.getKey())) {
/* 491*/            return entry.getValue(); 
/*   0*/           }
/*   0*/      } 
/* 494*/      return Collections.emptyList();
/*   0*/    }
/*   0*/    
/*   0*/    private Map.Entry<String, List<String>> scanHeaders(String name) {
/* 498*/      String lc = Normalizer.lowerCase(name);
/* 499*/      for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
/* 500*/        if (Normalizer.lowerCase(entry.getKey()).equals(lc)) {
/* 501*/            return entry; 
/*   0*/           }
/*   0*/      } 
/* 503*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public String cookie(String name) {
/* 507*/      Validate.notEmpty(name, "Cookie name must not be empty");
/* 508*/      return this.cookies.get(name);
/*   0*/    }
/*   0*/    
/*   0*/    public T cookie(String name, String value) {
/* 512*/      Validate.notEmpty(name, "Cookie name must not be empty");
/* 513*/      Validate.notNull(value, "Cookie value must not be null");
/* 514*/      this.cookies.put(name, value);
/* 515*/      return (T)this;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasCookie(String name) {
/* 519*/      Validate.notEmpty(name, "Cookie name must not be empty");
/* 520*/      return this.cookies.containsKey(name);
/*   0*/    }
/*   0*/    
/*   0*/    public T removeCookie(String name) {
/* 524*/      Validate.notEmpty(name, "Cookie name must not be empty");
/* 525*/      this.cookies.remove(name);
/* 526*/      return (T)this;
/*   0*/    }
/*   0*/    
/*   0*/    public Map<String, String> cookies() {
/* 530*/      return this.cookies;
/*   0*/    }
/*   0*/    
/*   0*/    private Base() {}
/*   0*/  }
/*   0*/  
/*   0*/  public static class Request extends Base<Connection.Request> implements Connection.Request {
/*   0*/    private Proxy proxy;
/*   0*/    
/*   0*/    private int timeoutMilliseconds;
/*   0*/    
/*   0*/    private int maxBodySizeBytes;
/*   0*/    
/*   0*/    private boolean followRedirects;
/*   0*/    
/*   0*/    private Collection<Connection.KeyVal> data;
/*   0*/    
/* 540*/    private String body = null;
/*   0*/    
/*   0*/    private boolean ignoreHttpErrors = false;
/*   0*/    
/*   0*/    private boolean ignoreContentType = false;
/*   0*/    
/*   0*/    private Parser parser;
/*   0*/    
/*   0*/    private boolean parserDefined = false;
/*   0*/    
/* 545*/    private String postDataCharset = "UTF-8";
/*   0*/    
/*   0*/    private SSLSocketFactory sslSocketFactory;
/*   0*/    
/*   0*/    Request() {
/* 549*/      this.timeoutMilliseconds = 30000;
/* 550*/      this.maxBodySizeBytes = 1048576;
/* 551*/      this.followRedirects = true;
/* 552*/      this.data = new ArrayList<>();
/* 553*/      this.method = Connection.Method.GET;
/* 554*/      addHeader("Accept-Encoding", "gzip");
/* 555*/      addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
/* 556*/      this.parser = Parser.htmlParser();
/*   0*/    }
/*   0*/    
/*   0*/    public Proxy proxy() {
/* 560*/      return this.proxy;
/*   0*/    }
/*   0*/    
/*   0*/    public Request proxy(Proxy proxy) {
/* 564*/      this.proxy = proxy;
/* 565*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public Request proxy(String host, int port) {
/* 569*/      this.proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
/* 570*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public int timeout() {
/* 574*/      return this.timeoutMilliseconds;
/*   0*/    }
/*   0*/    
/*   0*/    public Request timeout(int millis) {
/* 578*/      Validate.isTrue((millis >= 0), "Timeout milliseconds must be 0 (infinite) or greater");
/* 579*/      this.timeoutMilliseconds = millis;
/* 580*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public int maxBodySize() {
/* 584*/      return this.maxBodySizeBytes;
/*   0*/    }
/*   0*/    
/*   0*/    public Connection.Request maxBodySize(int bytes) {
/* 588*/      Validate.isTrue((bytes >= 0), "maxSize must be 0 (unlimited) or larger");
/* 589*/      this.maxBodySizeBytes = bytes;
/* 590*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean followRedirects() {
/* 594*/      return this.followRedirects;
/*   0*/    }
/*   0*/    
/*   0*/    public Connection.Request followRedirects(boolean followRedirects) {
/* 598*/      this.followRedirects = followRedirects;
/* 599*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean ignoreHttpErrors() {
/* 603*/      return this.ignoreHttpErrors;
/*   0*/    }
/*   0*/    
/*   0*/    public SSLSocketFactory sslSocketFactory() {
/* 607*/      return this.sslSocketFactory;
/*   0*/    }
/*   0*/    
/*   0*/    public void sslSocketFactory(SSLSocketFactory sslSocketFactory) {
/* 611*/      this.sslSocketFactory = sslSocketFactory;
/*   0*/    }
/*   0*/    
/*   0*/    public Connection.Request ignoreHttpErrors(boolean ignoreHttpErrors) {
/* 615*/      this.ignoreHttpErrors = ignoreHttpErrors;
/* 616*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean ignoreContentType() {
/* 620*/      return this.ignoreContentType;
/*   0*/    }
/*   0*/    
/*   0*/    public Connection.Request ignoreContentType(boolean ignoreContentType) {
/* 624*/      this.ignoreContentType = ignoreContentType;
/* 625*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public Request data(Connection.KeyVal keyval) {
/* 629*/      Validate.notNull(keyval, "Key val must not be null");
/* 630*/      this.data.add(keyval);
/* 631*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public Collection<Connection.KeyVal> data() {
/* 635*/      return this.data;
/*   0*/    }
/*   0*/    
/*   0*/    public Connection.Request requestBody(String body) {
/* 639*/      this.body = body;
/* 640*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public String requestBody() {
/* 644*/      return this.body;
/*   0*/    }
/*   0*/    
/*   0*/    public Request parser(Parser parser) {
/* 648*/      this.parser = parser;
/* 649*/      this.parserDefined = true;
/* 650*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public Parser parser() {
/* 654*/      return this.parser;
/*   0*/    }
/*   0*/    
/*   0*/    public Connection.Request postDataCharset(String charset) {
/* 658*/      Validate.notNull(charset, "Charset must not be null");
/* 659*/      if (!Charset.isSupported(charset)) {
/* 659*/          throw new IllegalCharsetNameException(charset); 
/*   0*/         }
/* 660*/      this.postDataCharset = charset;
/* 661*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public String postDataCharset() {
/* 665*/      return this.postDataCharset;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public static class Response extends Base<Connection.Response> implements Connection.Response {
/*   0*/    private static final int MAX_REDIRECTS = 20;
/*   0*/    
/*   0*/    private static final String LOCATION = "Location";
/*   0*/    
/*   0*/    private int statusCode;
/*   0*/    
/*   0*/    private String statusMessage;
/*   0*/    
/*   0*/    private ByteBuffer byteData;
/*   0*/    
/*   0*/    private InputStream bodyStream;
/*   0*/    
/*   0*/    private HttpURLConnection conn;
/*   0*/    
/*   0*/    private String charset;
/*   0*/    
/*   0*/    private String contentType;
/*   0*/    
/*   0*/    private boolean executed = false;
/*   0*/    
/*   0*/    private boolean inputStreamRead = false;
/*   0*/    
/* 681*/    private int numRedirects = 0;
/*   0*/    
/*   0*/    private Connection.Request req;
/*   0*/    
/* 687*/    private static final Pattern xmlContentTypeRxp = Pattern.compile("(application|text)/\\w*\\+?xml.*");
/*   0*/    
/*   0*/    private Response(Response previousResponse) throws IOException {
/* 695*/      if (previousResponse != null) {
/* 696*/        previousResponse.numRedirects++;
/* 697*/        if (this.numRedirects >= 20) {
/* 698*/            throw new IOException(String.format("Too many redirects occurred trying to load URL %s", new Object[] { previousResponse.url() })); 
/*   0*/           }
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    static Response execute(Connection.Request req) throws IOException {
/* 703*/      return execute(req, null);
/*   0*/    }
/*   0*/    
/*   0*/    static Response execute(Connection.Request req, Response previousResponse) throws IOException {
/*   0*/      Response res;
/* 707*/      Validate.notNull(req, "Request must not be null");
/* 708*/      Validate.notNull(req.url(), "URL must be specified to connect");
/* 709*/      String protocol = req.url().getProtocol();
/* 710*/      if (!protocol.equals("http") && !protocol.equals("https")) {
/* 711*/          throw new MalformedURLException("Only http & https protocols supported"); 
/*   0*/         }
/* 712*/      boolean methodHasBody = req.method().hasBody();
/* 713*/      boolean hasRequestBody = (req.requestBody() != null);
/* 714*/      if (!methodHasBody) {
/* 715*/          Validate.isFalse(hasRequestBody, "Cannot set a request body for HTTP method " + req.method()); 
/*   0*/         }
/* 718*/      String mimeBoundary = null;
/* 719*/      if (req.data().size() > 0 && (!methodHasBody || hasRequestBody)) {
/* 720*/        serialiseRequestUrl(req);
/* 721*/      } else if (methodHasBody) {
/* 722*/        mimeBoundary = setOutputContentType(req);
/*   0*/      } 
/* 724*/      long startTime = System.nanoTime();
/* 725*/      HttpURLConnection conn = createConnection(req);
/*   0*/      try {
/* 728*/        conn.connect();
/* 729*/        if (conn.getDoOutput()) {
/* 730*/            writePost(req, conn.getOutputStream(), mimeBoundary); 
/*   0*/           }
/* 732*/        int status = conn.getResponseCode();
/* 733*/        res = new Response(previousResponse);
/* 734*/        res.setupFromConnection(conn, previousResponse);
/* 735*/        res.req = req;
/* 738*/        if (res.hasHeader("Location") && req.followRedirects()) {
/* 739*/          if (status != 307) {
/* 740*/            req.method(Connection.Method.GET);
/* 741*/            req.data().clear();
/* 742*/            req.requestBody(null);
/* 743*/            req.removeHeader("Content-Type");
/*   0*/          } 
/* 746*/          String location = res.header("Location");
/* 747*/          if (location.startsWith("http:/") && location.charAt(6) != '/') {
/* 748*/              location = location.substring(6); 
/*   0*/             }
/* 749*/          URL redir = StringUtil.resolve(req.url(), location);
/* 750*/          req.url(HttpConnection.encodeUrl(redir));
/* 752*/          for (Map.Entry<String, String> cookie : res.cookies.entrySet()) {
/* 753*/              req.cookie(cookie.getKey(), cookie.getValue()); 
/*   0*/             }
/* 755*/          return execute(req, res);
/*   0*/        } 
/* 757*/        if ((status < 200 || status >= 400) && !req.ignoreHttpErrors()) {
/* 758*/            throw new HttpStatusException("HTTP error fetching URL", status, req.url().toString()); 
/*   0*/           }
/* 761*/        String contentType = res.contentType();
/* 762*/        if (contentType != null && !req.ignoreContentType() && !contentType.startsWith("text/") && !xmlContentTypeRxp.matcher(contentType).matches()) {
/* 767*/            throw new UnsupportedMimeTypeException("Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml", contentType, req.url().toString()); 
/*   0*/           }
/* 771*/        if (contentType != null && xmlContentTypeRxp.matcher(contentType).matches()) {
/* 773*/            if (req instanceof HttpConnection.Request && !((HttpConnection.Request)req).parserDefined) {
/* 774*/                req.parser(Parser.xmlParser()); 
/*   0*/               } 
/*   0*/           }
/* 778*/        res.charset = DataUtil.getCharsetFromContentType(res.contentType);
/* 779*/        if (conn.getContentLength() != 0 && req.method() != Connection.Method.HEAD) {
/* 780*/          res.bodyStream = null;
/* 781*/          res.bodyStream = (conn.getErrorStream() != null) ? conn.getErrorStream() : conn.getInputStream();
/* 782*/          if (res.hasHeaderWithValue("Content-Encoding", "gzip")) {
/* 783*/            res.bodyStream = new GZIPInputStream(res.bodyStream);
/* 784*/          } else if (res.hasHeaderWithValue("Content-Encoding", "deflate")) {
/* 785*/            res.bodyStream = new InflaterInputStream(res.bodyStream, new Inflater(true));
/*   0*/          } 
/* 787*/          res.bodyStream = ConstrainableInputStream.wrap(res.bodyStream, 32768, req.maxBodySize()).timeout(startTime, req.timeout());
/*   0*/        } else {
/* 792*/          res.byteData = DataUtil.emptyByteBuffer();
/*   0*/        } 
/* 794*/      } catch (IOException e) {
/* 797*/        conn.disconnect();
/* 798*/        throw e;
/*   0*/      } 
/* 801*/      res.executed = true;
/* 802*/      return res;
/*   0*/    }
/*   0*/    
/*   0*/    public int statusCode() {
/* 806*/      return this.statusCode;
/*   0*/    }
/*   0*/    
/*   0*/    public String statusMessage() {
/* 810*/      return this.statusMessage;
/*   0*/    }
/*   0*/    
/*   0*/    public String charset() {
/* 814*/      return this.charset;
/*   0*/    }
/*   0*/    
/*   0*/    public Response charset(String charset) {
/* 818*/      this.charset = charset;
/* 819*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public String contentType() {
/* 823*/      return this.contentType;
/*   0*/    }
/*   0*/    
/*   0*/    public Document parse() throws IOException {
/* 827*/      Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before parsing response");
/* 828*/      if (this.byteData != null) {
/* 829*/        this.bodyStream = new ByteArrayInputStream(this.byteData.array());
/* 830*/        this.inputStreamRead = false;
/*   0*/      } 
/* 832*/      Validate.isFalse(this.inputStreamRead, "Input stream already read and parsed, cannot re-read.");
/* 833*/      Document doc = DataUtil.parseInputStream(this.bodyStream, this.charset, this.url.toExternalForm(), this.req.parser());
/* 834*/      this.charset = doc.outputSettings().charset().name();
/* 835*/      this.inputStreamRead = true;
/* 836*/      safeClose();
/* 837*/      return doc;
/*   0*/    }
/*   0*/    
/*   0*/    private void prepareByteData() {
/* 841*/      Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
/* 842*/      if (this.byteData == null) {
/* 843*/        Validate.isFalse(this.inputStreamRead, "Request has already been read (with .parse())");
/*   0*/        try {
/* 845*/          this.byteData = DataUtil.readToByteBuffer(this.bodyStream, this.req.maxBodySize());
/* 846*/        } catch (IOException e) {
/* 847*/          throw new UncheckedIOException(e);
/*   0*/        } finally {
/* 849*/          this.inputStreamRead = true;
/* 850*/          safeClose();
/*   0*/        } 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public String body() {
/*   0*/      String body;
/* 856*/      prepareByteData();
/* 859*/      if (this.charset == null) {
/* 860*/        body = Charset.forName("UTF-8").decode(this.byteData).toString();
/*   0*/      } else {
/* 862*/        body = Charset.forName(this.charset).decode(this.byteData).toString();
/*   0*/      } 
/* 863*/      this.byteData.rewind();
/* 864*/      return body;
/*   0*/    }
/*   0*/    
/*   0*/    public byte[] bodyAsBytes() {
/* 868*/      prepareByteData();
/* 869*/      return this.byteData.array();
/*   0*/    }
/*   0*/    
/*   0*/    public Connection.Response bufferUp() {
/* 874*/      prepareByteData();
/* 875*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public BufferedInputStream bodyStream() {
/* 880*/      Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
/* 881*/      Validate.isFalse(this.inputStreamRead, "Request has already been read");
/* 882*/      this.inputStreamRead = true;
/* 883*/      return ConstrainableInputStream.wrap(this.bodyStream, 32768, this.req.maxBodySize());
/*   0*/    }
/*   0*/    
/*   0*/    private static HttpURLConnection createConnection(Connection.Request req) throws IOException {
/* 888*/      HttpURLConnection conn = (req.proxy() == null) ? (HttpURLConnection)req.url().openConnection() : (HttpURLConnection)req.url().openConnection(req.proxy());
/* 894*/      conn.setRequestMethod(req.method().name());
/* 895*/      conn.setInstanceFollowRedirects(false);
/* 896*/      conn.setConnectTimeout(req.timeout());
/* 897*/      conn.setReadTimeout(req.timeout() / 2);
/* 899*/      if (req.sslSocketFactory() != null && conn instanceof HttpsURLConnection) {
/* 900*/          ((HttpsURLConnection)conn).setSSLSocketFactory(req.sslSocketFactory()); 
/*   0*/         }
/* 901*/      if (req.method().hasBody()) {
/* 902*/          conn.setDoOutput(true); 
/*   0*/         }
/* 903*/      if (req.cookies().size() > 0) {
/* 904*/          conn.addRequestProperty("Cookie", getRequestCookieString(req)); 
/*   0*/         }
/* 905*/      for (Map.Entry<String, List<String>> header : req.multiHeaders().entrySet()) {
/* 906*/        for (String value : header.getValue()) {
/* 907*/            conn.addRequestProperty(header.getKey(), value); 
/*   0*/           }
/*   0*/      } 
/* 910*/      return conn;
/*   0*/    }
/*   0*/    
/*   0*/    private void safeClose() {
/* 917*/      if (this.conn != null) {
/* 918*/        this.conn.disconnect();
/* 919*/        this.conn = null;
/*   0*/      } 
/* 921*/      if (this.bodyStream != null) {
/*   0*/          try {
/* 923*/            this.bodyStream.close();
/* 924*/          } catch (IOException iOException) {
/*   0*/          
/*   0*/          } finally {
/* 927*/            this.bodyStream = null;
/*   0*/          }  
/*   0*/         }
/*   0*/    }
/*   0*/    
/*   0*/    private void setupFromConnection(HttpURLConnection conn, Response previousResponse) throws IOException {
/* 934*/      this.conn = conn;
/* 935*/      this.method = Connection.Method.valueOf(conn.getRequestMethod());
/* 936*/      this.url = conn.getURL();
/* 937*/      this.statusCode = conn.getResponseCode();
/* 938*/      this.statusMessage = conn.getResponseMessage();
/* 939*/      this.contentType = conn.getContentType();
/* 941*/      Map<String, List<String>> resHeaders = createHeaderMap(conn);
/* 942*/      processResponseHeaders(resHeaders);
/* 945*/      if (previousResponse != null) {
/* 946*/        for (Map.Entry<String, String> prevCookie : (Iterable<Map.Entry<String, String>>)previousResponse.cookies().entrySet()) {
/* 947*/          if (!hasCookie(prevCookie.getKey())) {
/* 948*/              cookie(prevCookie.getKey(), prevCookie.getValue()); 
/*   0*/             }
/*   0*/        } 
/* 950*/        previousResponse.safeClose();
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private static LinkedHashMap<String, List<String>> createHeaderMap(HttpURLConnection conn) {
/* 956*/      LinkedHashMap<String, List<String>> headers = new LinkedHashMap<>();
/* 957*/      int i = 0;
/*   0*/      while (true) {
/* 959*/        String key = conn.getHeaderFieldKey(i);
/* 960*/        String val = conn.getHeaderField(i);
/* 961*/        if (key == null && val == null) {
/*   0*/            break; 
/*   0*/           }
/* 963*/        i++;
/* 964*/        if (key == null || val == null) {
/*   0*/            continue; 
/*   0*/           }
/* 967*/        if (headers.containsKey(key)) {
/* 968*/          ((List<String>)headers.get(key)).add(val);
/*   0*/          continue;
/*   0*/        } 
/* 970*/        ArrayList<String> vals = new ArrayList<>();
/* 971*/        vals.add(val);
/* 972*/        headers.put(key, vals);
/*   0*/      } 
/* 975*/      return headers;
/*   0*/    }
/*   0*/    
/*   0*/    void processResponseHeaders(Map<String, List<String>> resHeaders) {
/* 979*/      for (Map.Entry<String, List<String>> entry : resHeaders.entrySet()) {
/* 980*/        String name = entry.getKey();
/* 981*/        if (name == null) {
/*   0*/            continue; 
/*   0*/           }
/* 984*/        List<String> values = entry.getValue();
/* 985*/        if (name.equalsIgnoreCase("Set-Cookie")) {
/* 986*/            for (String value : values) {
/* 987*/              if (value == null) {
/*   0*/                  continue; 
/*   0*/                 }
/* 989*/              TokenQueue cd = new TokenQueue(value);
/* 990*/              String cookieName = cd.chompTo("=").trim();
/* 991*/              String cookieVal = cd.consumeTo(";").trim();
/* 994*/              if (cookieName.length() > 0) {
/* 995*/                  cookie(cookieName, cookieVal); 
/*   0*/                 }
/*   0*/            }  
/*   0*/           }
/* 998*/        for (String value : values) {
/* 999*/            addHeader(name, value); 
/*   0*/           }
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private static String setOutputContentType(Connection.Request req) {
/*1005*/      String bound = null;
/*1006*/      if (req.hasHeader("Content-Type")) {
/*1011*/        if (req.header("Content-Type").contains("multipart/form-data") && !req.header("Content-Type").contains("boundary")) {
/*1013*/          bound = DataUtil.mimeBoundary();
/*1014*/          req.header("Content-Type", "multipart/form-data; boundary=" + bound);
/*   0*/        } 
/*1018*/      } else if (HttpConnection.needsMultipart(req)) {
/*1019*/        bound = DataUtil.mimeBoundary();
/*1020*/        req.header("Content-Type", "multipart/form-data; boundary=" + bound);
/*   0*/      } else {
/*1022*/        req.header("Content-Type", "application/x-www-form-urlencoded; charset=" + req.postDataCharset());
/*   0*/      } 
/*1024*/      return bound;
/*   0*/    }
/*   0*/    
/*   0*/    private static void writePost(Connection.Request req, OutputStream outputStream, String bound) throws IOException {
/*1028*/      Collection<Connection.KeyVal> data = req.data();
/*1029*/      BufferedWriter w = new BufferedWriter(new OutputStreamWriter(outputStream, req.postDataCharset()));
/*1031*/      if (bound != null) {
/*1033*/        for (Connection.KeyVal keyVal : data) {
/*1034*/          w.write("--");
/*1035*/          w.write(bound);
/*1036*/          w.write("\r\n");
/*1037*/          w.write("Content-Disposition: form-data; name=\"");
/*1038*/          w.write(HttpConnection.encodeMimeName(keyVal.key()));
/*1039*/          w.write("\"");
/*1040*/          if (keyVal.hasInputStream()) {
/*1041*/            w.write("; filename=\"");
/*1042*/            w.write(HttpConnection.encodeMimeName(keyVal.value()));
/*1043*/            w.write("\"\r\nContent-Type: ");
/*1044*/            w.write((keyVal.contentType() != null) ? keyVal.contentType() : "application/octet-stream");
/*1045*/            w.write("\r\n\r\n");
/*1046*/            w.flush();
/*1047*/            DataUtil.crossStreams(keyVal.inputStream(), outputStream);
/*1048*/            outputStream.flush();
/*   0*/          } else {
/*1050*/            w.write("\r\n\r\n");
/*1051*/            w.write(keyVal.value());
/*   0*/          } 
/*1053*/          w.write("\r\n");
/*   0*/        } 
/*1055*/        w.write("--");
/*1056*/        w.write(bound);
/*1057*/        w.write("--");
/*1058*/      } else if (req.requestBody() != null) {
/*1060*/        w.write(req.requestBody());
/*   0*/      } else {
/*   0*/        boolean first = true;
/*1065*/        for (Connection.KeyVal keyVal : data) {
/*1066*/          if (!first) {
/*1067*/            w.append('&');
/*   0*/          } else {
/*1069*/            first = false;
/*   0*/          } 
/*1071*/          w.write(URLEncoder.encode(keyVal.key(), req.postDataCharset()));
/*1072*/          w.write(61);
/*1073*/          w.write(URLEncoder.encode(keyVal.value(), req.postDataCharset()));
/*   0*/        } 
/*   0*/      } 
/*1076*/      w.close();
/*   0*/    }
/*   0*/    
/*   0*/    private static String getRequestCookieString(Connection.Request req) {
/*1080*/      StringBuilder sb = StringUtil.borrowBuilder();
/*   0*/      boolean first = true;
/*1082*/      for (Map.Entry<String, String> cookie : req.cookies().entrySet()) {
/*1083*/        if (!first) {
/*1084*/          sb.append("; ");
/*   0*/        } else {
/*1086*/          first = false;
/*   0*/        } 
/*1087*/        sb.append(cookie.getKey()).append('=').append(cookie.getValue());
/*   0*/      } 
/*1090*/      return StringUtil.releaseBuilder(sb);
/*   0*/    }
/*   0*/    
/*   0*/    private static void serialiseRequestUrl(Connection.Request req) throws IOException {
/*1095*/      URL in = req.url();
/*1096*/      StringBuilder url = StringUtil.borrowBuilder();
/*   0*/      boolean first = true;
/*1099*/      url.append(in.getProtocol()).append("://").append(in.getAuthority()).append(in.getPath()).append("?");
/*1105*/      if (in.getQuery() != null) {
/*1106*/        url.append(in.getQuery());
/*1107*/        first = false;
/*   0*/      } 
/*1109*/      for (Connection.KeyVal keyVal : req.data()) {
/*1110*/        Validate.isFalse(keyVal.hasInputStream(), "InputStream data not supported in URL query string.");
/*1111*/        if (!first) {
/*1112*/          url.append('&');
/*   0*/        } else {
/*1114*/          first = false;
/*   0*/        } 
/*1115*/        url.append(URLEncoder.encode(keyVal.key(), "UTF-8")).append('=').append(URLEncoder.encode(keyVal.value(), "UTF-8"));
/*   0*/      } 
/*1120*/      req.url(new URL(StringUtil.releaseBuilder(url)));
/*1121*/      req.data().clear();
/*   0*/    }
/*   0*/    
/*   0*/    Response() {}
/*   0*/  }
/*   0*/  
/*   0*/  private static boolean needsMultipart(Connection.Request req) {
/*1127*/    for (Connection.KeyVal keyVal : req.data()) {
/*1128*/      if (keyVal.hasInputStream()) {
/*1129*/          return true; 
/*   0*/         }
/*   0*/    } 
/*1131*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public static class KeyVal implements Connection.KeyVal {
/*   0*/    private String key;
/*   0*/    
/*   0*/    private String value;
/*   0*/    
/*   0*/    private InputStream stream;
/*   0*/    
/*   0*/    private String contentType;
/*   0*/    
/*   0*/    public static KeyVal create(String key, String value) {
/*1141*/      return new KeyVal().key(key).value(value);
/*   0*/    }
/*   0*/    
/*   0*/    public static KeyVal create(String key, String filename, InputStream stream) {
/*1145*/      return new KeyVal().key(key).value(filename).inputStream(stream);
/*   0*/    }
/*   0*/    
/*   0*/    public KeyVal key(String key) {
/*1151*/      Validate.notEmpty(key, "Data key must not be empty");
/*1152*/      this.key = key;
/*1153*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public String key() {
/*1157*/      return this.key;
/*   0*/    }
/*   0*/    
/*   0*/    public KeyVal value(String value) {
/*1161*/      Validate.notNull(value, "Data value must not be null");
/*1162*/      this.value = value;
/*1163*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public String value() {
/*1167*/      return this.value;
/*   0*/    }
/*   0*/    
/*   0*/    public KeyVal inputStream(InputStream inputStream) {
/*1171*/      Validate.notNull(this.value, "Data input stream must not be null");
/*1172*/      this.stream = inputStream;
/*1173*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public InputStream inputStream() {
/*1177*/      return this.stream;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasInputStream() {
/*1181*/      return (this.stream != null);
/*   0*/    }
/*   0*/    
/*   0*/    public Connection.KeyVal contentType(String contentType) {
/*1186*/      Validate.notEmpty(contentType);
/*1187*/      this.contentType = contentType;
/*1188*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public String contentType() {
/*1193*/      return this.contentType;
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/*1198*/      return this.key + "=" + this.value;
/*   0*/    }
/*   0*/  }
/*   0*/}
