/*   0*/package com.google.gson.internal.bind;
/*   0*/
/*   0*/import com.google.gson.Gson;
/*   0*/import com.google.gson.TypeAdapter;
/*   0*/import com.google.gson.TypeAdapterFactory;
/*   0*/import com.google.gson.annotations.JsonAdapter;
/*   0*/import com.google.gson.internal.ConstructorConstructor;
/*   0*/import com.google.gson.reflect.TypeToken;
/*   0*/
/*   0*/public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
/*   0*/  private final ConstructorConstructor constructorConstructor;
/*   0*/  
/*   0*/  public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
/*  37*/    this.constructorConstructor = constructorConstructor;
/*   0*/  }
/*   0*/  
/*   0*/  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> targetType) {
/*  43*/    JsonAdapter annotation = (JsonAdapter)targetType.getRawType().getAnnotation(JsonAdapter.class);
/*  44*/    if (annotation == null) {
/*  45*/        return null; 
/*   0*/       }
/*  47*/    return (TypeAdapter)getTypeAdapter(this.constructorConstructor, gson, targetType, annotation);
/*   0*/  }
/*   0*/  
/*   0*/  static TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson, TypeToken<?> fieldType, JsonAdapter annotation) {
/*  53*/    Class<?> value = annotation.value();
/*  55*/    if (TypeAdapter.class.isAssignableFrom(value)) {
/*  56*/      Class<TypeAdapter<?>> typeAdapterClass = (Class)value;
/*  57*/      typeAdapter = constructorConstructor.<TypeAdapter>get(TypeToken.get((Class)typeAdapterClass)).construct();
/*  58*/    } else if (TypeAdapterFactory.class.isAssignableFrom(value)) {
/*  59*/      Class<TypeAdapterFactory> typeAdapterFactory = (Class)value;
/*  60*/      typeAdapter = ((TypeAdapterFactory)constructorConstructor.<TypeAdapterFactory>get(TypeToken.get(typeAdapterFactory)).construct()).create(gson, fieldType);
/*   0*/    } else {
/*  64*/      throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
/*   0*/    } 
/*  67*/    if (typeAdapter == null) {
/*  67*/        return null; 
/*   0*/       }
/*  67*/    TypeAdapter<?> typeAdapter = typeAdapter.nullSafe();
/*  68*/    return typeAdapter;
/*   0*/  }
/*   0*/}
