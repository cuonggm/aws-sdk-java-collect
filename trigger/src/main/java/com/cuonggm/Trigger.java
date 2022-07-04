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

    // Prepare for SendCommandRequest
    private static String instanceId = null;

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        // Start App Log
        System.out.println("\nSTART APP");
        // Get Env Vars
        instanceId = System.getenv("instanceId");

        // Loop in messages
        List<SQSMessage> messages = input.getRecords();
        if (messages != null) {
            System.out.println("Count of Message: " + messages.size());
            for (SQSMessage message : messages) {
                System.out.println("Message Body: " + message.getBody());
                System.out.println("Count of Attributes: " + message.getAttributes().size());
                for (String key : message.getAttributes().keySet()) {
                    String value = message.getAttributes().get(key);
                    System.out.println(String.format("Message Attribute: '%s'='%s'", key, value));
                }
                handleMessage(message);
            }
        }
        System.out.println("END APP\n");
        return null;
    }

    private static void handleMessage(SQSMessage message) {
        // Get command
        String cmd = message.getMessageAttributes().get("command").getStringValue();
        // Get paramPrefix
        String paramPrefix = message.getMessageAttributes().get("paramPrefix").getStringValue();
        System.out.println("paramPrefix=" + paramPrefix);
        // Get target attributes
        Map<String, String> attributes = new HashMap<>();
        for (String key : message.getMessageAttributes().keySet()) {
            // Filter by paramPrefix
            if (key.startsWith(paramPrefix)) {
                attributes.put(key, message.getMessageAttributes().get(key).getStringValue());
            }
        }
        // Create SsmClient. No need Credentials Provider
        SsmClient ssmClient = SsmClient.builder()
                .region(Region.US_EAST_1)
                .build();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        LocalDateTime now = LocalDateTime.now();

        // Setup envCommand with command params
        for (String key : attributes.keySet()) {
            String param = key + "|" + attributes.get(key);
            cmd += " " + param;
        }
        // Trim result cmd
        cmd = cmd.trim();
        // Prepare params
        Map<String, List<String>> params = new HashMap<>();
        List<String> paramContent = new ArrayList<>();
        params.put("commands", paramContent);
        // Set command content
        paramContent.add(cmd);

        SendCommandRequest sendCommandRequest = SendCommandRequest.builder()
                .documentName("AWS-RunShellScript")
                .instanceIds(instanceId)
                .comment("Run at" + now.format(formatter))
                .parameters(params)
                .build();
        ssmClient.sendCommand(sendCommandRequest);
        System.out.println(String.format("Ran command '%s' in instance '%s' at '%s'", cmd, instanceId,
                now.format(formatter)));
    }

}
