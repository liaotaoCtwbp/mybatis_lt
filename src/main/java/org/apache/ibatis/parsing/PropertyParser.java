package org.apache.ibatis.parsing;

import java.util.Properties;

public class PropertyParser {
    private static final String KEY_PREFIX = "org.apache.ibatis.parsing.PropertyParser.";

    public static final String KEY_ENABLE_DEFULT_VALUE = KEY_PREFIX + "enable-default-value";

    public static final String KEY_DEFAULT_VALUE_SEPARATOR = KEY_PREFIX + "default-value-separator";

    private static final String ENABLE_DEFAULT_VALUE = "false";
    private static final String DEFAULT_VALUE_SEPARATOR=":";

    private PropertyParser(){

    }

    public static String parse(String string, Properties variables) {
        VariableTokenHander variableTokenHander = new VariableTokenHander(variables);
        GenericTokenParser genericTokenParser = new GenericTokenParser("${", "}", variableTokenHander);
        return genericTokenParser.parse(string);
    }

    private static class VariableTokenHander implements TokenHandler{
        private final Properties variables;
        private final boolean enableDefaultValue;
        private final String defaultValueSeparator;

        public VariableTokenHander(Properties variables) {
            this.variables = variables;
            this.enableDefaultValue = Boolean.parseBoolean(getPropertyValue(KEY_ENABLE_DEFULT_VALUE, ENABLE_DEFAULT_VALUE));
            this.defaultValueSeparator = getPropertyValue(KEY_DEFAULT_VALUE_SEPARATOR, DEFAULT_VALUE_SEPARATOR);
        }

        private String getPropertyValue(String key, String defaultValue){
            return (variables == null)? defaultValue:variables.getProperty(key, defaultValue);
        }

        @Override
        public String handleToken(String content) {
            if (variables != null) {
                String key = content;
                if(enableDefaultValue) {
                    final int separatorIndex = content.indexOf(defaultValueSeparator);
                    String defaultValue = null;
                    if (separatorIndex >= 0) {
                        key = content.substring(0, separatorIndex);
                        defaultValue = content.substring(separatorIndex+defaultValueSeparator.length());
                    }

                    if (defaultValue != null) {
                        return variables.getProperty(key, defaultValue);
                    }
                }

                if (variables.containsKey(key)){
                    return variables.getProperty(key);
                }
            }
            return "${" + content + "}";
        }
    }
}
