package com.xiaoyu.lingdian.impl;

import com.xiaoyu.lingdian.core.mybatis.dao.MyBatisDAO;
import com.xiaoyu.lingdian.core.mybatis.page.Page;
import com.xiaoyu.lingdian.core.mybatis.page.PageRequest;
import com.xiaoyu.lingdian.entity.CoreAoyunCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 奥运二维码表
*/
@Service("coreAoyunCodeService")
public class CoreAoyunCodeServiceImpl implements CoreAoyunCodeService {

	@Autowired
	private MyBatisDAO myBatisDAO;

	@Override
	public boolean insertCoreAoyunCode(CoreAoyunCode coreAoyunCode) {
		myBatisDAO.insert(coreAoyunCode);
		return true;
	}

	@Override
	public boolean updateCoreAoyunCode(CoreAoyunCode coreAoyunCode) {
		myBatisDAO.update(coreAoyunCode);
		return true;
	}

	@Override
	public boolean deleteCoreAoyunCode(CoreAoyunCode coreAoyunCode) {
		myBatisDAO.delete(coreAoyunCode);
		return true;
	}

	@Override
	public boolean deleteBatchByIds(List<String> list ) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("list", list);
		myBatisDAO.delete("deleteBatchCoreAoyunCodeByIds",hashMap);
		return true;
	}

	@Override
	public CoreAoyunCode getCoreAoyunCode(CoreAoyunCode coreAoyunCode) {
		return (CoreAoyunCode) myBatisDAO.findForObject(coreAoyunCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CoreAoyunCode> findCoreAoyunCodeList() {
		return myBatisDAO.findForList("findCoreAoyunCodeForLists", null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<CoreAoyunCode> findCoreAoyunCodePage(int pageNum, int pageSize) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		return myBatisDAO.findForPage("findCoreAoyunCodeForPages", new PageRequest(pageNum, pageSize, hashMap));
	}

}