package nodv.service;

import nodv.model.Notification;
import nodv.model.User;
import nodv.repository.NotificationRepository;
import nodv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    UserService userService;

    public List<Notification> findByReceiverId(String receiverId) throws Exception {
        System.out.println(receiverId);
        System.out.println(notificationRepository.findAll().get(0).getReceiverId());
        List<Notification> notification = notificationRepository.findByReceivedIdIsReadIsTrue(receiverId,false);
        System.out.println(notification);
        if(notification.size()==0) {
            throw new Exception("Notification not found");
        }
        return notification;
    }

    public Notification createNotification(Notification notification,String userId){

        notification.setIsRead(false);
        notification.setReceiverId(userId);

        return notificationRepository.save(notification);}
    public Notification updateNotification(String id) throws Exception {
      Optional<Notification> updateNotification = notificationRepository.findById(id);
        if (updateNotification.isEmpty()) {
            throw new Exception("notification not found");
        }
        updateNotification.get().setIsRead(true);
        return notificationRepository.save(updateNotification.get());
    }

}
