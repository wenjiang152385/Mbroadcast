package com.oraro.mbroadcast.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/11/9.
 */
public class BeanUtil {
    /**
     * java反射bean的get方法
     *
     * @param objectClass
     * @param fieldName
     * @return
     */

    @SuppressWarnings("unchecked")
    public static Method getGetMethod(Class objectClass, String fieldName) {

        StringBuffer sb = new StringBuffer();

        sb.append("get");

        sb.append(fieldName.substring(0, 1).toUpperCase());

        sb.append(fieldName.substring(1));

        Method method = null;
        try {
            method = objectClass.getMethod(sb.toString());
            return method;

        } catch (Exception e) {
            if (null == method) {
            }
            e.printStackTrace();
        }

        return null;

    }


    /**
     * java反射bean的set方法
     *
     * @param objectClass
     * @param fieldName
     * @return
     */

    @SuppressWarnings("unchecked")
    public static Method getSetMethod(Class objectClass, String fieldName) {

        try {

            Class[] parameterTypes = new Class[1];

            Field field = objectClass.getDeclaredField(fieldName);

            parameterTypes[0] = field.getType();

            StringBuffer sb = new StringBuffer();

            sb.append("set");

            sb.append(fieldName.substring(0, 1).toUpperCase());

            sb.append(fieldName.substring(1));

            Method method = objectClass.getMethod(sb.toString(), parameterTypes);
            return method;

        } catch (Exception e) {

            e.printStackTrace();

        }
        return null;
    }


    /**
     * 执行set方法
     *
     * @param o
     * @param fieldName
     * @param value
     */
    public static void invokeSet(Object o, String fieldName, Object value) {

        Method method = getSetMethod(o.getClass(), fieldName);

        try {
            method.invoke(o, new Object[]{value});
        } catch (Exception e) {

            e.printStackTrace();
        }

    }


    /**
     * 执行get方法
     *
     * @param o
     * @param fieldName
     * @return
     */
    public static Object invokeGet(Object o, String fieldName) {

        Method method = getGetMethod(o.getClass(), fieldName);
        if (null == method) {
            return "";
        }
        try {
            Object obj = method.invoke(o, new Object[0]);
            if (null == obj) {
                obj = "";
            }
            return obj;
        } catch (Exception e) {

            e.printStackTrace();

        }
        return "";
    }
}
