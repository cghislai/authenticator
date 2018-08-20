package com.charlyghislain.authenticator.ejb.util;

public class FilterUtils {

    public static String toSqlContainsString(String query) {
        return "%" + query + "%";
    }
}
