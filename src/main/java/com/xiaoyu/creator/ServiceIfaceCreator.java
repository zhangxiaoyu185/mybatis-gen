package com.xiaoyu.creator;

public class ServiceIfaceCreator extends AbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		AbstractCreator.JavaBeanParam param = new AbstractCreator.JavaBeanParam();
		param.setTemplateName("serviceIface.vm");
		param.setFileNameSuffix("Service");
		param.setPackageSuffix("service");
		param.setSuffix(".java");
		return param;
	}

}
