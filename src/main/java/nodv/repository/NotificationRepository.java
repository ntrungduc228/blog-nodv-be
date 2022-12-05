package nodv.repository;

import nodv.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface NotificationRepository extends MongoRepository<Notification, String> {
//    List<No>
    List<Notification> findByReceiverId(String receiverId, Pageable pageable);
    List<Notification> findByReceiverIdAndIsReadIsTrue(String receiverId);
    List<Notification> findByReceiverIdAndIsReadIsFalse(String receiverId);
//    @Query(sort = "{ createdDate : -1 }")
    List<Notification> findByReceiverIdAndIsRead(String receiverId, Boolean valueOf, Pageable pageable );
}
