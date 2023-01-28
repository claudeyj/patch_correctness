/*   0*/package com.fasterxml.jackson.databind.deser.std;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonProcessingException;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.JsonNode;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.node.ArrayNode;
/*   0*/import com.fasterxml.jackson.databind.node.NullNode;
/*   0*/import com.fasterxml.jackson.databind.node.ObjectNode;
/*   0*/import java.io.IOException;
/*   0*/
/*   0*/public class JsonNodeDeserializer extends BaseNodeDeserializer<JsonNode> {
/*  23*/  private static final JsonNodeDeserializer instance = new JsonNodeDeserializer();
/*   0*/  
/*   0*/  protected JsonNodeDeserializer() {
/*  25*/    super(JsonNode.class);
/*   0*/  }
/*   0*/  
/*   0*/  public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass) {
/*  32*/    if (nodeClass == ObjectNode.class) {
/*  33*/        return (JsonDeserializer<? extends JsonNode>)ObjectDeserializer.getInstance(); 
/*   0*/       }
/*  35*/    if (nodeClass == ArrayNode.class) {
/*  36*/        return (JsonDeserializer<? extends JsonNode>)ArrayDeserializer.getInstance(); 
/*   0*/       }
/*  39*/    return instance;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode getNullValue(DeserializationContext ctxt) {
/*  50*/    return NullNode.getInstance();
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonNode getNullValue() {
/*  56*/    return NullNode.getInstance();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  67*/    switch (p.getCurrentTokenId()) {
/*   0*/      case 1:
/*  69*/        return deserializeObject(p, ctxt, ctxt.getNodeFactory());
/*   0*/      case 3:
/*  71*/        return deserializeArray(p, ctxt, ctxt.getNodeFactory());
/*   0*/    } 
/*  73*/    return deserializeAny(p, ctxt, ctxt.getNodeFactory());
/*   0*/  }
/*   0*/  
/*   0*/  static final class ObjectDeserializer extends BaseNodeDeserializer<ObjectNode> {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*  88*/    protected static final ObjectDeserializer _instance = new ObjectDeserializer();
/*   0*/    
/*   0*/    protected ObjectDeserializer() {
/*  90*/      super(ObjectNode.class);
/*   0*/    }
/*   0*/    
/*   0*/    public static ObjectDeserializer getInstance() {
/*  92*/      return _instance;
/*   0*/    }
/*   0*/    
/*   0*/    public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/*  97*/      if (p.getCurrentToken() == JsonToken.START_OBJECT) {
/*  98*/        p.nextToken();
/*  99*/        return deserializeObject(p, ctxt, ctxt.getNodeFactory());
/*   0*/      } 
/* 103*/      if (p.getCurrentToken() == JsonToken.FIELD_NAME) {
/* 104*/          return deserializeObject(p, ctxt, ctxt.getNodeFactory()); 
/*   0*/         }
/* 106*/      throw ctxt.mappingException(ObjectNode.class);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static final class ArrayDeserializer extends BaseNodeDeserializer<ArrayNode> {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/* 115*/    protected static final ArrayDeserializer _instance = new ArrayDeserializer();
/*   0*/    
/*   0*/    protected ArrayDeserializer() {
/* 117*/      super(ArrayNode.class);
/*   0*/    }
/*   0*/    
/*   0*/    public static ArrayDeserializer getInstance() {
/* 119*/      return _instance;
/*   0*/    }
/*   0*/    
/*   0*/    public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 124*/      if (p.isExpectedStartArrayToken()) {
/* 125*/          return deserializeArray(p, ctxt, ctxt.getNodeFactory()); 
/*   0*/         }
/* 127*/      throw ctxt.mappingException(ArrayNode.class);
/*   0*/    }
/*   0*/  }
/*   0*/}
