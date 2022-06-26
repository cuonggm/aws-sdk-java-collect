package com.cuonggm.send_command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;
import software.amazon.awssdk.services.ssm.model.SendCommandResponse;

/**
 * Hello world!
 */
public final class Ssm {

    public static String instanceId = "";
    public static String command = "";

    private Ssm() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        // Start App
        System.out.println("Start App");
        CredentialsProvider.setupAwsCredentials();
        SsmClient ssmClient = SsmClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(SystemPropertyCredentialsProvider.create())
            .build();

        instanceId = PropertyReader.getProperty("config.properties", "instanceid");
        command = PropertyReader.getProperty("config.properties", "command");
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
