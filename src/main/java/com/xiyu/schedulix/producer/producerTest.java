package com.xiyu.schedulix.producer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xiyu.schedulix.Email;  
  

//@Component
//@EnableScheduling
public class producerTest {

    @Autowired
   // private JmsMessagingTemplate jmsMessagingTemplate;
    private JmsTemplate jmsTemplate;
    private Integer i=1;
   // @Autowired
    //private Queue queue;

   @Scheduled(fixedDelay = 10)
    // 每3s执行1次
    public void send() {
      //  log.info(">> send !!!");
    	jmsTemplate.convertAndSend("test", new Email("info@example.com", "Hello"+i));
    	i++;
    }

}