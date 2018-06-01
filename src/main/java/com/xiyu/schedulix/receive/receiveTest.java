package com.xiyu.schedulix.receive;

import org.springframework.jms.annotation.JmsListener;  
import org.springframework.stereotype.Component;

import com.xiyu.schedulix.Email;  
  
@Component  
public class receiveTest {  
     // 使用JmsListener配置消费者监听的队列，其中text是接收到的消息  
    @JmsListener(destination = "test")  
    public void receiveQueue(Email text) throws InterruptedException {  
        System.out.println("收到的报文为:"+text); 
        Thread.sleep(10000);
    }  
} 
