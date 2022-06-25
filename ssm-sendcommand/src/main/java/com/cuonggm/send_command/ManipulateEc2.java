package com.cuonggm.send_command;

import java.util.List;

import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceStatusRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceStatusResponse;
import software.amazon.awssdk.services.ec2.model.InstanceStatus;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;

public class ManipulateEc2 {
    public static void main(String[] args) {
        System.out.println("Start Manipulating");

        // Setup credentials to access AWS Service
        CredentialsProvider.setupAwsCredentials();

        String instanceId = PropertyReader.getProperty("config.properties", "manipulate_ec2.instance_id");

        // Create EC2 Client
        Ec2Client client = Ec2Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(SystemPropertyCredentialsProvider.create())
            .build();

        // Manipulating
        startInstance(client, instanceId);
        
        // Check Instance Status
        while(true) {
            try {
                Thread.sleep(3000);
                checkEc2Status(client, instanceId);
            } catch (InterruptedException e) {
                System.out.println("Sleep Error: " + e.getMessage());
            }
        }
        
        // System.out.println("End Manipulating");
    }

    public static void startInstance(Ec2Client client, String instanceId) {
        StartInstancesRequest startInstancesRequest = StartInstancesRequest.builder()
            .instanceIds(instanceId)
            .build();
        
        client.startInstances(startInstancesRequest);
        System.out.println("Requested to start EC2: " + instanceId);
    }

    public static void checkEc2Status(Ec2Client client, String instanceId) {
        DescribeInstanceStatusRequest request = DescribeInstanceStatusRequest.builder()
            .instanceIds(instanceId)
            .includeAllInstances(true)
            .build();

        DescribeInstanceStatusResponse response = client.describeInstanceStatus(request);
        List<InstanceStatus> statuses = response.instanceStatuses();
        System.out.println("Status count: " + statuses.size());
        for (InstanceStatus status : statuses) {
            System.out.println("Instance ID: " + status.instanceId());
            System.out.println("AZ: " + status.availabilityZone());
            System.out.println("InstanceState NameAsString: " + status.instanceState().nameAsString());
            System.out.println("");
        }
    }
}
