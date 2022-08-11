package com.baioretto.debugkeeper.util;

import com.baioretto.debugkeeper.exception.DebugHelperInternalException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class Reflections {
    private Reflections() {
        throw new UnsupportedOperationException();
    }

    public static Class<?> getClass(String coordinate) {
        try {
            return Class.forName(coordinate);
        } catch (ClassNotFoundException e) {
            throw new DebugHelperInternalException(e);
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new DebugHelperInternalException(e);
        }
        constructor.setAccessible(true);
        return constructor;
    }

    public static Constructor<?> getConstructor(String clazz, Class<?>... parameterTypes) {
        return Reflections.getConstructor(Reflections.getClass(clazz), parameterTypes);
    }

    public static Object newInstance(Constructor<?> constructor, Object... initArgs) {
        try {
            return constructor.newInstance(initArgs);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new DebugHelperInternalException(e);
        }
    }

    public static Field getField(Class<?> clazz, String name) {
        Field field;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new DebugHelperInternalException(e);
        }
        field.setAccessible(true);
        return field;
    }

    public static Field getField(String clazz, String name) {
        return Reflections.getField(Reflections.getClass(clazz), name);
    }

    public static Object get(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new DebugHelperInternalException(e);
        }
    }

    public static void set(Field field, Object obj, Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new DebugHelperInternalException(e);
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        Method method;
        try {
            method = clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new DebugHelperInternalException(e);
        }
        method.setAccessible(true);
        return method;
    }

    public static Method getMethod(String clazz, String name, Class<?>... parameterTypes) {
        return Reflections.getMethod(Reflections.getClass(clazz), name, parameterTypes);
    }

    public static Object invoke(Method method, Object obj, Object... args) {
        Object result;
        try {
            result = method.invoke(obj, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new DebugHelperInternalException(e);
        }
        return result;
    }
}