/*작성자 이윤석*/

package com.iwork.biz.product;

import org.springframework.web.multipart.MultipartFile;

public class ProductVO {

	private int pnum;
	private String pname;
	private int price;
	private int maxpeople;
	private String imgname;
	private String[] imgList;
	
	public String[] getImgList() {
		return imgList;
	}
	public void setImgList(String[] imgList) {
		this.imgList = imgList;
	}
	private MultipartFile[] img;
	
	public MultipartFile[] getImg() {
		return img;
	}
	public void setImg(MultipartFile[] img) {
		this.img = img;
	}
	public int getPnum() {
		return pnum;
	}
	public void setPnum(int pnum) {
		this.pnum = pnum;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getMaxpeople() {
		return maxpeople;
	}
	public void setMaxpeople(int maxpeople) {
		this.maxpeople = maxpeople;
	}
	public String getImgname() {
		return imgname;
	}
	public void setImgname(String imgname) {
		this.imgname = imgname;
	}

}
