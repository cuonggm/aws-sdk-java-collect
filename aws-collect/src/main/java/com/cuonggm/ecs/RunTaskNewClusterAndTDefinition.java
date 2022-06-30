package com.cuonggm.ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.cuonggm.utils.Property;

import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
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
import software.amazon.awssdk.services.ecs.model.TaskDefinition;
import software.amazon.awssdk.services.ecs.waiters.EcsWaiter;

/**
 * RunTaskNewClusterAndTDefinition
 */
public final class RunTaskNewClusterAndTDefinition {

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
        // Create ECSClient
        EcsClient ecsClient = EcsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(SystemPropertyCredentialsProvider.create())
            .build();
        // Get list of cluster
        ListClustersResponse listClustersResponse = ecsClient.listClusters();
        // Show list of cluster
        System.out.println("List of Cluster");
        for(String clusterArn : listClustersResponse.clusterArns()) {
            System.out.println("ClusterARN: " + clusterArn);
        }
        // Create Cluster
        // Create CreateClusterRequest
        CreateClusterRequest createClusterRequest = CreateClusterRequest.builder()
            .clusterName(CLUSTER_NAME)
            .build();
        CreateClusterResponse createClusterResponse = ecsClient.createCluster(createClusterRequest);
        Cluster cluster = createClusterResponse.cluster();
        // Show CreateClusterResponse
        System.out.println(String.format("Created cluster:\n\tCluster Name: \'%s\'\n\tCluster ARN: \'%s\'", 
            cluster.clusterName(), 
            cluster.clusterArn()));
        // Create Task 
        // Create Log Options - for log to console
        Map<String, String> logOptions = new HashMap<String, String>() {{
            put("awslogs-group", "/ecs/" + TASK_DEFINITION_FAMILY);
            put("awslogs-region", "us-east-1");
            put("awslogs-stream-prefix", "ecs");
            put("awslogs-create-group", "true");
        }};
        // Create LogConfiguration
        LogConfiguration logConfiguration = LogConfiguration.builder()
            .logDriver(LogDriver.AWSLOGS)
            .options(logOptions)
            .build();
        // Create ContainerDefinition
        ContainerDefinition containerDefinition = ContainerDefinition.builder()
            .name(CONTAINER_DEFINITION_NAME)
            .image(IMAGE_URI)
            .environment(ENVIRONMENT_VARIABLES)
            .logConfiguration(logConfiguration)
            .build();
        // Create RuntimePlatform
        RuntimePlatform runtimePlatform = RuntimePlatform.builder()
            .operatingSystemFamily(OSFamily.LINUX)
            .build();
        // Create RegisterTaskDefinition
        RegisterTaskDefinitionRequest registerTaskDefinitionRequest = RegisterTaskDefinitionRequest.builder()
            .family(TASK_DEFINITION_FAMILY)
            .requiresCompatibilities(Compatibility.FARGATE)
            .runtimePlatform(runtimePlatform)
            .networkMode(NetworkMode.AWSVPC)
            .executionRoleArn(EXECUTION_ROLE_ARN)
            .cpu(".25 vCPU")
            .memory("0.5 GB")
            .containerDefinitions(containerDefinition)
            .build();
        RegisterTaskDefinitionResponse registerTaskDefinitionResponse = ecsClient.registerTaskDefinition(registerTaskDefinitionRequest);
        // Get TaskDefinition
        TaskDefinition taskDefinition = registerTaskDefinitionResponse.taskDefinition();
        // Show RegisterTaskDefinitionResponse
        System.out.println("Created task definition successfully");
        System.out.println(String.format("--- TaskDef.family=\'%s\' ---", taskDefinition.family()));
        System.out.println(String.format("--- TaskDef.cpu=\'%s\' ---", taskDefinition.cpu()));
        System.out.println(String.format("--- TaskDef.momory=\'%s\' ---", taskDefinition.memory()));
        // Run Task
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
        RunTaskResponse runTaskResponse = ecsClient.runTask(runTaskRequest);
        // Wait until running task successfully
        DescribeTasksRequest describeTasksRequest = DescribeTasksRequest.builder()
            .cluster(CLUSTER_NAME)
            .tasks(runTaskResponse.tasks().get(0).taskArn())
            .build();
        EcsWaiter waiter = ecsClient.waiter();
        WaiterResponse<DescribeTasksResponse> dWaiterResponse = waiter.waitUntilTasksStopped(describeTasksRequest);
        dWaiterResponse.matched().response().ifPresent(new Consumer<DescribeTasksResponse>() {

            @Override
            public void accept(DescribeTasksResponse t) {
                System.out.println("Successfully Finished Task");
            }
            
        });
    }
}
