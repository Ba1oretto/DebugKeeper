package com.baioretto.debugkeeper.compatible;

import com.baioretto.debugkeeper.util.Reflections;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Hashtable;

public class VersionCompatible {
    private final static Hashtable<Class<?>, Object> cache = new Hashtable<>();

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz, SupportVersion ver) {
        Object impl = cache.get(clazz);
        if (impl == null) {
            impl = Reflections.newInstance(Reflections.getConstructor(Reflections.getClass(clazz.getPackageName() + ".version." + ver.toString())));
            cache.put(clazz, impl);
        }
        return (T) impl;
    }

    public final static SupportVersion version;

    static {
        String uselessVersion = 'v' + Bukkit.getVersion().split(" ")[2];
        uselessVersion = uselessVersion.substring(0, uselessVersion.length() - 1).replaceAll("[.]", "_");
        int position = uselessVersion.lastIndexOf("_") + 1;
        if (uselessVersion.split("_").length == 3)
            version = SupportVersion.getByName(uselessVersion.substring(0, position) + 'R' + uselessVersion.substring(position));
        else version = SupportVersion.getByName(uselessVersion);
    }

    public enum SupportVersion {
        v1_18("v1_18"),
        v1_18_R1("v1_18_R1"),
        v1_18_R2("v1_18_R2"),
        v1_19("v1_19"),
        v1_19_R1("v1_19_R1"),
        v1_19_R2("v1_19_R2");

        private static final HashMap<String, SupportVersion> BY_NAME = new HashMap<>();

        static {
            for (SupportVersion value : values()) {
                BY_NAME.put(value.name(), value);
            }
        }

        private final String version;

        SupportVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return version;
        }

        public static @Nullable SupportVersion getByName(String name) {
            return BY_NAME.get(name);
        }
    }
}