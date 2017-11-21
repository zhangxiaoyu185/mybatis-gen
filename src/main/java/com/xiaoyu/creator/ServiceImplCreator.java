package com.xiaoyu.creator;

public class ServiceImplCreator extends AbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		AbstractCreator.JavaBeanParam param = new AbstractCreator.JavaBeanParam();
		param.setTemplateName("serviceImpl.vm");
		param.setFileNameSuffix("ServiceImpl");
		param.setPackageSuffix("service.impl");
		param.setSuffix(".java");
		return param;
	}

}
