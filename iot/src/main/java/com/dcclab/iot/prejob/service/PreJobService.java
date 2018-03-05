package com.dcclab.iot.prejob.service;

import com.dcclab.iot.prejob.vo.PreJobVO;

public interface PreJobService {
	
	public void loadCpuInfo(PreJobVO preJobVO) throws Exception;
	
	public void loadRealdataToKafka(String realData) throws Exception;
	
	public void transDataToSpkStreaming(String realData) throws Exception;
	
	public void storeRealdata(String realData) throws Exception; 
}
