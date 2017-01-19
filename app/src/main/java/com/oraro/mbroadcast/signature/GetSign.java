package com.oraro.mbroadcast.signature;

import android.content.Context;

public class GetSign {
	public static String s = "";
	static{
		try {
			System.loadLibrary("signature");
			System.out.println("signature load ok!");
		} catch (Exception e) {
			System.out.println("signature load failed!");
		}
	}
	
	public static native Object invoked(Context context,String pkg,String content);

}
