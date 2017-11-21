package com.xiaoyu.pt;

public class HtmlModifyCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("html_modify.vm");
		param.setFileNameSuffix("_modify");
		param.setPackageSuffix("");
		param.setSuffix(".html");
		return param;
	}

}
