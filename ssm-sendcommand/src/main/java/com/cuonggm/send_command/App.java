package com.cuonggm.send_command;

public class App {
    public static void main(String[] args) {

        // Check if have some params or not
        if(args.length <= 0) {
            System.out.println("Need provide params...");
            return;
        }

        // Print all params as command
        String params = "";
        for (String param : args) {
            params += param + " ";
        }
        System.out.println("Command: " + params);

        // Convert args to lowercase
        for(int i=0; i< args.length; i++) {
            args[i] = args[i].toLowerCase();
        }

        // Setup Credentials to manipulate with AWS Services
        CredentialsProvider.setupAwsCredentials();

        // EC2 Start Instance
        if(args.length==3 && args[0].equals("ec2") && args[1].equals("start")) {
            new Ec2().start(args[2]);
        }  
    }
}
