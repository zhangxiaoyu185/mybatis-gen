package com.xiaoyu.pt;

public class PTMapperCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("PTMapper.vm");
		param.setFileNameSuffix("Mapper");
		param.setPackageSuffix("mapper");
		param.setSuffix(".xml");
		return param;
	}

}
