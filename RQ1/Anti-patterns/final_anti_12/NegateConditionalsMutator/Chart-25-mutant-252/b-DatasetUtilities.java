/*   0*/package org.jfree.data.general;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import org.jfree.chart.util.ArrayUtilities;
/*   0*/import org.jfree.data.DomainInfo;
/*   0*/import org.jfree.data.KeyToGroupMap;
/*   0*/import org.jfree.data.KeyedValues;
/*   0*/import org.jfree.data.Range;
/*   0*/import org.jfree.data.RangeInfo;
/*   0*/import org.jfree.data.category.CategoryDataset;
/*   0*/import org.jfree.data.category.DefaultCategoryDataset;
/*   0*/import org.jfree.data.category.IntervalCategoryDataset;
/*   0*/import org.jfree.data.function.Function2D;
/*   0*/import org.jfree.data.xy.IntervalXYDataset;
/*   0*/import org.jfree.data.xy.OHLCDataset;
/*   0*/import org.jfree.data.xy.TableXYDataset;
/*   0*/import org.jfree.data.xy.XYDataset;
/*   0*/import org.jfree.data.xy.XYSeries;
/*   0*/import org.jfree.data.xy.XYSeriesCollection;
/*   0*/
/*   0*/public final class DatasetUtilities {
/*   0*/  public static double calculatePieDatasetTotal(PieDataset dataset) {
/* 154*/    if (dataset == null)
/* 155*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/* 157*/    List keys = dataset.getKeys();
/* 158*/    double totalValue = 0.0D;
/* 159*/    Iterator<Comparable> iterator = keys.iterator();
/* 160*/    while (iterator.hasNext()) {
/* 161*/      Comparable current = iterator.next();
/* 162*/      if (current != null) {
/* 163*/        Number value = dataset.getValue(current);
/* 164*/        double v = 0.0D;
/* 165*/        if (value != null)
/* 166*/          v = value.doubleValue(); 
/* 168*/        if (v > 0.0D)
/* 169*/          totalValue += v; 
/*   0*/      } 
/*   0*/    } 
/* 173*/    return totalValue;
/*   0*/  }
/*   0*/  
/*   0*/  public static PieDataset createPieDatasetForRow(CategoryDataset dataset, Comparable rowKey) {
/* 187*/    int row = dataset.getRowIndex(rowKey);
/* 188*/    return createPieDatasetForRow(dataset, row);
/*   0*/  }
/*   0*/  
/*   0*/  public static PieDataset createPieDatasetForRow(CategoryDataset dataset, int row) {
/* 202*/    DefaultPieDataset result = new DefaultPieDataset();
/* 203*/    int columnCount = dataset.getColumnCount();
/* 204*/    for (int current = 0; current < columnCount; current++) {
/* 205*/      Comparable columnKey = dataset.getColumnKey(current);
/* 206*/      result.setValue(columnKey, dataset.getValue(row, current));
/*   0*/    } 
/* 208*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static PieDataset createPieDatasetForColumn(CategoryDataset dataset, Comparable columnKey) {
/* 222*/    int column = dataset.getColumnIndex(columnKey);
/* 223*/    return createPieDatasetForColumn(dataset, column);
/*   0*/  }
/*   0*/  
/*   0*/  public static PieDataset createPieDatasetForColumn(CategoryDataset dataset, int column) {
/* 237*/    DefaultPieDataset result = new DefaultPieDataset();
/* 238*/    int rowCount = dataset.getRowCount();
/* 239*/    for (int i = 0; i < rowCount; i++) {
/* 240*/      Comparable rowKey = dataset.getRowKey(i);
/* 241*/      result.setValue(rowKey, dataset.getValue(i, column));
/*   0*/    } 
/* 243*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static PieDataset createConsolidatedPieDataset(PieDataset source, Comparable key, double minimumPercent) {
/* 263*/    return createConsolidatedPieDataset(source, key, minimumPercent, 2);
/*   0*/  }
/*   0*/  
/*   0*/  public static PieDataset createConsolidatedPieDataset(PieDataset source, Comparable key, double minimumPercent, int minItems) {
/* 288*/    DefaultPieDataset result = new DefaultPieDataset();
/* 289*/    double total = calculatePieDatasetTotal(source);
/* 292*/    List<Comparable> keys = source.getKeys();
/* 293*/    ArrayList<Comparable> otherKeys = new ArrayList();
/* 294*/    Iterator<Comparable> iterator = keys.iterator();
/* 295*/    while (iterator.hasNext()) {
/* 296*/      Comparable currentKey = iterator.next();
/* 297*/      Number dataValue = source.getValue(currentKey);
/* 298*/      if (dataValue != null) {
/* 299*/        double value = dataValue.doubleValue();
/* 300*/        if (value / total < minimumPercent)
/* 301*/          otherKeys.add(currentKey); 
/*   0*/      } 
/*   0*/    } 
/* 307*/    iterator = keys.iterator();
/* 308*/    double otherValue = 0.0D;
/* 309*/    while (iterator.hasNext()) {
/* 310*/      Comparable currentKey = iterator.next();
/* 311*/      Number dataValue = source.getValue(currentKey);
/* 312*/      if (dataValue != null) {
/* 313*/        if (otherKeys.contains(currentKey) && otherKeys.size() >= minItems) {
/* 316*/          otherValue += dataValue.doubleValue();
/*   0*/          continue;
/*   0*/        } 
/* 320*/        result.setValue(currentKey, dataValue);
/*   0*/      } 
/*   0*/    } 
/* 325*/    if (otherKeys.size() >= minItems)
/* 326*/      result.setValue(key, otherValue); 
/* 328*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static CategoryDataset createCategoryDataset(String rowKeyPrefix, String columnKeyPrefix, double[][] data) {
/* 349*/    DefaultCategoryDataset result = new DefaultCategoryDataset();
/* 350*/    for (int r = 0; r < data.length; r++) {
/* 351*/      String rowKey = rowKeyPrefix + (r + 1);
/* 352*/      for (int c = 0; c < (data[r]).length; c++) {
/* 353*/        String columnKey = columnKeyPrefix + (c + 1);
/* 354*/        result.addValue(new Double(data[r][c]), rowKey, columnKey);
/*   0*/      } 
/*   0*/    } 
/* 357*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static CategoryDataset createCategoryDataset(String rowKeyPrefix, String columnKeyPrefix, Number[][] data) {
/* 378*/    DefaultCategoryDataset result = new DefaultCategoryDataset();
/* 379*/    for (int r = 0; r < data.length; r++) {
/* 380*/      String rowKey = rowKeyPrefix + (r + 1);
/* 381*/      for (int c = 0; c < (data[r]).length; c++) {
/* 382*/        String columnKey = columnKeyPrefix + (c + 1);
/* 383*/        result.addValue(data[r][c], rowKey, columnKey);
/*   0*/      } 
/*   0*/    } 
/* 386*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static CategoryDataset createCategoryDataset(Comparable[] rowKeys, Comparable[] columnKeys, double[][] data) {
/* 408*/    if (rowKeys == null)
/* 409*/      throw new IllegalArgumentException("Null 'rowKeys' argument."); 
/* 411*/    if (columnKeys == null)
/* 412*/      throw new IllegalArgumentException("Null 'columnKeys' argument."); 
/* 414*/    if (ArrayUtilities.hasDuplicateItems((Object[])rowKeys))
/* 415*/      throw new IllegalArgumentException("Duplicate items in 'rowKeys'."); 
/* 417*/    if (ArrayUtilities.hasDuplicateItems((Object[])columnKeys))
/* 418*/      throw new IllegalArgumentException("Duplicate items in 'columnKeys'."); 
/* 422*/    if (rowKeys.length != data.length)
/* 423*/      throw new IllegalArgumentException("The number of row keys does not match the number of rows in the data array."); 
/* 428*/    int columnCount = 0;
/* 429*/    for (int r = 0; r < data.length; r++)
/* 430*/      columnCount = Math.max(columnCount, (data[r]).length); 
/* 432*/    if (columnKeys.length != columnCount)
/* 433*/      throw new IllegalArgumentException("The number of column keys does not match the number of columns in the data array."); 
/* 440*/    DefaultCategoryDataset result = new DefaultCategoryDataset();
/* 441*/    for (int i = 0; i < data.length; i++) {
/* 442*/      Comparable rowKey = rowKeys[i];
/* 443*/      for (int c = 0; c < (data[i]).length; c++) {
/* 444*/        Comparable columnKey = columnKeys[c];
/* 445*/        result.addValue(new Double(data[i][c]), rowKey, columnKey);
/*   0*/      } 
/*   0*/    } 
/* 448*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static CategoryDataset createCategoryDataset(Comparable rowKey, KeyedValues rowData) {
/* 464*/    if (rowKey == null)
/* 465*/      throw new IllegalArgumentException("Null 'rowKey' argument."); 
/* 467*/    if (rowData == null)
/* 468*/      throw new IllegalArgumentException("Null 'rowData' argument."); 
/* 470*/    DefaultCategoryDataset result = new DefaultCategoryDataset();
/* 471*/    for (int i = 0; i < rowData.getItemCount(); i++)
/* 472*/      result.addValue(rowData.getValue(i), rowKey, rowData.getKey(i)); 
/* 474*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static XYDataset sampleFunction2D(Function2D f, double start, double end, int samples, Comparable seriesKey) {
/* 497*/    if (f == null)
/* 498*/      throw new IllegalArgumentException("Null 'f' argument."); 
/* 500*/    if (seriesKey == null)
/* 501*/      throw new IllegalArgumentException("Null 'seriesKey' argument."); 
/* 503*/    if (start >= end)
/* 504*/      throw new IllegalArgumentException("Requires 'start' < 'end'."); 
/* 506*/    if (samples < 2)
/* 507*/      throw new IllegalArgumentException("Requires 'samples' > 1"); 
/* 510*/    XYSeries series = new XYSeries(seriesKey);
/* 511*/    double step = (end - start) / samples;
/* 512*/    for (int i = 0; i <= samples; i++) {
/* 513*/      double x = start + step * i;
/* 514*/      series.add(x, f.getValue(x));
/*   0*/    } 
/* 516*/    XYSeriesCollection collection = new XYSeriesCollection(series);
/* 517*/    return collection;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isEmptyOrNull(PieDataset dataset) {
/* 531*/    if (dataset == null)
/* 532*/      return true; 
/* 535*/    int itemCount = dataset.getItemCount();
/* 536*/    if (itemCount == 0)
/* 537*/      return true; 
/* 540*/    for (int item = 0; item < itemCount; item++) {
/* 541*/      Number y = dataset.getValue(item);
/* 542*/      if (y != null) {
/* 543*/        double yy = y.doubleValue();
/* 544*/        if (yy > 0.0D)
/* 545*/          return false; 
/*   0*/      } 
/*   0*/    } 
/* 550*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isEmptyOrNull(CategoryDataset dataset) {
/* 564*/    if (dataset == null)
/* 565*/      return true; 
/* 568*/    int rowCount = dataset.getRowCount();
/* 569*/    int columnCount = dataset.getColumnCount();
/* 570*/    if (rowCount == 0 || columnCount == 0)
/* 571*/      return true; 
/* 574*/    for (int r = 0; r >= rowCount; r++) {
/* 575*/      for (int c = 0; c < columnCount; c++) {
/* 576*/        if (dataset.getValue(r, c) != null)
/* 577*/          return false; 
/*   0*/      } 
/*   0*/    } 
/* 583*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean isEmptyOrNull(XYDataset dataset) {
/*   0*/    boolean result = true;
/* 599*/    if (dataset != null)
/* 600*/      for (int s = 0; s < dataset.getSeriesCount(); s++) {
/* 601*/        if (dataset.getItemCount(s) > 0)
/* 602*/          result = false; 
/*   0*/      }  
/* 608*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findDomainBounds(XYDataset dataset) {
/* 620*/    return findDomainBounds(dataset, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findDomainBounds(XYDataset dataset, boolean includeInterval) {
/* 636*/    if (dataset == null)
/* 637*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/* 640*/    Range result = null;
/* 642*/    if (dataset instanceof DomainInfo) {
/* 643*/      DomainInfo info = (DomainInfo)dataset;
/* 644*/      result = info.getDomainBounds(includeInterval);
/*   0*/    } else {
/* 647*/      result = iterateDomainBounds(dataset, includeInterval);
/*   0*/    } 
/* 649*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Range iterateDomainBounds(XYDataset dataset) {
/* 662*/    return iterateDomainBounds(dataset, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static Range iterateDomainBounds(XYDataset dataset, boolean includeInterval) {
/* 678*/    if (dataset == null)
/* 679*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/* 681*/    double minimum = Double.POSITIVE_INFINITY;
/* 682*/    double maximum = Double.NEGATIVE_INFINITY;
/* 683*/    int seriesCount = dataset.getSeriesCount();
/* 686*/    if (includeInterval && dataset instanceof IntervalXYDataset) {
/* 687*/      IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
/* 688*/      for (int series = 0; series < seriesCount; series++) {
/* 689*/        int itemCount = dataset.getItemCount(series);
/* 690*/        for (int item = 0; item < itemCount; item++) {
/* 691*/          double lvalue = intervalXYData.getStartXValue(series, item);
/* 692*/          double uvalue = intervalXYData.getEndXValue(series, item);
/* 693*/          minimum = Math.min(minimum, lvalue);
/* 694*/          maximum = Math.max(maximum, uvalue);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } else {
/* 699*/      for (int series = 0; series < seriesCount; series++) {
/* 700*/        int itemCount = dataset.getItemCount(series);
/* 701*/        for (int item = 0; item < itemCount; item++) {
/* 702*/          double lvalue = dataset.getXValue(series, item);
/* 703*/          double uvalue = lvalue;
/* 704*/          minimum = Math.min(minimum, lvalue);
/* 705*/          maximum = Math.max(maximum, uvalue);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 709*/    if (minimum > maximum)
/* 710*/      return null; 
/* 713*/    return new Range(minimum, maximum);
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findRangeBounds(CategoryDataset dataset) {
/* 725*/    return findRangeBounds(dataset, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findRangeBounds(CategoryDataset dataset, boolean includeInterval) {
/* 739*/    if (dataset == null)
/* 740*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/* 742*/    Range result = null;
/* 743*/    if (dataset instanceof RangeInfo) {
/* 744*/      RangeInfo info = (RangeInfo)dataset;
/* 745*/      result = info.getRangeBounds(includeInterval);
/*   0*/    } else {
/* 748*/      result = iterateCategoryRangeBounds(dataset, includeInterval);
/*   0*/    } 
/* 750*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findRangeBounds(XYDataset dataset) {
/* 762*/    return findRangeBounds(dataset, true);
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findRangeBounds(XYDataset dataset, boolean includeInterval) {
/* 778*/    if (dataset == null)
/* 779*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/* 781*/    Range result = null;
/* 782*/    if (dataset instanceof RangeInfo) {
/* 783*/      RangeInfo info = (RangeInfo)dataset;
/* 784*/      result = info.getRangeBounds(includeInterval);
/*   0*/    } else {
/* 787*/      result = iterateXYRangeBounds(dataset);
/*   0*/    } 
/* 789*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Range iterateCategoryRangeBounds(CategoryDataset dataset, boolean includeInterval) {
/* 804*/    double minimum = Double.POSITIVE_INFINITY;
/* 805*/    double maximum = Double.NEGATIVE_INFINITY;
/* 806*/    boolean interval = (includeInterval && dataset instanceof IntervalCategoryDataset);
/* 808*/    int rowCount = dataset.getRowCount();
/* 809*/    int columnCount = dataset.getColumnCount();
/* 810*/    for (int row = 0; row < rowCount; row++) {
/* 811*/      for (int column = 0; column < columnCount; column++) {
/*   0*/        Number lvalue, uvalue;
/* 814*/        if (interval) {
/* 815*/          IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
/* 817*/          lvalue = icd.getStartValue(row, column);
/* 818*/          uvalue = icd.getEndValue(row, column);
/*   0*/        } else {
/* 821*/          lvalue = dataset.getValue(row, column);
/* 822*/          uvalue = lvalue;
/*   0*/        } 
/* 824*/        if (lvalue != null)
/* 825*/          minimum = Math.min(minimum, lvalue.doubleValue()); 
/* 827*/        if (uvalue != null)
/* 828*/          maximum = Math.max(maximum, uvalue.doubleValue()); 
/*   0*/      } 
/*   0*/    } 
/* 832*/    if (minimum == Double.POSITIVE_INFINITY)
/* 833*/      return null; 
/* 836*/    return new Range(minimum, maximum);
/*   0*/  }
/*   0*/  
/*   0*/  public static Range iterateXYRangeBounds(XYDataset dataset) {
/* 849*/    double minimum = Double.POSITIVE_INFINITY;
/* 850*/    double maximum = Double.NEGATIVE_INFINITY;
/* 851*/    int seriesCount = dataset.getSeriesCount();
/* 852*/    for (int series = 0; series < seriesCount; series++) {
/* 853*/      int itemCount = dataset.getItemCount(series);
/* 854*/      for (int item = 0; item < itemCount; item++) {
/*   0*/        double lvalue, uvalue;
/* 857*/        if (dataset instanceof IntervalXYDataset) {
/* 858*/          IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
/* 860*/          lvalue = intervalXYData.getStartYValue(series, item);
/* 861*/          uvalue = intervalXYData.getEndYValue(series, item);
/* 863*/        } else if (dataset instanceof OHLCDataset) {
/* 864*/          OHLCDataset highLowData = (OHLCDataset)dataset;
/* 865*/          lvalue = highLowData.getLowValue(series, item);
/* 866*/          uvalue = highLowData.getHighValue(series, item);
/*   0*/        } else {
/* 869*/          lvalue = dataset.getYValue(series, item);
/* 870*/          uvalue = lvalue;
/*   0*/        } 
/* 872*/        if (!Double.isNaN(lvalue))
/* 873*/          minimum = Math.min(minimum, lvalue); 
/* 875*/        if (!Double.isNaN(uvalue))
/* 876*/          maximum = Math.max(maximum, uvalue); 
/*   0*/      } 
/*   0*/    } 
/* 880*/    if (minimum == Double.POSITIVE_INFINITY)
/* 881*/      return null; 
/* 884*/    return new Range(minimum, maximum);
/*   0*/  }
/*   0*/  
/*   0*/  public static Number findMinimumDomainValue(XYDataset dataset) {
/* 902*/    if (dataset == null)
/* 903*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/* 905*/    Number result = null;
/* 907*/    if (dataset instanceof DomainInfo) {
/* 908*/      DomainInfo info = (DomainInfo)dataset;
/* 909*/      return new Double(info.getDomainLowerBound(true));
/*   0*/    } 
/* 912*/    double minimum = Double.POSITIVE_INFINITY;
/* 913*/    int seriesCount = dataset.getSeriesCount();
/* 914*/    for (int series = 0; series < seriesCount; series++) {
/* 915*/      int itemCount = dataset.getItemCount(series);
/* 916*/      for (int item = 0; item < itemCount; item++) {
/*   0*/        double value;
/* 919*/        if (dataset instanceof IntervalXYDataset) {
/* 920*/          IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
/* 922*/          value = intervalXYData.getStartXValue(series, item);
/*   0*/        } else {
/* 925*/          value = dataset.getXValue(series, item);
/*   0*/        } 
/* 927*/        if (!Double.isNaN(value))
/* 928*/          minimum = Math.min(minimum, value); 
/*   0*/      } 
/*   0*/    } 
/* 933*/    if (minimum == Double.POSITIVE_INFINITY) {
/* 934*/      result = null;
/*   0*/    } else {
/* 937*/      result = new Double(minimum);
/*   0*/    } 
/* 941*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Number findMaximumDomainValue(XYDataset dataset) {
/* 957*/    if (dataset == null)
/* 958*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/* 960*/    Number result = null;
/* 962*/    if (dataset instanceof DomainInfo) {
/* 963*/      DomainInfo info = (DomainInfo)dataset;
/* 964*/      return new Double(info.getDomainUpperBound(true));
/*   0*/    } 
/* 969*/    double maximum = Double.NEGATIVE_INFINITY;
/* 970*/    int seriesCount = dataset.getSeriesCount();
/* 971*/    for (int series = 0; series < seriesCount; series++) {
/* 972*/      int itemCount = dataset.getItemCount(series);
/* 973*/      for (int item = 0; item < itemCount; item++) {
/*   0*/        double value;
/* 976*/        if (dataset instanceof IntervalXYDataset) {
/* 977*/          IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
/* 979*/          value = intervalXYData.getEndXValue(series, item);
/*   0*/        } else {
/* 982*/          value = dataset.getXValue(series, item);
/*   0*/        } 
/* 984*/        if (!Double.isNaN(value))
/* 985*/          maximum = Math.max(maximum, value); 
/*   0*/      } 
/*   0*/    } 
/* 989*/    if (maximum == Double.NEGATIVE_INFINITY) {
/* 990*/      result = null;
/*   0*/    } else {
/* 993*/      result = new Double(maximum);
/*   0*/    } 
/* 998*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Number findMinimumRangeValue(CategoryDataset dataset) {
/*1016*/    if (dataset == null)
/*1017*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/*1021*/    if (dataset instanceof RangeInfo) {
/*1022*/      RangeInfo info = (RangeInfo)dataset;
/*1023*/      return new Double(info.getRangeLowerBound(true));
/*   0*/    } 
/*1028*/    double minimum = Double.POSITIVE_INFINITY;
/*1029*/    int seriesCount = dataset.getRowCount();
/*1030*/    int itemCount = dataset.getColumnCount();
/*1031*/    for (int series = 0; series < seriesCount; series++) {
/*1032*/      for (int item = 0; item < itemCount; item++) {
/*   0*/        Number value;
/*1034*/        if (dataset instanceof IntervalCategoryDataset) {
/*1035*/          IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
/*1037*/          value = icd.getStartValue(series, item);
/*   0*/        } else {
/*1040*/          value = dataset.getValue(series, item);
/*   0*/        } 
/*1042*/        if (value != null)
/*1043*/          minimum = Math.min(minimum, value.doubleValue()); 
/*   0*/      } 
/*   0*/    } 
/*1047*/    if (minimum == Double.POSITIVE_INFINITY)
/*1048*/      return null; 
/*1051*/    return new Double(minimum);
/*   0*/  }
/*   0*/  
/*   0*/  public static Number findMinimumRangeValue(XYDataset dataset) {
/*1072*/    if (dataset == null)
/*1073*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/*1077*/    if (dataset instanceof RangeInfo) {
/*1078*/      RangeInfo info = (RangeInfo)dataset;
/*1079*/      return new Double(info.getRangeLowerBound(true));
/*   0*/    } 
/*1084*/    double minimum = Double.POSITIVE_INFINITY;
/*1085*/    int seriesCount = dataset.getSeriesCount();
/*1086*/    for (int series = 0; series < seriesCount; series++) {
/*1087*/      int itemCount = dataset.getItemCount(series);
/*1088*/      for (int item = 0; item < itemCount; item++) {
/*   0*/        double value;
/*1091*/        if (dataset instanceof IntervalXYDataset) {
/*1092*/          IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
/*1094*/          value = intervalXYData.getStartYValue(series, item);
/*1096*/        } else if (dataset instanceof OHLCDataset) {
/*1097*/          OHLCDataset highLowData = (OHLCDataset)dataset;
/*1098*/          value = highLowData.getLowValue(series, item);
/*   0*/        } else {
/*1101*/          value = dataset.getYValue(series, item);
/*   0*/        } 
/*1103*/        if (!Double.isNaN(value))
/*1104*/          minimum = Math.min(minimum, value); 
/*   0*/      } 
/*   0*/    } 
/*1109*/    if (minimum == Double.POSITIVE_INFINITY)
/*1110*/      return null; 
/*1113*/    return new Double(minimum);
/*   0*/  }
/*   0*/  
/*   0*/  public static Number findMaximumRangeValue(CategoryDataset dataset) {
/*1133*/    if (dataset == null)
/*1134*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/*1138*/    if (dataset instanceof RangeInfo) {
/*1139*/      RangeInfo info = (RangeInfo)dataset;
/*1140*/      return new Double(info.getRangeUpperBound(true));
/*   0*/    } 
/*1146*/    double maximum = Double.NEGATIVE_INFINITY;
/*1147*/    int seriesCount = dataset.getRowCount();
/*1148*/    int itemCount = dataset.getColumnCount();
/*1149*/    for (int series = 0; series < seriesCount; series++) {
/*1150*/      for (int item = 0; item < itemCount; item++) {
/*   0*/        Number value;
/*1152*/        if (dataset instanceof IntervalCategoryDataset) {
/*1153*/          IntervalCategoryDataset icd = (IntervalCategoryDataset)dataset;
/*1155*/          value = icd.getEndValue(series, item);
/*   0*/        } else {
/*1158*/          value = dataset.getValue(series, item);
/*   0*/        } 
/*1160*/        if (value != null)
/*1161*/          maximum = Math.max(maximum, value.doubleValue()); 
/*   0*/      } 
/*   0*/    } 
/*1165*/    if (maximum == Double.NEGATIVE_INFINITY)
/*1166*/      return null; 
/*1169*/    return new Double(maximum);
/*   0*/  }
/*   0*/  
/*   0*/  public static Number findMaximumRangeValue(XYDataset dataset) {
/*1189*/    if (dataset == null)
/*1190*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/*1194*/    if (dataset instanceof RangeInfo) {
/*1195*/      RangeInfo info = (RangeInfo)dataset;
/*1196*/      return new Double(info.getRangeUpperBound(true));
/*   0*/    } 
/*1202*/    double maximum = Double.NEGATIVE_INFINITY;
/*1203*/    int seriesCount = dataset.getSeriesCount();
/*1204*/    for (int series = 0; series < seriesCount; series++) {
/*1205*/      int itemCount = dataset.getItemCount(series);
/*1206*/      for (int item = 0; item < itemCount; item++) {
/*   0*/        double value;
/*1208*/        if (dataset instanceof IntervalXYDataset) {
/*1209*/          IntervalXYDataset intervalXYData = (IntervalXYDataset)dataset;
/*1211*/          value = intervalXYData.getEndYValue(series, item);
/*1213*/        } else if (dataset instanceof OHLCDataset) {
/*1214*/          OHLCDataset highLowData = (OHLCDataset)dataset;
/*1215*/          value = highLowData.getHighValue(series, item);
/*   0*/        } else {
/*1218*/          value = dataset.getYValue(series, item);
/*   0*/        } 
/*1220*/        if (!Double.isNaN(value))
/*1221*/          maximum = Math.max(maximum, value); 
/*   0*/      } 
/*   0*/    } 
/*1225*/    if (maximum == Double.NEGATIVE_INFINITY)
/*1226*/      return null; 
/*1229*/    return new Double(maximum);
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findStackedRangeBounds(CategoryDataset dataset) {
/*1245*/    return findStackedRangeBounds(dataset, 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findStackedRangeBounds(CategoryDataset dataset, double base) {
/*1259*/    if (dataset == null)
/*1260*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/*1262*/    Range result = null;
/*1263*/    double minimum = Double.POSITIVE_INFINITY;
/*1264*/    double maximum = Double.NEGATIVE_INFINITY;
/*1265*/    int categoryCount = dataset.getColumnCount();
/*1266*/    for (int item = 0; item < categoryCount; item++) {
/*1267*/      double positive = base;
/*1268*/      double negative = base;
/*1269*/      int seriesCount = dataset.getRowCount();
/*1270*/      for (int series = 0; series < seriesCount; series++) {
/*1271*/        Number number = dataset.getValue(series, item);
/*1272*/        if (number != null) {
/*1273*/          double value = number.doubleValue();
/*1274*/          if (value > 0.0D)
/*1275*/            positive += value; 
/*1277*/          if (value < 0.0D)
/*1278*/            negative += value; 
/*   0*/        } 
/*   0*/      } 
/*1283*/      minimum = Math.min(minimum, negative);
/*1284*/      maximum = Math.max(maximum, positive);
/*   0*/    } 
/*1286*/    if (minimum <= maximum)
/*1287*/      result = new Range(minimum, maximum); 
/*1289*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findStackedRangeBounds(CategoryDataset dataset, KeyToGroupMap map) {
/*1306*/    Range result = null;
/*1307*/    if (dataset != null) {
/*1310*/      int[] groupIndex = new int[dataset.getRowCount()];
/*1311*/      for (int i = 0; i < dataset.getRowCount(); i++)
/*1312*/        groupIndex[i] = map.getGroupIndex(map.getGroup(dataset.getRowKey(i))); 
/*1318*/      int groupCount = map.getGroupCount();
/*1319*/      double[] minimum = new double[groupCount];
/*1320*/      double[] maximum = new double[groupCount];
/*1322*/      int categoryCount = dataset.getColumnCount();
/*1323*/      for (int item = 0; item < categoryCount; item++) {
/*1324*/        double[] positive = new double[groupCount];
/*1325*/        double[] negative = new double[groupCount];
/*1326*/        int seriesCount = dataset.getRowCount();
/*1327*/        for (int series = 0; series < seriesCount; series++) {
/*1328*/          Number number = dataset.getValue(series, item);
/*1329*/          if (number != null) {
/*1330*/            double value = number.doubleValue();
/*1331*/            if (value > 0.0D)
/*1332*/              positive[groupIndex[series]] = positive[groupIndex[series]] + value; 
/*1335*/            if (value < 0.0D)
/*1336*/              negative[groupIndex[series]] = negative[groupIndex[series]] + value; 
/*   0*/          } 
/*   0*/        } 
/*1342*/        for (int g = 0; g < groupCount; g++) {
/*1343*/          minimum[g] = Math.min(minimum[g], negative[g]);
/*1344*/          maximum[g] = Math.max(maximum[g], positive[g]);
/*   0*/        } 
/*   0*/      } 
/*1347*/      for (int j = 0; j < groupCount; j++)
/*1348*/        result = Range.combine(result, new Range(minimum[j], maximum[j])); 
/*   0*/    } 
/*1353*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Number findMinimumStackedRangeValue(CategoryDataset dataset) {
/*1367*/    Number result = null;
/*1368*/    if (dataset != null) {
/*1369*/      double minimum = 0.0D;
/*1370*/      int categoryCount = dataset.getRowCount();
/*1371*/      for (int item = 0; item < categoryCount; item++) {
/*1372*/        double total = 0.0D;
/*1374*/        int seriesCount = dataset.getColumnCount();
/*1375*/        for (int series = 0; series < seriesCount; series++) {
/*1376*/          Number number = dataset.getValue(series, item);
/*1377*/          if (number != null) {
/*1378*/            double value = number.doubleValue();
/*1379*/            if (value < 0.0D)
/*1380*/              total += value; 
/*   0*/          } 
/*   0*/        } 
/*1385*/        minimum = Math.min(minimum, total);
/*   0*/      } 
/*1388*/      result = new Double(minimum);
/*   0*/    } 
/*1390*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Number findMaximumStackedRangeValue(CategoryDataset dataset) {
/*1404*/    Number result = null;
/*1406*/    if (dataset != null) {
/*1407*/      double maximum = 0.0D;
/*1408*/      int categoryCount = dataset.getColumnCount();
/*1409*/      for (int item = 0; item < categoryCount; item++) {
/*1410*/        double total = 0.0D;
/*1411*/        int seriesCount = dataset.getRowCount();
/*1412*/        for (int series = 0; series < seriesCount; series++) {
/*1413*/          Number number = dataset.getValue(series, item);
/*1414*/          if (number != null) {
/*1415*/            double value = number.doubleValue();
/*1416*/            if (value > 0.0D)
/*1417*/              total += value; 
/*   0*/          } 
/*   0*/        } 
/*1421*/        maximum = Math.max(maximum, total);
/*   0*/      } 
/*1423*/      result = new Double(maximum);
/*   0*/    } 
/*1426*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findStackedRangeBounds(TableXYDataset dataset) {
/*1439*/    return findStackedRangeBounds(dataset, 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findStackedRangeBounds(TableXYDataset dataset, double base) {
/*1453*/    if (dataset == null)
/*1454*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/*1456*/    double minimum = base;
/*1457*/    double maximum = base;
/*1458*/    for (int itemNo = 0; itemNo < dataset.getItemCount(); itemNo++) {
/*1459*/      double positive = base;
/*1460*/      double negative = base;
/*1461*/      int seriesCount = dataset.getSeriesCount();
/*1462*/      for (int seriesNo = 0; seriesNo < seriesCount; seriesNo++) {
/*1463*/        double y = dataset.getYValue(seriesNo, itemNo);
/*1464*/        if (!Double.isNaN(y))
/*1465*/          if (y > 0.0D) {
/*1466*/            positive += y;
/*   0*/          } else {
/*1469*/            negative += y;
/*   0*/          }  
/*   0*/      } 
/*1473*/      if (positive > maximum)
/*1474*/        maximum = positive; 
/*1476*/      if (negative < minimum)
/*1477*/        minimum = negative; 
/*   0*/    } 
/*1480*/    if (minimum <= maximum)
/*1481*/      return new Range(minimum, maximum); 
/*1484*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public static double calculateStackTotal(TableXYDataset dataset, int item) {
/*1500*/    double total = 0.0D;
/*1501*/    int seriesCount = dataset.getSeriesCount();
/*1502*/    for (int s = 0; s < seriesCount; s++) {
/*1503*/      double value = dataset.getYValue(s, item);
/*1504*/      if (!Double.isNaN(value))
/*1505*/        total += value; 
/*   0*/    } 
/*1508*/    return total;
/*   0*/  }
/*   0*/  
/*   0*/  public static Range findCumulativeRangeBounds(CategoryDataset dataset) {
/*1523*/    if (dataset == null)
/*1524*/      throw new IllegalArgumentException("Null 'dataset' argument."); 
/*   0*/    boolean allItemsNull = true;
/*1529*/    double minimum = 0.0D;
/*1530*/    double maximum = 0.0D;
/*1531*/    for (int row = 0; row < dataset.getRowCount(); row++) {
/*1532*/      double runningTotal = 0.0D;
/*1533*/      for (int column = 0; column < dataset.getColumnCount() - 1; 
/*1534*/        column++) {
/*1535*/        Number n = dataset.getValue(row, column);
/*1536*/        if (n != null) {
/*1537*/          allItemsNull = false;
/*1538*/          double value = n.doubleValue();
/*1539*/          runningTotal += value;
/*1540*/          minimum = Math.min(minimum, runningTotal);
/*1541*/          maximum = Math.max(maximum, runningTotal);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1545*/    if (!allItemsNull)
/*1546*/      return new Range(minimum, maximum); 
/*1549*/    return null;
/*   0*/  }
/*   0*/}
