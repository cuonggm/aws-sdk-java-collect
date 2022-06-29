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
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.AssignPublicIp;
import software.amazon.awssdk.services.ecs.model.AwsVpcConfiguration;
import software.amazon.awssdk.services.ecs.model.Cluster;
import software.amazon.awssdk.services.ecs.model.Compatibility;
import software.amazon.awssdk.services.ecs.model.ContainerDefinition;
import software.amazon.awssdk.services.ecs.model.CreateClusterRequest;
import software.amazon.awssdk.services.ecs.model.CreateClusterResponse;
import software.amazon.awssdk.services.ecs.model.DescribeTasksRequest;
import software.amazon.awssdk.services.ecs.model.DescribeTasksResponse;
import software.amazon.awssdk.services.ecs.model.KeyValuePair;
import software.amazon.awssdk.services.ecs.model.LaunchType;
import software.amazon.awssdk.services.ecs.model.ListClustersResponse;
import software.amazon.awssdk.services.ecs.model.LogConfiguration;
import software.amazon.awssdk.services.ecs.model.LogDriver;
import software.amazon.awssdk.services.ecs.model.NetworkConfiguration;
import software.amazon.awssdk.services.ecs.model.NetworkMode;
import software.amazon.awssdk.services.ecs.model.OSFamily;
import software.amazon.awssdk.services.ecs.model.RegisterTaskDefinitionRequest;
import software.amazon.awssdk.services.ecs.model.RegisterTaskDefinitionResponse;
import software.amazon.awssdk.services.ecs.model.RunTaskRequest;
import software.amazon.awssdk.services.ecs.model.RunTaskResponse;
import software.amazon.awssdk.services.ecs.model.RuntimePlatform;
import software.amazon.awssdk.services.ecs.model.Task;
import software.amazon.awssdk.services.ecs.model.TaskDefinition;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;
import software.amazon.awssdk.services.ssm.model.SendCommandResponse;
import software.amazon.awssdk.services.ssm.waiters.SsmWaiter;

/**
 * RunEc2AppViaSsm
 */
public final class RunEc2AppViaSsm {

    // To specify ID
    private static final long currentTimeMillis = System.currentTimeMillis();
    // Cluster Name
    private static final String CLUSTER_NAME = "cluster-name-" + currentTimeMillis;
    // TaskDefinitionFamily
    private static final String TASK_DEFINITION_FAMILY = "task-definition-family-" + currentTimeMillis;
    // ContainerDefinitionName
    private static final String CONTAINER_DEFINITION_NAME = "container-definition-name-" + currentTimeMillis;
    // Docker Image URI (ECR)
    private static final String IMAGE_URI = "public.ecr.aws/g6d1h5m3/show-env-vars";
    /**
     * Environment Variables must not contain space.
     */
    private static final List<KeyValuePair> ENVIRONMENT_VARIABLES = new ArrayList<KeyValuePair>() {{
        add(KeyValuePair.builder().name("aaa").value("aaa").build());
        add(KeyValuePair.builder().name("bbb").value("bbb").build());
        add(KeyValuePair.builder().name("ccc").value("ccc").build());
        add(KeyValuePair.builder().name("xxx").value("xxx").build());
        add(KeyValuePair.builder().name("yyy").value("yyy").build());
        add(KeyValuePair.builder().name("zzz").value("zzz").build());
    }};

    // Specify subnet. No need specify VPC
    private static final List<String> SUBNETS = new ArrayList<String>() {{
        add("subnet-0fdbde6c78a1dea3b");
        add("subnet-0782a18628ecc3bcb");
        add("subnet-0d80a9460017006fd");
        add("subnet-0b6c614b65eb69b42");
        add("subnet-041bbb0c795903a61");
        add("subnet-0d203386ace56a902");
    }};
    private static final List<String> SECURITY_GROUPS = new ArrayList<String>() {{
        add("sg-015cdc7fa41c0ff29");
    }};

    // Sleep time between Loops (ms)
    private static final long LOOP_SLEEP_TIME = 1000;

    /**
     * This role must have 2 policy:
     * 1. logs:CreateLogGroup for awslogs-create-group
     * 2. ecsTaskExecutionRole
     */
    private static final String EXECUTION_ROLE_ARN = "arn:aws:iam::375395022000:role/RoleCreateLogGroupAndExecuteTask";

    /**
     * Says hello to the world.
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
