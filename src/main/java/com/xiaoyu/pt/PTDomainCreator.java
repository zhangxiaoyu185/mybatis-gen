package com.xiaoyu.pt;

public class PTDomainCreator extends PTAbstractCreator {

	@Override
	public JavaBeanParam configBeanParam() {
		JavaBeanParam param = new JavaBeanParam();
		param.setTemplateName("PTDomain.vm");
		param.setFileNameSuffix("");
		param.setPackageSuffix("entity");
		param.setSuffix(".java");
		return param;
	}

}
