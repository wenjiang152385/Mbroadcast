package com.oraro.mbroadcast.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.utils.LogUtils;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/3 0003.
 */
public class FlightExcelActivity extends Activity {

    private static String[] FILE_SIZE_UNIT = {"B","KB","MB","GB","TB",};

    private Context context;
    private ExpandableListView expandableListView;
    //private String[] parents = new String[]{"EXCEL文件夹", "音频文件夹"};
    private String[] audio = new String[]{"音频文件夹"};
    private String[] excel=new String[]{"EXCEL文件夹"};
    private File sdDir;
    private File[] files1;
    private File[] files2;
    public final static String REQUEST_FILE_PATH = "request_file_path";
    public final static String REQUEST_FILE_NAME = "request_file_name";
    private int isSelectAudio;
    private int type;

    private String mFileTimeFormat = "yyyy年MM月dd日 HH:mm:ss";
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(mFileTimeFormat);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Intent intent = getIntent();
//        isSelectAudio = intent.getIntExtra("isSelectAudio", 2);
//        LogUtils.e("zmy", "isSelectAudio=========" + isSelectAudio);
        type = intent.getIntExtra("type",1);
        setContentView(R.layout.activity_excellist_show);
        expandableListView = (ExpandableListView) findViewById(R.id.el_listview);
        TextView tv = (TextView) findViewById(R.id.header_left_btn);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initData();
        MyExpandableAdapter adapter = new MyExpandableAdapter();
        expandableListView.setAdapter(adapter);
        for (int i = 0; i <adapter.getGroupCount() ; i++) {
            expandableListView.expandGroup(i);
        }
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent();
                if (groupPosition == 0&&type==1) {
                    intent.putExtra(REQUEST_FILE_PATH, files1[childPosition].getAbsolutePath());
                    intent.putExtra(REQUEST_FILE_NAME, files1[childPosition].getName());
                } else {
                    intent.putExtra(REQUEST_FILE_PATH, files2[childPosition].getAbsolutePath());
                    intent.putExtra(REQUEST_FILE_NAME, files2[childPosition].getName());
                }
                setResult(RESULT_OK, intent);
                finish();
                return false;
            }
        });

    }

    private void initData() {
        try {
            boolean sdCardExist = Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory();//获取根目录
            }
            //创建文件夹
            String path1 = sdDir.getPath() + Constants.SELECTED_EXCEL_FILE_DIRECTORY;

            String path2 = sdDir.getPath() + Constants.SELECTED_MEDIA_FILE_DIRECTORY;
            File file1 = new File(path1);
            File file2 = new File(path2);
            if (!file1.exists()) {
                file1.mkdir();
            }
            if (!file2.exists()) {
                file2.mkdir();
            }

            files1 = file1.listFiles();
            Arrays.sort(files1, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    if (lhs.lastModified() < rhs.lastModified()) {
                        return 1;
                    }
                    return -1;
                }

            });

            files2 = file2.listFiles();
            Arrays.sort(files2, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    if (lhs.lastModified() < rhs.lastModified()) {
                        return 1;
                    }
                    return -1;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyExpandableAdapter extends BaseExpandableListAdapter {


        //-----------------Child----------------//

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition == 0 &&type==1) {
                return files1.length;
            } else {
                return files2.length;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        //显示子视图
        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_xls, null);
            TextView textView = (TextView) convertView.findViewById(R.id.TextView);
            TextView mTvFileTime = (TextView) convertView.findViewById(R.id.tv_file_time);
            TextView mTvFileSize = (TextView) convertView.findViewById(R.id.tv_file_size);
            if (groupPosition == 0 && type==1) {
                textView.setText(files1[childPosition].getName());
                mTvFileTime.setText(mSimpleDateFormat.format(new Date(files1[childPosition].lastModified())));
                mTvFileSize.setText(fileSizeToStr(files1[childPosition].length()));
            } else {
                textView.setText(files2[childPosition].getName());
                mTvFileTime.setText(mSimpleDateFormat.format(new Date(files2[childPosition].lastModified())));
                mTvFileSize.setText(fileSizeToStr(files2[childPosition].length()));
            }
//
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        //---------Group-------//
        @Override
        public int getGroupCount() {
            if (type==2) {
                return audio.length;
            } else {

                return excel.length;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }


        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }

        //显示组视图
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.activity_b, null);
                viewHolder.tv1 = (TextView) convertView.findViewById(R.id.tv_b1);
                viewHolder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (type==2) {
                viewHolder.tv1.setText(audio[groupPosition]);
            } else {
                viewHolder.tv1.setText(excel[groupPosition]);
            }
            if (isExpanded) {
                viewHolder.iv1.setImageResource(R.drawable.arrow1);
            } else {
                viewHolder.iv1.setImageResource(R.drawable.arrow);
            }


            return convertView;
        }


    }

    private String fileSizeToStr(double size) {
        String mSizeStr = size + FILE_SIZE_UNIT[0];
        for(int i = FILE_SIZE_UNIT.length - 1; i > -1; i--){
            double unit = Math.pow(1024,i);
            if(size > unit){
                BigDecimal bigDecimal = new BigDecimal(size / unit);
                mSizeStr = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() + FILE_SIZE_UNIT[i];
                return mSizeStr;
            }
        }
        return mSizeStr;
    }

    public class ViewHolder {
        TextView tv1;
        ImageView iv1;
    }
}
