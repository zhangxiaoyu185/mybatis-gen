package com.xiaoyu.pt;

public class PTControllerCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("PTController.vm");
		param.setFileNameSuffix("Controller");
		param.setPackageSuffix("controller");
		param.setSuffix(".java");
		return param;
	}

}
