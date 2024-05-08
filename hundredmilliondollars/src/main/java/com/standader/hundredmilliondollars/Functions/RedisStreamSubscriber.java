package com.standader.hundredmilliondollars.Functions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

import com.standader.hundredmilliondollars.Services.MessagePublisher;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisStreamSubscriber {
    private final MessagePublisher messagePublisher;
    @Autowired
    private RedisConnectionFactory connectionFactory;

    
    public RedisStreamSubscriber(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    public Subscription subscribeToStream(String streamKey, String consumerGroupName, String consumerId) {
        // System.out.println("Subscribe to start");
        StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options = StreamMessageListenerContainerOptions.builder()
            .pollTimeout(Duration.ofSeconds(1))
            .targetType(String.class)
            .build();
        // System.out.println("After Listner Container Options");
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer = StreamMessageListenerContainer.create(connectionFactory, options);
        
        // System.out.println("After Listner Container ");
        StreamListener<String, ObjectRecord<String, String>> streamListener = message -> {
            System.out.println("Received message from stream: " + message.getStream() + " Value: " + message.getValue());
            String message_content = message.getValue();
            try {
                messagePublisher.publish(message_content);
            } catch(Exception e) {
                System.err.println("Error: Sink Publish: " + e.getLocalizedMessage());
            }
        };
        // System.out.println("After listening object");

        Subscription subscription = listenerContainer.receive(Consumer.from(consumerGroupName, consumerId), StreamOffset.create(streamKey, ReadOffset.lastConsumed()), streamListener);
        try {
            listenerContainer.start();
        } catch(Exception e) {
            System.err.println("Error in Subscribe Listening Container in Redis" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        System.out.println("Subscribed-----------------------------------------------------");
        

        return subscription;
    }
}
