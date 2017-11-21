package com.xiaoyu.lingdian.service;

import com.xiaoyu.lingdian.core.mybatis.page.Page;
import com.xiaoyu.lingdian.entity.CoreAoyunCode;

import java.util.List;

/**
* 奥运二维码表
*/
public interface CoreAoyunCodeService {

	/**
	* 添加
	* @param coreAoyunCode
	* @return
	*/
	public boolean insertCoreAoyunCode(CoreAoyunCode coreAoyunCode);

	/**
	* 修改
	* @param coreAoyunCode
	* @return
	*/
	public boolean updateCoreAoyunCode(CoreAoyunCode coreAoyunCode);

	/**
	* 删除
	* @param coreAoyunCode
	* @return
	*/
	public boolean deleteCoreAoyunCode(CoreAoyunCode coreAoyunCode);

	/**
	* 批量删除
	* @param list
	* @return boolean
	*/
	public boolean deleteBatchByIds(List<String> list);

	/**
	* 查询
	* @param coreAoyunCode
	* @return
	*/
	public CoreAoyunCode getCoreAoyunCode(CoreAoyunCode coreAoyunCode);

	/**
	* 查询所有List
	* @return List
	*/
	public List<CoreAoyunCode> findCoreAoyunCodeList();

	/**
	* 查询所有Page
	* @return Page
	*/
	public Page<CoreAoyunCode> findCoreAoyunCodePage(int pageNum, int pageSize);

}