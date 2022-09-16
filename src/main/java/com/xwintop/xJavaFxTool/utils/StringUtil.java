package com.xwintop.xJavaFxTool.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yanzhao
 * @version 1.0
 * @classname StringUtil
 * @date 2022/9/7 13:52
 * @description TODO
 */
public class StringUtil
{
    /**
     * @param str
     * @param rex
     * @return
     */
    public static List<String> getStringRex(String str, String rex) {
        List<String> stringList = new ArrayList<>();
        Pattern r = Pattern.compile(rex);
        Matcher m = r.matcher(str);
        while (m.find()) {
            stringList.add(m.group());
        }
        return stringList;
    }


    /*
     * 中文转unicode编码
     */
    public static String gbEncoding(String gbString) {
        char[] utfBytes = gbString.toCharArray();
        StringBuilder unicodeBytes = new StringBuilder();
        for (char utfByte : utfBytes) {
            String hexB = Integer.toHexString(utfByte);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes.append("\\u").append(hexB);
        }
        return unicodeBytes.toString();
    }


}
