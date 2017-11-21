package com.xiaoyu.pt;

public class JsDetailCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("js_detail.vm");
		param.setFileNameSuffix("_detail");
		param.setPackageSuffix("");
		param.setSuffix(".js");
		return param;
	}

}
