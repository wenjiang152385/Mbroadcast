package com.oraro.mbroadcast.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.FlightInfoTemp;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public class ErrorExcelDialog extends Dialog {
    private final List<FlightInfoTemp> errorlist;
    private final Context context;
    //定义回调事件，用于dialog的点击事件
//    public interface OnCustomDialogListener{
//        public void back(String name);
//    }

//    private OnCustomDialogListener customDialogListener;
    EditText etName;
    private ListView listview;

    public ErrorExcelDialog(Context context, List<FlightInfoTemp> errorlist) {
        super(context);
        this.context = context;
        this.errorlist = errorlist;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_excel);
        //设置标题
//        if(errorlist.size() > 0){
//            setTitle("解析完成,解析错误的条目：");
//        }else{
//            setTitle("解析完成");
//        }
        setTitle("解析完成,解析错误的条目：");
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(new MyListAdapter(errorlist));
//        etName = (EditText)findViewById(R.id.edit);
        Button clickBtn = (Button) findViewById(R.id.clickbtn);
        clickBtn.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
//            customDialogListener.back(String.valueOf(etName.getText()));
            ErrorExcelDialog.this.dismiss();
        }
    };

    private class MyListAdapter extends BaseAdapter {

        private  List<FlightInfoTemp> errorlist;
        ViewHolder holder;
        private class ViewHolder {
            public TextView text;
        }

        public MyListAdapter(List<FlightInfoTemp> errorlist){
            this.errorlist = errorlist;
        }

        @Override
        public int getCount() {
            return errorlist.size();
        }

        @Override
        public FlightInfoTemp getItem(int position) {
            return errorlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = View.inflate(context,R.layout.dialog_excelitem,null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            FlightInfoTemp error = getItem(position);
            holder.text.setText("   航班号: " + error.getFlightNumber() + "  计划起飞: " + error.getPlanToTakeOffDate());
            return convertView;
        }
    }

}
