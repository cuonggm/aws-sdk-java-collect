package com.cuonggm.ec2;

import com.cuonggm.utils.CredentialsSetup;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

public class LaunchInstance {
    
    private static final String AMI_UBUNTU_2204_LTS = "ami-052efd3df9dad4825";

    public static void main(String[] args) {
        // Setup Credentials
        CredentialsSetup.run();
        // Ec2Client
        Ec2Client ec2Client = Ec2Client.builder()
            .region(Region.US_EAST_1)
            .build();
        RunInstancesRequest runInstancesRequest = RunInstancesRequest.builder()
            .imageId(AMI_UBUNTU_2204_LTS)
            .instanceType(InstanceType.T2_MICRO)
            .maxCount(1)
            .minCount(1)
            .build();
            RunInstancesResponse runInstancesResponse = ec2Client.runInstances(runInstancesRequest);
            Instance instance = runInstancesResponse.instances().get(0);
            System.out.println(String.format("Successfully started EC2 Instance: %s", instance.instanceTypeAsString()));
            System.out.println("Instance ID: " + instance.instanceId());
            System.out.println("Public IP: " + instance.publicIpAddress());
            System.out.println("Public DNS: " + instance.publicDnsName());
            System.out.println("Private IP: " + instance.privateIpAddress());
            System.out.println("Private DNS: " + instance.privateDnsName());

        // Check status
        
    }
}
