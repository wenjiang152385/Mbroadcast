package com.oraro.mbroadcast.utils;

import android.content.Context;
import android.util.Log;

import com.oraro.mbroadcast.ui.activity.City;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class PromptXmlPullParser {
    private Context mContext;
    private String mXmlName;
    private String mAirCompany;

    public PromptXmlPullParser(Context context, String xmlName, String airCompany) {
        mContext = context;
        mXmlName = xmlName;
        mAirCompany = airCompany;
    }

    private XmlPullParser getXMLFromAssets() {
        XmlPullParser xmlPullParser = null;
        InputStream is = null;
        try {
            is = mContext.getAssets().open(mXmlName);
            xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlPullParser.setInput(is, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return xmlPullParser;
    }

    public List<String> parseByPull() {
        List<String> list = null;
        boolean isAirCompany = false;
        try {
            //创建一个资源ID
            XmlPullParser parse = getXMLFromAssets();
            int eventType = parse.getEventType();
            try {
                while (eventType != parse.END_DOCUMENT) {
                    String nodename = parse.getName();
                    switch (eventType) {

                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<>();
                            break;

                        case XmlPullParser.START_TAG:
                            if ("name".equals(nodename)) {
                                if (mAirCompany.equals(parse.nextText())) {
                                    isAirCompany = true;
                                }
                            }

                            if ("prompt".equals(nodename) && isAirCompany) {
                                list.add(parse.nextText());
                            }


                            break;
                        case XmlPullParser.END_TAG:
                            if ("aircompany".equals(nodename)) {
                                isAirCompany = false;
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = parse.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return list;
    }
}
