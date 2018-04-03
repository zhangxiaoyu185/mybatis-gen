package com.xiaoyu.pt;

public class PTSwaggerVoCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("PTSwaggerVo.vm");
		param.setFileNameSuffix("VO");
		param.setPackageSuffix("vo");
		param.setSuffix(".java");
		return param;
	}

}
