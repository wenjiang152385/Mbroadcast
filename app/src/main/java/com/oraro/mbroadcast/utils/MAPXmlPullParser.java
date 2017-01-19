package com.oraro.mbroadcast.utils;

import android.content.Context;
import android.util.Log;

import com.oraro.mbroadcast.ui.activity.City;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class MAPXmlPullParser {

    private Context mContext;
    private String mXmlName;

    public MAPXmlPullParser(Context context, String xmlName) {
        mContext = context;
        mXmlName = xmlName;
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

    public Map<String,String> parseByPull() {
        City city = null;
        Map<String,String> cityMap = null;
        try {
            //创建一个资源ID
            XmlPullParser parse = getXMLFromAssets();
            int eventType = parse.getEventType();
            try {
                while (eventType != parse.END_DOCUMENT) {
                    String nodeName = parse.getName();
                    switch (eventType) {

                        case XmlPullParser.START_DOCUMENT:
                            cityMap = new HashMap<>();
                            break;

                        case XmlPullParser.START_TAG:
                            if ("map".equals(nodeName)) {
                                city = new City();
                            } else if ("key".equals(nodeName)) {
                                city.setAlias(parse.nextText());
                            } else if ("value".equals(nodeName)) {
                                city.setName(parse.nextText());
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if ("map".equals(nodeName)) {
                                cityMap.put(city.getAlias(),city.getName());
                                city = null;
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
        return cityMap;
    }
}
