package com.xiaoyu.pt;

public class HtmlAddCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("html_add.vm");
		param.setFileNameSuffix("_add");
		param.setPackageSuffix("");
		param.setSuffix(".html");
		return param;
	}

}
