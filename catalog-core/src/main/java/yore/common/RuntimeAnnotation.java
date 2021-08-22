package yore.common;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

/**
 * @author Yore Yuan
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeAnnotation {

    /**
     * The unit of running time,
     * the default is milliseconds.
     */
    ChronoUnit unit() default ChronoUnit.SECONDS;

    /**
     * Description
     */
    String descr() default "";

}
