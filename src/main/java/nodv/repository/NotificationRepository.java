package nodv.repository;

import nodv.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
//    @Query(value = "{'receiver.id: ?0'}")
    List<Notification> findByReceivedId(String receiverId);
    @Query(value = "{'receivedId' : ?0, 'isRead': ?1}")
    List<Notification> findByReceivedIdIsReadIsTrue(String receiverId, boolean isRead);
}
