/*   0*/package com.fasterxml.jackson.databind.introspect;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.util.Annotations;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import java.lang.annotation.Annotation;
/*   0*/import java.lang.reflect.AnnotatedElement;
/*   0*/import java.lang.reflect.Constructor;
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.lang.reflect.Modifier;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.LinkedHashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public final class AnnotatedClass extends Annotated {
/*  17*/  private static final AnnotationMap[] NO_ANNOTATION_MAPS = new AnnotationMap[0];
/*   0*/  
/*   0*/  protected final Class<?> _class;
/*   0*/  
/*   0*/  protected final List<Class<?>> _superTypes;
/*   0*/  
/*   0*/  protected final AnnotationIntrospector _annotationIntrospector;
/*   0*/  
/*   0*/  protected final ClassIntrospector.MixInResolver _mixInResolver;
/*   0*/  
/*   0*/  protected final Class<?> _primaryMixIn;
/*   0*/  
/*   0*/  protected AnnotationMap _classAnnotations;
/*   0*/  
/*   0*/  protected boolean _creatorsResolved = false;
/*   0*/  
/*   0*/  protected AnnotatedConstructor _defaultConstructor;
/*   0*/  
/*   0*/  protected List<AnnotatedConstructor> _constructors;
/*   0*/  
/*   0*/  protected List<AnnotatedMethod> _creatorMethods;
/*   0*/  
/*   0*/  protected AnnotatedMethodMap _memberMethods;
/*   0*/  
/*   0*/  protected List<AnnotatedField> _fields;
/*   0*/  
/*   0*/  private AnnotatedClass(Class<?> cls, List<Class<?>> superTypes, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir, AnnotationMap classAnnotations) {
/* 117*/    this._class = cls;
/* 118*/    this._superTypes = superTypes;
/* 119*/    this._annotationIntrospector = aintr;
/* 120*/    this._mixInResolver = mir;
/* 121*/    this._primaryMixIn = (this._mixInResolver == null) ? null : this._mixInResolver.findMixInClassFor(this._class);
/* 123*/    this._classAnnotations = classAnnotations;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedClass withAnnotations(AnnotationMap ann) {
/* 128*/    return new AnnotatedClass(this._class, this._superTypes, this._annotationIntrospector, this._mixInResolver, ann);
/*   0*/  }
/*   0*/  
/*   0*/  public static AnnotatedClass construct(Class<?> cls, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir) {
/* 140*/    return new AnnotatedClass(cls, ClassUtil.findSuperTypes(cls, null), aintr, mir, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static AnnotatedClass constructWithoutSuperTypes(Class<?> cls, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir) {
/* 152*/    return new AnnotatedClass(cls, Collections.emptyList(), aintr, mir, null);
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getAnnotated() {
/* 163*/    return this._class;
/*   0*/  }
/*   0*/  
/*   0*/  public int getModifiers() {
/* 166*/    return this._class.getModifiers();
/*   0*/  }
/*   0*/  
/*   0*/  public String getName() {
/* 169*/    return this._class.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public <A extends Annotation> A getAnnotation(Class<A> acls) {
/* 174*/    if (this._classAnnotations == null) {
/* 175*/        resolveClassAnnotations(); 
/*   0*/       }
/* 177*/    return this._classAnnotations.get(acls);
/*   0*/  }
/*   0*/  
/*   0*/  public Type getGenericType() {
/* 182*/    return this._class;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getRawType() {
/* 187*/    return this._class;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Annotation> annotations() {
/* 192*/    if (this._classAnnotations == null) {
/* 193*/        resolveClassAnnotations(); 
/*   0*/       }
/* 195*/    return this._classAnnotations.annotations();
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotationMap getAllAnnotations() {
/* 200*/    if (this._classAnnotations == null) {
/* 201*/        resolveClassAnnotations(); 
/*   0*/       }
/* 203*/    return this._classAnnotations;
/*   0*/  }
/*   0*/  
/*   0*/  public Annotations getAnnotations() {
/* 213*/    if (this._classAnnotations == null) {
/* 214*/        resolveClassAnnotations(); 
/*   0*/       }
/* 216*/    return this._classAnnotations;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasAnnotations() {
/* 220*/    if (this._classAnnotations == null) {
/* 221*/        resolveClassAnnotations(); 
/*   0*/       }
/* 223*/    return (this._classAnnotations.size() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedConstructor getDefaultConstructor() {
/* 228*/    if (!this._creatorsResolved) {
/* 229*/        resolveCreators(); 
/*   0*/       }
/* 231*/    return this._defaultConstructor;
/*   0*/  }
/*   0*/  
/*   0*/  public List<AnnotatedConstructor> getConstructors() {
/* 236*/    if (!this._creatorsResolved) {
/* 237*/        resolveCreators(); 
/*   0*/       }
/* 239*/    return this._constructors;
/*   0*/  }
/*   0*/  
/*   0*/  public List<AnnotatedMethod> getStaticMethods() {
/* 244*/    if (!this._creatorsResolved) {
/* 245*/        resolveCreators(); 
/*   0*/       }
/* 247*/    return this._creatorMethods;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<AnnotatedMethod> memberMethods() {
/* 252*/    if (this._memberMethods == null) {
/* 253*/        resolveMemberMethods(); 
/*   0*/       }
/* 255*/    return this._memberMethods;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMemberMethodCount() {
/* 260*/    if (this._memberMethods == null) {
/* 261*/        resolveMemberMethods(); 
/*   0*/       }
/* 263*/    return this._memberMethods.size();
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
/* 268*/    if (this._memberMethods == null) {
/* 269*/        resolveMemberMethods(); 
/*   0*/       }
/* 271*/    return this._memberMethods.find(name, paramTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public int getFieldCount() {
/* 275*/    if (this._fields == null) {
/* 276*/        resolveFields(); 
/*   0*/       }
/* 278*/    return this._fields.size();
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<AnnotatedField> fields() {
/* 283*/    if (this._fields == null) {
/* 284*/        resolveFields(); 
/*   0*/       }
/* 286*/    return this._fields;
/*   0*/  }
/*   0*/  
/*   0*/  private void resolveClassAnnotations() {
/* 302*/    this._classAnnotations = new AnnotationMap();
/* 304*/    if (this._annotationIntrospector != null) {
/* 306*/      if (this._primaryMixIn != null) {
/* 307*/          _addClassMixIns(this._classAnnotations, this._class, this._primaryMixIn); 
/*   0*/         }
/* 310*/      _addAnnotationsIfNotPresent(this._classAnnotations, this._class.getDeclaredAnnotations());
/* 313*/      for (Class<?> cls : this._superTypes) {
/* 315*/        _addClassMixIns(this._classAnnotations, cls);
/* 316*/        _addAnnotationsIfNotPresent(this._classAnnotations, cls.getDeclaredAnnotations());
/*   0*/      } 
/* 325*/      _addClassMixIns(this._classAnnotations, Object.class);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void resolveCreators() {
/* 336*/    List<AnnotatedConstructor> constructors = null;
/* 337*/    Constructor[] arrayOfConstructor = (Constructor[])this._class.getDeclaredConstructors();
/* 338*/    for (Constructor<?> ctor : arrayOfConstructor) {
/* 339*/      if ((ctor.getParameterTypes()).length == 0) {
/* 340*/        this._defaultConstructor = _constructConstructor(ctor, true);
/*   0*/      } else {
/* 342*/        if (constructors == null) {
/* 343*/            constructors = new ArrayList<AnnotatedConstructor>(Math.max(10, arrayOfConstructor.length)); 
/*   0*/           }
/* 345*/        constructors.add(_constructConstructor(ctor, false));
/*   0*/      } 
/*   0*/    } 
/* 348*/    if (constructors == null) {
/* 349*/      this._constructors = Collections.emptyList();
/*   0*/    } else {
/* 351*/      this._constructors = constructors;
/*   0*/    } 
/* 354*/    if (this._primaryMixIn != null && (
/* 355*/      this._defaultConstructor != null || !this._constructors.isEmpty())) {
/* 356*/        _addConstructorMixIns(this._primaryMixIn); 
/*   0*/       }
/* 365*/    if (this._annotationIntrospector != null) {
/* 366*/      if (this._defaultConstructor != null && 
/* 367*/        this._annotationIntrospector.hasIgnoreMarker(this._defaultConstructor)) {
/* 368*/          this._defaultConstructor = null; 
/*   0*/         }
/* 371*/      if (this._constructors != null) {
/* 373*/          for (int i = this._constructors.size(); --i >= 0;) {
/* 374*/            if (this._annotationIntrospector.hasIgnoreMarker(this._constructors.get(i))) {
/* 375*/                this._constructors.remove(i); 
/*   0*/               }
/*   0*/          }  
/*   0*/         }
/*   0*/    } 
/* 380*/    List<AnnotatedMethod> creatorMethods = null;
/* 383*/    for (Method m : this._class.getDeclaredMethods()) {
/* 384*/      if (Modifier.isStatic(m.getModifiers())) {
/* 389*/        if (creatorMethods == null) {
/* 390*/            creatorMethods = new ArrayList<AnnotatedMethod>(8); 
/*   0*/           }
/* 392*/        creatorMethods.add(_constructCreatorMethod(m));
/*   0*/      } 
/*   0*/    } 
/* 394*/    if (creatorMethods == null) {
/* 395*/      this._creatorMethods = Collections.emptyList();
/*   0*/    } else {
/* 397*/      this._creatorMethods = creatorMethods;
/* 399*/      if (this._primaryMixIn != null) {
/* 400*/          _addFactoryMixIns(this._primaryMixIn); 
/*   0*/         }
/* 403*/      if (this._annotationIntrospector != null) {
/* 405*/          for (int i = this._creatorMethods.size(); --i >= 0;) {
/* 406*/            if (this._annotationIntrospector.hasIgnoreMarker(this._creatorMethods.get(i))) {
/* 407*/                this._creatorMethods.remove(i); 
/*   0*/               }
/*   0*/          }  
/*   0*/         }
/*   0*/    } 
/* 412*/    this._creatorsResolved = true;
/*   0*/  }
/*   0*/  
/*   0*/  private void resolveMemberMethods() {
/* 423*/    this._memberMethods = new AnnotatedMethodMap();
/* 424*/    AnnotatedMethodMap mixins = new AnnotatedMethodMap();
/* 426*/    _addMemberMethods(this._class, this._memberMethods, this._primaryMixIn, mixins);
/* 429*/    for (Class<?> cls : this._superTypes) {
/* 430*/      Class<?> mixin = (this._mixInResolver == null) ? null : this._mixInResolver.findMixInClassFor(cls);
/* 431*/      _addMemberMethods(cls, this._memberMethods, mixin, mixins);
/*   0*/    } 
/* 434*/    if (this._mixInResolver != null) {
/* 435*/      Class<?> mixin = this._mixInResolver.findMixInClassFor(Object.class);
/* 436*/      if (mixin != null) {
/* 437*/          _addMethodMixIns(this._class, this._memberMethods, mixin, mixins); 
/*   0*/         }
/*   0*/    } 
/* 447*/    if (this._annotationIntrospector != null && 
/* 448*/      !mixins.isEmpty()) {
/* 449*/      Iterator<AnnotatedMethod> it = mixins.iterator();
/* 450*/      while (it.hasNext()) {
/* 451*/        AnnotatedMethod mixIn = it.next();
/*   0*/        try {
/* 453*/          Method m = Object.class.getDeclaredMethod(mixIn.getName(), mixIn.getRawParameterTypes());
/* 454*/          if (m != null) {
/* 455*/            AnnotatedMethod am = _constructMethod(m);
/* 456*/            _addMixOvers(mixIn.getAnnotated(), am, false);
/* 457*/            this._memberMethods.add(am);
/*   0*/          } 
/* 459*/        } catch (Exception exception) {}
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void resolveFields() {
/* 472*/    Map<String, AnnotatedField> foundFields = _findFields(this._class, null);
/* 473*/    if (foundFields == null || foundFields.size() == 0) {
/* 474*/      this._fields = Collections.emptyList();
/*   0*/    } else {
/* 476*/      this._fields = new ArrayList<AnnotatedField>(foundFields.size());
/* 477*/      this._fields.addAll(foundFields.values());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addClassMixIns(AnnotationMap annotations, Class<?> toMask) {
/* 495*/    if (this._mixInResolver != null) {
/* 496*/        _addClassMixIns(annotations, toMask, this._mixInResolver.findMixInClassFor(toMask)); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addClassMixIns(AnnotationMap annotations, Class<?> toMask, Class<?> mixin) {
/* 503*/    if (mixin == null) {
/*   0*/        return; 
/*   0*/       }
/* 507*/    _addAnnotationsIfNotPresent(annotations, mixin.getDeclaredAnnotations());
/* 516*/    for (Class<?> parent : ClassUtil.findSuperTypes(mixin, toMask)) {
/* 517*/        _addAnnotationsIfNotPresent(annotations, parent.getDeclaredAnnotations()); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addConstructorMixIns(Class<?> mixin) {
/* 529*/    MemberKey[] ctorKeys = null;
/* 530*/    int ctorCount = (this._constructors == null) ? 0 : this._constructors.size();
/* 531*/    for (Constructor<?> ctor : mixin.getDeclaredConstructors()) {
/* 532*/      if ((ctor.getParameterTypes()).length == 0) {
/* 533*/        if (this._defaultConstructor != null) {
/* 534*/            _addMixOvers(ctor, this._defaultConstructor, false); 
/*   0*/           }
/*   0*/      } else {
/* 537*/        if (ctorKeys == null) {
/* 538*/          ctorKeys = new MemberKey[ctorCount];
/* 539*/          for (int j = 0; j < ctorCount; j++) {
/* 540*/              ctorKeys[j] = new MemberKey(((AnnotatedConstructor)this._constructors.get(j)).getAnnotated()); 
/*   0*/             }
/*   0*/        } 
/* 543*/        MemberKey key = new MemberKey(ctor);
/* 545*/        for (int i = 0; i < ctorCount; ) {
/* 546*/          if (!key.equals(ctorKeys[i])) {
/*   0*/            i++;
/*   0*/            continue;
/*   0*/          } 
/* 549*/          _addMixOvers(ctor, this._constructors.get(i), true);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addFactoryMixIns(Class<?> mixin) {
/* 558*/    MemberKey[] methodKeys = null;
/* 559*/    int methodCount = this._creatorMethods.size();
/* 561*/    for (Method m : mixin.getDeclaredMethods()) {
/* 562*/      if (Modifier.isStatic(m.getModifiers())) {
/* 565*/          if ((m.getParameterTypes()).length != 0) {
/* 568*/            if (methodKeys == null) {
/* 569*/              methodKeys = new MemberKey[methodCount];
/* 570*/              for (int j = 0; j < methodCount; j++) {
/* 571*/                  methodKeys[j] = new MemberKey(((AnnotatedMethod)this._creatorMethods.get(j)).getAnnotated()); 
/*   0*/                 }
/*   0*/            } 
/* 574*/            MemberKey key = new MemberKey(m);
/* 575*/            for (int i = 0; i < methodCount; ) {
/* 576*/              if (!key.equals(methodKeys[i])) {
/*   0*/                i++;
/*   0*/                continue;
/*   0*/              } 
/* 579*/              _addMixOvers(m, this._creatorMethods.get(i), true);
/*   0*/            } 
/*   0*/          }  
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMemberMethods(Class<?> cls, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns) {
/* 595*/    if (mixInCls != null) {
/* 596*/        _addMethodMixIns(cls, methods, mixInCls, mixIns); 
/*   0*/       }
/* 598*/    if (cls == null) {
/*   0*/        return; 
/*   0*/       }
/* 603*/    for (Method m : cls.getDeclaredMethods()) {
/* 604*/      if (_isIncludableMemberMethod(m)) {
/* 607*/        AnnotatedMethod old = methods.find(m);
/* 608*/        if (old == null) {
/* 609*/          AnnotatedMethod newM = _constructMethod(m);
/* 610*/          methods.add(newM);
/* 612*/          old = mixIns.remove(m);
/* 613*/          if (old != null) {
/* 614*/              _addMixOvers(old.getAnnotated(), newM, false); 
/*   0*/             }
/*   0*/        } else {
/* 620*/          _addMixUnders(m, old);
/* 629*/          if (old.getDeclaringClass().isInterface() && !m.getDeclaringClass().isInterface()) {
/* 630*/              methods.add(old.withMethod(m)); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMethodMixIns(Class<?> targetClass, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns) {
/* 639*/    List<Class<?>> parents = new ArrayList<Class<?>>();
/* 640*/    parents.add(mixInCls);
/* 641*/    ClassUtil.findSuperTypes(mixInCls, targetClass, parents);
/* 642*/    for (Class<?> mixin : parents) {
/* 643*/      for (Method m : mixin.getDeclaredMethods()) {
/* 644*/        if (_isIncludableMemberMethod(m)) {
/* 647*/          AnnotatedMethod am = methods.find(m);
/* 652*/          if (am != null) {
/* 653*/            _addMixUnders(m, am);
/*   0*/          } else {
/* 660*/            am = mixIns.find(m);
/* 661*/            if (am != null) {
/* 662*/              _addMixUnders(m, am);
/*   0*/            } else {
/* 664*/              mixIns.add(_constructMethod(m));
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Map<String, AnnotatedField> _findFields(Class<?> c, Map<String, AnnotatedField> fields) {
/* 684*/    Class<?> parent = c.getSuperclass();
/* 685*/    if (parent != null) {
/* 690*/      fields = _findFields(parent, fields);
/* 691*/      for (Field f : c.getDeclaredFields()) {
/* 693*/        if (_isIncludableField(f)) {
/* 701*/          if (fields == null) {
/* 702*/              fields = new LinkedHashMap<String, AnnotatedField>(); 
/*   0*/             }
/* 704*/          fields.put(f.getName(), _constructField(f));
/*   0*/        } 
/*   0*/      } 
/* 707*/      if (this._mixInResolver != null) {
/* 708*/        Class<?> mixin = this._mixInResolver.findMixInClassFor(c);
/* 709*/        if (mixin != null) {
/* 710*/            _addFieldMixIns(parent, mixin, fields); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 714*/    return fields;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addFieldMixIns(Class<?> targetClass, Class<?> mixInCls, Map<String, AnnotatedField> fields) {
/* 725*/    List<Class<?>> parents = new ArrayList<Class<?>>();
/* 726*/    parents.add(mixInCls);
/* 727*/    ClassUtil.findSuperTypes(mixInCls, targetClass, parents);
/* 728*/    for (Class<?> mixin : parents) {
/* 729*/      for (Field mixinField : mixin.getDeclaredFields()) {
/* 731*/        if (_isIncludableField(mixinField)) {
/* 734*/          String name = mixinField.getName();
/* 736*/          AnnotatedField maskedField = fields.get(name);
/* 737*/          if (maskedField != null) {
/* 738*/              _addOrOverrideAnnotations(maskedField, mixinField.getDeclaredAnnotations()); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedMethod _constructMethod(Method m) {
/* 756*/    if (this._annotationIntrospector == null) {
/* 757*/        return new AnnotatedMethod(this, m, _emptyAnnotationMap(), null); 
/*   0*/       }
/* 759*/    return new AnnotatedMethod(this, m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), null);
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedConstructor _constructConstructor(Constructor<?> ctor, boolean defaultCtor) {
/* 764*/    if (this._annotationIntrospector == null) {
/* 765*/        return new AnnotatedConstructor(this, ctor, _emptyAnnotationMap(), _emptyAnnotationMaps((ctor.getParameterTypes()).length)); 
/*   0*/       }
/* 767*/    if (defaultCtor) {
/* 768*/        return new AnnotatedConstructor(this, ctor, _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), null); 
/*   0*/       }
/* 770*/    Annotation[][] paramAnns = ctor.getParameterAnnotations();
/* 771*/    int paramCount = (ctor.getParameterTypes()).length;
/* 777*/    AnnotationMap[] resolvedAnnotations = null;
/* 778*/    if (paramCount != paramAnns.length) {
/* 782*/      Class<?> dc = ctor.getDeclaringClass();
/* 784*/      if (dc.isEnum() && paramCount == paramAnns.length + 2) {
/* 785*/        Annotation[][] old = paramAnns;
/* 786*/        paramAnns = new Annotation[old.length + 2][];
/* 787*/        System.arraycopy(old, 0, paramAnns, 2, old.length);
/* 788*/        resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
/* 789*/      } else if (dc.isMemberClass()) {
/* 791*/        if (paramCount == paramAnns.length + 1) {
/* 793*/          Annotation[][] old = paramAnns;
/* 794*/          paramAnns = new Annotation[old.length + 1][];
/* 795*/          System.arraycopy(old, 0, paramAnns, 1, old.length);
/* 796*/          resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
/*   0*/        } 
/*   0*/      } 
/* 799*/      if (resolvedAnnotations == null) {
/* 800*/          throw new IllegalStateException("Internal error: constructor for " + ctor.getDeclaringClass().getName() + " has mismatch: " + paramCount + " parameters; " + paramAnns.length + " sets of annotations"); 
/*   0*/         }
/*   0*/    } else {
/* 804*/      resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
/*   0*/    } 
/* 806*/    return new AnnotatedConstructor(this, ctor, _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), resolvedAnnotations);
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedMethod _constructCreatorMethod(Method m) {
/* 812*/    if (this._annotationIntrospector == null) {
/* 813*/        return new AnnotatedMethod(this, m, _emptyAnnotationMap(), _emptyAnnotationMaps((m.getParameterTypes()).length)); 
/*   0*/       }
/* 815*/    return new AnnotatedMethod(this, m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), _collectRelevantAnnotations(m.getParameterAnnotations()));
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedField _constructField(Field f) {
/* 821*/    if (this._annotationIntrospector == null) {
/* 822*/        return new AnnotatedField(this, f, _emptyAnnotationMap()); 
/*   0*/       }
/* 824*/    return new AnnotatedField(this, f, _collectRelevantAnnotations(f.getDeclaredAnnotations()));
/*   0*/  }
/*   0*/  
/*   0*/  private AnnotationMap _emptyAnnotationMap() {
/* 828*/    return new AnnotationMap();
/*   0*/  }
/*   0*/  
/*   0*/  private AnnotationMap[] _emptyAnnotationMaps(int count) {
/* 832*/    if (count == 0) {
/* 833*/        return NO_ANNOTATION_MAPS; 
/*   0*/       }
/* 835*/    AnnotationMap[] maps = new AnnotationMap[count];
/* 836*/    for (int i = 0; i < count; i++) {
/* 837*/        maps[i] = _emptyAnnotationMap(); 
/*   0*/       }
/* 839*/    return maps;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _isIncludableMemberMethod(Method m) {
/* 850*/    if (Modifier.isStatic(m.getModifiers())) {
/* 851*/        return false; 
/*   0*/       }
/* 857*/    if (m.isSynthetic() || m.isBridge()) {
/* 858*/        return false; 
/*   0*/       }
/* 861*/    int pcount = (m.getParameterTypes()).length;
/* 862*/    return (pcount <= 2);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _isIncludableField(Field f) {
/* 870*/    if (f.isSynthetic()) {
/* 871*/        return false; 
/*   0*/       }
/* 874*/    int mods = f.getModifiers();
/* 875*/    if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) {
/* 876*/        return false; 
/*   0*/       }
/* 878*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotationMap[] _collectRelevantAnnotations(Annotation[][] anns) {
/* 889*/    int len = anns.length;
/* 890*/    AnnotationMap[] result = new AnnotationMap[len];
/* 891*/    for (int i = 0; i < len; i++) {
/* 892*/        result[i] = _collectRelevantAnnotations(anns[i]); 
/*   0*/       }
/* 894*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotationMap _collectRelevantAnnotations(Annotation[] anns) {
/* 899*/    return _addAnnotationsIfNotPresent(new AnnotationMap(), anns);
/*   0*/  }
/*   0*/  
/*   0*/  private AnnotationMap _addAnnotationsIfNotPresent(AnnotationMap result, Annotation[] anns) {
/* 908*/    if (anns != null) {
/* 909*/      List<Annotation> fromBundles = null;
/* 910*/      for (Annotation ann : anns) {
/* 912*/        boolean wasNotPresent = result.addIfNotPresent(ann);
/* 913*/        if (wasNotPresent && _isAnnotationBundle(ann)) {
/* 914*/            fromBundles = _addFromBundle(ann, fromBundles); 
/*   0*/           }
/*   0*/      } 
/* 917*/      if (fromBundles != null) {
/* 918*/          _addAnnotationsIfNotPresent(result, fromBundles.<Annotation>toArray(new Annotation[fromBundles.size()])); 
/*   0*/         }
/*   0*/    } 
/* 921*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private List<Annotation> _addFromBundle(Annotation bundle, List<Annotation> result) {
/* 926*/    for (Annotation a : bundle.annotationType().getDeclaredAnnotations()) {
/* 928*/      if (!(a instanceof java.lang.annotation.Target) && !(a instanceof java.lang.annotation.Retention)) {
/* 931*/        if (result == null) {
/* 932*/            result = new ArrayList<Annotation>(); 
/*   0*/           }
/* 934*/        result.add(a);
/*   0*/      } 
/*   0*/    } 
/* 936*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private void _addAnnotationsIfNotPresent(AnnotatedMember target, Annotation[] anns) {
/* 941*/    if (anns != null) {
/* 942*/      List<Annotation> fromBundles = null;
/* 943*/      for (Annotation ann : anns) {
/* 944*/        boolean wasNotPresent = target.addIfNotPresent(ann);
/* 945*/        if (wasNotPresent && _isAnnotationBundle(ann)) {
/* 946*/            fromBundles = _addFromBundle(ann, fromBundles); 
/*   0*/           }
/*   0*/      } 
/* 949*/      if (fromBundles != null) {
/* 950*/          _addAnnotationsIfNotPresent(target, fromBundles.<Annotation>toArray(new Annotation[fromBundles.size()])); 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void _addOrOverrideAnnotations(AnnotatedMember target, Annotation[] anns) {
/* 957*/    if (anns != null) {
/* 958*/      List<Annotation> fromBundles = null;
/* 959*/      for (Annotation ann : anns) {
/* 960*/        boolean wasModified = target.addOrOverride(ann);
/* 961*/        if (!wasModified && _isAnnotationBundle(ann)) {
/* 962*/            fromBundles = _addFromBundle(ann, fromBundles); 
/*   0*/           }
/*   0*/      } 
/* 965*/      if (fromBundles != null) {
/* 966*/          _addOrOverrideAnnotations(target, fromBundles.<Annotation>toArray(new Annotation[fromBundles.size()])); 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMixOvers(Constructor<?> mixin, AnnotatedConstructor target, boolean addParamAnnotations) {
/* 978*/    _addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
/* 979*/    if (addParamAnnotations) {
/* 980*/      Annotation[][] pa = mixin.getParameterAnnotations();
/* 981*/      for (int i = 0, len = pa.length; i < len; i++) {
/* 982*/        for (Annotation a : pa[i]) {
/* 983*/            target.addOrOverrideParam(i, a); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMixOvers(Method mixin, AnnotatedMethod target, boolean addParamAnnotations) {
/* 996*/    _addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
/* 997*/    if (addParamAnnotations) {
/* 998*/      Annotation[][] pa = mixin.getParameterAnnotations();
/* 999*/      for (int i = 0, len = pa.length; i < len; i++) {
/*1000*/        for (Annotation a : pa[i]) {
/*1001*/            target.addOrOverrideParam(i, a); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMixUnders(Method src, AnnotatedMethod target) {
/*1012*/    _addAnnotationsIfNotPresent(target, src.getDeclaredAnnotations());
/*   0*/  }
/*   0*/  
/*   0*/  private final boolean _isAnnotationBundle(Annotation ann) {
/*1016*/    return (this._annotationIntrospector != null && this._annotationIntrospector.isAnnotationBundle(ann));
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1027*/    return "[AnnotedClass " + this._class.getName() + "]";
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*1032*/    return this._class.getName().hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/*1037*/    if (o == this) {
/*1037*/        return true; 
/*   0*/       }
/*1038*/    if (o == null || o.getClass() != getClass()) {
/*1038*/        return false; 
/*   0*/       }
/*1039*/    return (((AnnotatedClass)o)._class == this._class);
/*   0*/  }
/*   0*/}
