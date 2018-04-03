package com.xiaoyu.pt;

public class PTSwaggerControllerCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("PTSwaggerController.vm");
		param.setFileNameSuffix("Controller");
		param.setPackageSuffix("controller");
		param.setSuffix(".java");
		return param;
	}

}
