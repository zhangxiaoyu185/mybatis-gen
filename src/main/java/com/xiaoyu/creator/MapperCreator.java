package com.xiaoyu.creator;

public class MapperCreator extends AbstractCreator {

	@Override
	public AbstractCreator.JavaBeanParam configBeanParam() {
		AbstractCreator.JavaBeanParam param = new AbstractCreator.JavaBeanParam();
		param.setTemplateName("mapper.vm");
		param.setFileNameSuffix("POMapper");
		param.setUseCamelCaseForFileName(true);
		param.setPackageSuffix("");
		param.setSuffix(".xml");
		return param;
	}

}
