package com.oraro.mbroadcast.utils;

import android.content.Context;

import com.oraro.mbroadcast.vo.FieldsSelectVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/11/9.
 */
public class FieldSelectReadXml {
    private Context mContext;
    private String mXmlName;

    public FieldSelectReadXml(Context context, String xmlName) {
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

    public List<FieldsSelectVO> readXML(){
        FieldsSelectVO fieldsSelectVO = new FieldsSelectVO();
        List<FieldsSelectVO> list = new ArrayList<FieldsSelectVO>();
        try {
            //创建一个资源ID
            XmlPullParser parse = getXMLFromAssets();
            int eventType = parse.getEventType();
            try {
                while (eventType != parse.END_DOCUMENT) {
                    String nodeName = parse.getName();
                    switch (eventType) {

                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<FieldsSelectVO>();
                            break;

                        case XmlPullParser.START_TAG:
                            if (FieldsSelectVO.TAG_MAP.equals(nodeName)) {
                                fieldsSelectVO = new FieldsSelectVO();
                            }else if (FieldsSelectVO.TAG_KEY.equals(nodeName)) {
                                fieldsSelectVO.setKey(parse.nextText());
                            } else if (FieldsSelectVO.TAG_VALUE.equals(nodeName)) {
                                fieldsSelectVO.setValue(parse.nextText());
                            }else if (FieldsSelectVO.TAG_REQUIRED.equals(nodeName)) {
                                fieldsSelectVO.setRequired(String.valueOf(FieldsSelectVO.REQUIRED_VALUE).equals(parse.nextText()));
                            }else if (FieldsSelectVO.TAG_NO_REQUIRED_DEFAULT.equals(nodeName)) {
                                String v = parse.nextText();
                                fieldsSelectVO.setNoRequiredDefault(String.valueOf(FieldsSelectVO.NO_REQUIRED_DEFAULT_VALUE).equals(v));
                                fieldsSelectVO.setNoRequiredSet(String.valueOf(FieldsSelectVO.NO_REQUIRED_DEFAULT_VALUE).equals(v));
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if (FieldsSelectVO.TAG_MAP.equals(nodeName)) {
                                list.add(fieldsSelectVO);
                                fieldsSelectVO = null;
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
