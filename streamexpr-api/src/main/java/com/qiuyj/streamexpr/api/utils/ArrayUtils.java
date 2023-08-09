package com.qiuyj.streamexpr.api.utils;

import java.lang.reflect.Array;
import java.util.Deque;

/**
 * @author qiuyj
 * @since 2023-08-05
 */
@SuppressWarnings("unchecked")
public abstract class ArrayUtils {

    private ArrayUtils() { /* for private */ }

    public static <T> T[] transferToArray(Class<T> componentType, Deque<T> queue) {
        return transferToArray(componentType, queue, 0, queue.size());
    }

    /**
     * 将队列里面的元素按照入队的顺序转换到数组里面（队列里面的元素会被移除）
     * @param componentType 数组元素的类型
     * @param queue 队列
     * @return 转换后的数组
     * @param <T> 数组元素的类型
     */
    public static <T> T[] transferToArray(Class<T> componentType, Deque<T> queue, int startIndex, int arrayLen) {
        T[] array = (T[]) Array.newInstance(componentType, arrayLen);
        if (startIndex == 0) {
            switch (arrayLen) {
                case 0: break;
                case 10: array[9] = queue.removeLast();
                case 9:  array[8] = queue.removeLast();
                case 8:  array[7] = queue.removeLast();
                case 7:  array[6] = queue.removeLast();
                case 6:  array[5] = queue.removeLast();
                case 5:  array[4] = queue.removeLast();
                case 4:  array[3] = queue.removeLast();
                case 3:  array[2] = queue.removeLast();
                case 2:  array[1] = queue.removeLast();
                case 1:  array[0] = queue.removeLast(); break;
                default: array = queue.toArray(array);
            }
        }
        else {
            while (--arrayLen >= 0) {
                array[arrayLen] = queue.removeLast();
            }
        }
        return array;
    }

    /**
     * 创建数组的工具方法，内部提供相关优化
     * 如果数量小于10，那么直接赋值
     * 如果大于等于10，那么采用System.arraycopy
     * @param componentType 数组元素类型
     * @param first 第一个元素
     * @param others 后续元素
     * @return 对应类型的数组
     * @param <T> 数组的类型
     */
    public static <T> T[] makeArray(Class<T> componentType, T first, T... others) {
        int othersLength = others.length;
        T[] array = (T[]) Array.newInstance(componentType, othersLength + 1);
        array[0] = first;
        switch (othersLength) {
            case 0: break;
            case 9: array[9] = others[8];
            case 8: array[8] = others[7];
            case 7: array[7] = others[6];
            case 6: array[6] = others[5];
            case 5: array[5] = others[4];
            case 4: array[4] = others[3];
            case 3: array[3] = others[2];
            case 2: array[2] = others[1];
            case 1: array[1] = others[0]; break;
            default: System.arraycopy(others, 0, array, 1, othersLength);
        }
        return array;
    }
}
