package com.oraro.mbroadcast.utils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.IExcelChangedListener;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.model.AirlineCompany;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.FlightInfoTemp;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/10/22 0022.
 */
public class ParseExcelUtils {
    public static boolean mIsParsing = false;

    private final static String TAG = ParseExcelUtils.class.getSimpleName();

    private static SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sFormat1 = new SimpleDateFormat("yyyyMMdd");

    private static short[] yyyyMMdd = {14, 31, 57, 58, 179, 184, 185, 186, 187, 188};
    private static short[] HHmmss = {20, 32, 176, 190, 191, 192};

    private static List<Short> yyyyMMddList = new ArrayList<>();
    private static List<Short> hhMMssList = new ArrayList<>();


    private static String mDeparture = "";
    private static Date mModelDate;
    private static Map<String, String> mFieldMap;
    private static Map<String, String> mAirCompanyMap;
    private static DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
    private static GenerateService s = new GenerateService();

    static {
        for (int i = 0; i < yyyyMMdd.length; i++) {
            yyyyMMddList.add(yyyyMMdd[i]);
        }
        for (int j = 0; j < HHmmss.length; j++) {

            hhMMssList.add(HHmmss[j]);
        }
    }

    private static IExcelChangedListener mIExcelChangedListener;


    public static void setCallBack(IExcelChangedListener iExcelChangedListener) {
        mIExcelChangedListener = iExcelChangedListener;
    }

