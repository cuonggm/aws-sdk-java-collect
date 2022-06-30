package com.cuonggm.ec2;

import java.util.function.Consumer;

import com.cuonggm.utils.CredentialsSetup;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesResponse;
import software.amazon.awssdk.services.ec2.waiters.Ec2Waiter;

public class StartInstance {
    
    private static final String TARGET_INSTANCE_ID = "i-0aa0a65457817c2e6";

    public static void main(String[] args) {
        // Setup Credentials
        CredentialsSetup.run();
        // Ec2Client
        Ec2Client ec2Client = Ec2Client.builder()
            .region(Region.US_EAST_1)
            .build();
        // Create Request
        StartInstancesRequest request = StartInstancesRequest.builder()
            .instanceIds(TARGET_INSTANCE_ID)
            .build();
        // Execute
        StartInstancesResponse response = ec2Client.startInstances(request);
        System.out.println("hasStartingInstances=" + response.hasStartingInstances());

        // Check status
        Ec2Waiter waiter = ec2Client.waiter();
        DescribeInstancesRequest dRequest = DescribeInstancesRequest.builder()
            .instanceIds(TARGET_INSTANCE_ID)
            .build();
        WaiterResponse<DescribeInstancesResponse> waiterResponse = waiter.waitUntilInstanceRunning(dRequest);
        waiterResponse.matched().response().ifPresent(new Consumer<DescribeInstancesResponse>() {

            @Override
            public void accept(DescribeInstancesResponse t) {
                System.out.println("Successfully Started EC2 Instance");
            }
            
        });
    }
}
