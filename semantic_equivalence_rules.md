
# Rules summarized by authors of the paper [**On the Efficiency of Test Suite based Program Repair: A Systematic Assessment of 16 Automated Repair Systems for Java Programs**](https://arxiv.org/pdf/2008.00914.pdf) to determine whether the APR patch is semantically equivalent with the corresponding developer patch

## Rule 1: Different fields with the same value (or alias)
**Illustrations**

tool patch:
```
- return cAvailableLocaleSet.contains(locale);
+ return availableLocaleList().contains(locale); 
```
oracle patch:
```
- return cAvailableLocaleSet.contains(locale);
+ return cAvailableLocaleList.contains(locale);
```
## Rule 2: Same exception but different messages
**Illustrations**

tool patch:
```
+ throw new NumberFormatException(str + " is not a valid number.");
```
oracle patch:
```
+ throw new NumberFormatException();
```

## Rule 3: Variable initialization with new rather than a default value
**Illustrations**

tool patch:
```
+ if (str == null) str = "";
```
oracle patch:
```
+ if (str == null) str = new String();
```

## Rule 4: if statement instead of a ternary operator
**Illustrations**

tool patch:
```
+ classes[i] = array[i] == null ? null : array[i].getClass();
```
oracle patch:
```
+ if (array[i] == null) continue;
+ classes[i] = array[i].getClass();
```

## Rule 5: Unrolling a method
**Illustrations**

tool patch:
```
- this.elitismRate = elitismRate;
+ setElitismRate(elitismRate);
```
oracle patch:
```
- this.elitismRate = elitismRate;
+ if (elitismRate>(double)1.0){throw ...;}
+ if (elitismRate<(double)0.0){throw ...;}
```

## Rule 6: Replacing a value without a side effect
**Illustrations**

tool patch:
```
- int g = (int) ((value - this.lowerBound) / (this.upperBound 
+ int g = (int) ((v - this.lowerBound) / (this.upperBound
```
oracle patch:
```
- v = Math.min(v, this.upperBound);
+ value = Math.min(v, this.upperBound);
```

## Rule 7: Enumerating
**Illustrations**

tool patch:
```
- if (fa * fb >= 0.0 ) {
+ if (fa * fb > 0.0 ) {
```
oracle patch:
```
- if (fa * fb >= 0.0 ) {
+ if (fa * fb >= 0.0 &&!(fa * fb==0.0))
```

## Rule 8: Unnecessary code uncleaned
**Illustrations**

tool patch:
```
- boolean wasWhite= false;
  for(int i= 0; i<value.length(); ++i) {
- if(Character.isWhitespace(c)) { ...... } 
- wasWhite= false;
```
oracle patch:
```
  for(int i= 0; i<value.length(); ++i) {
- if(Character.isWhitespace(c)) { ...... }
- wasWhite= false;
```

## Rule 9: Return earlier instead of a packaged return
**Illustrations**

tool patch:
```
- return foundDigit && !hasExp;
+ return foundDigit && !hasExp && !hasDecPoint;
```
oracle patch:
```
+ if (hasDecPoint==true){return false;}
```

## Rule 10: More null checks
**Illustrations**

tool patch:
```
+ if (searchList[i] == null || replacementList[i] == null) 
+ { continue; }
```
oracle patch:
```
+ if(noMoreMatchesForReplIndex[i]||searchList[i]==null
+ ||searchList[i].length()==0||replacementList[i]==null)
+ { continue; }
```