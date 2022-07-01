package com.cuonggm;

import java.util.List;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

/**
 * App
 */
public class Trigger implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        List<SQSMessage> messages = input.getRecords();
        System.out.println("Count of Message: " + messages.size());
        for(SQSMessage message : messages) {
            System.out.println("Message Name: " + message.getBody());
            System.out.println("Count of Attributes: " + message.getAttributes().size());
            for(String key : message.getAttributes().keySet()) {
                String value = message.getAttributes().get(key);
                System.out.println(String.format("Key=%s,value=%s", key, value));
            }
        }
        return null;
    }
    
}
