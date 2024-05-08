package com.standader.hundredmilliondollars.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Service;
import com.standader.hundredmilliondollars.Models.*;
import com.standader.hundredmilliondollars.Repository.*;
import com.google.gson.Gson;
import com.standader.hundredmilliondollars.Functions.*;
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomUserMappingRepository chatRoomUserMappingRepository;
    private final RedisUtil redisUtil;
    private final Gson gson = new Gson();
    ChatService(ChatRoomRepository chatRoomRepository, ChatRoomUserMappingRepository chatRoomUserMappingRepository, UserRepository userRepository, RedisUtil redisUtil) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomUserMappingRepository = chatRoomUserMappingRepository;
        this.userRepository = userRepository;
        this.redisUtil = redisUtil;
    }

    public Boolean chatRoomGeneration(String room_id, List<String> participated_users) {
        ChatRoom chatRoom = new ChatRoom(room_id);
        try {
            System.out.println("System called");
            for (String participated_user : participated_users) {
                Optional<User> userOptional = userRepository.findByUserId(participated_user);
                System.out.println(userOptional);
                if (userOptional.isPresent()) {
                    System.out.println("Username present in ChatRoomGeneration");
                    User user = userOptional.get();
                    chatRoomRepository.save(chatRoom);
                    ChatRoomUserMapping chatRoomUserMapping = new ChatRoomUserMapping(user, chatRoom);
                    chatRoomUserMappingRepository.save(chatRoomUserMapping);
                }else {
                    return false;
                }
            }
        } catch(Exception e) {
            return false;
        }
        return true;
    }
    public List<String> chatRoomFetch(String user_id) {
        try {
            Optional<User> userOptional = userRepository.findByUserId(user_id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                List<ChatRoomUserMapping> chatRoomUserMappingOptional = chatRoomUserMappingRepository.findByUser(user);
                if (!chatRoomUserMappingOptional.isEmpty()) {
                    System.out.println("ChatROom Fetch NOt Empty");
                    List<String> chatRoomList = new ArrayList<String>();
                    for (ChatRoomUserMapping chatRoomUserMapping : chatRoomUserMappingOptional) {
                        chatRoomList.add(chatRoomUserMapping.getChatRoom().getRoomId());
                        
                    }
                    return chatRoomList;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch(Exception e) {
            System.out.println("ChatRoomFetch Error" + e.getLocalizedMessage());
            return null;
        }
    }
    public List<String> chatRoomReEnterStreamToJson(List<MapRecord<String, Object, Object>> messageStream) {
        List<String> jsonStreamArray = new ArrayList<String>();
        for (MapRecord<String,Object, Object> messageStreamObject : messageStream) {
            Map<Object, Object> messageStreamValue = messageStreamObject.getValue();
            String jsonStream = gson.toJson(messageStreamValue);
            jsonStreamArray.add(jsonStream);
        }
        return jsonStreamArray;
    }
    
    
    
}
