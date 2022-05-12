package com.example.zooseeker.util;

import java.util.List;

public class Helper {
    /**
     * Gets last element in list
     * @param list
     * @param <T>
     * @return Last element T in list
     */
    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }
}
