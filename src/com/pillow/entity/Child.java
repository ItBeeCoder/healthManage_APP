/**
 *日期：2017年8月30日下午4:30:23
pillow
TODO
author：shaozq
 */
package com.pillow.entity;

import java.io.Serializable;

/**
 * @author shaozq
 *2017年8月30日下午4:30:23
 */
public class Child implements Serializable {

	/**
	 * 创建子女实体类，定义需要用到的属性
	 */
	private static final long serialVersionUID = 1L;
	private Integer childId;
	private String username;//shaozq
	private String password;
	private String address;
	private String telephone;
	/**
	 * @return the childId
	 */
	public Integer getChildId() {
		return childId;
	}
	/**
	 * @param childId the childId to set
	 */
	public void setChildId(Integer childId) {
		this.childId = childId;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}
	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}	
	
	
}
