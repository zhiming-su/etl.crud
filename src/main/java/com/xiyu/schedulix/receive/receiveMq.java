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
public class receiveMq {

	// private producerTest pt=new producerTest();
	Logger logger = LoggerFactory.getLogger(getClass());
	// @Autowired
	// private JmsTemplate jmsTemplate;
	// private JmsMessagingTemplate jmsMessagingTemplate;
	@Autowired
	private SchedulixJobController sjc;
	//public Map<String, String> jobID =new HashMap<String, String>();
	public Map<String, String> jobID = Collections.synchronizedMap(new HashMap<String, String>());
	public Map<String, Long> wenjianTime = new HashMap<String, Long>();
	@Value("${xiyu.etl.schedulix.maxjob}")
	private int maxjob = 5;
	private String destination = "finanace-etl-convert";
	private String wenjianType="NULL";

	// 使用JmsListener配置消费者监听的队列，其中text是接收到的消息
	@JmsListener(destination = "finanace-etl-convert")
	public void receiveQueue(Message message) throws InterruptedException, JMSException, JSONException {
		TextMessage getTextMsg = (TextMessage) message;
		String textMsg = getTextMsg.getText();
		JSONObject js = new JSONObject(textMsg);
		String wenjianId = js.getString("wenjianId");
		String jobPath = null;
		wenjianType = js.getString("wenjianLxBm");
		logger.info("finanace submit wenjian id :" + textMsg);
		// send("wenjian_id_status", new Job(wenjianId, "200"));
		jobPath="SYSTEM."+"HUATAI_YX_"+wenjianType+".HUATAI_YX_BATCH_"+wenjianType;
		String schedulixJobID = SchedulixCMD.etlConvert(jobPath, wenjianId );
		jobID.put(wenjianId, schedulixJobID);
		//wenjianTime.put(schedulixJobID, Calendar.getInstance().getTimeInMillis());
		sjc.addNewJOB(wenjianId, wenjianType,schedulixJobID, "300",destination);
		while (true) {
			if (jobID.size() == maxjob) {
				// logger.info("Warning:" + "Reach the current maximum number of jobs, waiting
				// for 5s!!");
				Thread.sleep(3000);
				// checkJobStatus();
				//killAndCancelJob();
				// mqSize = GetActiveMqSize.getMqSize();
				// send("wenjian_id_status", new Job(wenjianId, "200"));
			} else {
				break;
			}
		}

	}

	// @SuppressWarnings("rawtypes")
	@Scheduled(fixedDelay = 10)
	public void checkJobStatus() {
		//Map<String, String> jobIDCheck = jobID;
	
		synchronized(jobID) {
		for (Iterator<Entry<String, String>> jobInfo = jobID.entrySet().iterator(); jobInfo.hasNext();) {
			Map.Entry<String, String> item = jobInfo.next();
			String key = (String) item.getKey();
			String val = (String) item.getValue();
			String flag = SchedulixCMD.etlConvertResult(val);
			if (flag.equals("success")) {
				// send("wenjian_id_status", new Job(key, "200"));
				sjc.addNewJOB(key, wenjianType,val, "200",destination);
				logger.info("finanace INSERT DB:"+"destination: "+destination + " wenjianId: " + key + " jobID: " + val + " statusID: " + "200");
				jobInfo.remove();
				//jobID.remove(key);
			} else if (flag.equals("error")) {
				// job.setMsg("失败");
				// send("wenjian_id_status", new Job(key, "500"));
				sjc.addNewJOB(key,wenjianType, val, "500",destination);
				logger.info("INSERT DB:"+"destination: "+destination  + " wenjianId: " + key + " jobID: " + val + " statusID: " + "500");
				jobInfo.remove();
				SchedulixCMD.cancelErrorJob(val);
				logger.info("finanace  jobID: " + val + "  Cancled Error JOB!!");
				//jobID.remove(key);
			} else if (flag.equals("cancelled")) {
				// job.setMsg("作业已经取消");
				// send("wenjian_id_status", new Job(key, "301"));
				sjc.addNewJOB(key,wenjianType, val, "301",destination);
				logger.info("finanace INSERT DB:" +"destination: "+destination + " wenjianId: " + key + " jobID: " + val + " statusID: " + "301");
				jobInfo.remove();
				//jobID.remove(key);
			} else if (flag.equals("keyNotFound")) {
				// job.setMsg("作业ID不存在");
				// send("wenjian_id_status", new Job(key, "302"));
				sjc.addNewJOB(key,wenjianType, val, "302",destination);
				logger.info("finanace INSERT DB:" +"destination: "+destination + " wenjianId: " + key + " jobID: " + val + " statusID: " + "302");
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
