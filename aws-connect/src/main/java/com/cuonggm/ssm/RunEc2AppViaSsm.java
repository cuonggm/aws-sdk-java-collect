package com.cuonggm.ssm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cuonggm.utils.Property;

import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;

/**
 * RunEc2AppViaSsm
 */
public final class RunEc2AppViaSsm {

    /**
     * Main Method
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        // Start App Log
        System.out.println("Start App");
        // Store Credentials
        System.out.println("Storing accessKeyId and secretAccessKey");
        // Set accessKeyId
        System.setProperty("aws.accessKeyId", Property.get("credentials.properties", "aws.access_key_id"));
        // Set secretAccessKey
        System.setProperty("aws.secretAccessKey", Property.get("credentials.properties", "aws.secret_access_key"));
        System.out.println("Store keypair successfully");
        // Create Ec2Client
        // Ec2Client client = Ec2Client.builder()
        //     .region(Region.US_EAST_1)
        //     .credentialsProvider(SystemPropertyCredentialsProvider.create())
        //     .build();

        SsmClient ssmClient = SsmClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(SystemPropertyCredentialsProvider.create())
            .build();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        LocalDateTime now = LocalDateTime.now();

        Map<String, List<String>> params = new HashMap<>();
        List<String> paramContent = new ArrayList<>();
        params.put("commands", paramContent);
        
        // Set command content
        paramContent.add("/home/ssm-user/now.sh");

        SendCommandRequest sendCommandRequest = SendCommandRequest.builder()
            .documentName("AWS-RunShellScript")
            .instanceIds("i-0aa0a65457817c2e6")
            .comment("Run at" + now.format(formatter))
            .parameters(params)
            .build();
        ssmClient.sendCommand(sendCommandRequest);
        System.out.println("End App");
    }
}
