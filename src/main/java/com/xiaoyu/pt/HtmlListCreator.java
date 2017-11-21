package com.xiaoyu.pt;

public class HtmlListCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("html_list.vm");
		param.setFileNameSuffix("");
		param.setPackageSuffix("");
		param.setSuffix(".html");
		return param;
	}

}
