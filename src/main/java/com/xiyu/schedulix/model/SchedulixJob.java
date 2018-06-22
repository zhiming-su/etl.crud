package com.xiyu.schedulix.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ETL_JOB")
public class SchedulixJob {
	@Id
	private String WENJIAN_ID;
	private String DESTINATION;
    private String JOB_ID;
    private String STATUS;

    public String getDESTINATION() {
		return DESTINATION;
	}
	public void setDESTINATION(String dESTINATION) {
		DESTINATION = dESTINATION;
	}
	public String getWENJIAN_ID() {
		return WENJIAN_ID;
	}
	public void setWENJIAN_ID(String wENJIAN_ID) {
		WENJIAN_ID = wENJIAN_ID;
	}
	public String getJOB_ID() {
		return JOB_ID;
	}
	public void setJOB_ID(String jOB_ID) {
		JOB_ID = jOB_ID;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

    
}