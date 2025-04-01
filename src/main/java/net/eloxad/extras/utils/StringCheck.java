package net.eloxad.extras.utils;

import java.util.ArrayList;
import java.util.List;

public class StringCheck {

    public static List<String> getStringStartsWith(String s, String[] arr) {
        List<String> list = new ArrayList();

        for(String str : arr) {
            if (str.toLowerCase().startsWith(s.toLowerCase())) {
                list.add(str);
            }
        }

        if (list.isEmpty()) {
            list = List.of(arr);
        }

        return list;
    }
}
