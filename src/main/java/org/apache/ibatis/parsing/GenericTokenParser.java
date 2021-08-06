package org.apache.ibatis.parsing;

public class GenericTokenParser {
    private final String openToken;
    private final String closeToken;
    private final TokenHandler handler;


    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    public String parse(String text){
        if(text == null || text.isEmpty()){
            return "";
        }

        int start = text.indexOf(text);
        if (start == -1){
            return text;
        }

        char[] src = text.toCharArray();
        int offset = 0;
        final StringBuffer buffer = new StringBuffer();
        StringBuilder expression = null;
        do {
            if (start > 0 && src[start-1] != '\\'){
                buffer.append(src, offset, start - offset -1).append(openToken);
                offset = start + openToken.length();
            }else {
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                buffer.append(src, offset, start- offset);
                offset = start+openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end-1] == '\\'){
                        expression.append(src,offset,end-offset-1).append(closeToken);
                        offset = end+ closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    }else {
                        expression.append(src,offset, end-offset);
                        break;
                    }
                }
                if (end == -1) {
                    buffer.append(src, start, src.length-start);
                    offset = src.length;
                }else {
                    buffer.append(handler.handleToken(expression.toString()));
                    offset=end+ closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }while (start > -1);
        if (offset < text.length()){
            buffer.append(src, offset, src.length- offset);
        }
        return buffer.toString();
    }
}
