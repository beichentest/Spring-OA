/**
 * Project Name:SpringOA
 * File Name:User.java
 * Package Name:com.zml.oa.entity
 * Date:2014-11-8下午11:12:48
 *
 */
package com.zml.oa.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @ClassName: User
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: zml
 * @date: 2014-11-8 下午11:12:48
 *
 */

@Entity
@Table(name = "T_PROJECT")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Project implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -622628316981046957L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true)
	private Integer id;
	/**
	 * 项目名称
	 */
	@Column(name = "NAME")
	private String name;
	/**
	 * 开发语言及版本
	 */
	@Column(name = "LANGUAGE")
	private String language;
	/**
	 * 编译说明
	 */
	@Column(name = "BUILD_INFO")
	private String buildInfo; 
	/**
	 * 是否有测试用例（0 无  ，1 有）
	 */
	@Column(name = "HAVE_TEST")
	private String haveTest;
	/**
	 * 是否有源码（0 无  ，1 有）
	 */
	@Column(name = "HAVE_CODE")
	private String havetCode;
	/**
	 * 是否有发布版本（0 无  ，1 有）
	 */
	@Column(name = "HAVE_RELEASE")
	private String have_release;
	/**
	 * 开发人员
	 */
	@Column(name = "CODER")
	private String coder;
	/**
	 * 应用服务器信息
	 */
	@Column(name = "SERVER")
	private String server;
	/**
	 * 操作系统
	 */
	@Column(name = "OS")
	private String os;
	/**
	 * 数据库
	 */
	@Column(name = "DB")
	private String db;
	/**
	 * 上线时间
	 */
	@Column(name = "RELEASE_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date release_time;
	/**
	 * 状态（0 正常，1 下线）
	 */
	@Column(name = "STATUS")
	private String status;
	/**
	 * 下线时间
	 */
	@Column(name = "DISCARD_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date discardTime;
	/**
	 * 备注
	 */
	@Column(name = "MEMO")
	private String memo;
	/**
	 * 开发svn地址
	 */
	@Column(name = "CODER_SVN")
	private String coderSvn;
	/**
	 * 测试svn地址
	 */
	@Column(name = "TESTER_SVN")
	private String testerSvn;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getBuildInfo() {
		return buildInfo;
	}
	public void setBuildInfo(String buildInfo) {
		this.buildInfo = buildInfo;
	}
	public String getHaveTest() {
		return haveTest;
	}
	public void setHaveTest(String haveTest) {
		this.haveTest = haveTest;
	}
	public String getHavetCode() {
		return havetCode;
	}
	public void setHavetCode(String havetCode) {
		this.havetCode = havetCode;
	}
	public String getHave_release() {
		return have_release;
	}
	public void setHave_release(String have_release) {
		this.have_release = have_release;
	}
	public String getCoder() {
		return coder;
	}
	public void setCoder(String coder) {
		this.coder = coder;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getDb() {
		return db;
	}
	public void setDb(String db) {
		this.db = db;
	}
	public Date getRelease_time() {
		return release_time;
	}
	public void setRelease_time(Date release_time) {
		this.release_time = release_time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getDiscardTime() {
		return discardTime;
	}
	public void setDiscardTime(Date discardTime) {
		this.discardTime = discardTime;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getCoderSvn() {
		return coderSvn;
	}
	public void setCoderSvn(String coderSvn) {
		this.coderSvn = coderSvn;
	}
	public String getTesterSvn() {
		return testerSvn;
	}
	public void setTesterSvn(String testerSvn) {
		this.testerSvn = testerSvn;
	}
}
