package main.java.ConfLoader;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvironmentLoader {

    private static final String CONFIG_FILE = "variables.conf";
    private static final String configPath = "src/resources/variables.conf";

    private static class GatlingConfig {
            private String scnEnable;
            private String percent;

        public String getScnEnable() {
            return scnEnable;
        }

        public void setScnEnable(String scnEnable) {
            this.scnEnable = scnEnable;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }
    }

    public static Map<Object, Object> loadProperties() {
        Properties props = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(configPath))) {
            props.load(reader);
            return new HashMap<>(props);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении конфигурации: " + configPath, e);
        }
    }

    /*
    Set env from config file. At the moment it`s realised by hardcore,
    but it will be done with parameterization all env which started with "gatling.*"
    */
    private static GatlingConfig parseGatlingConfig(Map<Object, Object> config) {
        return new GatlingConfig() {{
            setScnEnable((String) config.get("gatling.scn.enable"));
            setPercent((String) config.get("gatling.percent"));
        }};
    }

    //Only for debug
    public static void printGatlingConfig() {
        Map<Object,Object> config = loadProperties();
        GatlingConfig gatlingConfig = parseGatlingConfig(config);

        System.out.println(gatlingConfig.getPercent() + gatlingConfig.getScnEnable());
    };

    public static Map<String, String> addToSession() {
        Map<Object,Object> config = loadProperties();
        GatlingConfig gatlingConfig = parseGatlingConfig(config);
        //printGatlingConfig();

        //Parse all getters and their values into Map. After all return map for usage in gatling
        Map<String,String> mapOfGetters = new HashMap<>();
        Method[] methods = gatlingConfig.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get") && method.getParameterCount() == 0 && !method.getName().equals("getClass")) {
                try {
                    String variableName = method.getName().substring(3);
                    variableName = Character.toLowerCase(variableName.charAt(0)) + variableName.substring(1);
                    String value = method.invoke(gatlingConfig).toString();
                    mapOfGetters.put(variableName, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mapOfGetters;
    }
}
