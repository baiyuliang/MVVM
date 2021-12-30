package com.byl.mvvm.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by zhuxiaoan on 2019/4/25 0025.
 */
public class GenericParadigmUtils {

    public static Class parseGenericParadigm(Object object, int position) {
        if (object == null) {
            return null;
        }

        return GenericParadigmUtils.parseGenericParadigm(object.getClass(), position);
    }

    public static Class parseGenericParadigm(Class clazz, int position) {
        if (clazz == null) {
            return null;
        }
        List<Pathfinder> pathfinders = new ArrayList<Pathfinder>(1);
        pathfinders.add(new ConsistentPathfinder(Integer.MAX_VALUE, position));
        return GenericParadigmUtils.parseGenericParadigm(clazz, pathfinders);
    }

    public static Class parseGenericParadigm(Object object, List<Pathfinder> pathfinders) {
        if (object == null) {
            return null;
        }
        return GenericParadigmUtils.parseGenericParadigm(object.getClass(), pathfinders);
    }

    public static Class parseGenericParadigm(Class clazz, List<Pathfinder> pathfinders) {

        if (!GenericParadigmUtils.isGenericParadigm(clazz) || pathfinders == null || pathfinders.isEmpty()) {
            return null;
        }
        assertPathfinder(pathfinders);
        Pathfinder pathfinder = pathfinders.get(0);
        boolean isConsistentPathfinder = pathfinder instanceof ConsistentPathfinder;
        int size = pathfinders.size();
        Type type = clazz.getGenericSuperclass();

        return GenericParadigmUtils.getGenericClassPlus(type, 0, size, isConsistentPathfinder, pathfinders);
    }

    public static Class parseInterfaceGenericParadigm(Object object, int who, int position) {
        if (object == null) {
            return null;
        }

        return GenericParadigmUtils.parseInterfaceGenericParadigm(object.getClass(), who, position);
    }

    public static Class parseInterfaceGenericParadigm(Class clazz, int who, int position) {
        if (clazz == null) {
            return null;
        }
        List<Pathfinder> pathfinders = new ArrayList<Pathfinder>(1);
        pathfinders.add(new ConsistentPathfinder(Integer.MAX_VALUE, position));
        return GenericParadigmUtils.parseInterfaceGenericParadigm(clazz, who, pathfinders);
    }

    public static Class parseInterfaceGenericParadigm(Object object, int who, List<Pathfinder> pathfinders) {
        if (object == null) {
            return null;
        }
        return GenericParadigmUtils.parseInterfaceGenericParadigm(object.getClass(), who, pathfinders);
    }

    public static Class parseInterfaceGenericParadigm(Class clazz, int who, List<Pathfinder> pathfinders) {
        if (!GenericParadigmUtils.isInterfaceGenericParadigm(clazz) || pathfinders == null || pathfinders.isEmpty()) {
            return null;
        }
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        int length = genericInterfaces.length;
        if (who < 0 || who >= length) {
            return null;
        }
        assertPathfinder(pathfinders);
        Pathfinder pathfinder = pathfinders.get(0);
        boolean isConsistentPathfinder = pathfinder instanceof ConsistentPathfinder;
        int size = pathfinders.size();
        Type type = genericInterfaces[who];

        return GenericParadigmUtils.getGenericClassPlus(type, 0, size, isConsistentPathfinder, pathfinders);
    }

    public static boolean isInterfaceGenericParadigm(Object object) {
        if (object == null) {
            return false;
        }
        return GenericParadigmUtils.isInterfaceGenericParadigm(object.getClass());
    }

    public static boolean isInterfaceGenericParadigm(Class clazz) {
        if (clazz == null) {
            return false;
        }
        Type[] genericInterfaces =
                clazz.getGenericInterfaces();
        return genericInterfaces != null && genericInterfaces.length > 0;
    }

    public static boolean isGenericParadigm(Object object) {
        if (object == null) {
            return false;
        }
        return GenericParadigmUtils.isGenericParadigm(object.getClass());
    }

    public static boolean isGenericParadigm(Class clazz) {
        if (clazz == null) {
            return false;
        }
        Type genericSuperclass = clazz.getGenericSuperclass();
        return genericSuperclass instanceof ParameterizedType;
    }

    private static Class getGenericClassPlus(Type type, int level, int size, boolean isConsistentPathfinder, List<Pathfinder> pathfinders) {
        if (isConsistentPathfinder || level < size) {
            // 得到指路人指明前进的道路
            Pathfinder pathfinder = isConsistentPathfinder ? pathfinders.get(0) : pathfinders.get(level);
            if (type instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                int length = types.length;
                int position = pathfinder.position;
                if (position < 0 || position >= length) {
                    return null;
                }
                return getGenericClassPlus(types[position], level + 1, size, isConsistentPathfinder, pathfinders);
            }
        }

        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        }
        return null;
    }

    @Deprecated
    private static Class getGenericClass(Type type, int level, int size, boolean isConsistentPathfinder, List<Pathfinder> pathfinders) {
        if (isConsistentPathfinder) {
            // 特殊, 找到泛型的最深处类型
            if (type instanceof Class) {
                return (Class) type;
            }
        } else {
            // 指定指路人
            if (level >= size) {
                if (type instanceof Class) {
                    return (Class) type;
                } else if (type instanceof ParameterizedType) {
                    return (Class) ((ParameterizedType) type).getRawType();
                }
            }
        }

        // 得到指路人指明前进的道路
        Pathfinder pathfinder = isConsistentPathfinder ? pathfinders.get(0) : pathfinders.get(level);
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            int length = types.length;
            int position = pathfinder.position;
            if (position < 0 || position >= length) {
                return null;
            }
            return getGenericClass(types[position], level + 1, size, isConsistentPathfinder, pathfinders);
        } else {
            return null;
        }
    }

    private static void assertPathfinder(List<Pathfinder> pathfinders) {
        if (pathfinders == null || pathfinders.isEmpty()) {
            throw new IllegalArgumentException("Oh, No, It`s not have Pathfinder...");
        }
        Pathfinder pathfinder = pathfinders.get(0);
        boolean isConsistentPathfinder = pathfinder instanceof ConsistentPathfinder;
        if (!isConsistentPathfinder) {
            int size = pathfinders.size();
            for (int level = 0; level < size; level++) {
                pathfinder = pathfinders.get(level);
                if (level != pathfinder.depth) {
                    throw new IllegalArgumentException("Oh, No, Pathfinders is incomplete...");
                }
            }
        }
    }

    private static class ConsistentPathfinder extends Pathfinder {
        public ConsistentPathfinder(int in_depth, int in_position) {
            super.depth = in_depth;
            super.position = in_position;
        }
    }

    public static class Pathfinder {
        public int depth;
        public int position;

        public Pathfinder() {
        }

        public Pathfinder(int in_depth, int in_position) {
            this.depth = in_depth;
            this.position = in_position;
        }
    }

}

