package com.cuonggm.ecs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.cuonggm.utils.Property;

import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.AssignPublicIp;
import software.amazon.awssdk.services.ecs.model.AwsVpcConfiguration;
import software.amazon.awssdk.services.ecs.model.Cluster;
import software.amazon.awssdk.services.ecs.model.DescribeTasksRequest;
import software.amazon.awssdk.services.ecs.model.DescribeTasksResponse;
import software.amazon.awssdk.services.ecs.model.LaunchType;
import software.amazon.awssdk.services.ecs.model.NetworkConfiguration;
import software.amazon.awssdk.services.ecs.model.RunTaskRequest;
import software.amazon.awssdk.services.ecs.model.RunTaskResponse;
import software.amazon.awssdk.services.ecs.model.TaskDefinition;
import software.amazon.awssdk.services.ecs.waiters.EcsWaiter;

/**
 * RunFargateByTaskDefinition
 */
public final class RunTaskWithEnvVarFile {

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

    // Task Definition ARN
    private static final String TASK_DEFINITION_ARN = "arn:aws:ecs:us-east-1:375395022000:task-definition/RunWithEnvVarsFile:2";

    /**
     * Main App
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
        // Create ECSClient
        EcsClient ecsClient = EcsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(SystemPropertyCredentialsProvider.create())
            .build();
        // Get Cluster
        Cluster cluster = Cluster.builder()
            .clusterArn("arn:aws:ecs:us-east-1:375395022000:cluster/DemoFargateCluster")
            .build();
        // Create TaskDefinition
        TaskDefinition taskDefinition = TaskDefinition.builder()
            .taskDefinitionArn(TASK_DEFINITION_ARN)
            .build();
        // Create AwsVPCConfiguration
        AwsVpcConfiguration vpcConfiguration = AwsVpcConfiguration.builder()
            .subnets(SUBNETS)
            .securityGroups(SECURITY_GROUPS)
            .assignPublicIp(AssignPublicIp.ENABLED)
            .build();
        // Create NetworkingConfiguration
        NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
            .awsvpcConfiguration(vpcConfiguration)
            .build();
        // Create RunTaskRequest
        RunTaskRequest runTaskRequest = RunTaskRequest.builder()
            .taskDefinition(taskDefinition.taskDefinitionArn())
            .cluster(cluster.clusterArn())
            .launchType(LaunchType.FARGATE)
            .count(1)
            .networkConfiguration(networkConfiguration)
            .build();
        // Execute Run Task
        System.out.println("Started Task");
        RunTaskResponse runTaskResponse = ecsClient.runTask(runTaskRequest);
        // Wait until running task successfully

        // Check status until become STOPPED
        DescribeTasksRequest describeTasksRequest = DescribeTasksRequest.builder()
            .cluster(cluster.clusterArn())
            .tasks(runTaskResponse.tasks().get(0).taskArn())
            .build();
        EcsWaiter waiter = ecsClient.waiter();
        WaiterResponse<DescribeTasksResponse> wResponse = waiter.waitUntilTasksStopped(describeTasksRequest);
        wResponse.matched().response().ifPresent(new Consumer<DescribeTasksResponse>() {

            @Override
            public void accept(DescribeTasksResponse t) {
                System.out.println("Finished Task");
            }
            
        });
    }
}
