package com.xiaoyu.lingdian.controller;

import com.xiaoyu.lingdian.controller.BaseController;
import com.xiaoyu.lingdian.core.mybatis.page.Page;
import com.xiaoyu.lingdian.entity.CoreAoyunCode;
import com.xiaoyu.lingdian.tool.RandomUtil;
import com.xiaoyu.lingdian.tool.StringUtil;
import com.xiaoyu.lingdian.tool.out.ResultMessageBuilder;
import com.xiaoyu.lingdian.vo.CoreAoyunCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
* 奥运二维码表
*/
@Controller
@RequestMapping(value="/coreAoyunCode")
public class CoreAoyunCodeController extends BaseController {

	/**
	* 奥运二维码表
	*/
	@Autowired
	private com.xiaoyu.lingdian.service.CoreAoyunCodeService coreAoyunCodeService;
	
	/**
	* 添加
	*
	* @param craceWechat 所属公众号
	* @param craceCode 二维码附件
	* @return
	*/
	@RequestMapping(value="/add/coreAoyunCode", method=RequestMethod.POST)
	public void addCoreAoyunCode (String craceWechat, String craceCode,  HttpServletResponse response) {
		logger.info("[CoreAoyunCodeController]:begin addCoreAoyunCode");

		CoreAoyunCode coreAoyunCode = new CoreAoyunCode();
		String uuid = RandomUtil.generateString(16);
		coreAoyunCode.setCraceUuid(uuid);
				coreAoyunCode.setCraceWechat(craceWechat);
				coreAoyunCode.setCraceCode(craceCode);
		
		coreAoyunCodeService.insertCoreAoyunCode(coreAoyunCode);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "新增成功!"),response);
		logger.info("[CoreAoyunCodeController]:end addCoreAoyunCode");
	}

	/**
	* 修改
	*
	* @param craceUuid 标识UUID
	* @param craceWechat 所属公众号
	* @param craceCode 二维码附件
	* @return
	*/
	@RequestMapping(value="/update/coreAoyunCode", method=RequestMethod.POST)
	public void updateCoreAoyunCode (String craceUuid, String craceWechat, String craceCode,  HttpServletResponse response) {
		logger.info("[CoreAoyunCodeController]:begin updateCoreAoyunCode");

		if (StringUtil.isEmpty(craceUuid)) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "[标识UUID]不能为空!"), response);
			return;
		}

		CoreAoyunCode coreAoyunCode = new CoreAoyunCode();
		coreAoyunCode.setCraceUuid(craceUuid);
		coreAoyunCode.setCraceWechat(craceWechat);
		coreAoyunCode.setCraceCode(craceCode);

		coreAoyunCodeService.updateCoreAoyunCode(coreAoyunCode);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "修改成功!"),response);
		logger.info("[CoreAoyunCodeController]:end updateCoreAoyunCode");
	}

	/**
	* 删除
	*
	* @param craceUuid 标识UUID
	* @return
	*/
	@RequestMapping(value="/delete/one", method=RequestMethod.POST)
	public void deleteCoreAoyunCode (String craceUuid, HttpServletResponse response) {
		logger.info("[CoreAoyunCodeController]:begin deleteCoreAoyunCode");

		if (StringUtil.isEmpty(craceUuid)) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "[标识UUID]不能为空!"), response);
			return;
		}

		CoreAoyunCode coreAoyunCode = new CoreAoyunCode();
		coreAoyunCode.setCraceUuid(craceUuid);

		coreAoyunCodeService.deleteCoreAoyunCode(coreAoyunCode);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "删除成功!"),response);
		logger.info("[CoreAoyunCodeController]:end deleteCoreAoyunCode");
	}

	/**
	* 批量删除
	*
	* @param craceUuids UUID集合
	* @return
	*/
	@RequestMapping(value="/delete/batch", method=RequestMethod.POST)
	public void deleteBatchCoreAoyunCode (String craceUuids, HttpServletResponse response) {
		logger.info("[CoreAoyunCodeController]:begin deleteBatchCoreAoyunCode");

		if (StringUtil.isEmpty(craceUuid)) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "[UUID集合]不能为空!"), response);
			return;
		}

		String[] uuids=craceUuids.split("\\|");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < uuids.length; i++) {
			list.add(uuids[i]);
		}
		if (list.size() == 0) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "请选择批量删除对象！"), response);
			return;
		}
		coreAoyunCodeService.deleteBatchByIds(list);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "批量删除成功!"),response);
		logger.info("[CoreAoyunCodeController]:end deleteBatchCoreAoyunCode");
	}

	/**
	* 获取单个
	*
	* @param craceUuid 标识UUID
	* @return
	*/
	@RequestMapping(value="/views", method=RequestMethod.POST)
	public void viewsCoreAoyunCode (String craceUuid, HttpServletResponse response) {
		logger.info("[CoreAoyunCodeController]:begin viewsCoreAoyunCode");

		if (StringUtil.isEmpty(craceUuid)) {
			writeAjaxJSONResponse(ResultMessageBuilder.build(false, -1, "[标识UUID]不能为空!"), response);
			return;
		}

		CoreAoyunCode coreAoyunCode = new CoreAoyunCode();
		coreAoyunCode.setCraceUuid(craceUuid);

		coreAoyunCode = coreAoyunCodeService.getCoreAoyunCode(coreAoyunCode);

		CoreAoyunCodeVO coreAoyunCodeVO = new CoreAoyunCodeVO();
		coreAoyunCodeVO.convertPOToVO(coreAoyunCode);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "获取单个信息成功", coreAoyunCodeVO), response);
		logger.info("[CoreAoyunCodeController]:end viewsCoreAoyunCode");
	}

	/**
	* 获取列表<List>
	* 
	* @return
	*/
	@RequestMapping(value="/find/all", method=RequestMethod.POST)
	public void findCoreAoyunCodeList (HttpServletResponse response) {
		logger.info("[CoreAoyunCodeController]:begin findCoreAoyunCodeList");

		List<CoreAoyunCode> lists = coreAoyunCodeService.findCoreAoyunCodeList();
		List<CoreAoyunCodeVO> vos = new ArrayList<CoreAoyunCodeVO>();
		CoreAoyunCodeVO vo;
		for (CoreAoyunCode coreAoyunCode : lists) {
			vo = new CoreAoyunCode();
			vo.convertPOToVO(coreAoyunCode);
			vos.add(vo);
		}

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "list列表获取成功!", vos),response);
		logger.info("[CoreAoyunCodeController]:end findCoreAoyunCodeList");
	}

	/**
	* 获取列表<Page>
	* 
	* @param pageNum 页码
	* @param pageSize 页数
	* @return
	*/
	@RequestMapping(value="/find/by/cnd", method=RequestMethod.POST)
	public void findCoreAoyunCodePage (Integer pageNum, Integer pageSize, HttpServletResponse response) {
		logger.info("[CoreAoyunCodeController]:begin findCoreAoyunCodePage");

		if (pageNum == null || pageNum == 0) {
			pageNum = 1;
		}
		if (pageSize == null || pageSize == 0) {
			pageSize = 10;
		}
		Page<CoreAoyunCode> page = coreAoyunCodeService.findCoreAoyunCodePage(pageNum, pageSize);
		Page<CoreAoyunCodeVO> pageVO = new Page<CoreAoyunCodeVO>(page.getPageNumber(), page.getPageSize(), page.getTotalCount());
		List<CoreAoyunCodeVO> vos = new ArrayList<CoreAoyunCodeVO>();
		CoreAoyunCodeVO vo;
		for (CoreAoyunCode coreAoyunCode : page.getResult()) {
			vo = new CoreAoyunCodeVO();
			vo.convertPOToVO(coreAoyunCode);
			vos.add(vo);
		}
		pageVO.setResult(vos);

		writeAjaxJSONResponse(ResultMessageBuilder.build(true, 1, "page列表获取成功!", pageVO),response);
		logger.info("[CoreAoyunCodeController]:end findCoreAoyunCodePage");
	}

}