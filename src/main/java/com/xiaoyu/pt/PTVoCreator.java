package com.xiaoyu.pt;

public class PTVoCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("PTVo.vm");
		param.setFileNameSuffix("VO");
		param.setPackageSuffix("vo");
		param.setSuffix(".java");
		return param;
	}

}
