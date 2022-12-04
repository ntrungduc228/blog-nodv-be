package nodv.repository;

import nodv.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByReceiverId(String receiverId);
    List<Notification> findByReceiverIdAndIsReadIsTrue(String receiverId);
    List<Notification> findByReceiverIdAndIsReadIsFalse(String receiverId);

    List<Notification> findByReceiverIdAndIsRead(String receiverId, Boolean isRead);
}
