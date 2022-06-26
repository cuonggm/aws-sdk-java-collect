package com.cuonggm.my_cli;

public class CredentialsProvider {
    
    public static void setupAwsCredentials() {
        String accessKeyId = PropertyReader.getProperty("credentials.properties", "aws.access_key_id");
        String secretAccessKey = PropertyReader.getProperty("credentials.properties", "aws.secret_access_key");

        System.setProperty("aws.accessKeyId", accessKeyId);
        System.setProperty("aws.secretAccessKey", secretAccessKey);
        
        System.out.println("Init AWS Credentials Successfully");
        // System.out.println(System.getProperty("aws.accessKeyId"));
        // System.out.println(System.getProperty("aws.secretAccessKey"));
    }

}
