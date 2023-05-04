package com.example.mission_leesooho;

import com.example.mission_leesooho.standard.util.Ut;

public class TestUt {
    public static boolean setFieldValue(Object o, String fieldName, Object value) {
        return Ut.reflection.setFieldValue(o, fieldName, value);
    }
}