package com.oraro.mbroadcast.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TTS语音效果控制工具
 * @author 王子榕
 */
public class TTSControlUtil {
    private static final String TAG = TTSControlUtil.class.getSimpleName();
    /**
     * 将航班进行语音话处理
     * @param inStr 待处理的文字
     * @return 处理后的文字
     */
    public static String playFormat(String inStr){
        StringBuffer tempBuffer = new StringBuffer();
        boolean firstLetter = true;
        char[] inArray = inStr.toCharArray();
        for (int i = 0;i<inArray.length;i++){
            char chars = inArray[i];
            if(isLetter(chars)){
                if(firstLetter){
                    tempBuffer.append(","+chars+".");
                    firstLetter = false;
                }else {
                    tempBuffer.append(chars+".");
                }

            }else if(isNumber(chars)){
                firstLetter = true;
                if(inArray.length - i < 5){
                    tempBuffer.append(chars);
                }else{
                    String mTimeStr = new String(inArray,i,5);
                    if(isTime(mTimeStr)){
                        i = i + 4;
                        //如果是 00:00时间格式的字段，则作为普通字符串处理，并记录该时间字段，并将i移动到时间字段后面
                        tempBuffer.append(mTimeStr);
                    }else{
                        tempBuffer.append(chars+" ");
                    }
                }

            }else{
                firstLetter = true;
                tempBuffer.append(chars);
            }
        }
        String result = tempBuffer.toString();
        LogUtils.e(TAG,"chulihou:"+result);
        return result;
    }





    /**
     * 判断是否是字母
     * @param str
     * @return
     */
    private static boolean isLetter(char str){
        if ((str <= 'Z' && str >= 'A')
                || (str <= 'z' && str >= 'a')) {
//            System.out.println(str.charAt(i) + "是字母");
            return true;
        } else {
//            System.out.println(str.charAt(i) + "不是字母");
            return false;
        }
    }


    /**
     * 判断是否是数字
     * @param str
     * @return
     */
    private static boolean isNumber(char str){
            return Character.isDigit(str);
    }

    private static boolean isTime(char[] chars,int index){
        int length = chars.length;
        int begin = index;
        int end = index;
        if(index+4<length){
            end = index+4;
        }else{
            end = length -1;
        }

        if(index-4>=0){
            end = index-4;
        }else{
            end = 0;
        }
        for (;begin<=end;begin++){
           if( chars[begin] == ':'|| chars[begin] == '：'){
               return true;
           }
        }
        return false;
    }

    /**
     *
     * @param str
     * @return 如果是 00:00时间格式的字段，则返回true
     */
    private static boolean isTime(String str){
        Pattern mPattern = Pattern.compile("\\d{2}:\\d{2}");
        Matcher mMatcher = mPattern.matcher(str);
        return mMatcher.matches();
    }
}
