package com.cuonggm.utils;

public class CredentialsSetup {

    public static void run() {
        String accessKeyId = Property.get("credentials.properties", "aws.access_key_id");
        String secretAccessKey = Property.get("credentials.properties", "aws.secret_access_key");

        System.setProperty("aws.accessKeyId", accessKeyId);
        System.setProperty("aws.secretAccessKey", secretAccessKey);
        
        System.out.println("Init AWS Credentials Successfully");
    }
    
}
