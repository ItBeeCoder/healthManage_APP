/**
 *日期：2017年8月30日下午4:28:16
pillow
TODO
author：shaozq
 */
package com.pillow.entity;

import java.io.Serializable;

/**
 * @author shaozq
 *2017年8月30日下午4:28:16
 */
public class Oldman implements Serializable {

	/**
	 * 创建老人实体类，定义需要用到的属性
	 */
	private static final long serialVersionUID = 1L;
	private Integer oldManId;
	
	//////private String account;
	private String username;
	private String Oldmanuseraccount;
	private String password;
	private String gender;
	private Double height;
	private Double weight;
	private Integer age;
	private String illness;
	private String address;
	private String telephone;
	
	/**
	 * @return the oldManId
	 */
	public Integer getOldManId() {
		return oldManId;
	}
	/**
	 * @param oldManId the oldManId to set
	 */
	public void setOldManId(Integer oldManId) {
		this.oldManId = oldManId;
	}
	
	/**
	 * @return the oldmanuseraccount
	 */
	public String getOldmanuseraccount() {
		return Oldmanuseraccount;
	}
	/**
	 * @param oldmanuseraccount the oldmanuseraccount to set
	 */
	public void setOldmanuseraccount(String oldmanuseraccount) {
		Oldmanuseraccount = oldmanuseraccount;
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
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return the height
	 */
	public Double getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(Double height) {
		this.height = height;
	}
	/**
	 * @return the weight
	 */
	public Double getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	/**
	 * @return the age
	 */
	public Integer getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(Integer age) {
		this.age = age;
	}
	/**
	 * @return the illness
	 */
	public String getIllness() {
		return illness;
	}
	/**
	 * @param illness the illness to set
	 */
	public void setIllness(String illness) {
		this.illness = illness;
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
