/*   0*/package com.fasterxml.jackson.databind.introspect;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*   0*/import com.fasterxml.jackson.annotation.PropertyAccessor;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.lang.reflect.Member;
/*   0*/import java.lang.reflect.Method;
/*   0*/
/*   0*/public interface VisibilityChecker<T extends VisibilityChecker<T>> {
/*   0*/  T with(JsonAutoDetect paramJsonAutoDetect);
/*   0*/  
/*   0*/  T with(JsonAutoDetect.Visibility paramVisibility);
/*   0*/  
/*   0*/  T withVisibility(PropertyAccessor paramPropertyAccessor, JsonAutoDetect.Visibility paramVisibility);
/*   0*/  
/*   0*/  T withGetterVisibility(JsonAutoDetect.Visibility paramVisibility);
/*   0*/  
/*   0*/  T withIsGetterVisibility(JsonAutoDetect.Visibility paramVisibility);
/*   0*/  
/*   0*/  T withSetterVisibility(JsonAutoDetect.Visibility paramVisibility);
/*   0*/  
/*   0*/  T withCreatorVisibility(JsonAutoDetect.Visibility paramVisibility);
/*   0*/  
/*   0*/  T withFieldVisibility(JsonAutoDetect.Visibility paramVisibility);
/*   0*/  
/*   0*/  boolean isGetterVisible(Method paramMethod);
/*   0*/  
/*   0*/  boolean isGetterVisible(AnnotatedMethod paramAnnotatedMethod);
/*   0*/  
/*   0*/  boolean isIsGetterVisible(Method paramMethod);
/*   0*/  
/*   0*/  boolean isIsGetterVisible(AnnotatedMethod paramAnnotatedMethod);
/*   0*/  
/*   0*/  boolean isSetterVisible(Method paramMethod);
/*   0*/  
/*   0*/  boolean isSetterVisible(AnnotatedMethod paramAnnotatedMethod);
/*   0*/  
/*   0*/  boolean isCreatorVisible(Member paramMember);
/*   0*/  
/*   0*/  boolean isCreatorVisible(AnnotatedMember paramAnnotatedMember);
/*   0*/  
/*   0*/  boolean isFieldVisible(Field paramField);
/*   0*/  
/*   0*/  boolean isFieldVisible(AnnotatedField paramAnnotatedField);
/*   0*/  
/*   0*/  @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, isGetterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.ANY, creatorVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
/*   0*/  public static class Std implements VisibilityChecker<Std>, Serializable {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/* 170*/    protected static final Std DEFAULT = new Std(Std.class.<JsonAutoDetect>getAnnotation(JsonAutoDetect.class));
/*   0*/    
/*   0*/    protected final JsonAutoDetect.Visibility _getterMinLevel;
/*   0*/    
/*   0*/    protected final JsonAutoDetect.Visibility _isGetterMinLevel;
/*   0*/    
/*   0*/    protected final JsonAutoDetect.Visibility _setterMinLevel;
/*   0*/    
/*   0*/    protected final JsonAutoDetect.Visibility _creatorMinLevel;
/*   0*/    
/*   0*/    protected final JsonAutoDetect.Visibility _fieldMinLevel;
/*   0*/    
/*   0*/    public static Std defaultInstance() {
/* 178*/      return DEFAULT;
/*   0*/    }
/*   0*/    
/*   0*/    public Std(JsonAutoDetect ann) {
/* 189*/      this._getterMinLevel = ann.getterVisibility();
/* 190*/      this._isGetterMinLevel = ann.isGetterVisibility();
/* 191*/      this._setterMinLevel = ann.setterVisibility();
/* 192*/      this._creatorMinLevel = ann.creatorVisibility();
/* 193*/      this._fieldMinLevel = ann.fieldVisibility();
/*   0*/    }
/*   0*/    
/*   0*/    public Std(JsonAutoDetect.Visibility getter, JsonAutoDetect.Visibility isGetter, JsonAutoDetect.Visibility setter, JsonAutoDetect.Visibility creator, JsonAutoDetect.Visibility field) {
/* 201*/      this._getterMinLevel = getter;
/* 202*/      this._isGetterMinLevel = isGetter;
/* 203*/      this._setterMinLevel = field;
/* 204*/      this._creatorMinLevel = creator;
/* 205*/      this._fieldMinLevel = field;
/*   0*/    }
/*   0*/    
/*   0*/    public Std(JsonAutoDetect.Visibility v) {
/* 217*/      if (v == JsonAutoDetect.Visibility.DEFAULT) {
/* 218*/        this._getterMinLevel = DEFAULT._getterMinLevel;
/* 219*/        this._isGetterMinLevel = DEFAULT._isGetterMinLevel;
/* 220*/        this._setterMinLevel = DEFAULT._setterMinLevel;
/* 221*/        this._creatorMinLevel = DEFAULT._creatorMinLevel;
/* 222*/        this._fieldMinLevel = DEFAULT._fieldMinLevel;
/*   0*/      } else {
/* 224*/        this._getterMinLevel = v;
/* 225*/        this._isGetterMinLevel = v;
/* 226*/        this._setterMinLevel = v;
/* 227*/        this._creatorMinLevel = v;
/* 228*/        this._fieldMinLevel = v;
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    public Std with(JsonAutoDetect ann) {
/* 242*/      Std curr = this;
/* 243*/      if (ann != null) {
/* 244*/        curr = curr.withGetterVisibility(ann.getterVisibility());
/* 245*/        curr = curr.withIsGetterVisibility(ann.isGetterVisibility());
/* 246*/        curr = curr.withSetterVisibility(ann.setterVisibility());
/* 247*/        curr = curr.withCreatorVisibility(ann.creatorVisibility());
/* 248*/        curr = curr.withFieldVisibility(ann.fieldVisibility());
/*   0*/      } 
/* 250*/      return curr;
/*   0*/    }
/*   0*/    
/*   0*/    public Std with(JsonAutoDetect.Visibility v) {
/* 256*/      if (v == JsonAutoDetect.Visibility.DEFAULT) {
/* 257*/          return DEFAULT; 
/*   0*/         }
/* 259*/      return new Std(v);
/*   0*/    }
/*   0*/    
/*   0*/    public Std withVisibility(PropertyAccessor method, JsonAutoDetect.Visibility v) {
/* 265*/      switch (method) {
/*   0*/        case GETTER:
/* 267*/          return withGetterVisibility(v);
/*   0*/        case SETTER:
/* 269*/          return withSetterVisibility(v);
/*   0*/        case CREATOR:
/* 271*/          return withCreatorVisibility(v);
/*   0*/        case FIELD:
/* 273*/          return withFieldVisibility(v);
/*   0*/        case IS_GETTER:
/* 275*/          return withIsGetterVisibility(v);
/*   0*/        case ALL:
/* 277*/          return with(v);
/*   0*/      } 
/* 281*/      return this;
/*   0*/    }
/*   0*/    
/*   0*/    public Std withGetterVisibility(JsonAutoDetect.Visibility v) {
/* 287*/      if (v == JsonAutoDetect.Visibility.DEFAULT) {
/* 287*/          v = DEFAULT._getterMinLevel; 
/*   0*/         }
/* 288*/      if (this._getterMinLevel == v) {
/* 288*/          return this; 
/*   0*/         }
/* 289*/      return new Std(v, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
/*   0*/    }
/*   0*/    
/*   0*/    public Std withIsGetterVisibility(JsonAutoDetect.Visibility v) {
/* 294*/      if (v == JsonAutoDetect.Visibility.DEFAULT) {
/* 294*/          v = DEFAULT._isGetterMinLevel; 
/*   0*/         }
/* 295*/      if (this._isGetterMinLevel == v) {
/* 295*/          return this; 
/*   0*/         }
/* 296*/      return new Std(this._getterMinLevel, v, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
/*   0*/    }
/*   0*/    
/*   0*/    public Std withSetterVisibility(JsonAutoDetect.Visibility v) {
/* 301*/      if (v == JsonAutoDetect.Visibility.DEFAULT) {
/* 301*/          v = DEFAULT._setterMinLevel; 
/*   0*/         }
/* 302*/      if (this._setterMinLevel == v) {
/* 302*/          return this; 
/*   0*/         }
/* 303*/      return new Std(this._getterMinLevel, this._isGetterMinLevel, v, this._creatorMinLevel, this._fieldMinLevel);
/*   0*/    }
/*   0*/    
/*   0*/    public Std withCreatorVisibility(JsonAutoDetect.Visibility v) {
/* 308*/      if (v == JsonAutoDetect.Visibility.DEFAULT) {
/* 308*/          v = DEFAULT._creatorMinLevel; 
/*   0*/         }
/* 309*/      if (this._creatorMinLevel == v) {
/* 309*/          return this; 
/*   0*/         }
/* 310*/      return new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, v, this._fieldMinLevel);
/*   0*/    }
/*   0*/    
/*   0*/    public Std withFieldVisibility(JsonAutoDetect.Visibility v) {
/* 315*/      if (v == JsonAutoDetect.Visibility.DEFAULT) {
/* 315*/          v = DEFAULT._fieldMinLevel; 
/*   0*/         }
/* 316*/      if (this._fieldMinLevel == v) {
/* 316*/          return this; 
/*   0*/         }
/* 317*/      return new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, v);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isCreatorVisible(Member m) {
/* 328*/      return this._creatorMinLevel.isVisible(m);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isCreatorVisible(AnnotatedMember m) {
/* 333*/      return isCreatorVisible(m.getMember());
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isFieldVisible(Field f) {
/* 338*/      return this._fieldMinLevel.isVisible(f);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isFieldVisible(AnnotatedField f) {
/* 343*/      return isFieldVisible(f.getAnnotated());
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isGetterVisible(Method m) {
/* 348*/      return this._getterMinLevel.isVisible(m);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isGetterVisible(AnnotatedMethod m) {
/* 353*/      return isGetterVisible(m.getAnnotated());
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isIsGetterVisible(Method m) {
/* 358*/      return this._isGetterMinLevel.isVisible(m);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isIsGetterVisible(AnnotatedMethod m) {
/* 363*/      return isIsGetterVisible(m.getAnnotated());
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isSetterVisible(Method m) {
/* 368*/      return this._setterMinLevel.isVisible(m);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isSetterVisible(AnnotatedMethod m) {
/* 373*/      return isSetterVisible(m.getAnnotated());
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 384*/      return "[Visibility:" + " getter: " + this._getterMinLevel + ", isGetter: " + this._isGetterMinLevel + ", setter: " + this._setterMinLevel + ", creator: " + this._creatorMinLevel + ", field: " + this._fieldMinLevel + "]";
/*   0*/    }
/*   0*/  }
/*   0*/}
