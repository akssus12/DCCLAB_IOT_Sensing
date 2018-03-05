package com.dcclab.iot.prejob.dao;

import org.springframework.stereotype.Repository;
import com.dcclab.iot.common.dao.AbstractDAO;
import com.dcclab.iot.prejob.vo.PreJobVO;

@Repository("preJobDao")
public class PreJobDao extends AbstractDAO {
	public void insertCpuInfo(PreJobVO preJobVO) {
		insert("dcclab.insertCpuInfo", preJobVO);
	}
	
	public void insertRealdata(String realData) {
		insert("dcclab.insertRealdata",realData);
	}
}
