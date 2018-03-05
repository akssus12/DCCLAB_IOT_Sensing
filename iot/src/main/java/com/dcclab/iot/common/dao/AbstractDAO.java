package com.dcclab.iot.common.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractDAO {
	protected Log log = LogFactory.getLog(AbstractDAO.class);

	@Autowired
	private SqlSessionTemplate sqlSession;

	protected void printQueryId(String queryId) {
		if (log.isDebugEnabled()) {
			log.debug("\t QueryId \t : " + queryId);
		}
	}

	public void insert(String QueryId, Object Params) {
		printQueryId(QueryId);
		sqlSession.insert(QueryId, Params);
	}

	public Object update(String QueryId, Object Params) {
		printQueryId(QueryId);
		return sqlSession.update(QueryId, Params);
	}

	public Object delete(String QueryId, Object Params) {
		printQueryId(QueryId);
		return sqlSession.delete(QueryId, Params);
	}

	public Object selectOne(String QueryId) {
		printQueryId(QueryId);
		return sqlSession.selectOne(QueryId);
	}

	public Object selectOne(String QueryId, Object Params) {
		printQueryId(QueryId);
		return sqlSession.selectOne(QueryId, Params);
	}

	@SuppressWarnings("rawtypes")
	public List selectList(String QueryId) {
		printQueryId(QueryId);
		return sqlSession.selectList(QueryId);
	}

	@SuppressWarnings("rawtypes")
	public List selectList(String QueryId, Object Params) {
		printQueryId(QueryId);
		return sqlSession.selectList(QueryId, Params);
	}

}
