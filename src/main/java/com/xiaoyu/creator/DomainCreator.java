package com.xiaoyu.creator;

public class DomainCreator extends AbstractCreator {

	@Override
	public AbstractCreator.JavaBeanParam configBeanParam() {
		AbstractCreator.JavaBeanParam param = new AbstractCreator.JavaBeanParam();
		param.setTemplateName("domain.vm");
		param.setFileNameSuffix("PO");
		param.setPackageSuffix("dao.entity");
		param.setSuffix(".java");
		return param;
	}

}
