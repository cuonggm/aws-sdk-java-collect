package com.cuonggm.send_command;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;
import software.amazon.awssdk.services.ssm.model.SendCommandResponse;

/**
 * Hello world!
 */
public final class App {

    public static String instanceId = "";
    public static String command = "";

    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        // Load properties
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            instanceId = prop.getProperty("instanceid");
            command = prop.getProperty("command");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Start App
        System.out.println("Start App");
        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();


        runCommand(ssmClient);
        ssmClient.close();
        System.out.println("End App\n");
    }

    public static void runCommand(SsmClient ssmClient) {
        System.out.println("Start Command");

        // ShellScripts
        Map<String, ArrayList<String>> params = new HashMap<>();
        ArrayList<String> commandContents = new ArrayList<>();
        commandContents.add(command);
        params.put("commands", commandContents);

        SendCommandRequest sendCommandRequest = SendCommandRequest.builder()
        .instanceIds(instanceId)
        .documentName("AWS-RunShellScript")
        .parameters(params)
        .build();

        SendCommandResponse sendCommandResponse = ssmClient.sendCommand(sendCommandRequest);

        System.out.println("CommandId: " + sendCommandResponse.command().commandId());
        System.out.println("CommandComment: " + sendCommandResponse.command().comment());
        System.out.println("Commands: " + sendCommandResponse.command().parameters().get("commands"));

        System.out.println("End Command");
    }
}