    /**
     * @param path 待解析的Excel文件路径
     * @param list 解析完成后存放数据的集合
     * @return 如果正在解析则返回false，或者解析失败返回false，解析成功返回true
     */
    public static boolean parse(String path, List<FlightInfo> list) {
        if (mIsParsing) {
            return false;
        }
        mIsParsing = true;
        InsertExcelUtils.init();
        String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
        if (fileName.contains("_")) {
            try {
                mDeparture = fileName.split("_")[1];
                String path1;
                path1 = (fileName.split("_")[0]);
                mModelDate = sFormat1.parse(path1);
            } catch (Exception e) {
                e.printStackTrace();
                mIsParsing = false;
                return false;
            }
        } else {
            mIsParsing = false;
            return false;
        }

        if (!TextUtils.isEmpty(path)) {
            String mExcelType = path.substring(path.lastIndexOf("."), path.length());
            try {
                FileInputStream mFileInputStream = new FileInputStream(new File(path));
                if (mFileInputStream != null && ".xls".equals(mExcelType.trim().toLowerCase())) {
                    // 创建 Excel 2003 工作簿对象
                    HSSFWorkbook mHSSFWorkbook = new HSSFWorkbook(mFileInputStream);
                    //解析所有页
                    mIsParsing = false;
                    return parseSheet(mHSSFWorkbook, list);
                } else if (".xlsx".equals(mExcelType.trim().toLowerCase())) {
                    // 创建 Excel 2007 工作簿对象
                    //  workbook = new XSSFWorkbook(fileStream);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mIsParsing = false;
        return false;
    }


    private static boolean parseSheet(Workbook workbook, List<FlightInfo> list) {
        int mSheetCount = workbook.getNumberOfSheets();

        MAPXmlPullParser fieldXmlPullParser = new MAPXmlPullParser(MBroadcastApplication.getMyContext(), "field.xml");
        mFieldMap = fieldXmlPullParser.parseByPull();
        MAPXmlPullParser companyXmlPullParser = new MAPXmlPullParser(MBroadcastApplication.getMyContext(), "company.xml");
        mAirCompanyMap = companyXmlPullParser.parseByPull();
        if (mSheetCount > 0) {
            //解析每一页
            return parseEverySheet(mSheetCount, workbook, list);
        } else {
            mIsParsing = false;
            return false;
        }
    }

    private static boolean parseEverySheet(int sheetCount, Workbook workbook, List<FlightInfo> list) {
        List<AirlineCompany> mAirList = new ArrayList<AirlineCompany>();
        try {
            mAirList = DBManager.getInstance(MBroadcastApplication.getMyContext()).queryAll(DBManager.getInstance(MBroadcastApplication.getMyContext()).getAirlineCompanyDao(DBManager.READ_ONLY));
        } catch (Exception e) {
        }
        AirlineCompany airlineCompany;
        if (mAirList.isEmpty()) {
            airlineCompany = new AirlineCompany();
            airlineCompany.setAirlineCompanyName("东方航空公司");
            DBManager.getInstance(MBroadcastApplication.getMyContext()).insert(airlineCompany, DBManager.getInstance(MBroadcastApplication.getMyContext()).getAirlineCompanyDao(DBManager.WRITE_ONLY));
        } else {
            airlineCompany = mAirList.get(0);
        }
        Long mCompanyId = airlineCompany.getId();

        for (int i = 0; i < sheetCount; i++) {
            Sheet mSheet = workbook.getSheetAt(i);
            //每一页开始时，先判断第一行是不是合并单元格
            int mRowCount = mSheet.getLastRowNum();//获取总行数
            if (null != mIExcelChangedListener) {
                mIExcelChangedListener.getSheetNumber(mRowCount);
            }
            boolean mIsCombine = false;
            if (mRowCount > 0) {
                Row mIndex0Row = mSheet.getRow(0);
                Cell mIndex1Cell = mIndex0Row.getCell(1);// 获取第2个单元格中的内容
                if (null == mIndex1Cell) {
                    continue;
                }
                if ("".equals(mIndex1Cell.toString())) {
                    mIsCombine = true;
                }
                parseRow(mCompanyId, mIsCombine, mRowCount, mSheet, list);
            }
        }
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static boolean parseRow(long companyId, boolean mIsCombine, int rowCount, Sheet sheet, List<FlightInfo> list) {
        Map<Integer, String> mMapTitleKey = getKeyList(mIsCombine, sheet);
        int i = 0;
        //如果第一行是合并单元格，则起始解析行为第二行
        if (mIsCombine) {
            i = 2;
        } else {
            i = 1;
        }
        for (; i <= rowCount; i++) {
            if (null != mIExcelChangedListener) {
                mIExcelChangedListener.getEntityListSize(i);
            }
            Row mRow = sheet.getRow(i);
            FlightInfo bean = new FlightInfo();
            bean.setCompanyInfoId(companyId);
            bean.setDeparture(mDeparture);
            bean.setDate(mModelDate);
            for (Map.Entry<Integer, String> entry : mMapTitleKey.entrySet()) {
                String mKey = entry.getValue();
                Cell mCell = mRow.getCell(entry.getKey());// 得到每一个单元格
                String mCellValue = "";
                if (null == mCell) {
                } else {
                    mCellValue = getCellData(mCell, mKey, bean);// 得到每一个单元格值
                }
            }
            if ("北京".equals(bean.getArrivalStation())
                    || "上海".equals(bean.getArrivalStation())
                    || "广州".equals(bean.getArrivalStation())
                    || "深圳".equals(bean.getArrivalStation())
                    || (bean.getFlightNumber() != null && bean.getFlightNumber().startsWith("CES"))
                    || (bean.getFlightNumber() != null && bean.getFlightNumber().startsWith("CSH"))) {
                continue;
            }

            if (null != bean.getFlightNumber() && bean.getFlightNumber().length() > 2) {
                String key = bean.getFlightNumber().substring(0, 2);
                bean.setAirCompany(mAirCompanyMap.get(key));
            }
            List<FlightInfo> onlyFlightNumber = manager.queryFlightInfoByFlightNumberAndDate(bean.getDate(), bean.getFlightNumber());
            if (onlyFlightNumber.size() < 1) {
                list.add(bean);
            } else {
                FlightInfoTemp flightInfoTempo = manager.queryByFlightInfoPid(onlyFlightNumber.get(0).getId());
                if (null != flightInfoTempo) {
                    flightInfoTempo.setDate(bean.getDate());
                    flightInfoTempo.setFlightNumber(bean.getFlightNumber());
                    flightInfoTempo.setPlanToTakeOffDate(bean.getPlanToTakeOffDate());
                    flightInfoTempo.setPlaneType(bean.getPlaneType());
                    flightInfoTempo.setArrivalStation(bean.getArrivalStation());
                    flightInfoTempo.update();
                    List playEntrys = manager.queryPlayEntryByFlightInfoTempId(flightInfoTempo.getId());
                    manager.deleteList(playEntrys, manager.getPlayEntryDao(DBManager.WRITE_ONLY));
                    s.generatePlayUpdate(flightInfoTempo);
                } else {
                    list.add(bean);
                }
            }

        }

        return true;
    }

    /**
     * 获取标题行字段的集合
     *
     * @param sheet 第几页
     * @return 返回键的集合，注意该键用英文替换了中文
     */
    private static Map<Integer, String> getKeyList(boolean mIsCombine, Sheet sheet) {
        Map<Integer, String> keyMap = new HashMap<>();
        Row mRow = null;
        if (mIsCombine) {
            mRow = sheet.getRow(1);
        } else {
            mRow = sheet.getRow(0);
        }
        int cell_num = mRow.getLastCellNum();// 获取单元格的总列数
        for (int i = 0; i < cell_num; i++) {
            Cell mCell = mRow.getCell(i);
            if (null == mCell) {
                keyMap.put(i, "");
            } else {
                //将mCell单元格的值和索引保存在list内
                getTitleList(keyMap, mCell, i);
            }
        }
        return keyMap;
    }


    private static void getTitleList(Map<Integer, String> keyMap, Cell cell, int i) {
        boolean mIsExists = false;
        for (Map.Entry<String, String> entry : mFieldMap.entrySet()) {
            String entryKey = entry.getKey();
            if (entryKey.contains(cell.toString())) {
                mIsExists = true;
                keyMap.put(i, entry.getValue());
            }
        }
        if (!mIsExists) {
            switch (cell.toString()) {

                case "计划到达":
                    keyMap.put(i, "arrive");
                    break;

                case "进出港":
                    keyMap.put(i, "inOut");
                    break;

                case "国际/国内":
                    keyMap.put(i, "area");
                    break;

                case "备注":
                    keyMap.put(i, "property");
                    break;

                case "始发(3码)":
                    keyMap.put(i, "start3");
                    break;

                case "目的(3码)":
                    keyMap.put(i, "aim3");
                    break;

                case "机号":
                    keyMap.put(i, "planeNumber");
                    break;

                case "登机口":
                    keyMap.put(i, "DengJiKou");
                    break;

                case "经停1(3码）":
                    keyMap.put(i, "JingTing1");
                    break;

                case "经停站1":
                    keyMap.put(i, "JingTingZhan1");
                    break;

                case "经停1降落时间":
                    keyMap.put(i, "JingTing1JiangLuoShiJian");
                    break;

                case "经停1起飞时间":
                    keyMap.put(i, "JingTing1QiFeiShiJian");
                    break;

                case "经停1登机口":
                    keyMap.put(i, "JingTing1DengJiKou");
                    break;

                case "经停2（3码）":
                    keyMap.put(i, "JingTing2");
                    break;

                case "经停站2":
                    keyMap.put(i, "JingTingZhan2");
                    break;

                case "经停2降落时间":
                    keyMap.put(i, "JingTing2JiangLuoShiJian");
                    break;

                case "经停2起飞时间":
                    keyMap.put(i, "JingTing2QiFeiShiJian");
                    break;

                case "经停2登机口":
                    keyMap.put(i, "JingTing2DengJiKou");
                    break;

                case "数据源来自（国际三码）":
                    keyMap.put(i, "ShuJuYuanMa");
                    break;

                default:
                    keyMap.put(i, "");
                    break;
            }
        }
    }

    /**
     * 将Excel表格中单元格类型转换为String类型，
     * 注意：该方法只能将部分单元格类型转换为String
     *
     * @param cell 每一个单元格
     * @return 将每一个单元格类型转换为String类型，并返回
     */
    private static String getCellData(Cell cell, String key, FlightInfo bean) {
        String cellValue = "无";
        if (cell != null) {
            try {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK://3空白
                        cellValue = "无";
                        InsertExcelUtils.setData(key, cellValue, bean);
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
                            switch (key) {
                                case "launch":
                                    InsertExcelUtils.setData(key, sFormat.format(date), bean);
                                    break;
                                case "inOut":
                                    InsertExcelUtils.setData(key, sFormat.format(new Date()), bean);
                                    break;
                                default:
                                    InsertExcelUtils.setData(key, date, bean);
                                    break;
                            }
                            cellValue = "\"" + sFormat.format(date) + "\"";// 将单元格的数值转化为相应的格式并加引号变为String类型
                        } else {
//                            Double numberDate = new BigDecimal(cell.getNumericCellValue()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
//                            InsertExcelUtils.setData(key, "" + numberDate, bean);

                            //现在的excel格式里面时间格式是2240，有可能是数值类型的
                            cellValue = replaceBlank(""+cell.getNumericCellValue());
                            InsertExcelUtils.setData(key, cellValue, bean);
                        }
                        break;
                    case Cell.CELL_TYPE_STRING: //字符串型 1
                        switch (key) {
                            case "launch":
//                                LogUtils.d(TAG, "Excel getCellData Cell.CELL_TYPE_STRING case launch = " + cell.toString());
                                String launch = cell.getStringCellValue();
//                                String launch0 = launch.substring(0, 2);
//                                String launch1 = launch.substring(2, launch.length());
//                                launch = launch0 + ":" + launch1;
//
//                                LogUtils.d(TAG, "Excel getCellData launch = " + launch + "--->" + key);
                                InsertExcelUtils.setData(key, launch, bean);
                                break;
                            default:
                                cellValue = replaceBlank(cell.getStringCellValue());
                                InsertExcelUtils.setData(key, cellValue, bean);
                                break;
                        }
                        break;
                    case Cell.CELL_TYPE_FORMULA: //公式型 2
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cellValue = replaceBlank(cell.getStringCellValue());
                        InsertExcelUtils.setData(key, cellValue, bean);
                        break;
                    case Cell.CELL_TYPE_BOOLEAN: //布尔型 4
                        cellValue = String.valueOf(cell.getBooleanCellValue());
                        InsertExcelUtils.setData(key, cellValue, bean);
                        break;
                    case Cell.CELL_TYPE_ERROR: //错误 5
                        cellValue = "!#REF!";
                        InsertExcelUtils.setData(key, cellValue, bean);
                        break;
                    default:
                        cellValue = "无";
                        InsertExcelUtils.setData(key, cellValue, bean);
                        break;
                }

            } catch (Exception e) {
                return cellValue;
            }
        }
        return cellValue;
    }

    private static String replaceBlank(String source) {
        String dest = "";
        if (source != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(source);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
