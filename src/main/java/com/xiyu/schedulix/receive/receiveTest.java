package com.xiyu.schedulix.receive;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import com.xiyu.schedulix.dao.Job;
import com.xiyu.schedulx.api.util.SchedulixCMD;

@Component
public class receiveTest {

	// private producerTest pt=new producerTest();
	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	// private JmsTemplate jmsTemplate;
	private JmsMessagingTemplate jmsMessagingTemplate;

	public Map<String, String> jobID = new HashMap<String, String>();

	// 使用JmsListener配置消费者监听的队列，其中text是接收到的消息
	@JmsListener(destination = "wenjian_id")
	public void receiveQueue(Message message) throws InterruptedException, JMSException {
		TextMessage textMsg = (TextMessage) message;
		String wenjianId = textMsg.getText().replaceAll("\"", "");
		logger.info("receive:" + textMsg.getText());
		message.acknowledge();
		while (true) {
			if (jobID.size() == 2) {
				logger.info("Warning:" + "Reach the current maximum number of jobs, waiting for 5s!!");
				Thread.sleep(5000);
				checkJobStatus();
				// send("wenjian_id_status", new Job(wenjianId, "200"));
			} else {
				break;
			}
		}
		// send("wenjian_id_status", new Job(wenjianId, "200"));
		jobID.put(wenjianId, SchedulixCMD.etlConvert("SYSTEM.TEST", wenjianId));

	}

	public void send(String str, Job job) {
		logger.info("send !!!");
	    jmsMessagingTemplate.convertAndSend(str, job);
	}

	// @SuppressWarnings("rawtypes")
	public void checkJobStatus() {
		// log.info(">> send !!!");
		// jmsTemplate.convertAndSend("wenjian_id", "000"+i);
		// i++;
		logger.info("Check Job Status!");
		// Iterator<Map.Entry<String, String>> jobInfo = jobID.entrySet().iterator();

		// Iterator<Entry<String, String>> jobInfo =jobID.entrySet().iterator();
		for (Iterator<Entry<String, String>> jobInfo = jobID.entrySet().iterator(); jobInfo.hasNext();) {
			Map.Entry<String, String> item = jobInfo.next();
			String key = (String) item.getKey();
			String val = (String) item.getValue();
			String flag = SchedulixCMD.etlConvertResult(val);
			if (flag.equals("success")) {
				send("wenjian_id_status", new Job(key, "200"));
				logger.info("producer:" + " wenjianId: " + key + " jobID: " + val + " 状态ID: " + "200");
				jobInfo.remove();
			} else if (flag.equals("error")) {
				// job.setMsg("失败");
				send("wenjian_id_status", new Job(key, "500"));
				logger.info("producer:" + " wenjianId: " + key + " jobID: " + val + " 状态ID: " + "500");
				jobInfo.remove();
			} else if (flag.equals("cancelled")) {
				// job.setMsg("作业已经取消");
				send("wenjian_id_status", new Job(key, "301"));
				logger.info("producer:" + " wenjianId: " + key + " jobID: " + val + " 状态ID: " + "301");
				jobInfo.remove();
			} else if (flag.equals("keyNotFound")) {
				// job.setMsg("作业ID不存在");
				send("wenjian_id_status", new Job(key, "302"));
				logger.info("producer:" + " wenjianId: " + key + " jobID: " + val + " 状态ID: " + "302");
				jobInfo.remove();
			} else {
				// job.setMsg("正在执行");
				// jmsTemplate.convertAndSend("wenjian_id_status", new Job(key, "300"));
				logger.info("producer:" + " wenjianId: " + key + " jobID: " + val + " 状态ID: " + "300");
			}
		}
		// System.out.println("----------"+jobID.size());

	}
}
