/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yore.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Yore Yuan
 */
public class RuntimeAspect {

    /**
     * Print execution time
     *
     * @param clazz Class
     * @param args Parameter list
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
                        case SECONDS: System.out.println(" Time spent：" + ((end-start) / 1000.0) + " s"); break;
                        case MINUTES: System.out.println(" Time spent：" + ((end-start) / 1000.0 / 60.0) + " m"); break;
                        case HOURS: System.out.println(" Time spent：" + ((end-start) / 1000.0 / 3600.0) + " h"); break;
                        case HALF_DAYS: System.out.println(" Time spent：" + ((end-start) / 1000.0 / 43200.0) + " HalfDays"); break;
                        case DAYS: System.out.println(" Time spent：" + ((end-start) / 1000.0 / 86400.0) + " day"); break;
                        case WEEKS: System.out.println(" Time spent：" + ((end-start) / 1000.0 / (7 * 86400.0)) + " week"); break;
                        case MONTHS: System.out.println(" Time spent：" + ((end-start) / 1000.0 / (31556952L / 12)) + " month"); break;
                        case YEARS: System.out.println(" Time spent：" + ((end-start) / 1000 / 31556952L) + " year"); break;
                        default: System.out.println(" Time spent：" + (end-start) + " ms"); break;
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
