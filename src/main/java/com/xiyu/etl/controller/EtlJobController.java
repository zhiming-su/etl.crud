package com.xiyu.etl.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiyu.etl.model.EtlJob;
import com.xiyu.etl.model.InfoEtlJob;
import com.xiyu.etl.model.PrimaryKeyEtlJob;
import com.xiyu.etl.repository.EtlJobRepository;
import com.xiyu.etl.repository.InfoEtlJobRepository;

@RestController
@Component
public class EtlJobController {

	@Autowired 
	private InfoEtlJobRepository isjr;

	@Autowired 
	private EtlJobRepository sjr;
	
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public Optional<InfoEtlJob> addNewJOB(@RequestParam(name = "WENJIAN_ID", required = true) String WENJIAN_ID,
			@RequestParam(name = "WENJIAN_LX", required = true) String WENJIAN_LX) {

		PrimaryKeyEtlJob pk = new PrimaryKeyEtlJob();
		pk.setWENJIAN_ID(WENJIAN_ID);
		pk.setWENJIAN_LX(WENJIAN_LX);

		return isjr.findById(pk);
		// return "Saved";
	}
	@RequestMapping(value = "/save", method = RequestMethod.GET)
	public String save(@RequestParam(name = "WENJIAN_ID", required = true) String WENJIAN_ID,
			@RequestParam(name = "WENJIAN_LX", required = true) String WENJIAN_LX) {

		PrimaryKeyEtlJob pk = new PrimaryKeyEtlJob();
		pk.setWENJIAN_ID(WENJIAN_ID);
		pk.setWENJIAN_LX(WENJIAN_LX);
		
		EtlJob etljobEntity= new EtlJob();
		Optional<InfoEtlJob> infoetljobEntity= isjr.findById(pk);
		
		etljobEntity.setWENJIAN_ID(infoetljobEntity.get().getWENJIAN_ID());
		etljobEntity.setWENJIAN_LX(infoetljobEntity.get().getWENJIAN_LX());
		etljobEntity.setDESTINATION(infoetljobEntity.get().getDESTINATION());
		etljobEntity.setJOB_ID(infoetljobEntity.get().getJOB_ID());
		etljobEntity.setSTATUS(infoetljobEntity.get().getSTATUS());
		
		sjr.save(etljobEntity);

		return "OK";
		// return "Saved";
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String delete(@RequestParam(name = "WENJIAN_ID", required = true) String WENJIAN_ID,
			@RequestParam(name = "WENJIAN_LX", required = true) String WENJIAN_LX) {

		PrimaryKeyEtlJob pk = new PrimaryKeyEtlJob();
		pk.setWENJIAN_ID(WENJIAN_ID);
		pk.setWENJIAN_LX(WENJIAN_LX);
		
		
		sjr.deleteById(pk);

		return "OK";
		// return "Saved";
	}

}