package com.oraro.mbroadcast.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.ISearchBarCallback;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.HistoryFlightTempEdit;
import com.oraro.mbroadcast.ui.adapter.HistoryFlightAdapter;
import com.oraro.mbroadcast.ui.widget.NewSearchBar;
import com.oraro.mbroadcast.ui.widget.RefreshLayout;
import com.oraro.mbroadcast.utils.PopupList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchFlightActivity extends AppCompatActivity implements PopupList.OnPopupListClickListener{

    private ListView mFlightList;
    private List<Integer> mWidthList = new ArrayList<>();
    private RefreshLayout mRefreshLayout;
    private HistoryFlightAdapter mAdapter;
    private static int mIndex = 0;
    private NewSearchBar mNewSearchBar;
    private ImageView mImageButton;
    private TextView mTitle;
    private TextView mLeftBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(savedInstanceState);
        List<String> popupMenuItemList = new ArrayList<>();
        popupMenuItemList.add(this.getString(R.string.popuplist_edit));
        PopupList popupList = new PopupList();
        popupList.init(this, mFlightList, popupMenuItemList, this);
        final ImageView indicator = new ImageView(this);
        indicator.setImageResource(R.mipmap.popuplist_default_arrow);
        popupList.setIndicatorView(indicator);
        popupList.setIndicatorSize(dp2px(16), dp2px(8));
        popupList.setTextSizePixel(30);

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<HistoryFlightTempEdit> historyFlightTempEditList = getHisFlightEdit(0);
        //避免删除航班引发的空指针异常
        Iterator iterator = historyFlightTempEditList.iterator();
        while (iterator.hasNext()) {
            HistoryFlightTempEdit tempInfo = (HistoryFlightTempEdit) iterator.next();
            if (null == tempInfo.getFlightInfoTemp()) {
                iterator.remove();
            }
        }
        mAdapter = new HistoryFlightAdapter(this, historyFlightTempEditList);
        mFlightList.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIndex = 0;
    }

    private void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_flight);
        TextView title0 = (TextView) findViewById(R.id.text_title_play_time);
        TextView title1 = (TextView) findViewById(R.id.text_title_flight_number);
        TextView title2 = (TextView) findViewById(R.id.text_title_destination);
        TextView title3 = (TextView) findViewById(R.id.text_title_type);
        TextView title4 = (TextView) findViewById(R.id.text_title_delay_information);
        TextView title5 = (TextView) findViewById(R.id.text_title_time);
        TextView title6 = (TextView) findViewById(R.id.text_title_boarding_gate_information);
        mImageButton = (ImageView) findViewById(R.id.header_left_img);
        mImageButton.setVisibility(View.INVISIBLE);
        mTitle = (TextView) findViewById(R.id.header_text);
        mTitle.setText("历史记录");
        mLeftBtn = (TextView) findViewById(R.id.header_left_btn);
        mLeftBtn.setVisibility(View.VISIBLE);
        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mNewSearchBar = (NewSearchBar) findViewById(R.id.search);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.flight_view);
        mFlightList = (ListView) findViewById(R.id.flight_list);
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                updateListData();
            }
        });

        mNewSearchBar.setSearchBarCallback(new ISearchBarCallback() {

            @Override
            public void setItemInfo(FlightInfoTemp flightInfoTemp, int position) {

            }

            @Override
            public void setChangeEditText(String text) {
//                mNewSearchBar.upDateListView(DBManager.getInstance(SearchFlightActivity.this).queryFlightInfoTempByEditLike(text));
            }
        });
        observerView(title0);
        observerView(title1);
        observerView(title2);
        observerView(title3);
        observerView(title4);
        observerView(title5);
        observerView(title6);

    }

//    private HistoryFlightTempEdit[] getHistoryData{
//
//     List<HistoryFlightTempEdit> HistoryList =  DBManager.getInstance(this).queryAll(DBManager.getInstance(this).getHistoryFlightTempEditDao(DBManager.READ_ONLY));
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIndex = 0;
    }

    private void updateListData() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIndex++;
                List<HistoryFlightTempEdit> list = getHisFlightEdit(mIndex);
                if (list.isEmpty() || list.size() == 0) {
                    Toast.makeText(SearchFlightActivity.this, "无法加载更多了", Toast.LENGTH_SHORT).show();
                } else {
                    mAdapter.updateList(list);
                }
                mRefreshLayout.setLoading(false);
            }
        }, 1000 * 1);
    }

    private List<HistoryFlightTempEdit> getHisFlightEdit(int page) {
        List<HistoryFlightTempEdit> list = DBManager.getInstance(this).queryByHistoryFlightTemp(page);
        return list;
    }

    private void observerView(final View view) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                int width = view.getWidth();
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                mWidthList.add(width);
//                Log.e("wjq","width = " + width + "size = " + mWidthList.size());
                return true;
            }
        });
    }

    protected int dp2px(float value) {
        final float scale = this.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    @Override
    public void onPopupListClick(View contextView, int contextPosition, int position) {
        if (0 == position) {
            long id = ((HistoryFlightTempEdit)mAdapter.getItem(contextPosition)).getFlightInfoTemp().getId();
            startActivity(id);
        }
    }

    private void startActivity(long id) {
        Intent intent = new Intent(SearchFlightActivity.this,FlightInfoActivity.class);
        intent.putExtra("info",id);
        startActivity(intent);
    }
}
