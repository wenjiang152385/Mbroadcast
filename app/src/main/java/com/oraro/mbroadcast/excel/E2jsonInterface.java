package com.oraro.mbroadcast.excel;

import java.util.List;

/**
 * Excel转json模块，外部调用接口
 *
 * @author zmy
 */
public interface E2jsonInterface {

    /**
     * 判断sd卡是否存在，若存在获取外部存储的根路径
     * @return 返回外部存储根路径，若不存在则返回null
     */
    String getSdcardDir();

    /**
     *递归方法搜索指定路径下文件
     * @param sdcardDir 外部存储根路径
     * @return 返回文件的绝对路径，若不存在则返回null
     */
    String searchFilePath(String sdcardDir);

    /**
     * 解析Excel文件并转换为json
     * @return 返回解析后的json，若文件内容不存在则返回null
     */
    String parseExcel();

}
