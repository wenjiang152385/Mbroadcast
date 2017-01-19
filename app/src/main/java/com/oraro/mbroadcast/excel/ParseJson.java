package com.oraro.mbroadcast.excel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.oraro.mbroadcast.listener.IExcelChangedListener;
import com.oraro.mbroadcast.utils.LogUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2016/8/5
 *
 * @author zmy
 */
public class ParseJson implements E2jsonInterface {

    private SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
    private short[] yyyyMMdd = {14, 31, 57, 58, 179, 184, 185, 186, 187, 188};
    private short[] HHmmss = {20, 32, 176, 190, 191, 192};

    private List<Short> yyyyMMddList = new ArrayList<>();

    private List<Short> hhMMssList = new ArrayList<>();

    private IExcelChangedListener mIExcelChangedListener;

    // Excel文件名称
    private String excelName;
    private String fileAbsolutePath;
    private boolean isCombine = false;//是否合并单元格，默认没有合并单元格
    private Context context;
    private SharedPreferences sp;


    {
        for (int i = 0; i < yyyyMMdd.length; i++) {

            yyyyMMddList.add(yyyyMMdd[i]);
        }
        for (int j = 0; j < HHmmss.length; j++) {

            hhMMssList.add(HHmmss[j]);
        }
    }

    public ParseJson(String path) {
        this.fileAbsolutePath = path;
    }

    public ParseJson(Context context, String path) {
        this.context = context;
        this.fileAbsolutePath = path;
    }

    public void setCallback(IExcelChangedListener iExcelChangedListener) {
        mIExcelChangedListener = iExcelChangedListener;
    }

