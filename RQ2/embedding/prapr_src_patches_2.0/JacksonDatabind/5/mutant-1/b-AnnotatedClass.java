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
/*   0*/import java.util.LinkedList;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public final class AnnotatedClass extends Annotated {
/*  15*/  private static final AnnotationMap[] NO_ANNOTATION_MAPS = new AnnotationMap[0];
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
/* 115*/    this._class = cls;
/* 116*/    this._superTypes = superTypes;
/* 117*/    this._annotationIntrospector = aintr;
/* 118*/    this._mixInResolver = mir;
/* 119*/    this._primaryMixIn = (this._mixInResolver == null) ? null : this._mixInResolver.findMixInClassFor(this._class);
/* 121*/    this._classAnnotations = classAnnotations;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedClass withAnnotations(AnnotationMap ann) {
/* 126*/    return new AnnotatedClass(this._class, this._superTypes, this._annotationIntrospector, this._mixInResolver, ann);
/*   0*/  }
/*   0*/  
/*   0*/  public static AnnotatedClass construct(Class<?> cls, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir) {
/* 138*/    return new AnnotatedClass(cls, ClassUtil.findSuperTypes(cls, null), aintr, mir, null);
/*   0*/  }
/*   0*/  
/*   0*/  public static AnnotatedClass constructWithoutSuperTypes(Class<?> cls, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir) {
/* 150*/    return new AnnotatedClass(cls, Collections.emptyList(), aintr, mir, null);
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getAnnotated() {
/* 161*/    return this._class;
/*   0*/  }
/*   0*/  
/*   0*/  public int getModifiers() {
/* 164*/    return this._class.getModifiers();
/*   0*/  }
/*   0*/  
/*   0*/  public String getName() {
/* 167*/    return this._class.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public <A extends Annotation> A getAnnotation(Class<A> acls) {
/* 172*/    if (this._classAnnotations == null) {
/* 173*/        resolveClassAnnotations(); 
/*   0*/       }
/* 175*/    return this._classAnnotations.get(acls);
/*   0*/  }
/*   0*/  
/*   0*/  public Type getGenericType() {
/* 180*/    return this._class;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getRawType() {
/* 185*/    return this._class;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<Annotation> annotations() {
/* 190*/    if (this._classAnnotations == null) {
/* 191*/        resolveClassAnnotations(); 
/*   0*/       }
/* 193*/    return this._classAnnotations.annotations();
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotationMap getAllAnnotations() {
/* 198*/    if (this._classAnnotations == null) {
/* 199*/        resolveClassAnnotations(); 
/*   0*/       }
/* 201*/    return this._classAnnotations;
/*   0*/  }
/*   0*/  
/*   0*/  public Annotations getAnnotations() {
/* 211*/    if (this._classAnnotations == null) {
/* 212*/        resolveClassAnnotations(); 
/*   0*/       }
/* 214*/    return this._classAnnotations;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasAnnotations() {
/* 218*/    if (this._classAnnotations == null) {
/* 219*/        resolveClassAnnotations(); 
/*   0*/       }
/* 221*/    return (this._classAnnotations.size() > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedConstructor getDefaultConstructor() {
/* 226*/    if (!this._creatorsResolved) {
/* 227*/        resolveCreators(); 
/*   0*/       }
/* 229*/    return this._defaultConstructor;
/*   0*/  }
/*   0*/  
/*   0*/  public List<AnnotatedConstructor> getConstructors() {
/* 234*/    if (!this._creatorsResolved) {
/* 235*/        resolveCreators(); 
/*   0*/       }
/* 237*/    return this._constructors;
/*   0*/  }
/*   0*/  
/*   0*/  public List<AnnotatedMethod> getStaticMethods() {
/* 242*/    if (!this._creatorsResolved) {
/* 243*/        resolveCreators(); 
/*   0*/       }
/* 245*/    return this._creatorMethods;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<AnnotatedMethod> memberMethods() {
/* 250*/    if (this._memberMethods == null) {
/* 251*/        resolveMemberMethods(); 
/*   0*/       }
/* 253*/    return this._memberMethods;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMemberMethodCount() {
/* 258*/    if (this._memberMethods == null) {
/* 259*/        resolveMemberMethods(); 
/*   0*/       }
/* 261*/    return this._memberMethods.size();
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
/* 266*/    if (this._memberMethods == null) {
/* 267*/        resolveMemberMethods(); 
/*   0*/       }
/* 269*/    return this._memberMethods.find(name, paramTypes);
/*   0*/  }
/*   0*/  
/*   0*/  public int getFieldCount() {
/* 273*/    if (this._fields == null) {
/* 274*/        resolveFields(); 
/*   0*/       }
/* 276*/    return this._fields.size();
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<AnnotatedField> fields() {
/* 281*/    if (this._fields == null) {
/* 282*/        resolveFields(); 
/*   0*/       }
/* 284*/    return this._fields;
/*   0*/  }
/*   0*/  
/*   0*/  private void resolveClassAnnotations() {
/* 300*/    this._classAnnotations = new AnnotationMap();
/* 302*/    if (this._annotationIntrospector != null) {
/* 304*/      if (this._primaryMixIn != null) {
/* 305*/          _addClassMixIns(this._classAnnotations, this._class, this._primaryMixIn); 
/*   0*/         }
/* 308*/      _addAnnotationsIfNotPresent(this._classAnnotations, this._class.getDeclaredAnnotations());
/* 311*/      for (Class<?> cls : this._superTypes) {
/* 313*/        _addClassMixIns(this._classAnnotations, cls);
/* 314*/        _addAnnotationsIfNotPresent(this._classAnnotations, cls.getDeclaredAnnotations());
/*   0*/      } 
/* 323*/      _addClassMixIns(this._classAnnotations, Object.class);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void resolveCreators() {
/* 334*/    List<AnnotatedConstructor> constructors = null;
/* 335*/    Constructor[] arrayOfConstructor = (Constructor[])this._class.getDeclaredConstructors();
/* 336*/    for (Constructor<?> ctor : arrayOfConstructor) {
/* 337*/      if ((ctor.getParameterTypes()).length == 0) {
/* 338*/        this._defaultConstructor = _constructConstructor(ctor, true);
/*   0*/      } else {
/* 340*/        if (constructors == null) {
/* 341*/            constructors = new ArrayList<AnnotatedConstructor>(Math.max(10, arrayOfConstructor.length)); 
/*   0*/           }
/* 343*/        constructors.add(_constructConstructor(ctor, false));
/*   0*/      } 
/*   0*/    } 
/* 346*/    if (constructors == null) {
/* 347*/      this._constructors = Collections.emptyList();
/*   0*/    } else {
/* 349*/      this._constructors = constructors;
/*   0*/    } 
/* 352*/    if (this._primaryMixIn != null && (
/* 353*/      this._defaultConstructor != null || !this._constructors.isEmpty())) {
/* 354*/        _addConstructorMixIns(this._primaryMixIn); 
/*   0*/       }
/* 363*/    if (this._annotationIntrospector != null) {
/* 364*/      if (this._defaultConstructor != null && 
/* 365*/        this._annotationIntrospector.hasIgnoreMarker(this._defaultConstructor)) {
/* 366*/          this._defaultConstructor = null; 
/*   0*/         }
/* 369*/      if (this._constructors != null) {
/* 371*/          for (int i = this._constructors.size(); --i >= 0;) {
/* 372*/            if (this._annotationIntrospector.hasIgnoreMarker(this._constructors.get(i))) {
/* 373*/                this._constructors.remove(i); 
/*   0*/               }
/*   0*/          }  
/*   0*/         }
/*   0*/    } 
/* 378*/    List<AnnotatedMethod> creatorMethods = null;
/* 381*/    for (Method m : this._class.getDeclaredMethods()) {
/* 382*/      if (Modifier.isStatic(m.getModifiers())) {
/* 387*/        if (creatorMethods == null) {
/* 388*/            creatorMethods = new ArrayList<AnnotatedMethod>(8); 
/*   0*/           }
/* 390*/        creatorMethods.add(_constructCreatorMethod(m));
/*   0*/      } 
/*   0*/    } 
/* 392*/    if (creatorMethods == null) {
/* 393*/      this._creatorMethods = Collections.emptyList();
/*   0*/    } else {
/* 395*/      this._creatorMethods = creatorMethods;
/* 397*/      if (this._primaryMixIn != null) {
/* 398*/          _addFactoryMixIns(this._primaryMixIn); 
/*   0*/         }
/* 401*/      if (this._annotationIntrospector != null) {
/* 403*/          for (int i = this._creatorMethods.size(); --i >= 0;) {
/* 404*/            if (this._annotationIntrospector.hasIgnoreMarker(this._creatorMethods.get(i))) {
/* 405*/                this._creatorMethods.remove(i); 
/*   0*/               }
/*   0*/          }  
/*   0*/         }
/*   0*/    } 
/* 410*/    this._creatorsResolved = true;
/*   0*/  }
/*   0*/  
/*   0*/  private void resolveMemberMethods() {
/* 421*/    this._memberMethods = new AnnotatedMethodMap();
/* 422*/    AnnotatedMethodMap mixins = new AnnotatedMethodMap();
/* 424*/    _addMemberMethods(this._class, this._memberMethods, this._primaryMixIn, mixins);
/* 427*/    for (Class<?> cls : this._superTypes) {
/* 428*/      Class<?> mixin = (this._mixInResolver == null) ? null : this._mixInResolver.findMixInClassFor(cls);
/* 429*/      _addMemberMethods(cls, this._memberMethods, mixin, mixins);
/*   0*/    } 
/* 432*/    if (this._mixInResolver != null) {
/* 433*/      Class<?> mixin = this._mixInResolver.findMixInClassFor(Object.class);
/* 434*/      if (mixin != null) {
/* 435*/          _addMethodMixIns(this._class, this._memberMethods, mixin, mixins); 
/*   0*/         }
/*   0*/    } 
/* 445*/    if (this._annotationIntrospector != null && 
/* 446*/      !mixins.isEmpty()) {
/* 447*/      Iterator<AnnotatedMethod> it = mixins.iterator();
/* 448*/      while (it.hasNext()) {
/* 449*/        AnnotatedMethod mixIn = it.next();
/*   0*/        try {
/* 451*/          Method m = Object.class.getDeclaredMethod(mixIn.getName(), mixIn.getRawParameterTypes());
/* 452*/          if (m != null) {
/* 453*/            AnnotatedMethod am = _constructMethod(m);
/* 454*/            _addMixOvers(mixIn.getAnnotated(), am, false);
/* 455*/            this._memberMethods.add(am);
/*   0*/          } 
/* 457*/        } catch (Exception exception) {}
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void resolveFields() {
/* 470*/    Map<String, AnnotatedField> foundFields = _findFields(this._class, null);
/* 471*/    if (foundFields == null || foundFields.size() == 0) {
/* 472*/      this._fields = Collections.emptyList();
/*   0*/    } else {
/* 474*/      this._fields = new ArrayList<AnnotatedField>(foundFields.size());
/* 475*/      this._fields.addAll(foundFields.values());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addClassMixIns(AnnotationMap annotations, Class<?> toMask) {
/* 493*/    if (this._mixInResolver != null) {
/* 494*/        _addClassMixIns(annotations, toMask, this._mixInResolver.findMixInClassFor(toMask)); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addClassMixIns(AnnotationMap annotations, Class<?> toMask, Class<?> mixin) {
/* 501*/    if (mixin == null) {
/*   0*/        return; 
/*   0*/       }
/* 505*/    _addAnnotationsIfNotPresent(annotations, mixin.getDeclaredAnnotations());
/* 514*/    for (Class<?> parent : ClassUtil.findSuperTypes(mixin, toMask)) {
/* 515*/        _addAnnotationsIfNotPresent(annotations, parent.getDeclaredAnnotations()); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addConstructorMixIns(Class<?> mixin) {
/* 527*/    MemberKey[] ctorKeys = null;
/* 528*/    int ctorCount = (this._constructors == null) ? 0 : this._constructors.size();
/* 529*/    for (Constructor<?> ctor : mixin.getDeclaredConstructors()) {
/* 530*/      if ((ctor.getParameterTypes()).length == 0) {
/* 531*/        if (this._defaultConstructor != null) {
/* 532*/            _addMixOvers(ctor, this._defaultConstructor, false); 
/*   0*/           }
/*   0*/      } else {
/* 535*/        if (ctorKeys == null) {
/* 536*/          ctorKeys = new MemberKey[ctorCount];
/* 537*/          for (int j = 0; j < ctorCount; j++) {
/* 538*/              ctorKeys[j] = new MemberKey(((AnnotatedConstructor)this._constructors.get(j)).getAnnotated()); 
/*   0*/             }
/*   0*/        } 
/* 541*/        MemberKey key = new MemberKey(ctor);
/* 543*/        for (int i = 0; i < ctorCount; ) {
/* 544*/          if (!key.equals(ctorKeys[i])) {
/*   0*/            i++;
/*   0*/            continue;
/*   0*/          } 
/* 547*/          _addMixOvers(ctor, this._constructors.get(i), true);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addFactoryMixIns(Class<?> mixin) {
/* 556*/    MemberKey[] methodKeys = null;
/* 557*/    int methodCount = this._creatorMethods.size();
/* 559*/    for (Method m : mixin.getDeclaredMethods()) {
/* 560*/      if (Modifier.isStatic(m.getModifiers())) {
/* 563*/          if ((m.getParameterTypes()).length != 0) {
/* 566*/            if (methodKeys == null) {
/* 567*/              methodKeys = new MemberKey[methodCount];
/* 568*/              for (int j = 0; j < methodCount; j++) {
/* 569*/                  methodKeys[j] = new MemberKey(((AnnotatedMethod)this._creatorMethods.get(j)).getAnnotated()); 
/*   0*/                 }
/*   0*/            } 
/* 572*/            MemberKey key = new MemberKey(m);
/* 573*/            for (int i = 0; i < methodCount; ) {
/* 574*/              if (!key.equals(methodKeys[i])) {
/*   0*/                i++;
/*   0*/                continue;
/*   0*/              } 
/* 577*/              _addMixOvers(m, this._creatorMethods.get(i), true);
/*   0*/            } 
/*   0*/          }  
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMemberMethods(Class<?> cls, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns) {
/* 593*/    if (mixInCls != null) {
/* 594*/        _addMethodMixIns(cls, methods, mixInCls, mixIns); 
/*   0*/       }
/* 596*/    if (cls == null) {
/*   0*/        return; 
/*   0*/       }
/* 601*/    for (Method m : cls.getDeclaredMethods()) {
/* 602*/      if (_isIncludableMemberMethod(m)) {
/* 605*/        AnnotatedMethod old = methods.find(m);
/* 606*/        if (old == null) {
/* 607*/          AnnotatedMethod newM = _constructMethod(m);
/* 608*/          methods.add(newM);
/* 610*/          old = mixIns.remove(m);
/* 611*/          if (old != null) {
/* 612*/              _addMixOvers(old.getAnnotated(), newM, false); 
/*   0*/             }
/*   0*/        } else {
/* 618*/          _addMixUnders(m, old);
/* 627*/          if (old.getDeclaringClass().isInterface() && !m.getDeclaringClass().isInterface()) {
/* 628*/              methods.add(old.withMethod(m)); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMethodMixIns(Class<?> targetClass, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns) {
/* 637*/    List<Class<?>> parents = new ArrayList<Class<?>>();
/* 638*/    parents.add(mixInCls);
/* 639*/    ClassUtil.findSuperTypes(mixInCls, this._class, parents);
/* 640*/    for (Class<?> mixin : parents) {
/* 641*/      for (Method m : mixin.getDeclaredMethods()) {
/* 642*/        if (_isIncludableMemberMethod(m)) {
/* 645*/          AnnotatedMethod am = methods.find(m);
/* 650*/          if (am != null) {
/* 651*/            _addMixUnders(m, am);
/*   0*/          } else {
/* 658*/            mixIns.add(_constructMethod(m));
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Map<String, AnnotatedField> _findFields(Class<?> c, Map<String, AnnotatedField> fields) {
/* 677*/    Class<?> parent = c.getSuperclass();
/* 678*/    if (parent != null) {
/* 683*/      fields = _findFields(parent, fields);
/* 684*/      for (Field f : c.getDeclaredFields()) {
/* 686*/        if (_isIncludableField(f)) {
/* 694*/          if (fields == null) {
/* 695*/              fields = new LinkedHashMap<String, AnnotatedField>(); 
/*   0*/             }
/* 697*/          fields.put(f.getName(), _constructField(f));
/*   0*/        } 
/*   0*/      } 
/* 700*/      if (this._mixInResolver != null) {
/* 701*/        Class<?> mixin = this._mixInResolver.findMixInClassFor(c);
/* 702*/        if (mixin != null) {
/* 703*/            _addFieldMixIns(parent, mixin, fields); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 707*/    return fields;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addFieldMixIns(Class<?> targetClass, Class<?> mixInCls, Map<String, AnnotatedField> fields) {
/* 718*/    List<Class<?>> parents = new ArrayList<Class<?>>();
/* 719*/    parents.add(mixInCls);
/* 720*/    ClassUtil.findSuperTypes(mixInCls, targetClass, parents);
/* 721*/    for (Class<?> mixin : parents) {
/* 722*/      for (Field mixinField : mixin.getDeclaredFields()) {
/* 724*/        if (_isIncludableField(mixinField)) {
/* 727*/          String name = mixinField.getName();
/* 729*/          AnnotatedField maskedField = fields.get(name);
/* 730*/          if (maskedField != null) {
/* 731*/              _addOrOverrideAnnotations(maskedField, mixinField.getDeclaredAnnotations()); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedMethod _constructMethod(Method m) {
/* 749*/    if (this._annotationIntrospector == null) {
/* 750*/        return new AnnotatedMethod(m, _emptyAnnotationMap(), null); 
/*   0*/       }
/* 752*/    return new AnnotatedMethod(m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), null);
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedConstructor _constructConstructor(Constructor<?> ctor, boolean defaultCtor) {
/* 757*/    if (this._annotationIntrospector == null) {
/* 758*/        return new AnnotatedConstructor(ctor, _emptyAnnotationMap(), _emptyAnnotationMaps((ctor.getParameterTypes()).length)); 
/*   0*/       }
/* 760*/    if (defaultCtor) {
/* 761*/        return new AnnotatedConstructor(ctor, _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), null); 
/*   0*/       }
/* 763*/    Annotation[][] paramAnns = ctor.getParameterAnnotations();
/* 764*/    int paramCount = (ctor.getParameterTypes()).length;
/* 770*/    AnnotationMap[] resolvedAnnotations = null;
/* 771*/    if (paramCount != paramAnns.length) {
/* 775*/      Class<?> dc = ctor.getDeclaringClass();
/* 777*/      if (dc.isEnum() && paramCount == paramAnns.length + 2) {
/* 778*/        Annotation[][] old = paramAnns;
/* 779*/        paramAnns = new Annotation[old.length + 2][];
/* 780*/        System.arraycopy(old, 0, paramAnns, 2, old.length);
/* 781*/        resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
/* 782*/      } else if (dc.isMemberClass()) {
/* 784*/        if (paramCount == paramAnns.length + 1) {
/* 786*/          Annotation[][] old = paramAnns;
/* 787*/          paramAnns = new Annotation[old.length + 1][];
/* 788*/          System.arraycopy(old, 0, paramAnns, 1, old.length);
/* 789*/          resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
/*   0*/        } 
/*   0*/      } 
/* 792*/      if (resolvedAnnotations == null) {
/* 793*/          throw new IllegalStateException("Internal error: constructor for " + ctor.getDeclaringClass().getName() + " has mismatch: " + paramCount + " parameters; " + paramAnns.length + " sets of annotations"); 
/*   0*/         }
/*   0*/    } else {
/* 797*/      resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
/*   0*/    } 
/* 799*/    return new AnnotatedConstructor(ctor, _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), resolvedAnnotations);
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedMethod _constructCreatorMethod(Method m) {
/* 805*/    if (this._annotationIntrospector == null) {
/* 806*/        return new AnnotatedMethod(m, _emptyAnnotationMap(), _emptyAnnotationMaps((m.getParameterTypes()).length)); 
/*   0*/       }
/* 808*/    return new AnnotatedMethod(m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), _collectRelevantAnnotations(m.getParameterAnnotations()));
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotatedField _constructField(Field f) {
/* 814*/    if (this._annotationIntrospector == null) {
/* 815*/        return new AnnotatedField(f, _emptyAnnotationMap()); 
/*   0*/       }
/* 817*/    return new AnnotatedField(f, _collectRelevantAnnotations(f.getDeclaredAnnotations()));
/*   0*/  }
/*   0*/  
/*   0*/  private AnnotationMap _emptyAnnotationMap() {
/* 821*/    return new AnnotationMap();
/*   0*/  }
/*   0*/  
/*   0*/  private AnnotationMap[] _emptyAnnotationMaps(int count) {
/* 825*/    if (count == 0) {
/* 826*/        return NO_ANNOTATION_MAPS; 
/*   0*/       }
/* 828*/    AnnotationMap[] maps = new AnnotationMap[count];
/* 829*/    for (int i = 0; i < count; i++) {
/* 830*/        maps[i] = _emptyAnnotationMap(); 
/*   0*/       }
/* 832*/    return maps;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _isIncludableMemberMethod(Method m) {
/* 843*/    if (Modifier.isStatic(m.getModifiers())) {
/* 844*/        return false; 
/*   0*/       }
/* 850*/    if (m.isSynthetic() || m.isBridge()) {
/* 851*/        return false; 
/*   0*/       }
/* 854*/    int pcount = (m.getParameterTypes()).length;
/* 855*/    return (pcount <= 2);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean _isIncludableField(Field f) {
/* 863*/    if (f.isSynthetic()) {
/* 864*/        return false; 
/*   0*/       }
/* 867*/    int mods = f.getModifiers();
/* 868*/    if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) {
/* 869*/        return false; 
/*   0*/       }
/* 871*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotationMap[] _collectRelevantAnnotations(Annotation[][] anns) {
/* 882*/    int len = anns.length;
/* 883*/    AnnotationMap[] result = new AnnotationMap[len];
/* 884*/    for (int i = 0; i < len; i++) {
/* 885*/        result[i] = _collectRelevantAnnotations(anns[i]); 
/*   0*/       }
/* 887*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected AnnotationMap _collectRelevantAnnotations(Annotation[] anns) {
/* 892*/    AnnotationMap annMap = new AnnotationMap();
/* 893*/    _addAnnotationsIfNotPresent(annMap, anns);
/* 894*/    return annMap;
/*   0*/  }
/*   0*/  
/*   0*/  private void _addAnnotationsIfNotPresent(AnnotationMap result, Annotation[] anns) {
/* 903*/    if (anns != null) {
/* 904*/      List<Annotation[]> bundles = null;
/* 905*/      for (Annotation ann : anns) {
/* 906*/        if (_isAnnotationBundle(ann)) {
/* 907*/          if (bundles == null) {
/* 908*/              bundles = (List)new LinkedList<Annotation>(); 
/*   0*/             }
/* 910*/          bundles.add(ann.annotationType().getDeclaredAnnotations());
/*   0*/        } else {
/* 912*/          result.addIfNotPresent(ann);
/*   0*/        } 
/*   0*/      } 
/* 915*/      if (bundles != null) {
/* 916*/          for (Annotation[] annotations : bundles) {
/* 917*/              _addAnnotationsIfNotPresent(result, annotations); 
/*   0*/             } 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void _addAnnotationsIfNotPresent(AnnotatedMember target, Annotation[] anns) {
/* 925*/    if (anns != null) {
/* 926*/      List<Annotation[]> bundles = null;
/* 927*/      for (Annotation ann : anns) {
/* 928*/        if (_isAnnotationBundle(ann)) {
/* 929*/          if (bundles == null) {
/* 930*/              bundles = (List)new LinkedList<Annotation>(); 
/*   0*/             }
/* 932*/          bundles.add(ann.annotationType().getDeclaredAnnotations());
/*   0*/        } else {
/* 934*/          target.addIfNotPresent(ann);
/*   0*/        } 
/*   0*/      } 
/* 937*/      if (bundles != null) {
/* 938*/          for (Annotation[] annotations : bundles) {
/* 939*/              _addAnnotationsIfNotPresent(target, annotations); 
/*   0*/             } 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void _addOrOverrideAnnotations(AnnotatedMember target, Annotation[] anns) {
/* 947*/    if (anns != null) {
/* 948*/      List<Annotation[]> bundles = null;
/* 949*/      for (Annotation ann : anns) {
/* 950*/        if (_isAnnotationBundle(ann)) {
/* 951*/          if (bundles == null) {
/* 952*/              bundles = (List)new LinkedList<Annotation>(); 
/*   0*/             }
/* 954*/          bundles.add(ann.annotationType().getDeclaredAnnotations());
/*   0*/        } else {
/* 956*/          target.addOrOverride(ann);
/*   0*/        } 
/*   0*/      } 
/* 959*/      if (bundles != null) {
/* 960*/          for (Annotation[] annotations : bundles) {
/* 961*/              _addOrOverrideAnnotations(target, annotations); 
/*   0*/             } 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMixOvers(Constructor<?> mixin, AnnotatedConstructor target, boolean addParamAnnotations) {
/* 974*/    _addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
/* 975*/    if (addParamAnnotations) {
/* 976*/      Annotation[][] pa = mixin.getParameterAnnotations();
/* 977*/      for (int i = 0, len = pa.length; i < len; i++) {
/* 978*/        for (Annotation a : pa[i]) {
/* 979*/            target.addOrOverrideParam(i, a); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMixOvers(Method mixin, AnnotatedMethod target, boolean addParamAnnotations) {
/* 992*/    _addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
/* 993*/    if (addParamAnnotations) {
/* 994*/      Annotation[][] pa = mixin.getParameterAnnotations();
/* 995*/      for (int i = 0, len = pa.length; i < len; i++) {
/* 996*/        for (Annotation a : pa[i]) {
/* 997*/            target.addOrOverrideParam(i, a); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMixUnders(Method src, AnnotatedMethod target) {
/*1008*/    _addAnnotationsIfNotPresent(target, src.getDeclaredAnnotations());
/*   0*/  }
/*   0*/  
/*   0*/  private final boolean _isAnnotationBundle(Annotation ann) {
/*1013*/    return (this._annotationIntrospector != null && this._annotationIntrospector.isAnnotationBundle(ann));
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1025*/    return "[AnnotedClass " + this._class.getName() + "]";
/*   0*/  }
/*   0*/}
