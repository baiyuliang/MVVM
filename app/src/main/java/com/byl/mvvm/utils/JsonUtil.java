package com.byl.mvvm.utils;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * JsonUtil
 *
 * @author lishide
 * @date 2020/7/19
 */
public class JsonUtil {

    private static Gson gson = new Gson();

    public static <T> RequestBody getRequestBody(T bean) {
        return RequestBody.create(MediaType.parse("application/json"), getBeanToString(bean));
    }

    public static <T> String getBeanToString(T bean) {
        return gson.toJson(bean);
    }

    /**
     * 格式化json字符串
     *
     * @param jsonStr 需要格式化的json串
     * @return 格式化后的json串
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char last;
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            //遇到{ [换行，且下一行缩进
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                //遇到} ]换行，当前行缩进
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                //遇到,换行
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }
        return sb.toString();
    }

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

    /**
     * http 请求数据返回 json 中中文字符为 unicode 编码转汉字转码
     *
     * @param theString
     * @return 转化后的结果.
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuilder outBuffer = new StringBuilder(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    switch (aChar) {
                        case 't':
                            aChar = '\t';
                            break;
                        case 'r':
                            aChar = '\r';
                            break;
                        case 'n':
                            aChar = '\n';
                            break;
                        case 'f':
                            aChar = '\f';
                            break;
                        default:
                            break;
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }
}
