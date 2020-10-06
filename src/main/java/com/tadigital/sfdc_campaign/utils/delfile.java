package com.tadigital.sfdc_campaign.utils;

import java.io.File;

public class delfile {

	public static void delete(String filepath) {
		File file=new File(filepath);
		if(file.exists()){
			if(file.delete()){
				System.out.println("File deleted successfully");
			}else{
				System.out.println("Fail to delete file");
			}
		}
	}
}
