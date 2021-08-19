package yore.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Yore Yuan
 */
public class RuntimeAspect {

    /**
     * 执行方法的时间
     *
     * @param clazz 类
     * @param args 参数列表
     */
    public static void printSpend(final Class<?> clazz, String[] args) {
        try {
            final Object obj = clazz.getConstructor(new Class[] {}).newInstance();
            printSpend(obj, args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public static void printSpend(final Object obj, String[] args) {
        try {
            final Method[] methods = obj.getClass().getDeclaredMethods();
            for (final Method method : methods) {
                final RuntimeAnnotation runtimeAnnotation = method.getAnnotation(RuntimeAnnotation.class);
                if (runtimeAnnotation != null) {
                    long start = System.currentTimeMillis();
                    Object result = method.invoke(obj, new Object[]{args});
                    long end = System.currentTimeMillis();
                    System.out.println("---------------- " + runtimeAnnotation.descr());
                    switch (runtimeAnnotation.unit()) {
                        case SECONDS: System.out.println(" 用时：" + ((end-start) / 1000.0) + " s"); break;
                        case MINUTES: System.out.println(" 用时：" + ((end-start) / 1000.0 / 60.0) + " m"); break;
                        case HOURS: System.out.println(" 用时：" + ((end-start) / 1000.0 / 3600.0) + " h"); break;
                        case HALF_DAYS: System.out.println(" 用时：" + ((end-start) / 1000.0 / 43200.0) + " HalfDays"); break;
                        case DAYS: System.out.println(" 用时：" + ((end-start) / 1000.0 / 86400.0) + " day"); break;
                        case WEEKS: System.out.println(" 用时：" + ((end-start) / 1000.0 / (7 * 86400.0)) + " week"); break;
                        case MONTHS: System.out.println(" 用时：" + ((end-start) / 1000.0 / (31556952L / 12)) + " month"); break;
                        case YEARS: System.out.println(" 用时：" + ((end-start) / 1000 / 31556952L) + " year"); break;
                        default: System.out.println(" 用时：" + (end-start) + " ms"); break;
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
