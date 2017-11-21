package com.xiaoyu.lingdian.entity;


/**
* 奥运二维码表
* @author: zhangy
* @since: 2017年11月15日 17:08:05
* @history:
*/
public class CoreAoyunCode extends BaseEntity {

	private static final long serialVersionUID = 1L;

		/**标识UNID*/
	private Integer craceUnid;

		/**标识UUID*/
	private String craceUuid;

		/**所属公众号*/
	private String craceWechat;

		/**二维码附件*/
	private String craceCode;

			public Integer getCraceUnid() {
		return craceUnid;
	}

	public void setCraceUnid(Integer craceUnid) {
		this.craceUnid = craceUnid;
	}

		public String getCraceUuid() {
		return craceUuid;
	}

	public void setCraceUuid(String craceUuid) {
		this.craceUuid = craceUuid;
	}

		public String getCraceWechat() {
		return craceWechat;
	}

	public void setCraceWechat(String craceWechat) {
		this.craceWechat = craceWechat;
	}

		public String getCraceCode() {
		return craceCode;
	}

	public void setCraceCode(String craceCode) {
		this.craceCode = craceCode;
	}

		public CoreAoyunCode() {
	}

}