package tw.pago.pagobackend.util;


import java.beans.PropertyDescriptor;
import java.util.stream.Stream;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class EntityPropertyUtil {

  public static String[] getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    return Stream.of(src.getPropertyDescriptors()).map(PropertyDescriptor::getName)
        .filter(propertyName -> src.getPropertyValue(propertyName) == null)
        .toArray(String[]::new);
  }

  public static String[] getPresentPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    return Stream.of(src.getPropertyDescriptors())
        .map(PropertyDescriptor::getName)
        .filter(propertyName -> src.getPropertyValue(propertyName) != null)
        .toArray(String[]::new);
  }


}
