package com.shbj.intentfilterdemo.socket;

/*
 *  @项目名：  IntentFilterDemo 
 *  @包名：    com.shbj.intentfilterdemo.socket
 *  @文件名:   ServerSocketService
 *  @创建者:   shenbinjian
 *  @创建时间:  2018/12/19 22:02
 *  @描述：    TODO
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class ServerSocketService extends Service {
    private static String[] messages;
    private static final String TAG = "ServerSocketService";

    @Override
    public void onCreate() {
        super.onCreate();
        messages = new String[]{
                "serverMessage-1",
                "serverMessage-2",
                "serverMessage-3",
                "serverMessage-4",
                "serverMessage-5",
                "serverMessage-6",
        };
        new Thread(new ServerSocketTask()).start();
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class ServerSocketTask implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(8919);
                while (true) {
                    Socket incoming = serverSocket.accept();
                    new Thread(new OneServerTask(incoming)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private class OneServerTask implements Runnable {

            private Socket incoming;

            private OneServerTask(Socket incoming) {
                this.incoming = incoming;
            }

            @Override
            public void run() {
                try {
                    InputStream inputStream = incoming.getInputStream();
                    OutputStream outputStream = incoming.getOutputStream();
                    Scanner in = new Scanner(inputStream, "utf-8");
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    out.println("欢迎访问ServerSocket!");
                    out.flush();
                    boolean done = false;
                    while (!done && in.hasNextLine()) {
                        String nextLine = in.nextLine();
                        Log.d(TAG, "client: " + nextLine);
                        if ("BYE".equals(nextLine)) {
                            done = true;
                        }
                        Random random = new Random();
                        String message = messages[random.nextInt(messages.length)];
                        out.println(message);
                        out.flush();
                    }
                    out.close();
                    in.close();
                    incoming.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