    /**
     * 判断sd卡是否存在，若存在获取外部存储的根路径
     *
     * @return 返回外部存储根路径，若不存在则返回null
     */
    @Override
    public String getSdcardDir() {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 递归方法搜索指定路径下文件
     *
     * @param sdcardDir 外部存储的根路径
     * @return 返回文件的绝对路径，若不存在则返回null
     */
    @Override
    public String searchFilePath(String sdcardDir) {
        String needFile = "";
        try {
            File a = new File(sdcardDir);
            String[] file = a.list();
            File temp = null;
            String pathA;
            for (int i = 0; i < file.length; i++) {
                if (sdcardDir.endsWith(File.separator)) {
                    pathA = sdcardDir + file[i];
                } else {
                    pathA = sdcardDir + File.separator + file[i];
                }
                temp = new File(pathA);
                String name = (temp.getName()).toString();
                if (name.equals(".")
                        || name.equals("..")
                        || name.equalsIgnoreCase("Android")
                        || name.equalsIgnoreCase("LOST.DIR")
                        || name.equalsIgnoreCase("UCDownloads")
                        || name.equalsIgnoreCase("Tencent")
                        || name.equalsIgnoreCase("system")
                        || name.equalsIgnoreCase("wandoujia")
                        || name.equalsIgnoreCase("DCIM")
                        || name.equalsIgnoreCase("media")
                        || name.equalsIgnoreCase("music")
                        || name.equalsIgnoreCase("movies")
                        || name.equalsIgnoreCase("wangxin")
                        || name.equalsIgnoreCase("tencentmapsdk")
                        || name.equalsIgnoreCase("taobao")
                        || name.equalsIgnoreCase("qqmusic")
                        || name.equalsIgnoreCase("alipay")
                        || name.startsWith(".")
                        || name.startsWith("com.")) {
                    continue;
                }
                if (temp.isFile()) {
                    if (name.equals(excelName)) {
                        needFile = temp.getAbsolutePath();
                    }
                }
                if (temp.isDirectory()) {
                    searchFilePath(pathA);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return needFile;
    }


    @Override
    public String parseExcel() {
        Workbook workbook = null;
        try {
            workbook = getWeebWork();// 获取Excel表的对象
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        String everySheetJson;
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int s = 0; s < numberOfSheets; s++) {
            Sheet sheet = workbook.getSheetAt(s);
            Row _row = sheet.getRow(0);
            Cell _cell = _row.getCell(0);// 得到每一个单元格
            String value = getCellData(_cell);// 得到每一个单元格值
            if (value != null) {
                ArrayList keyList = getKeyList(sheet);
                everySheetJson = readEverySheet(sheet, keyList);
                sb.append(everySheetJson);
            }
        }
        return sb.toString();
    }

    /**
     * 读取Excel表中每一页的内容
     *
     * @param sheet   第几页
     * @param keyList 标题行内容
     * @return 返回每一页的String类型的json字符串
     */
    private String readEverySheet(Sheet sheet, ArrayList keyList) {
        String jsonNodeData = "";
        String jsonNodeStr;
        String _cellValue;
        int row_num = sheet.getLastRowNum();
        LogUtils.e("zmy","row_num======共有多少行===========" + row_num);
        if (null != mIExcelChangedListener) {
            mIExcelChangedListener.getSheetNumber(row_num);
        }
        isCombine = getIsCombine(sheet);
        if (isCombine) {
            for (int i = 2; i <= row_num; i++) {
                jsonNodeStr = "";
                Row _row = sheet.getRow(i);
                for (int j = 0; j < keyList.size(); j++) {
                    String jsonKey = keyList.get(j).toString();
                    Cell _cell = _row.getCell(j);// 得到每一个单元格
                    _cellValue = getCellData(_cell);// 得到每一个单元格值
                    if (_cell != null) {

                        jsonNodeStr += getJsonNodeElement(jsonKey, _cellValue, _cell.getCellType());
                    } else {
                        jsonNodeStr += getJsonNodeElement(jsonKey, _cellValue, -1);
                    }
                }
                jsonNodeData += getJsonNodeString(jsonNodeStr);
            }
        } else {
            for (int i = 1; i <= row_num; i++) {
                jsonNodeStr = "";
                Row _row = sheet.getRow(i);
                mIExcelChangedListener.getRowIndex(i);
                for (int j = 0; j < keyList.size(); j++) {
                    String jsonKey = keyList.get(j).toString();
                    Cell _cell = _row.getCell(j);
                    _cellValue = getCellData(_cell);
                    if (_cell != null) {

                        jsonNodeStr += getJsonNodeElement(jsonKey, _cellValue, _cell.getCellType());
                    } else {
                        jsonNodeStr += getJsonNodeElement(jsonKey, _cellValue, -1);
                    }
                }
                jsonNodeData += getJsonNodeString(jsonNodeStr);
            }
        }
        jsonNodeData = "{\"sheet"  + "\":[" + jsonNodeData.substring(0, jsonNodeData.length() - 1) + "]}";
        return jsonNodeData;
    }

    /**
     * 判断是否第一行合并单元格
     *
     * @param sheet 第几页
     * @return 若第一行合并则返回true，否则返回false
     */
    private boolean getIsCombine(Sheet sheet) {
        int row_num = sheet.getLastRowNum();//获取总行数
        if (row_num > 0) {
            Row row = sheet.getRow(0);
            Cell content = row.getCell(1);// 获取第2个单元格中的内容
            if ("".equals(content.toString())) {
                isCombine = true;
            }
        }
        return isCombine;
    }

    /**
     * 获取标题行字段的集合
     *
     * @param sheet 第几页
     * @return 返回键的集合，注意该键用英文替换了中文
     */
    public ArrayList getKeyList(Sheet sheet) {
        ArrayList<String> keyList = new ArrayList<>();
        int row_num = sheet.getLastRowNum();//获取总行数
        if (row_num > 0) {
            Row row = sheet.getRow(0);
            Cell orderno = row.getCell(1);// 获取指定单元格中的数据
            if ("".equals(orderno.toString())) {
                row = sheet.getRow(1);
                isCombine = true;
            }
            int cell_num = row.getLastCellNum();// 获取单元格的总列数
            for (int i = 0; i < cell_num; i++) {
                Cell _cell = row.getCell(i);
                if (null == _cell) {
                    keyList.add(null);
                } else {
                    getTitleList(keyList, _cell);
                }
            }
        }
        return keyList;
    }

    private void getTitleList(ArrayList<String> keyList, Cell _cell) {
        switch (_cell.toString()) {
            case "日期":
                keyList.add("date");
                break;
            case "航班号":
                keyList.add("flightNumber");
                break;
            case "计划到达":
                keyList.add("arrive");
                break;
            case "计划起飞":
                keyList.add("launch");
                break;
            case "进出港":
                keyList.add("inOut");
                break;
            case "国际/国内":
                keyList.add("area");
                break;
            case "备注":
                keyList.add("property");
                break;
            case "始发(3码)":
                keyList.add("start3");
                break;
            case "始发站":
                keyList.add("startStation");
                break;
            case "目的(3码)":
                keyList.add("aim3");
                break;
            case "目的站":
                keyList.add("aimStation");
                break;

            case "登机口":
                keyList.add("DengJiKou");
                break;
            case "经停1(3码）":
                keyList.add("JingTing1");
                break;
            case "经停站1":
                keyList.add("JingTingZhan1");
                break;
            case "经停1降落时间":
                keyList.add("JingTing1JiangLuoShiJian");
                break;
            case "经停1起飞时间":
                keyList.add("JingTing1QiFeiShiJian");
                break;
            case "经停1登机口":
                keyList.add("JingTing1DengJiKou");
                break;
            case "经停2（3码）":
                keyList.add("JingTing2");
                break;
            case "经停站2":
                keyList.add("JingTingZhan2");
                break;
            case "经停2降落时间":
                keyList.add("JingTing2JiangLuoShiJian");
                break;
            case "经停2起飞时间":
                keyList.add("JingTing2QiFeiShiJian");
                break;
            case "经停2登机口":
                keyList.add("JingTing2DengJiKou");
                break;
            case "数据源来自（国际三码）":
                keyList.add("ShuJuYuanMa");
                break;


        }
    }

    /**
     * 获取Excel表的对象
     *
     * @return Excel表对象
     * @throws IOException
     */
    private Workbook getWeebWork() throws IOException {
        Workbook workbook = null;

        if (null != fileAbsolutePath) {
            String fileType = fileAbsolutePath.substring(fileAbsolutePath.lastIndexOf("."),
                    fileAbsolutePath.length());
            FileInputStream fileStream = new FileInputStream(new File(fileAbsolutePath));
            if (".xls".equals(fileType.trim().toLowerCase())) {
                workbook = new HSSFWorkbook(fileStream);// 创建 Excel 2003 工作簿对象
            } else if (".xlsx".equals(fileType.trim().toLowerCase())) {
              //  workbook = new XSSFWorkbook(fileStream);// 创建 Excel 2007 工作簿对象
            }
        }
        return workbook;
    }

    /**
     * 将Excel表格中单元格类型转换为String类型，
     * 注意：该方法只能将部分单元格类型转换为String
     *
     * @param cell 每一个单元格
     * @return 将每一个单元格类型转换为String类型，并返回
     */
    private String getCellData(Cell cell) {
        String cellValue = "无";
        if (cell != null) {
            try {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK://空白
                        cellValue = "无";
                        break;
                    case Cell.CELL_TYPE_NUMERIC: //数值型 0----日期类型也是数值型的一种
                        if (DateUtil.isCellDateFormatted(cell)) {// 如果单元格是日期类型
                            short format = cell.getCellStyle().getDataFormat();// format为日期格式
                            if (yyyyMMddList.contains(format)) {
                                sFormat = new SimpleDateFormat("yyyy-MM-dd");
                            } else if (hhMMssList.contains(format)) {
                                sFormat = new SimpleDateFormat("HH:mm:ss");
                            }
                            Date date = cell.getDateCellValue();// 得到单元格的数值
                            cellValue = "\"" + sFormat.format(date) + "\"";// 将单元格的数值转化为相应的格式并加引号变为String类型
                        } else {
                            Double numberDate = new BigDecimal(cell.getNumericCellValue()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                            cellValue = "\"" + numberDate + "\"";
                        }
                        break;
                    case Cell.CELL_TYPE_STRING: //字符串型 1
                        cellValue = replaceBlank(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA: //公式型 2
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cellValue = replaceBlank(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_BOOLEAN: //布尔型 4
                        cellValue = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_ERROR: //错误 5
                        cellValue = "!#REF!";
                        break;
                    default:
                        cellValue = "无";
                        break;
                }

            } catch (Exception e) {
                return cellValue;
            }
        }
        return cellValue;
    }

    private String replaceBlank(String source) {
        String dest = "";
        if (source != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(source);
            dest = m.replaceAll("");
        }
        return dest;
    }


    /**
     * 构造json节点字符串
     *
     * @param nodeString 读取Excel每一页的内容
     * @return
     */
    private String getJsonNodeString(String nodeString) {
        String tmpNode = "{" + nodeString.substring(0, nodeString.length() - 1) + "},";
        return tmpNode;
    }


    /**
     * 构造json节点中元素字符串
     *
     * @param keyString   json中的键，标题行集合中对应的元素
     * @param valueString json中的值
     * @param dataType
     * @return
     */
    private String getJsonNodeElement(String keyString, String valueString, int dataType) {
        String tmpElement = "\"" + keyString + "\"" + ":";
        switch (dataType) {
            case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                tmpElement += valueString;
                break;
            case HSSFCell.CELL_TYPE_STRING: // 字符串
                tmpElement += "\"" + valueString + "\"";
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                tmpElement += keyString;
                break;
            default:
                tmpElement += "\"" + valueString + "\"";
                break;
        }
        return (tmpElement + ",");
    }
}
