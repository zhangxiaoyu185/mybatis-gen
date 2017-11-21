package com.xiaoyu.pt;

public class JsAddCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("js_add.vm");
		param.setFileNameSuffix("_add");
		param.setPackageSuffix("");
		param.setSuffix(".js");
		return param;
	}

}
