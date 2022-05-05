package lowcoder.common.interfaces;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Values {
  //Predicates
  public static <T> Predicate<T> emptyPredicate() {
    return (it) -> {
      if(it == null) {
        return true;
      }
      
      if(it instanceof String) {
        return ((String) it).isEmpty();
      }
      
      return false; 
    };
  }
  
  public static <T> Predicate<T> notEmptyPredicate() {
    return (it) -> {
      if(it == null) {
        return false;
      }
      
      if(it instanceof String) {
        return !((String) it).isEmpty();
      }
      
      return true;
    };
  }

  public static String append(Object... values) {
    StringBuilder builder = new StringBuilder();
    for (Object value : values) {
      builder.append(value);
    }

    return builder.toString();
  }
  
  public static <T> boolean validate(Predicate<T> predicate, T value) {
    return predicate.test(value);
  }
  
  public static <T> T validate(Predicate<T> predicate, T value, Supplier<String> onError) {
    if(!predicate.test(value)) {
      throw new IllegalArgumentException(format(onError.get(), value));
    }
    
    return value;
  }

  public static String format(String pattern, Object... args) {
    return MessageFormat.format(pattern, args);
  }
  
  public static <T> Iterable<T> arrayToIterable(T... args) {
    if(args != null) {
      return Arrays.asList(args);
    }
    return new ArrayList<T>();
  }
}
