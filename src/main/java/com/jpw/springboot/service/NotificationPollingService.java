package com.jpw.springboot.service;


import com.jpw.springboot.model.Notification;
import com.jpw.springboot.model.Post;
import com.jpw.springboot.repositories.NotificationRepository;
import com.jpw.springboot.util.SystemConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.transaction.Transactional;

@Service("NotificationPollingService")
@Transactional
public class NotificationPollingService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
	
    @Autowired
    SocialService socialService;
	
    public void createNotification(Post post) {
        try {
	        Notification notification;
	        List<String> targets = socialService.getConnectionsEntityId(post.getUserId(), SystemConstants.FOLLOWING_CONNECTION);//post.getTaggedEntityId();
	
	        //notificationRepository.save(new Notification(post.getUserId(),targets,"NEW",post.getPostText()));
	
	        // creating notifications for each of the followers and storing in db
	        for (String target : targets) {
	            notification = notificationRepository.save(new Notification(post.getUserId(), target, "NEWS", post.getPostText()));
	
	            //TODO: post the notification to respective user
	
	            sendSpecific(notification);
	            
	        }
        } catch (Exception e) {
            e.printStackTrace();
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
