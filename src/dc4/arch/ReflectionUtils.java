package dc4.arch;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import ox.Reflection;
import ox.x.XList;

public class ReflectionUtils {

  /**
   * Reflection.findClasses is much faster, but it doesn't seem to work when running from inside a JAR file.
   */
  public static <T> XList<Class<T>> findClasses(String packageName, Class<T> classType) {
    try (ScanResult scanResult = new ClassGraph().acceptPackages(packageName).scan()) {
      ClassInfoList classList;
      if (classType.isInterface()) {
        classList = scanResult.getClassesImplementing(classType.getName());
      } else {
        classList = scanResult.getSubclasses(classType.getName());
      }
      return XList.create(classList.loadClasses(classType)).filter(c -> !Reflection.isAbstract(c));
    }
  }

  public static XList<Class<?>> findTestClasses() {
    try (ScanResult scanResult = new ClassGraph().enableAllInfo()
        .acceptPackages("dc4").scan()) {
      ClassInfoList widgetClasses = scanResult.getClassesWithMethodAnnotation("org.junit.jupiter.api.Test");
      widgetClasses.addAll(scanResult.getClassesWithMethodAnnotation("org.junit.jupiter.params.ParameterizedTest"));
      return XList.create(widgetClasses.loadClasses());
    }
  }

}
