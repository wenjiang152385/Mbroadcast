package com.oraro.mbroadcast.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.PlayEntry;

/**
 * Created by Administrator on 2016/8/26 0026.
 */
public abstract class BaseDialogFragment extends Fragment {
    protected PlayEntry mPlayEntry;
    private long id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initViews(inflater, container, savedInstanceState);
        return view;
    }

    protected abstract View initViews(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);


    protected void setId(long id) {
        this.id = id;
        mPlayEntry = (PlayEntry) DBManager.getInstance(getActivity()).queryById(id, DBManager.getInstance(getActivity()).getPlayEntryDao(DBManager.READ_ONLY));
    }


    protected long getTTSId() {
        return id;
    }

    protected String getTextDesc() {
        String desc = "";
        if(mPlayEntry != null){
            desc = mPlayEntry.getTextDesc();
        }
        return desc;
    }

    protected String getTextPath() {
        String path = "";
        path = mPlayEntry.getFileParentPath();
        return path;
    }

}
