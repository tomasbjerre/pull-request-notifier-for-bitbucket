package se.bjurr.prnfb.test;

import uk.co.jemos.podam.api.PodamFactoryImpl;

public class Podam {
 private static PodamFactoryImpl factory = new PodamFactoryImpl();

 public static <T> T populatedInstanceOf(Class<T> clazz) {
  return factory.manufacturePojo(clazz);
 }
}
