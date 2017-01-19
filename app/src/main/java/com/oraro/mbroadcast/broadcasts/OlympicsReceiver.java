package com.oraro.mbroadcast.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.mina.client.MinaFileClientThread;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.service.SerService;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.MD5Util;
import com.oraro.mbroadcast.vo.PlayVO;

import java.io.File;

/**
 *
 * Created by Administrator on 2016/10/8 0008.
 */
public class OlympicsReceiver extends BroadcastReceiver {

    public static final String EXTRA_EXCEL_FILE_PATH = "extra_excel_file_path";
    public static final String EXTRA_EXCEL_FILE_NAME = "extra_excel_file_name";

    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String ACTION1 = Constants.EXCEL_Transfer_Finish_String;
    private static final String ACTION2 = Constants.MD_Transfer_Finish_String;
    private static final String ACTION3 = Constants.EXCEL_Transfer_Fail;
    private static final String ACTION4 = Constants.MD_Transfer_Fail;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
/*            DataService service=new DataService();
            service.setAutoPlayStatus();
            IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
            try {
                iMyAidlInterface.autioPlay(service.getAutoPlayStatus());
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            if (!tm.getDeviceId().equals(Constants.HUAWEI_DeviceId)) {
                context.startService(new Intent(context,
                        SerService.class));
            Toast.makeText(context, "开机广播", Toast.LENGTH_LONG).show();
//            }
        }
        if (intent.getAction().equals(ACTION1)) {
            String ip = intent.getStringExtra("ip");
            Toast.makeText(context, "ip地址为" + ip + "Excel文件传输完成", Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(ACTION2)) {
            String ip = intent.getStringExtra("ip");
            Toast.makeText(context, "ip地址为" + ip + "音频文件传输完成", Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(ACTION3)) {
            final String ip = intent.getStringExtra("ip");
            String mFilepath = intent.getStringExtra(EXTRA_EXCEL_FILE_PATH);
            String mFileName = intent.getStringExtra(EXTRA_EXCEL_FILE_NAME);
            LogUtils.e("OlympicsReceiver " + ACTION3 + "::mFilepath = " + mFilepath);
            LogUtils.e("OlympicsReceiver " + ACTION3 + "::mFileName = " + mFileName);
            final PlayEntry playEntry = new PlayEntry();
            playEntry.setFileParentPath(mFilepath);
            playEntry.setFileName(mFileName);
            playEntry.setTextDesc("Import_Excel_File");
            Toast.makeText(context, "ip地址为" + ip + "Excel文件传输失败", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
            builder.setTitle("Excel文件传输失败");
            builder.setMessage("抱歉，ipExcel文件传输失败，是否重新传输？");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MinaFileClientThread minaStringFileThread = new MinaFileClientThread();
                    minaStringFileThread.setType(Constants.Import_Excel_File);
                    minaStringFileThread.setPlayVO(new PlayVO(playEntry));
                    minaStringFileThread.setIp(ip);
                    File file = new File(playEntry.getFileParentPath());
                    minaStringFileThread.setMd5sum(MD5Util.getFileMD5String(file));
                    MinaStringClientThread.getThreadPoolExecutor().execute(minaStringFileThread);
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
        }
        if (intent.getAction().equals(ACTION4)) {
            String ip = intent.getStringExtra("ip");
            Toast.makeText(context, "ip地址为" + ip + "音频文件传输失败", Toast.LENGTH_LONG).show();
        }
    }
}
