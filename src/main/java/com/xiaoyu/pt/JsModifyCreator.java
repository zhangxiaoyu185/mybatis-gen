package com.xiaoyu.pt;

public class JsModifyCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("js_modify.vm");
		param.setFileNameSuffix("_modify");
		param.setPackageSuffix("");
		param.setSuffix(".js");
		return param;
	}

}
