package com.example.zooseeker.util;

import java.util.List;

public class Helper {
    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }
}
