package com.cuonggm;

import java.util.List;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.Cluster;
import software.amazon.awssdk.services.ecs.model.DeleteClusterRequest;
import software.amazon.awssdk.services.ecs.model.DeleteClusterResponse;
import software.amazon.awssdk.services.ecs.model.DeregisterTaskDefinitionRequest;
import software.amazon.awssdk.services.ecs.model.DeregisterTaskDefinitionResponse;
import software.amazon.awssdk.services.ecs.model.DescribeClustersRequest;
import software.amazon.awssdk.services.ecs.model.DescribeClustersResponse;
import software.amazon.awssdk.services.ecs.model.ListClustersResponse;
import software.amazon.awssdk.services.ecs.model.ListTaskDefinitionsRequest;
import software.amazon.awssdk.services.ecs.model.ListTaskDefinitionsResponse;
import software.amazon.awssdk.services.ecs.model.TaskDefinitionStatus;

/**
 * DeleteClusterDeregisterTDef
 */
public final class DeleteClusterDeregisterTDef {

    // Cluster Name Prefix
    private static final String CLUSTER_NAME_PREFIX = "cluster-name-";
    // Cluster Name Prefix
    private static final String TASK_DEFINITIONS_PREFIX = "task-definition-family-";
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
        // Get ClusterArns
        ListClustersResponse listClustersResponse = ecsClient.listClusters();
        List<String> clusterArns = listClustersResponse.clusterArns();
        // Get Clusters
        DescribeClustersRequest describeClustersRequest = DescribeClustersRequest.builder()
            .clusters(clusterArns)
            .build();
        DescribeClustersResponse describeClustersResponse = ecsClient.describeClusters(describeClustersRequest);
        List<Cluster> clusters = describeClustersResponse.clusters();
        int deletedClusterCount = 0;
        for(Cluster cluster : clusters) {
            if(cluster.clusterName().startsWith(CLUSTER_NAME_PREFIX)) {
                deleteCluster(ecsClient, cluster.clusterArn());
                deletedClusterCount += 1;
            }
        }
        System.out.println(String.format("Deleted %d cluster", deletedClusterCount));
        // Deregister Task Denifitions
        ListTaskDefinitionsRequest listTaskDefinitionsRequest = ListTaskDefinitionsRequest.builder()
            .status(TaskDefinitionStatus.ACTIVE)
            .build();
        ListTaskDefinitionsResponse listTDRes = ecsClient.listTaskDefinitions(listTaskDefinitionsRequest);
        for(String tdArn : listTDRes.taskDefinitionArns()) {
            if(tdArn.startsWith(TASK_DEFINITIONS_PREFIX)) {
                deregisterTaskDenifition(ecsClient, tdArn);
            }
        }
        System.out.println(String.format("Deregistered %d task definitions" , listTDRes.taskDefinitionArns().size()));
    }

    public static void deleteCluster(EcsClient ecsClient, String clusterArn) {
        DeleteClusterRequest req = DeleteClusterRequest.builder()
            .cluster(clusterArn)
            .build();
        // System.out.println("Deleting clusterArn: " + req.cluster());
        DeleteClusterResponse res = ecsClient.deleteCluster(req);
        System.out.println("Deleted cluster: " + res.cluster().clusterName());
    }

    public static void deregisterTaskDenifition(EcsClient ecsClient, String taskDefinition) {
        DeregisterTaskDefinitionRequest deregisterTaskDefinitionRequest = DeregisterTaskDefinitionRequest.builder()
            .taskDefinition(taskDefinition)
            .build();
        // System.out.println("Deregisting TaskDef: " + taskDefinition);
        DeregisterTaskDefinitionResponse deregisterTaskDefinitionResponse = ecsClient.deregisterTaskDefinition(deregisterTaskDefinitionRequest);
        System.out.println("Deregisted TaskDef: " + deregisterTaskDefinitionResponse.taskDefinition().family());
    }
}
