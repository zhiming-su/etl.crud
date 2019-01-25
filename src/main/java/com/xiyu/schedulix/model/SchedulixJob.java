package com.xiyu.schedulix.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="ETL_JOB")
@IdClass(PrimaryKeySchedulixJob.class)
public class SchedulixJob {
	
	@Id
	@Column(name = "WENJIAN_ID")
	private String WENJIAN_ID;
	
	@Id
	@Column(name = "WENJIAN_LX")
	private String WENJIAN_LX;
	
	@Column(name = "DESTINATION")
	private String DESTINATION;
	
	@Column(name = "JOB_ID")
    private String JOB_ID;
	
	@Column(name = "STATUS")
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

	public String getWENJIAN_LX() {
		return WENJIAN_LX;
	}
	public void setWENJIAN_LX(String wENJIAN_LX) {
		WENJIAN_LX = wENJIAN_LX;
	}

    
}