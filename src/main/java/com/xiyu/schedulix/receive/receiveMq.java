package com.xiyu.schedulix.receive;

import java.util.Calendar;
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

import com.xiyu.schedulix.api.util.GetRunTime;
import com.xiyu.schedulix.api.util.SchedulixCMD;
import com.xiyu.schedulix.controller.SchedulixJobController;
import com.xiyu.schedulix.model.Job;

@Component
public class receiveMq {

	// private producerTest pt=new producerTest();
	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	// private JmsTemplate jmsTemplate;
	private JmsMessagingTemplate jmsMessagingTemplate;
	@Autowired
    private SchedulixJobController sjc;
	public Map<String, String> jobID = new HashMap<String, String>();
	public Map<String, Long> wenjianTime = new HashMap<String, Long>();

	// 使用JmsListener配置消费者监听的队列，其中text是接收到的消息
	@JmsListener(destination = "wenjian_id")
	public void receiveQueue(Message message) throws InterruptedException, JMSException {
		TextMessage textMsg = (TextMessage) message;
		String wenjianId = textMsg.getText().replaceAll("\"", "");
		// logger.info("receive:" + textMsg.getText());
		message.acknowledge();
		//sjc.addNewJOB(wenjianId, "2222", "200");
		while (true) {
			if (jobID.size() == 2) {
				logger.info("Warning:" + "Reach the current maximum number of jobs, waiting for 5s!!");
				Thread.sleep(5000);
				checkJobStatus();
				killAndCancelJob();
				// send("wenjian_id_status", new Job(wenjianId, "200"));
			} else {
				break;
			}
		}
		logger.info("submit wenjian id :" + textMsg.getText());
		// send("wenjian_id_status", new Job(wenjianId, "200"));
		String schedulixJobID = SchedulixCMD.etlConvert("SYSTEM.TEST", wenjianId);
		jobID.put(wenjianId, schedulixJobID);
		wenjianTime.put(schedulixJobID, Calendar.getInstance().getTimeInMillis());

	}

	public void send(String str, Job job) {
		logger.info("send !!!");
		jmsMessagingTemplate.convertAndSend(str, job);
	}

	// @SuppressWarnings("rawtypes")
	public void checkJobStatus() {
		for (Iterator<Entry<String, String>> jobInfo = jobID.entrySet().iterator(); jobInfo.hasNext();) {
			Map.Entry<String, String> item = jobInfo.next();
			String key = (String) item.getKey();
			String val = (String) item.getValue();
			String flag = SchedulixCMD.etlConvertResult(val);
			if (flag.equals("success")) {
				//send("wenjian_id_status", new Job(key, "200"));
				sjc.addNewJOB(key, val, "200");
				logger.info("INSERT DB:" + " wenjianId: " + key + " jobID: " + val + " statusID: " + "200");
				jobInfo.remove();
			} else if (flag.equals("error")) {
				// job.setMsg("失败");
				//send("wenjian_id_status", new Job(key, "500"));
				sjc.addNewJOB(key, val, "500");
				logger.info("INSERT DB:" + " wenjianId: " + key + " jobID: " + val + " statusID: " + "500");
				jobInfo.remove();
			} else if (flag.equals("cancelled")) {
				// job.setMsg("作业已经取消");
				//send("wenjian_id_status", new Job(key, "301"));
				sjc.addNewJOB(key, val, "301");
				logger.info("INSERT DB:" + " wenjianId: " + key + " jobID: " + val + " statusID: " + "301");
				jobInfo.remove();
			} else if (flag.equals("keyNotFound")) {
				// job.setMsg("作业ID不存在");
				//send("wenjian_id_status", new Job(key, "302"));
				sjc.addNewJOB(key, val, "302");
				logger.info("INSERT DB:" + " wenjianId: " + key + " jobID: " + val + " statusID: " + "302");
				jobInfo.remove();
			} else {
				// job.setMsg("正在执行");
				// jmsTemplate.convertAndSend("wenjian_id_status", new Job(key, "300"));
				logger.info("INFO(not insert db):" + " wenjianId: " + key + " jobID: " + val + " statusID: " + "300");
			}
		}
		// System.out.println("----------"+jobID.size());

	}

	public void killAndCancelJob() {
		for (Iterator<Entry<String, Long>> jobInfo = wenjianTime.entrySet().iterator(); jobInfo.hasNext();) {
			Map.Entry<String, Long> item = jobInfo.next();
			String key = (String) item.getKey();
			long val = item.getValue();
			long runingTime = Calendar.getInstance().getTimeInMillis() - val;
			String dbTime = GetRunTime.formatTime(runingTime);
			if (Double.parseDouble(dbTime) >= 25) {
				if(SchedulixCMD.killEtlJob(key) && SchedulixCMD.cancelErrorJob(key)) {
					logger.info("JOBID: "+key+" INFO: TIMEOUT TO KILL");
					jobInfo.remove();
				}
				
			} else {
				//
			}

		}
	}
}
