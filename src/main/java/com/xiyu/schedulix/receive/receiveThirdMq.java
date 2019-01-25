package com.xiyu.schedulix.receive;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xiyu.schedulix.api.util.GetRunTime;
import com.xiyu.schedulix.api.util.SchedulixCMD;
import com.xiyu.schedulix.controller.SchedulixJobController;
//import com.xiyu.schedulix.model.WenJian;

@Component
@Configuration
@ConditionalOnProperty(name = "xiyu.mq.type", havingValue = "activemq")
@EnableScheduling
public class receiveThirdMq {


	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SchedulixJobController sjc;

	public Map<String, String> jobThirdID = Collections.synchronizedMap(new HashMap<String, String>());
	public Map<String, Long> wenjianTime = new HashMap<String, Long>();
	@Value("${xiyu.etl.schedulix.maxjob}")
	private int maxjob = 5;
	private String destination = "finanace-third-etl";
	private String wenjianType="NULL";
	
	// 使用@KafkaListener配置消费者监听的队列，其中text是接收到的消息
	@JmsListener(destination = "finanace-third-etl")
	public void receiveQueue(Message message) throws InterruptedException, JMSException, JSONException {
		// flag=true;
		TextMessage getTextMsg = (TextMessage) message;
		String textMsg = getTextMsg.getText();
		JSONObject js = new JSONObject(textMsg);
		String wenjianId = js.getString("wenjianId");
		String jobPath = null;
		wenjianType = js.getString("wenjianLxBm");
		logger.info("third submit wenjian id :" + textMsg);
		// send("wenjian_id_status", new Job(wenjianId, "200"));
		jobPath="SYSTEM.HUATAI_WB_JOB.WB_SHUJU_BATCH";
		String schedulixJobID = SchedulixCMD.etlConvert(jobPath, wenjianId );
		jobThirdID.put(wenjianId, schedulixJobID);
		//wenjianTime.put(schedulixJobID, Calendar.getInstance().getTimeInMillis());
		sjc.addNewJOB(wenjianId, wenjianType,schedulixJobID, "300",destination);
		while (true) {
			if (jobThirdID.size() == maxjob) {

				Thread.sleep(3000);
	
			} else {
				break;
			}
		}

	}

	// @SuppressWarnings("rawtypes")
	@Scheduled(fixedDelay = 10)
	public void checkJobStatus() {
		//Map<String, String> jobIDCheck = jobID;
	
		synchronized(jobThirdID) {
		for (Iterator<Entry<String, String>> jobInfo = jobThirdID.entrySet().iterator(); jobInfo.hasNext();) {
			Map.Entry<String, String> item = jobInfo.next();
			String key = (String) item.getKey();
			String val = (String) item.getValue();
			String flag = SchedulixCMD.etlConvertResult(val);
			if (flag.equals("success")) {
				// send("wenjian_id_status", new Job(key, "200"));
				sjc.addNewJOB(key,wenjianType, val, "200",destination);
				logger.info("third INSERT DB:"+"destination: "+destination + " wenjianId: " + key + " jobID: " + val + " statusID: " + "200");
				jobInfo.remove();
				//jobID.remove(key);
			} else if (flag.equals("error")) {
				// job.setMsg("失败");
				// send("wenjian_id_status", new Job(key, "500"));
				sjc.addNewJOB(key,wenjianType, val, "500",destination);
				logger.info("third INSERT DB:"+"destination: "+destination  + " wenjianId: " + key + " jobID: " + val + " statusID: " + "500");
				jobInfo.remove();
				SchedulixCMD.cancelErrorJob(val);
				logger.info("third  jobID: " + val + "  Cancled Error JOB!!");
				//jobID.remove(key);
			} else if (flag.equals("cancelled")) {
				// job.setMsg("作业已经取消");
				// send("wenjian_id_status", new Job(key, "301"));
				sjc.addNewJOB(key,wenjianType, val, "301",destination);
				logger.info("third INSERT DB:" +"destination: "+destination + " wenjianId: " + key + " jobID: " + val + " statusID: " + "301");
				jobInfo.remove();
				//jobID.remove(key);
			} else if (flag.equals("keyNotFound")) {
				// job.setMsg("作业ID不存在");
				// send("wenjian_id_status", new Job(key, "302"));
				sjc.addNewJOB(key,wenjianType, val, "302",destination);
				logger.info("third INSERT DB:" +"destination: "+destination + " wenjianId: " + key + " jobID: " + val + " statusID: " + "302");
				jobInfo.remove();
				//jobID.remove(key);
			} else {
				// job.setMsg("正在执行");
				//sjc.addNewJOB(key, val, "300",destination);
				// jmsTemplate.convertAndSend("wenjian_id_status", new Job(key, "300"));
				// logger.info("INFO(not insert db):" + " wenjianId: " + key + " jobID: " + val
				// + " statusID: " + "300");
			}
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
			// logger.info("JOBID: " + key + " INFO: TIME " +Double.parseDouble(dbTime));
			if (Double.parseDouble(dbTime) >= 25) {
				if (SchedulixCMD.killEtlJob(key) && SchedulixCMD.cancelErrorJob(key)) {
					logger.info("JOBID: " + key + " INFO: TIMEOUT TO KILL");
					jobInfo.remove();
				}

			} else {
				//
			}

		}
	}
}
