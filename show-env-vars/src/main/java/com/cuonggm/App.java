package com.cuonggm;

import java.util.Map;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Start App");
        Map<String, String> environmentVariables = System.getenv();
        System.out.println("List of Env Variables");
        for(String key : environmentVariables.keySet()) {
            String value = environmentVariables.get(key);
            System.out.println(String.format("key=\'%s\'; value=\'%s\'", key, value));
        }
        System.out.println("End App");
    }
}
