package com.xiaoyu.pt;

public class PTServiceCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("PTService.vm");
		param.setFileNameSuffix("Service");
		param.setPackageSuffix("service");
		param.setSuffix(".java");
		return param;
	}

}
