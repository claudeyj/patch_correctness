/*   0*/package org.jfree.data.general;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.InvalidObjectException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectInputValidation;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.EventListener;
/*   0*/import java.util.List;
/*   0*/import javax.swing.event.EventListenerList;
/*   0*/
/*   0*/public abstract class AbstractDataset implements Dataset, Cloneable, Serializable, ObjectInputValidation {
/*   0*/  private static final long serialVersionUID = 1918768939869230744L;
/*   0*/  
/*  95*/  private DatasetGroup group = new DatasetGroup();
/*   0*/  
/*  96*/  private transient EventListenerList listenerList = new EventListenerList();
/*   0*/  
/*   0*/  public DatasetGroup getGroup() {
/* 107*/    return this.group;
/*   0*/  }
/*   0*/  
/*   0*/  public void setGroup(DatasetGroup group) {
/* 118*/    if (group == null)
/* 119*/      throw new IllegalArgumentException("Null 'group' argument."); 
/* 121*/    this.group = group;
/*   0*/  }
/*   0*/  
/*   0*/  public void addChangeListener(DatasetChangeListener listener) {
/* 132*/    this.listenerList.add(DatasetChangeListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChangeListener(DatasetChangeListener listener) {
/* 144*/    this.listenerList.remove(DatasetChangeListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasListener(EventListener listener) {
/* 160*/    List list = Arrays.asList(this.listenerList.getListenerList());
/* 161*/    return list.contains(listener);
/*   0*/  }
/*   0*/  
/*   0*/  protected void fireDatasetChanged() {
/* 170*/    notifyListeners(new DatasetChangeEvent(this, this));
/*   0*/  }
/*   0*/  
/*   0*/  protected void notifyListeners(DatasetChangeEvent event) {
/* 184*/    Object[] listeners = this.listenerList.getListenerList();
/* 185*/    for (int i = listeners.length - 2; i >= 0; i -= 2) {
/* 186*/      if (listeners[i] == DatasetChangeListener.class)
/* 187*/        ((DatasetChangeListener)listeners[i + 1]).datasetChanged(event); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/* 205*/    AbstractDataset clone = (AbstractDataset)super.clone();
/* 206*/    clone.listenerList = new EventListenerList();
/* 207*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream stream) throws IOException {
/* 218*/    stream.defaultWriteObject();
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 231*/    stream.defaultReadObject();
/* 232*/    this.listenerList = new EventListenerList();
/* 233*/    stream.registerValidation(this, 10);
/*   0*/  }
/*   0*/  
/*   0*/  public void validateObject() throws InvalidObjectException {
/* 255*/    fireDatasetChanged();
/*   0*/  }
/*   0*/}
