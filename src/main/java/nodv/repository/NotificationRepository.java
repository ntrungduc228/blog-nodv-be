package nodv.repository;

import nodv.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByReceiverId(String receiverId);
    List<Notification> findByReceiverIdAndIsReadIsTrue(String receiverId);
    List<Notification> findByReceiverIdAndIsReadIsFalse(String receiverId);
}
