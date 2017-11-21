package com.xiaoyu.pt;

public class HtmlDetailCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("html_detail.vm");
		param.setFileNameSuffix("_detail");
		param.setPackageSuffix("");
		param.setSuffix(".html");
		return param;
	}

}
