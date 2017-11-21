package com.xiaoyu.lingdian.controller;

import java.util.Date;
import com.xiaoyu.lingdian.controller.BaseController;
import com.xiaoyu.lingdian.core.mybatis.page.Page;
import com.xiaoyu.lingdian.entity.CoreAoyunNum;
import com.xiaoyu.lingdian.tool.RandomUtil;
import com.xiaoyu.lingdian.tool.StringUtil;
import com.xiaoyu.lingdian.tool.out.ResultMessageBuilder;
import com.xiaoyu.lingdian.vo.CoreAoyunNumVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
* 奥运编号表
*/
@Controller
@RequestMapping(value="/coreAoyunNum")
public class CoreAoyunNumController extends BaseController {

	/**
	* 奥运编号表
	*/
	@Autowired
	private com.xiaoyu.lingdian.service.CoreAoyunNumService coreAoyunNumService;
	
	/**
	* 添加
	*
	* @param cranmWechat 所属公众号
	* @param cranmNum 计数
	* @param cranmCdate 创建日期
	* @param cranmUdate 修改日期
	* @return
	*/
	@RequestMapping(value="/add/coreAoyunNum", method=RequestMethod.POST)
	public void addCoreAoyunNum (String cranmWechat, Integer cranmNum, Date cranmCdate, Date cranmUdate,  HttpServletResponse response) {
		logger.info("[CoreAoyunNumController]:begin addCoreAoyunNum");

		CoreAoyunNum coreAoyunNum = new CoreAoyunNum();
		String uuid = RandomUtil.generateString(16);
		coreAoyunNum.setCranmUuid(uuid);
				coreAoyunNum.setCranmWechat(cranmWechat);
				coreAoyunNum.setCranmNum(cranmNum);
				coreAoyunNum.setCranmCdate(cranmCdate);
				coreAoyunNum.setCranmUdate(cranmUdate);
		
		coreAoyunNumService.insertCoreAoyunNum(coreAoyunNum);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "新增成功!"),response);
		logger.info("[CoreAoyunNumController]:end addCoreAoyunNum");
	}

	/**
	* 修改
	*
	* @param cranmUuid 标识UUID
	* @param cranmWechat 所属公众号
	* @param cranmNum 计数
	* @param cranmCdate 创建日期
	* @param cranmUdate 修改日期
	* @return
	*/
	@RequestMapping(value="/update/coreAoyunNum", method=RequestMethod.POST)
	public void updateCoreAoyunNum (String cranmUuid, String cranmWechat, Integer cranmNum, Date cranmCdate, Date cranmUdate,  HttpServletResponse response) {
		logger.info("[CoreAoyunNumController]:begin updateCoreAoyunNum");

		if (StringUtil.isEmpty(cranmUuid)) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "[标识UUID]不能为空!"), response);
			return;
		}

		CoreAoyunNum coreAoyunNum = new CoreAoyunNum();
		coreAoyunNum.setCranmUuid(cranmUuid);
		coreAoyunNum.setCranmWechat(cranmWechat);
		coreAoyunNum.setCranmNum(cranmNum);
		coreAoyunNum.setCranmCdate(cranmCdate);
		coreAoyunNum.setCranmUdate(cranmUdate);

		coreAoyunNumService.updateCoreAoyunNum(coreAoyunNum);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "修改成功!"),response);
		logger.info("[CoreAoyunNumController]:end updateCoreAoyunNum");
	}

	/**
	* 删除
	*
	* @param cranmUuid 标识UUID
	* @return
	*/
	@RequestMapping(value="/delete/one", method=RequestMethod.POST)
	public void deleteCoreAoyunNum (String cranmUuid, HttpServletResponse response) {
		logger.info("[CoreAoyunNumController]:begin deleteCoreAoyunNum");

		if (StringUtil.isEmpty(cranmUuid)) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "[标识UUID]不能为空!"), response);
			return;
		}

		CoreAoyunNum coreAoyunNum = new CoreAoyunNum();
		coreAoyunNum.setCranmUuid(cranmUuid);

		coreAoyunNumService.deleteCoreAoyunNum(coreAoyunNum);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "删除成功!"),response);
		logger.info("[CoreAoyunNumController]:end deleteCoreAoyunNum");
	}

	/**
	* 批量删除
	*
	* @param cranmUuids UUID集合
	* @return
	*/
	@RequestMapping(value="/delete/batch", method=RequestMethod.POST)
	public void deleteBatchCoreAoyunNum (String cranmUuids, HttpServletResponse response) {
		logger.info("[CoreAoyunNumController]:begin deleteBatchCoreAoyunNum");

		if (StringUtil.isEmpty(cranmUuid)) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "[UUID集合]不能为空!"), response);
			return;
		}

		String[] uuids=cranmUuids.split("\\|");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < uuids.length; i++) {
			list.add(uuids[i]);
		}
		if (list.size() == 0) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "请选择批量删除对象！"), response);
			return;
		}
		coreAoyunNumService.deleteBatchByIds(list);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "批量删除成功!"),response);
		logger.info("[CoreAoyunNumController]:end deleteBatchCoreAoyunNum");
	}

	/**
	* 获取单个
	*
	* @param cranmUuid 标识UUID
	* @return
	*/
	@RequestMapping(value="/views", method=RequestMethod.POST)
	public void viewsCoreAoyunNum (String cranmUuid, HttpServletResponse response) {
		logger.info("[CoreAoyunNumController]:begin viewsCoreAoyunNum");

		if (StringUtil.isEmpty(cranmUuid)) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "[标识UUID]不能为空!"), response);
			return;
		}

		CoreAoyunNum coreAoyunNum = new CoreAoyunNum();
		coreAoyunNum.setCranmUuid(cranmUuid);

		coreAoyunNum = coreAoyunNumService.getCoreAoyunNum(coreAoyunNum);

		CoreAoyunNumVO coreAoyunNumVO = new CoreAoyunNumVO();
		coreAoyunNumVO.convertPOToVO(coreAoyunNum);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "获取单个信息成功", coreAoyunNumVO), response);
		logger.info("[CoreAoyunNumController]:end viewsCoreAoyunNum");
	}

	/**
	* 获取列表<List>
	* 
	* @return
	*/
	@RequestMapping(value="/find/all", method=RequestMethod.POST)
	public void findCoreAoyunNumList (HttpServletResponse response) {
		logger.info("[CoreAoyunNumController]:begin findCoreAoyunNumList");

		List<CoreAoyunNum> lists = coreAoyunNumService.findCoreAoyunNumList();
		List<CoreAoyunNumVO> vos = new ArrayList<CoreAoyunNumVO>();
		CoreAoyunNumVO vo;
		for (CoreAoyunNum coreAoyunNum : lists) {
			vo = new CoreAoyunNum();
			vo.convertPOToVO(coreAoyunNum);
			vos.add(vo);
		}

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "list列表获取成功!", vos),response);
		logger.info("[CoreAoyunNumController]:end findCoreAoyunNumList");
	}

	/**
	* 获取列表<Page>
	* 
	* @param pageNum 页码
	* @param pageSize 页数
	* @return
	*/
	@RequestMapping(value="/find/by/cnd", method=RequestMethod.POST)
	public void findCoreAoyunNumPage (Integer pageNum, Integer pageSize, HttpServletResponse response) {
		logger.info("[CoreAoyunNumController]:begin findCoreAoyunNumPage");

		if (pageNum == null || pageNum == 0) {
			pageNum = 1;
		}
		if (pageSize == null || pageSize == 0) {
			pageSize = 10;
		}
		Page<CoreAoyunNum> page = coreAoyunNumService.findCoreAoyunNumPage(pageNum, pageSize);
		Page<CoreAoyunNumVO> pageVO = new Page<CoreAoyunNumVO>(page.getPageNumber(), page.getPageSize(), page.getTotalCount());
		List<CoreAoyunNumVO> vos = new ArrayList<CoreAoyunNumVO>();
		CoreAoyunNumVO vo;
		for (CoreAoyunNum coreAoyunNum : page.getResult()) {
			vo = new CoreAoyunNumVO();
			vo.convertPOToVO(coreAoyunNum);
			vos.add(vo);
		}
		pageVO.setResult(vos);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "page列表获取成功!", pageVO),response);
		logger.info("[CoreAoyunNumController]:end findCoreAoyunNumPage");
	}

}