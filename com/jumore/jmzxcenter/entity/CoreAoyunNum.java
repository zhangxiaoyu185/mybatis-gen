package com.xiaoyu.lingdian.entity;

import java.util.Date;

/**
* 奥运编号表
* @author: zhangy
* @since: 2017年11月09日 21:19:57
* @history:
*/
public class CoreAoyunNum extends BaseEntity {

	private static final long serialVersionUID = 1L;

		/**标识UNID*/
	private Integer cranmUnid;

			/**标识UUID*/
	private String cranmUuid;

		/**所属公众号*/
	private String cranmWechat;

		/**计数*/
	private Integer cranmNum;

		/**创建日期*/
	private Date cranmCdate;

		/**修改日期*/
	private Date cranmUdate;

			public Integer getCranmUnid() {
		return cranmUnid;
	}

	public void setCranmUnid(Integer cranmUnid) {
		this.cranmUnid = cranmUnid;
	}

			public String getCranmUuid() {
		return cranmUuid;
	}

	public void setCranmUuid(String cranmUuid) {
		this.cranmUuid = cranmUuid;
	}

		public String getCranmWechat() {
		return cranmWechat;
	}

	public void setCranmWechat(String cranmWechat) {
		this.cranmWechat = cranmWechat;
	}

		public Integer getCranmNum() {
		return cranmNum;
	}

	public void setCranmNum(Integer cranmNum) {
		this.cranmNum = cranmNum;
	}

		public Date getCranmCdate() {
		return cranmCdate;
	}

	public void setCranmCdate(Date cranmCdate) {
		this.cranmCdate = cranmCdate;
	}

		public Date getCranmUdate() {
		return cranmUdate;
	}

	public void setCranmUdate(Date cranmUdate) {
		this.cranmUdate = cranmUdate;
	}

		public CoreAoyunNum() {
	}

}