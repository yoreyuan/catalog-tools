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
     * 运行花费时间的单位，
     * 默认为秒值
     */
    ChronoUnit unit() default ChronoUnit.SECONDS;

    /**
     * 描述信息
     */
    String descr() default "";

}
