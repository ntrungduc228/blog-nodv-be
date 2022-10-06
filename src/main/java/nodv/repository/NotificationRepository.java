package nodv.repository;

import nodv.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    @Query(value = "{'receiver.id: ?0'}")
    List<Notification> findByReceiverId(String id);
}
