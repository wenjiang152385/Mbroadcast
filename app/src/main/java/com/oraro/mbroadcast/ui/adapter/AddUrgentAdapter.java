package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.UrgentBroadcastXmlUtils;

import java.util.ArrayList;
import java.util.List;

import static com.oraro.mbroadcast.R.id.webView;

/**
 * Created by admin on 2016/11/23
 *
 * @author zmy
 */

public class AddUrgentAdapter extends BaseAdapter {
    private Context context;
    private List<UrgentBroadcastXmlUtils.UrgentFlightInfo> mouldList;
    private List<String> selectList;
    private ViewHolder holder;

    public AddUrgentAdapter(Context context, List<UrgentBroadcastXmlUtils.UrgentFlightInfo> mouldList) {
        this.context = context;
        this.mouldList = mouldList;
        selectList = new ArrayList<>();

    }

    public void setData(List<UrgentBroadcastXmlUtils.UrgentFlightInfo> list) {
        this.mouldList = list;
        selectList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mouldList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        WebView webView = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.lv_item_add_urgent, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linear_content);
            webView = new WebView(context);
            holder.linearLayout.addView(webView);
            holder.rl_click = (RelativeLayout) convertView.findViewById(R.id.rl_click);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            if (null != holder.linearLayout.getChildAt(0) && holder.linearLayout.getChildAt(0) instanceof WebView) {
                webView = (WebView) holder.linearLayout.getChildAt(0);
            }
        }
        sendInform(webView);
        holder.rl_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectList.contains(mouldList.get(position).getType())) {
                    selectList.remove(mouldList.get(position).getType());
                } else {
                    selectList.add(mouldList.get(position).getType());

                }
                notifyDataSetChanged();
            }
        });
        webView.setTag("" + position);
        webView.setBackgroundColor(0);
        webView.loadUrl(mouldList.get(position).getFile());
        holder.tv_title.setText(mouldList.get(position).getTitle());

        if (selectList.contains(mouldList.get(position).getType())) {
            holder.iv.setBackgroundResource(R.mipmap.btn_ok);
        } else {
            holder.iv.setBackgroundResource(R.mipmap.reminder_unselect);
        }
        return convertView;
    }

    public List<String> getData() {
        return selectList;
    }

    public void sendInform(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new OnMyWebViewClient());
    }

    private class OnMyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //view.loadUrl("javascript:changeStyle('" + Constants.WEBVIEW_NO_EDIT + "')");
            LogUtils.e("zmy","Constants.WEBVIEW_NO_EDIT========"+Constants.WEBVIEW_NO_EDIT);
//            view.loadUrl("javascript:LookingforBoardingPassengers_valuation('%s','%s','%s','%s','%s','%s','%s','%s')");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    class ViewHolder {
        TextView tv_title;
        LinearLayout linearLayout;
        RelativeLayout rl_click;
        ImageView iv;
    }
}
