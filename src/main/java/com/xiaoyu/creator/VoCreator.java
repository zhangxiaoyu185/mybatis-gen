package com.xiaoyu.creator;

public class VoCreator extends AbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("vo.vm");
		param.setFileNameSuffix("VO");
		param.setPackageSuffix("dao.vo");
		param.setSuffix(".java");
		return param;
	}

}
