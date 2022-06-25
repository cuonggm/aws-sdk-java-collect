package com.cuonggm.send_command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

    public static String getProperty(String propertiesFile, String id) {
        String property = "";

        try (InputStream input = App.class.getClassLoader().getResourceAsStream(propertiesFile)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value
            property = prop.getProperty(id);
            System.out.println(String.format("Getting property '%s': %s", id, property));
            return property;

        } catch (IOException ex) {
            System.out.println("Getting Property Error: " + ex.getMessage());
            return null;
        }
    }
    
}