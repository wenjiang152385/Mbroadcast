package com.oraro.mbroadcast.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by weijiaqi on 2016/8/23 0023.
 */
public class ModelEntity implements Parcelable {
    private String modelId;
    private String title;
    private List<ItemEntity> itemEntity;

    protected ModelEntity() {

    }

    protected ModelEntity(Parcel in) {
        modelId = in.readString();
        title = in.readString();
        itemEntity = in.readArrayList(ItemEntity.class.getClassLoader());
    }

    public static final Creator<ModelEntity> CREATOR = new Creator<ModelEntity>() {
        @Override
        public ModelEntity createFromParcel(Parcel in) {
            return new ModelEntity(in);
        }

        @Override
        public ModelEntity[] newArray(int size) {
            return new ModelEntity[size];
        }
    };

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ItemEntity> getItemEntity() {
        return itemEntity;
    }

    public void setItemEntity(List<ItemEntity> itemEntity) {
        this.itemEntity = itemEntity;
    }

    @Override
    public String toString() {
        String str = null;
        str = modelId + "\n"
                + title + "\n";

        return str;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(modelId);
        dest.writeString(title);
        dest.writeList(itemEntity);
    }


    public static class ItemEntity implements Parcelable {
        private String id;
        private String name;
        private String content;

        protected ItemEntity() {

        }

        protected ItemEntity(Parcel in) {
            id = in.readString();
            name = in.readString();
            content = in.readString();
        }

        public /*static */ final Creator<ItemEntity> CREATOR = new Creator<ItemEntity>() {
            @Override
            public ItemEntity createFromParcel(Parcel in) {
                return new ItemEntity(in);
            }

            @Override
            public ItemEntity[] newArray(int size) {
                return new ItemEntity[size];
            }
        };

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(content);
        }
    }
}
