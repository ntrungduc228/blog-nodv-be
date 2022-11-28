package nodv.service;

import nodv.model.Notification;
import nodv.model.User;
import nodv.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    UserService userService;

    public List<Notification> findByReceiverId(String receiverId,Boolean isRead) throws Exception {
        List<Notification> notification;
        if(isRead){
            notification=notificationRepository.findByReceiverIdAndIsReadIsTrue(receiverId);
        }else if(!isRead){
            notification=notificationRepository.findByReceiverIdAndIsReadIsFalse(receiverId);
        }else{
            notification=notificationRepository.findByReceiverId(receiverId);
        }

        if(notification.size()==0) {
            throw new Exception("Notification not found");
        }
        return notification;
    }

    public Notification createNotification(Notification notification,String userId){
        User user = userService.findById(userId);
        String receiverId = notification.getReceiverId();
        User receiver = userService.findById(receiverId);
        notification.setSender(user);
        notification.setIsRead(false);
        notification.setSenderId(userId);
        notification.setReceiverId(receiverId);
        notification.setReceiver(receiver);

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
