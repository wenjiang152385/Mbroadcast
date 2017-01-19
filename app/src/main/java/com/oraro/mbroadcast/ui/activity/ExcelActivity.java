package com.oraro.mbroadcast.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.FlightInfoDao;
import com.oraro.mbroadcast.dao.FlightInfoTempDao;
import com.oraro.mbroadcast.listener.IExcelChangedListener;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.mina.client.MinaFileClientThread;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.adapter.ExcelAdapter;
import com.oraro.mbroadcast.ui.widget.ErrorExcelDialog;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.MD5Util;
import com.oraro.mbroadcast.utils.ParseExcelUtils;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by dongyu on 2016/8/12 0012.
 *
 */
public class ExcelActivity extends BaseActivity {
    private final static String TAG = ExcelActivity.class.getSimpleName();
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    private Button button, button2;
    private ListView listView;
    private File file;
    ProgressDialog progressDialog;
    private List<String> fileNames = new ArrayList<>();
    private ExcelAdapter excelAdapter;
    private SharedPreferences sp;
    private String excelPaths = "";
    public static final String SETTING_INFOS = "Path";
    private SharedPreferences.Editor edit;
    List<FlightInfoTemp> errorlist = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    progressDialog.dismiss();
                    List<FlightInfoTemp> errorlist = (List<FlightInfoTemp>) msg.obj;
                    if (errorlist.size() > 0) {
                        ErrorExcelDialog errorExcelDialog = new ErrorExcelDialog(ExcelActivity.this, errorlist);
                        errorExcelDialog.show();
                    } else {
                        Toast.makeText(ExcelActivity.this, "解析完成", Toast.LENGTH_LONG).show();
                        //在EXCEL解析完成后，将通知音响接收该EXCEL文件
                        Set set = SPUtils.getPrefStringSet(ExcelActivity.this, "set", null);
                        if (null != set) {
                            Iterator iterator = set.iterator();
                            while (iterator.hasNext()) {
                                String ip = (String) iterator.next();
                                MinaFileClientThread minaFileClientThread = new MinaFileClientThread();
                                minaFileClientThread.setType(Constants.Import_Excel_File);
                                PlayEntry playEntry = new PlayEntry();
                                String mPath = file.getAbsolutePath();
                                playEntry.setFileParentPath(mPath);
                                playEntry.setFileName(file.getName());
                                playEntry.setTextDesc("Import_Excel_File");
                                minaFileClientThread.setPlayVO(new PlayVO(playEntry));
                                minaFileClientThread.setIp(ip);
                                minaFileClientThread.setMd5sum(MD5Util.getFileMD5String(file));
                                Toast.makeText(ExcelActivity.this, "开始向ip地址为" + minaFileClientThread.getIp() + "音响发送Excel文件", Toast.LENGTH_LONG).show();
                                MinaStringClientThread.getThreadPoolExecutor().execute(minaFileClientThread);
                                LogUtils.e(TAG, "send Excel file message ip = " + ip);
                                LogUtils.e(TAG, "send Excel file message id = " + new PlayVO(playEntry).getEntity().getId());
                                LogUtils.e(TAG, "send Excela file message path = " + new PlayVO(playEntry).getEntity().getFileParentPath());
                            }
                        }
                    }
                    EventBus.getDefault().post(new SimpleEvent(Constants.FLIGHT_EXCEL_ADD));

                    fileNames.add(file.getName());
                    saveSp();
                    excelAdapter.notifyDataSetChanged();
                    button.setText("继续添加");
                    button2.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    break;

                case 2:
                    progressDialog.setMessage("正在解析xls....");
                    progressDialog.setMax((int) msg.obj);
                    break;

                case 3:
                    progressDialog.setProgress((int) msg.obj);
                    break;


                case 4:
                    progressDialog.setMessage("正在插入数据对象.....");
                    progressDialog.setProgress((int) msg.obj);
                    break;

                case 5:
                    Toast.makeText(ExcelActivity.this, "xls格式不正确", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;

                default:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(SETTING_INFOS, Context.MODE_PRIVATE);
        edit = sp.edit();
        showListView();

        setHeader_textTitle(R.string.flight_edit);
        setHeader_left_btn();
    }

