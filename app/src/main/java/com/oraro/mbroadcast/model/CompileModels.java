package com.oraro.mbroadcast.model;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijiaqi on 2016/8/23 0023.
 */
public class CompileModels {
    private static CompileModels compileModels;

    /**
     * 单例模式
     */
    public CompileModels newInstance() {

        return null == compileModels ? new CompileModels() : compileModels;
    }

    private XmlPullParser getXmlFromID(Context context, int id) {
        XmlPullParser xmlPullParser = context.getResources().getXml(id);
        return xmlPullParser;
    }

    private XmlPullParser getXMLFromRaw(Context context, int id) {
        XmlPullParser xmlPullParser = null;
        InputStream is = null;
        is = context.getResources().openRawResource(id);
        try {
            xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlPullParser.setInput(is, "UTF-8");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return xmlPullParser;
    }

    public List<ModelEntity> parseXMLByPull(Context context, int id) {
        ModelEntity modelEntity = null;
        ModelEntity.ItemEntity itemEntity = null;
        List<ModelEntity> modelList = null;
        List<ModelEntity.ItemEntity> itemList = null;


        try {
            XmlPullParser parse = getXMLFromRaw(context, id);
            int eventType = parse.getEventType();
            while (eventType != parse.END_DOCUMENT) {
                String nodename = parse.getName();
                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        modelList = new ArrayList<>();
                        break;

                    case XmlPullParser.START_TAG:
                        if ("Model".equals(nodename)) {
                            itemList = new ArrayList<>();
                            modelEntity = new ModelEntity();
                        } else if ("modelId".equals(nodename)) {
                            modelEntity.setModelId(parse.nextText());
                        } else if ("title".equals(nodename)) {
                            modelEntity.setTitle(parse.nextText());
                        } else if ("item".equals(nodename)) {
                            itemEntity = new ModelEntity.ItemEntity();
                        } else if ("id".equals(nodename)) {
                            itemEntity.setId(parse.nextText());
                        } else if ("name".equals(nodename)) {
                            itemEntity.setName(parse.nextText());
                        } else if ("content".equals(nodename)) {
                            itemEntity.setContent(parse.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("item".equals(nodename)) {
                            itemList.add(itemEntity);
                            itemEntity = null;
                        } else if ("Model".equals(nodename)) {
                            modelEntity.setItemEntity(itemList);
                            modelList.add(modelEntity);
                            modelEntity = null;
                        }

                    default:
                        break;
                }
                eventType = parse.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return modelList;
    }
}

