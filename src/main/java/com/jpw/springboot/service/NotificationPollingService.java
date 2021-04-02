package com.jpw.springboot.service;


import com.jpw.springboot.model.Notification;
import com.jpw.springboot.model.Post;
import com.jpw.springboot.repositories.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("NotificationPollingService")
@Transactional
public class NotificationPollingService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void createNotification(Post post) {

        Notification notification;
        String[] targets = post.getTaggedEntityId();

        //notificationRepository.save(new Notification(post.getUserId(),targets,"NEW",post.getPostText()));

        // creating notifications for each of the followers and storing in db
        for (String target : targets) {
            notification = notificationRepository.save(new Notification(post.getUserId(), target, "NEW", post.getPostText()));

            //TODO: post the notification to respective user
            try {
                sendSpecific(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendSpecific(@Payload Notification msg) throws Exception {
//        OutputMessage out = new OutputMessage(
//                msg.getFrom(),
//                msg.getText(),
//                new SimpleDateFormat("HH:mm").format(new Date()));
        simpMessagingTemplate.convertAndSendToUser(
                msg.getTarget(), "/user/queue/specific-user", msg);
    }

    //TODO: process each notification of status NEW
}
