package com.standader.hundredmilliondollars.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.standader.hundredmilliondollars.Models.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomUserMappingRepository extends JpaRepository<ChatRoomUserMapping, Long>{
    List<ChatRoomUserMapping> findByUser(User user);
}
