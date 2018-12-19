package com.shbj.intentfilterdemo.service;

/*
 *  @项目名：  IntentFilterDemo 
 *  @包名：    com.shbj.intentfilterdemo.service
 *  @文件名:   MessengerService
 *  @创建者:   shenbinjian
 *  @创建时间:  2018/12/8 21:15
 *  @描述：    TODO
 */

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

public class MessengerService extends Service {
    private static final String TAG = "MessengerService";
    @SuppressLint("HandlerLeak")
    private static Handler sHandler = new Handler() {

        private Messenger mClientMessenger;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle data = msg.getData();
                    String client = data.getString("client");
                    Log.d(TAG, client);
                    mClientMessenger = msg.replyTo;
                    sendServiceMessage();
                    break;
                default:
                    break;
            }
        }

        private void sendServiceMessage() {
            Message message = Message.obtain();
            message.what=2;
            Bundle serviceData=new Bundle();
            serviceData.putString("service","服务端响应");
            message.setData(serviceData);
            try {
                mClientMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private Messenger mMessenger = new Messenger(sHandler);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
