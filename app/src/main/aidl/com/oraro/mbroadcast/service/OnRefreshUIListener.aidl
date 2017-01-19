// OnRefreshUIListener.aidl
package com.oraro.mbroadcast.service;

// Declare any non-default types here with import statements

interface OnRefreshUIListener {
   void completed (long id , String error);
   void frushPlaying(long id);
}
