package com.logus.common.config;

import org.apache.commons.text.StringEscapeUtils;

public class CustomHtmlEscapeUtil {

    public static String escapeCustom(String input) {
        if (input == null) {
            return null;
        }

        // 기본 HTML 이스케이프 처리
        String escaped = StringEscapeUtils.escapeHtml4(input);

        // 추가로 이스케이프할 문자를 처리
        escaped = escaped.replace("/", "&#47;"); // '/' 이스케이프 추가
        escaped = escaped.replace("'", "&#39;"); // 작은 따옴표 이스케이프 추가

        return escaped;
    }

    public static String unescapeCustom(String input) {
        if (input == null) {
            return null;
        }

        // 기본 HTML 언이스케이프 처리
        String unescaped = StringEscapeUtils.unescapeHtml4(input);

        // 추가로 언이스케이프할 문자를 처리
        unescaped = unescaped.replace("&#47;", "/"); // '/' 언이스케이프 추가
        unescaped = unescaped.replace("&#39;", "'"); // 작은 따옴표 언이스케이프 추가

        return unescaped;
    }

}
