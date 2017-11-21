package com.xiaoyu.lingdian.service;

import com.xiaoyu.lingdian.core.mybatis.page.Page;
import com.xiaoyu.lingdian.entity.CoreAoyunNum;

import java.util.List;

/**
* 奥运编号表
*/
public interface CoreAoyunNumService {

	/**
	* 添加
	* @param coreAoyunNum
	* @return
	*/
	public boolean insertCoreAoyunNum(CoreAoyunNum coreAoyunNum);

	/**
	* 修改
	* @param coreAoyunNum
	* @return
	*/
	public boolean updateCoreAoyunNum(CoreAoyunNum coreAoyunNum);

	/**
	* 删除
	* @param coreAoyunNum
	* @return
	*/
	public boolean deleteCoreAoyunNum(CoreAoyunNum coreAoyunNum);

	/**
	* 批量删除
	* @param list
	* @return boolean
	*/
	public boolean deleteBatchByIds(List<String> list);

	/**
	* 查询
	* @param coreAoyunNum
	* @return
	*/
	public CoreAoyunNum getCoreAoyunNum(CoreAoyunNum coreAoyunNum);

	/**
	* 查询所有List
	* @return List
	*/
	public List<CoreAoyunNum> findCoreAoyunNumList();

	/**
	* 查询所有Page
	* @return Page
	*/
	public Page<CoreAoyunNum> findCoreAoyunNumPage(int pageNum, int pageSize);

}