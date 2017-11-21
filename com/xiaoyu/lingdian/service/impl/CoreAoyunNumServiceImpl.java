package com.xiaoyu.lingdian.impl;

import com.xiaoyu.lingdian.core.mybatis.dao.MyBatisDAO;
import com.xiaoyu.lingdian.core.mybatis.page.Page;
import com.xiaoyu.lingdian.core.mybatis.page.PageRequest;
import com.xiaoyu.lingdian.entity.CoreAoyunNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 奥运编号表
*/
@Service("coreAoyunNumService")
public class CoreAoyunNumServiceImpl implements CoreAoyunNumService {

	@Autowired
	private MyBatisDAO myBatisDAO;

	@Override
	public boolean insertCoreAoyunNum(CoreAoyunNum coreAoyunNum) {
		myBatisDAO.insert(coreAoyunNum);
		return true;
	}

	@Override
	public boolean updateCoreAoyunNum(CoreAoyunNum coreAoyunNum) {
		myBatisDAO.update(coreAoyunNum);
		return true;
	}

	@Override
	public boolean deleteCoreAoyunNum(CoreAoyunNum coreAoyunNum) {
		myBatisDAO.delete(coreAoyunNum);
		return true;
	}

	@Override
	public boolean deleteBatchByIds(List<String> list ) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("list", list);
		myBatisDAO.delete("deleteBatchCoreAoyunNumByIds",hashMap);
		return true;
	}

	@Override
	public CoreAoyunNum getCoreAoyunNum(CoreAoyunNum coreAoyunNum) {
		return (CoreAoyunNum) myBatisDAO.findForObject(coreAoyunNum);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CoreAoyunNum> findCoreAoyunNumList() {
		return myBatisDAO.findForList("findCoreAoyunNumForLists", null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<CoreAoyunCode> findCoreAoyunNumPage(int pageNum, int pageSize) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		return myBatisDAO.findForPage("findCoreAoyunNumForPages", new PageRequest(pageNum, pageSize, hashMap));
	}

}