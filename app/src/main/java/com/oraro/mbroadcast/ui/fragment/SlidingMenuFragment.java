package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.utils.CustomFragmentManager;


/**
 * Created by Administrator on 2016/11/22 0022.
 *
 * @author jiang wen[佛祖保佑 永无BUG]
 */
public class SlidingMenuFragment extends Fragment {
    public final static int POSITION_FLIGHT_DELAY = 0;
    public final static int POSITION_URGENT_BROADCAST = 1;
    public final static int POSITION_EXCEL = 2;
    public final static int POSITION_ADD_FLIGHT = 3;
    public final static int POSITION_HISTORY = 4;
    public final static int POSITION_SETTINGS = 5;
    private String[] texts = new String[]{"航班延误", "紧急广播", "手工导入", "新增航班", "历史记录", "设置"};
    private int[] imageviews = {R.drawable.delay1, R.drawable.urgent_broadcast1, R.drawable.handexcel
            , R.drawable.addfight, R.drawable.history, R.drawable.settings};
    private ListView lv;
    public int clickPosition = -1;//默认为-1
    private MyAdapter adapter;
    private MainActivity mActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leftmenu, null);

        initView(view);

        return view;
    }

    public void setClickPosition(int position) {
        clickPosition = position;
        adapter.notifyDataSetChanged();
    }

    public int getClickPosition() {

        return clickPosition;
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = (MainActivity) activity;
        super.onAttach(activity);
    }

    private void initView(View view) {
        lv = (ListView) view.findViewById(R.id.listview_sm);
        adapter = new MyAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
                mActivity.hideSlidingMenu();
//                SimpleEvent simpleEvent=new SimpleEvent(Constants.MENU_CLICK_POSITION);
//                simpleEvent.setmParam(position);
//                EventBus.getDefault().post(simpleEvent);
                CustomFragmentManager.getInstance(mActivity).startFragment(position);
                clickPosition = position;

            }
        });

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return texts.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.leftmenu_item, null);
                viewHolder.iv_delay = (ImageView) convertView.findViewById(R.id.iv_delay);
                viewHolder.tv_delay = (TextView) convertView.findViewById(R.id.tv_delay);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }
            viewHolder.iv_delay.setBackgroundResource(imageviews[position]);
            viewHolder.tv_delay.setText(texts[position]);
            if (clickPosition == position) {
                convertView.setBackgroundColor(Color.parseColor("#FF474747"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#FF363636"));
            }


            return convertView;
        }
    }

    class ViewHolder {
        ImageView iv_delay;
        TextView tv_delay;

    }

}
