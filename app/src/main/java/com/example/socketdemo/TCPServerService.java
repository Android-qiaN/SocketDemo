package com.example.socketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * @描述:
 * @包名: com.example.socketdemo
 * @类名: TCPServerService
 * @日期: 2017/6/21
 * @版权: Copyright ® 烽火星空. All right reserved.
 * @作者: QianKun
 */

public class TCPServerService extends Service {

    private boolean mIsServiceDestoryed = false;
    private String[] mDefinedMessages = new String[]{
            "你好",
            "今天天气不错",
            "吃饭了吗",
            "我也爱你"
    };

    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed = true;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class TcpServer implements Runnable {
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                //监听本地8688端口
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                System.err.println("establish tcp server failed,port:8688");
                e.printStackTrace();
                return;
            }

            while (!mIsServiceDestoryed){
                try {
                    //接收客户端请求
                    final Socket client = serverSocket.accept();
                    System.out.println("accept");
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
        out.println("欢迎来到聊天室！");
        while (!mIsServiceDestoryed){
            String str = in.readLine();
            System.out.println("msg from client:"+str);
            if(str == null){
                break;
            }
            int i = new Random().nextInt(mDefinedMessages.length);
            String msg = mDefinedMessages[i];
            out.println(msg);
            System.out.println("send:"+msg);
        }
        System.out.println("client quit.");
        out.close();
        in.close();
        client.close();
    }
}
