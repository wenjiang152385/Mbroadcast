package com.oraro.mbroadcast.utils;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.FlightInfoTemp;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by admin on 2016/10/12
 *
 * @author zmy
 */
public class UrgentBroadcastXmlUtils {

    private UrgentFlightInfo ufi;
    public HashMap<String, UrgentFlightInfo> hashMap;
    private String type;

    public UrgentBroadcastXmlUtils(Context ctx) {
        parseUrgentBroadcastXml(ctx);
    }

    public void parseUrgentBroadcastXml(Context ctx) {
        XmlPullParser xpp = Xml.newPullParser();
        try {
            xpp.setInput(ctx.getResources().openRawResource(R.raw.urgent_broadcast), "UTF-8");
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        hashMap = new HashMap<>();
                        break;
                    case XmlPullParser.START_TAG:
                        String nodeName = xpp.getName();
                        if (nodeName.equals("type")) {
                            ufi = new UrgentFlightInfo();
                            if (xpp.getAttributeCount() > 0) {
                                type = xpp.getAttributeValue(0);
                            }
                            ufi.setType(type);
                        } else if (nodeName.equals("id")) {
                            ufi.setId(Long.parseLong(xpp.nextText()));
                        } else if (nodeName.equals("title")) {
                            ufi.setTitle(xpp.nextText());
                        } else if (nodeName.equals("file")) {
                            ufi.setFile(xpp.nextText());
                        } else if (nodeName.equals("param")) {
                            ufi.setParam(xpp.nextText());
                        } else if (nodeName.equals("set_html")) {
                            ufi.setSet_html(xpp.nextText());
                        } else if (nodeName.equals("get_data")) {
                            ufi.setGet_data(xpp.nextText());
                        } else if (nodeName.equals("set_params")) {
                            ufi.setSet_params(xpp.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("type".equals(xpp.getName())) {

                            hashMap.put(type, ufi);
                            ufi = null;
                        }
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String clickFlightInfo(String selectedType, FlightInfoTemp flightInfoTemp) {
        String param = hashMap.get(selectedType).getParam();
        if (param.isEmpty()) {
            return null;
        }
        String set_html = hashMap.get(selectedType).getSet_html();
        String[] params = param.split(",");
        for (String p : params) {
            Object field = getField(p, flightInfoTemp);
            if (field != null) {
                set_html = set_html.replaceFirst("%s", field.toString());
            }
        }

        return set_html;
    }
    public String getLastJsParams(String selectedType, String params) {
        String set_params = hashMap.get(selectedType).getSet_params();
        if (params.isEmpty()) {
            return null;
        }
        String[] paramsArr = params.split("-");
        for (int i = 0; i < paramsArr.length; i++) {
            set_params = set_params.replaceFirst("%s", paramsArr[i]);
        }
        return set_params;
    }

    public Object getField(String filedName, FlightInfoTemp flightInfoTemp) {
        Field[] declaredFields = flightInfoTemp.getClass().getDeclaredFields();
        for (Field file : declaredFields) {
            file.setAccessible(true);
            if (file.getName().equals(filedName)) {
                try {
                    return file.get(flightInfoTemp);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public class UrgentFlightInfo {
        private String type;
        private String title;
        private String file;
        private String param;
        private String set_html;
        private String get_data;
        private long id;
        private String set_params;

        public String getSet_params() {
            return set_params;
        }

        public void setSet_params(String set_params) {
            this.set_params = set_params;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public String getSet_html() {
            return set_html;
        }

        public void setSet_html(String set_html) {
            this.set_html = set_html;
        }

        public String getGet_data() {
            return get_data;
        }

        public void setGet_data(String get_data) {
            this.get_data = get_data;
        }
    }
}
