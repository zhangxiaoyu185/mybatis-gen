package com.xiaoyu.pt;

public class JsListCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("js_list.vm");
		param.setFileNameSuffix("");
		param.setPackageSuffix("");
		param.setSuffix(".js");
		return param;
	}

}
