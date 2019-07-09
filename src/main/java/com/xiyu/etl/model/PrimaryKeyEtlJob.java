package com.xiyu.etl.model;

import java.io.Serializable;


public class PrimaryKeyEtlJob implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String WENJIAN_ID;
	private String WENJIAN_LX;

	
	public String getWENJIAN_ID() {
		return WENJIAN_ID;
	}


	public void setWENJIAN_ID(String wENJIAN_ID) {
		WENJIAN_ID = wENJIAN_ID;
	}


	public String getWENJIAN_LX() {
		return WENJIAN_LX;
	}


	public void setWENJIAN_LX(String wENJIAN_LX) {
		WENJIAN_LX = wENJIAN_LX;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