    private void showListView() {
        String excelPaths = SPUtils.getPrefString(this, "excelPath", null);
        if (excelPaths != null) {
            LogUtils.e(TAG, "excelPaths===========" + excelPaths);
            String[] split = excelPaths.split("-");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                fileNames.add(s);
            }

            if (fileNames != null && !fileNames.isEmpty()) {
                LogUtils.e(TAG, "fileNames.size===========" + fileNames.size());
                listView.setVisibility(View.VISIBLE);
                excelAdapter = new ExcelAdapter(ExcelActivity.this, fileNames);
                listView.setAdapter(excelAdapter);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_excel;
    }

    @Override
    protected void initData(Bundle paramBundle) {

    }

    @Override
    protected void initView() {
        button = (Button) findViewById(R.id.activity_excel_button);
        button2 = (Button) findViewById(R.id.activity_excel_button2);
        listView = (ListView) findViewById(R.id.activity_excel_listView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LogUtils.e(TAG, "   onClick()  ");
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_PICKER_REQUEST_CODE);
//                } catch (android.content.ActivityNotFoundException ex) {
//
//                }
                Intent intent = new Intent(ExcelActivity.this, FlightExcelActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_PICKER_REQUEST_CODE);
//                } catch (android.content.ActivityNotFoundException ex) {
//
//                }
                Intent intent = new Intent(ExcelActivity.this, FlightExcelActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
            }
        });
        button2.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.GONE);
        excelAdapter = new ExcelAdapter(ExcelActivity.this, fileNames);
        listView.setAdapter(excelAdapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在检查文件....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressNumberFormat("%1d 条 / %2d 条");
    }

    public void getJson() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ParseExcelUtils.setCallBack(new IExcelChangedListener() {
                    @Override
                    public void getSheetNumber(int number) {
                        Message message = new Message();
                        message.arg1 = 2;
                        message.obj = number;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void getEntityListSize(int size) {
                        Message message = new Message();
                        message.arg1 = 4;
                        message.obj = size;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void getRowIndex(int size) {
                        Message message = new Message();
                        message.arg1 = 3;
                        message.obj = size;
                        handler.sendMessage(message);
                    }
                });
                long time1 = System.currentTimeMillis();
                List<FlightInfo> list = new ArrayList<>();
                boolean flag = ParseExcelUtils.parse(file.getAbsolutePath(), list);
                if (!flag) {
                    Message message = new Message();
                    message.arg1 = 5;
                    handler.sendMessage(message);
                } else {
                    List<FlightInfo> insertFlightInfo = new ArrayList();
                    GenerateService s = new GenerateService();
                    long time2 = System.currentTimeMillis();
                    LogUtils.e(TAG, "parse excel time = " + (time2 - time1));
                    DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
                    FlightInfoDao dao = manager.getFlightInfoDao(DBManager.WRITE_ONLY);
                    FlightInfoTempDao dao2 = manager.getFlightInfoTempDao(DBManager.WRITE_ONLY);
                    for (int i = 0; i < list.size(); i++) {
                        List<FlightInfo> onlyFlightNumber = manager.queryFlightInfoByFlightNumberAndDate(list.get(i).getDate(), list.get(i).getFlightNumber());
                        if (onlyFlightNumber.size() < 1) {
                            insertFlightInfo.add(list.get(i));
                        } else {
                            FlightInfoTemp flightInfoTempo = manager.queryByFlightInfoPid(onlyFlightNumber.get(0).getId());
                            flightInfoTempo.setDate( list.get(i).getDate());
                            flightInfoTempo.setFlightNumber( list.get(i).getFlightNumber());
                            flightInfoTempo.setPlanToTakeOffDate( list.get(i).getPlanToTakeOffDate());
                            flightInfoTempo.setPlaneType( list.get(i).getPlaneType());
                            flightInfoTempo.setArrivalStation( list.get(i).getArrivalStation());
                            flightInfoTempo.update();
                            s.generatePlayUpdate(flightInfoTempo);
                        }
                    }
                    if (insertFlightInfo.size() >= 1) {
                        manager.insertList(insertFlightInfo, dao);
                        s.copeFlightToTemp(insertFlightInfo);
                        errorlist = s.generatePlayRerror();
                    }
                    s.copeFlightToTemp(list);
                    errorlist = s.generatePlayRerror();
                    long time3 = System.currentTimeMillis();
                    LogUtils.e(TAG, "insert excel data to db time = " + (time3 - time2));
                    long time4 = System.currentTimeMillis();
                    LogUtils.e(TAG, "create playlist to db time = " + (time4 - time3));
                    Message ms = new Message();
                    ms.arg1 = 1;
                    ms.obj = errorlist;
                    handler.sendMessage(ms);
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            //Uri uri = data.getData();

//            //fix bug zentaoID:024 by wy ,begin
//            String name = file.getName();
            String name = data.getStringExtra(FlightExcelActivity.REQUEST_FILE_NAME);
            String path = data.getStringExtra(FlightExcelActivity.REQUEST_FILE_PATH);
            file = new File(path);
            String suffix = name.substring(name.lastIndexOf(".") + 1);

            if (!suffix.equals("xls") && !suffix.equals("XLS")) {
                Toast.makeText(ExcelActivity.this, "文件格式不正确,请使用xls的文件。", Toast.LENGTH_LONG).show();
                return;
            }
            //fix bug zentaoID:024 by wy ,end
            // LogUtils.e(TAG, "filename " + uri.getPath());

            getJson();
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }

    private void saveSp() {
        String str = "";
        if (fileNames != null && !fileNames.isEmpty()) {
            for (String path : fileNames) {
                str = path + "-" + str;
            }
        }
        if (str != null && !"".equals(str)) {
//            LogUtils.e(TAG, "onDestroy.excelPaths========" + excelPaths);
            SPUtils.setPrefString(ExcelActivity.this, "excelPath", str);
        }
    }

    /**
     * 拷贝航班到临时航班
     */
    public FlightInfoTemp copeFlightToTemp(FlightInfoTemp temp, FlightInfo flightInfo) {

        /**
         * 只处理起飞的航班，在机场降落的航班不会被加入航班临时表
         */
        Date time = flightInfo.getPlanToTakeOffDate();
        if (time == null) {
            return null;
        }
        temp.setAirCompany(flightInfo.getAirCompany());
        temp.setBoardingGate(flightInfo.getBoardingGate());
        temp.setDate(flightInfo.getDate());
        temp.setDeparture(flightInfo.getDeparture());
        temp.setArrivalStation(flightInfo.getArrivalStation());
        temp.setPlaneType(flightInfo.getPlaneType());
        temp.setPlaneNumber(flightInfo.getPlaneNumber());
//        temp.setImportAndExport(flightInfo.getImportAndExport());
//        temp.setInternationalOrDomestic(flightInfo.getInternationalOrDomestic());
        temp.setInternationalThreeYard(flightInfo.getInternationalThreeYard());
//        temp.setObjective(flightInfo.getObjective());
//        temp.setOriginating(flightInfo.getOriginating());
//        temp.setPlanToArrive(flightInfo.getPlanToArrive());
//        temp.setPlanToTakeOff(flightInfo.getPlanToTakeOff());
//        temp.setProperty(flightInfo.getProperty());
        temp.setStopOne(flightInfo.getStopOne());
        temp.setStopOneBoardingGate(flightInfo.getStopOneBoardingGate());
        temp.setStopOneDepartureTime(flightInfo.getStopOneDepartureTime());
        temp.setStopOneFalTime(flightInfo.getStopOneFalTime());
        temp.setStopStationOne(flightInfo.getStopStationOne());
        temp.setStopTwo(flightInfo.getStopTwo());
        temp.setStopTwoBoardingGate(flightInfo.getStopTwoBoardingGate());
        temp.setStopTwoDepartureTime(flightInfo.getStopTwoDepartureTime());
        temp.setStopTwoFalTime(flightInfo.getStopTwoFalTime());
        temp.setStopStationTwo(flightInfo.getStopStationTwo());
        temp.setFlightInfoPid(flightInfo.getId());
        temp.setFlightNumber(flightInfo.getFlightNumber());

        return temp;
    }
}
