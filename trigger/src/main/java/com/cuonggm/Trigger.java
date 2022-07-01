package com.cuonggm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;

/**
 * App
 */
public class Trigger implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        // Get Env Vars
        String envCommand = System.getenv("command");
        // String envAccessKeyId = System.getProperty("accessKeyId");
        // String envSecretAccessKey = System.getenv("secretAccessKey");
        String envInstanceId = System.getenv("instanceId");

        // Check message
        List<SQSMessage> messages = input.getRecords();
        if (messages != null) {
            System.out.println("Count of Message: " + messages.size());
            for (SQSMessage message : messages) {
                System.out.println("Message Name: " + message.getBody());
                System.out.println("Count of Attributes: " + message.getAttributes().size());
                for (String key : message.getAttributes().keySet()) {
                    String value = message.getAttributes().get(key);
                    System.out.println(String.format("Key=%s,value=%s", key, value));
                }
            }
        }

        // Start App Log
        System.out.println("Start App");
        // Store Credentials
        // System.out.println("Storing accessKeyId and secretAccessKey");
        // Set accessKeyId
        // System.setProperty("aws.accessKeyId", envAccessKeyId);
        // Set secretAccessKey
        // System.setProperty("aws.secretAccessKey", envSecretAccessKey);
        // System.out.println("Store keypair successfully");
        // Create Ec2Client
        // Ec2Client client = Ec2Client.builder()
        // .region(Region.US_EAST_1)
        // .credentialsProvider(SystemPropertyCredentialsProvider.create())
        // .build();

        SsmClient ssmClient = SsmClient.builder()
                .region(Region.US_EAST_1)
                // .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        LocalDateTime now = LocalDateTime.now();

        Map<String, List<String>> params = new HashMap<>();
        List<String> paramContent = new ArrayList<>();
        params.put("commands", paramContent);

        // Set command content
        paramContent.add(envCommand);

        SendCommandRequest sendCommandRequest = SendCommandRequest.builder()
                .documentName("AWS-RunShellScript")
                .instanceIds(envInstanceId)
                .comment("Run at" + now.format(formatter))
                .parameters(params)
                .build();
        ssmClient.sendCommand(sendCommandRequest);
        System.out.println(String.format("Ran command '%s' in instance '%s' at '%s'", envCommand, envInstanceId,
                now.format(formatter)));
        System.out.println("End App");

        return null;
    }

}
