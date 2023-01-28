/*   0*/package org.joda.time.field;
/*   0*/
/*   0*/import org.joda.time.DateTimeField;
/*   0*/import org.joda.time.DateTimeFieldType;
/*   0*/import org.joda.time.DurationField;
/*   0*/import org.joda.time.ReadablePartial;
/*   0*/
/*   0*/public final class ZeroIsMaxDateTimeField extends DecoratedDateTimeField {
/*   0*/  private static final long serialVersionUID = 961749798233026866L;
/*   0*/  
/*   0*/  public ZeroIsMaxDateTimeField(DateTimeField paramDateTimeField, DateTimeFieldType paramDateTimeFieldType) {
/*  46*/    super(paramDateTimeField, paramDateTimeFieldType);
/*  47*/    if (paramDateTimeField.getMinimumValue() != 0)
/*  48*/      throw new IllegalArgumentException("Wrapped field's minumum value must be zero"); 
/*   0*/  }
/*   0*/  
/*   0*/  public int get(long paramLong) {
/*  53*/    int i = getWrappedField().get(paramLong);
/*  54*/    if (i == 0)
/*  55*/      i = getMaximumValue(); 
/*  57*/    return i;
/*   0*/  }
/*   0*/  
/*   0*/  public long add(long paramLong, int paramInt) {
/*  61*/    return getWrappedField().add(paramLong, paramInt);
/*   0*/  }
/*   0*/  
/*   0*/  public long add(long paramLong1, long paramLong2) {
/*  65*/    return getWrappedField().add(paramLong1, paramLong2);
/*   0*/  }
/*   0*/  
/*   0*/  public long addWrapField(long paramLong, int paramInt) {
/*  69*/    return getWrappedField().addWrapField(paramLong, paramInt);
/*   0*/  }
/*   0*/  
/*   0*/  public int[] addWrapField(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfint, int paramInt2) {
/*  73*/    return getWrappedField().addWrapField(paramReadablePartial, paramInt1, paramArrayOfint, paramInt2);
/*   0*/  }
/*   0*/  
/*   0*/  public int getDifference(long paramLong1, long paramLong2) {
/*  77*/    return getWrappedField().getDifference(paramLong1, paramLong2);
/*   0*/  }
/*   0*/  
/*   0*/  public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/*  81*/    return getWrappedField().getDifferenceAsLong(paramLong1, paramLong2);
/*   0*/  }
/*   0*/  
/*   0*/  public long set(long paramLong, int paramInt) {
/*  85*/    int i = getMaximumValue();
/*  86*/    FieldUtils.verifyValueBounds(this, paramInt, 1, i);
/*  87*/    if (paramInt == i)
/*  88*/      paramInt = 0; 
/*  90*/    return getWrappedField().set(paramLong, paramInt);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isLeap(long paramLong) {
/*  94*/    return getWrappedField().isLeap(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  public int getLeapAmount(long paramLong) {
/*  98*/    return getWrappedField().getLeapAmount(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  public DurationField getLeapDurationField() {
/* 102*/    return getWrappedField().getLeapDurationField();
/*   0*/  }
/*   0*/  
/*   0*/  public int getMinimumValue() {
/* 111*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMinimumValue(long paramLong) {
/* 120*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMinimumValue(ReadablePartial paramReadablePartial) {
/* 129*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMinimumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfint) {
/* 138*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaximumValue() {
/* 148*/    return getWrappedField().getMaximumValue() + 1;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaximumValue(long paramLong) {
/* 158*/    return getWrappedField().getMaximumValue(paramLong) + 1;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaximumValue(ReadablePartial paramReadablePartial) {
/* 168*/    return getWrappedField().getMaximumValue(paramReadablePartial) + 1;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMaximumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfint) {
/* 178*/    this;
/* 178*/    return new DelegatedDateTimeField(null, null).getMaximumValue(paramReadablePartial, paramArrayOfint) + 1;
/*   0*/  }
/*   0*/  
/*   0*/  public long roundFloor(long paramLong) {
/* 182*/    return getWrappedField().roundFloor(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  public long roundCeiling(long paramLong) {
/* 186*/    return getWrappedField().roundCeiling(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  public long roundHalfFloor(long paramLong) {
/* 190*/    return getWrappedField().roundHalfFloor(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  public long roundHalfCeiling(long paramLong) {
/* 194*/    return getWrappedField().roundHalfCeiling(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  public long roundHalfEven(long paramLong) {
/* 198*/    return getWrappedField().roundHalfEven(paramLong);
/*   0*/  }
/*   0*/  
/*   0*/  public long remainder(long paramLong) {
/* 202*/    return getWrappedField().remainder(paramLong);
/*   0*/  }
/*   0*/}
