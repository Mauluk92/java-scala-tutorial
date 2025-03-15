package it.mauluk92.tutorial.scala.testutils.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecuteScalaProgram {
    String mainClass() default "";
    String[] classPath() default "";
    String modulePath() default "";
    String moduleName() default "";
    String[] commandLineArguments() default {};
}
