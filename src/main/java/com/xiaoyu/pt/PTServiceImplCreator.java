package com.xiaoyu.pt;

public class PTServiceImplCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("PTServiceImpl.vm");
		param.setFileNameSuffix("ServiceImpl");
		param.setPackageSuffix("service.impl");
		param.setSuffix(".java");
		return param;
	}

}
