package com.standader.hundredmilliondollars.Controller.Main.Chat;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.RSocket;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import com.standader.hundredmilliondollars.Services.*;

import ch.qos.logback.core.util.Duration;

import com.standader.hundredmilliondollars.Functions.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.standader.hundredmilliondollars.DTO.MainDTO.ChatDTO.*;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final RedisUtil redisUtil;
    private final RedisStreamSubscriber redisStreamSubscriber;
    private Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();
    private final MessagePublisher messagePublisher;
    public ChatController(ChatService chatService, RedisUtil redisUtil, RedisStreamSubscriber redisStreamSubscriber, MessagePublisher messagePublisher) {
        this.chatService = chatService;
        this.redisUtil = redisUtil;
        this.redisStreamSubscriber = redisStreamSubscriber;
        this.messagePublisher = messagePublisher;
    }
    @MutationMapping
    public ChatRoomGenerationRequest chatRoomGenerationRequest(@Argument(name="room_id") String room_id, @Argument(name = "participated_users") List<String> participated_users) {
        System.out.println("Server Mutation");
        Boolean succeeded = chatService.chatRoomGeneration(room_id, participated_users);
        ChatRoomGenerationRequest chatRoomGenerationRequest = new ChatRoomGenerationRequest(succeeded);
        return chatRoomGenerationRequest;
    }

    @MutationMapping
    public ChatRoomFetchRequest chatRoomFetchRequest(@Argument(name = "user_id") String user_id) {
        List<String> participated_chat_rooms = chatService.chatRoomFetch(user_id);
        Boolean succeeded = false;
        if ( participated_chat_rooms.isEmpty() || participated_chat_rooms != null) {
            succeeded = true;
        }
        ChatRoomFetchRequest chatRoomFetchRequest = new ChatRoomFetchRequest(succeeded, participated_chat_rooms);
        return chatRoomFetchRequest;
    }
    @MutationMapping
    public ChatRoomReEnterRequest chatRoomReEnterRequest(@Argument(name = "room_id") String room_id) {
        try {
            List<MapRecord<String, Object, Object>> messageStream = redisUtil.getAllMessages(room_id);
            List<String> jsonStream = chatService.chatRoomReEnterStreamToJson(messageStream);
            Boolean succeeded = true;
            ChatRoomReEnterRequest chatRoomReEnterRequest = new ChatRoomReEnterRequest(jsonStream, succeeded);
            return chatRoomReEnterRequest;
        } catch(Exception e) {
            ChatRoomReEnterRequest chatRoomReEnterRequest = new ChatRoomReEnterRequest(null, null);
            return chatRoomReEnterRequest;
        }
    }

    @MessageMapping("test")
    public Mono<String> test() {
        return Mono.just("Hello");
    }
    @MessageMapping("chat.{roomId}")
    public Flux<String> chatRoomPublish(Mono<Message> message ,@DestinationVariable("roomId") String roomId) {
        System.out.println("Stream executed");
        System.out.println(subscriptions.size() + "Subscriptions Size");
        message.doOnNext(message_obj -> {
            String message_content = message_obj.getMessage();
            redisUtil.setStreamKeyValue(roomId, message_content);
        }).subscribe();

        return messagePublisher.getMessages();
    }


    @ConnectMapping("setup.{roomId}")
    public void handleConnection(@DestinationVariable("roomId") String roomId, RSocketRequester requester) {
        System.out.println("Connection Mapping");
        System.out.println("RoomID: " + roomId);
        if (!subscriptions.containsKey(requester.toString())) {
            String consumerId = UUID.randomUUID().toString();
            redisUtil.createConsumerGroup(roomId, "my-group");
            Subscription subscription = redisStreamSubscriber.subscribeToStream(roomId, "my-group", consumerId);
            subscriptions.put(requester.toString(), subscription);
            System.out.println("request not overlap");
        } else {
            System.out.println("request overlap");
        }
        
        requester.rsocket().onClose().doFinally(signalType -> {
            System.out.println("Socket Disconnected for any reason" + signalType);
            Subscription sub_removed = subscriptions.remove(requester.toString());
            if (sub_removed != null) {
                sub_removed.cancel();
            }
        }).subscribe();
    }
}

