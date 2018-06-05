package com.xiyu.schedulix.producer;



import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.xiyu.schedulix.dao.Job;
import com.xiyu.schedulix.receive.receiveTest;
import com.xiyu.schedulx.api.util.SchedulixCMD;

  

//@Component
//@EnableScheduling
public class producerTest {

   
     Logger logger = LoggerFactory.getLogger(getClass());
   // @Autowired
    //private Queue queue;
    private  Map<String, String> jobID=new receiveTest().jobID;
    @Autowired
    // private JmsMessagingTemplate jmsMessagingTemplate;
     private  JmsTemplate jmsTemplate;
   @SuppressWarnings("rawtypes")
//@Scheduled(fixedDelay = 10)
    // 每3s执行1次
    public  void checkJobStatus() {
      //  log.info(">> send !!!");
    	//jmsTemplate.convertAndSend("wenjian_id", "000"+i);
    	//i++;
	
	   Iterator<Entry<String, String>> jobInfo =jobID.entrySet().iterator();
	   while (jobInfo.hasNext()) {
		    logger.info("开始检测作业状态！");
			Map.Entry entry = (Map.Entry) jobInfo.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			String flag = SchedulixCMD.etlConvertResult(val);
			if (flag.equals("success")) {
				jmsTemplate.convertAndSend("wenjian_id_status", new Job(key, "200"));
				logger.info("发送的报文为:" +" wenjianId: "+ key + " jobID: "+val+" 状态ID: " + "200");
				jobID.remove(key);
			} else if (flag.equals("error")) {
				// job.setMsg("失败");
				jmsTemplate.convertAndSend("wenjian_id_status", new Job(key, "500"));			
				logger.info("发送的报文为:" +" wenjianId: "+ key + " jobID: "+val+" 状态ID: " + "500");
				jobID.remove(key);
			} else if (flag.equals("cancelled")) {
				// job.setMsg("作业已经取消");
				jmsTemplate.convertAndSend("wenjian_id_status", new Job(key, "301"));			
				logger.info("发送的报文为:" +" wenjianId: "+ key + " jobID: "+val+" 状态ID: " + "301");
				jobID.remove(key);
			} else if (flag.equals("keyNotFound")) {
				// job.setMsg("作业ID不存在");
				jmsTemplate.convertAndSend("wenjian_id_status", new Job(key, "302"));
				logger.info("发送的报文为:" +" wenjianId: "+ key + " jobID: "+val+" 状态ID: " + "302");
				jobID.remove(key);
			} else {
				// job.setMsg("正在执行");
				//jmsTemplate.convertAndSend("wenjian_id_status", new Job(key, "300"));
				logger.info("发送的报文为:" +" wenjianId: "+ key + " jobID: "+val+" 状态ID: " + "300");
			}
		}
	  
    	
    }

}