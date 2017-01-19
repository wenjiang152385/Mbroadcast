package com.oraro.mbroadcast.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.ISearchBarCallback;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.HistoryFlightTempEdit;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.activity.FlightInfoActivity;
import com.oraro.mbroadcast.ui.adapter.HistoryFlightAdapter;
import com.oraro.mbroadcast.ui.widget.NewSearchBar;
import com.oraro.mbroadcast.ui.widget.RefreshLayout;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.PopupList;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24 0024.
 *
 * @author jiang wen[佛祖保佑 永无BUG]
 */
public class SearchFlightFragment extends Fragment {
    public final static String TAG_SEARCHFLIGHT = "SearchFlightFragment";
    private ListView mFlightList;
    private List<Integer> mWidthList = new ArrayList<>();
    private RefreshLayout mRefreshLayout;
    private HistoryFlightAdapter mAdapter;
    private static int mIndex = 0;
    private NewSearchBar mNewSearchBar;
    private ImageView mImageButton;
    private TextView mTitle;
    private TextView mLeftBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mWidthList.clear();
        View view = inflater.inflate(R.layout.activity_search_flight, null);
        initView(view);
        List<String> popupMenuItemList = new ArrayList<>();
        popupMenuItemList.add(this.getString(R.string.popuplist_edit));
        PopupList popupList = new PopupList();
        popupList.init(getActivity(), mFlightList, popupMenuItemList, mOnPopupListClickListener);
        final ImageView indicator = new ImageView(getActivity());
        indicator.setImageResource(R.mipmap.popuplist_default_arrow);
        popupList.setIndicatorView(indicator);
        popupList.setIndicatorSize(dp2px(16), dp2px(8));
        popupList.setTextSizePixel(30);
        return view;
    }

    private void initView(View view) {
        TextView title0 = (TextView) view.findViewById(R.id.text_title_play_time);
        TextView title1 = (TextView) view.findViewById(R.id.text_title_flight_number);
        TextView title2 = (TextView) view.findViewById(R.id.text_title_destination);
        TextView title3 = (TextView) view.findViewById(R.id.text_title_type);
        TextView title4 = (TextView) view.findViewById(R.id.text_title_delay_information);
        TextView title5 = (TextView) view.findViewById(R.id.text_title_time);
        TextView title6 = (TextView) view.findViewById(R.id.text_title_boarding_gate_information);
        mImageButton = (ImageView) view.findViewById(R.id.header_left_img);
        mImageButton.setVisibility(View.INVISIBLE);
        mTitle = (TextView) view.findViewById(R.id.header_text);
        mTitle.setText("历史记录");
        mLeftBtn = (TextView) view.findViewById(R.id.header_left_btn);
        mLeftBtn.setVisibility(View.VISIBLE);
        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
            }
        });
        mNewSearchBar = (NewSearchBar) view.findViewById(R.id.search);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.flight_view);
        mFlightList = (ListView) view.findViewById(R.id.flight_list);
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

    private PopupList.OnPopupListClickListener mOnPopupListClickListener = new PopupList.OnPopupListClickListener() {
        @Override
        public void onPopupListClick(View contextView, int contextPosition, int position) {
            if (0 == position) {
                long id = ((HistoryFlightTempEdit) mAdapter.getItem(contextPosition)).getFlightInfoTemp().getId();
                startActivity(id);
            }
        }
    };

    private void startActivity(long id) {
        SimpleEvent simpleEvent = new SimpleEvent(Constants.CALL_TO_START);
        simpleEvent.setmMsgId(id);
        EventBus.getDefault().post(simpleEvent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIndex = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        mIndex = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        //为了从编辑回来在刷新
        List<HistoryFlightTempEdit> historyFlightTempEditList = getHisFlightEdit(0);

        //避免删除航班引发的空指针异常
        Iterator iterator = historyFlightTempEditList.iterator();
        while (iterator.hasNext()) {
            HistoryFlightTempEdit tempInfo = (HistoryFlightTempEdit) iterator.next();
            if (null == tempInfo.getFlightInfoTemp()) {
                iterator.remove();
            }
        }

        mAdapter = new HistoryFlightAdapter(getActivity(), historyFlightTempEditList);
        mFlightList.setAdapter(mAdapter);
    }

    private void updateListData() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIndex++;
                List<HistoryFlightTempEdit> list = getHisFlightEdit(mIndex);
                if (list.isEmpty() || list.size() == 0) {
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "无法加载更多了", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mAdapter.updateList(list);
                }
                mRefreshLayout.setLoading(false);
            }
        }, 1000 * 1);
    }

    private List<HistoryFlightTempEdit> getHisFlightEdit(int page) {
        List<HistoryFlightTempEdit> list = DBManager.getInstance(getActivity()).queryByHistoryFlightTemp(page);
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
}
