package com.example.elephant.image2;

import java.io.Serializable;

/**
 * �?个图片对�?
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
}
