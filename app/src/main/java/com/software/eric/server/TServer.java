package com.software.eric.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.software.eric.wechat.model.Msg;

/**
 * Created by Mzz on 2015/11/4.
 */
public class TServer {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(6666);
        System.out.println("server......");
        Socket socket = server.accept();
        System.out.println("ok......");

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());


        System.out.println("stream ok ....");

        int i = 0;
        String msga[] = {"干嘛","呵呵","我去洗澡了"};
        while(true){
            Msg msg = (Msg) in.readObject();
            System.out.println(msg.getMsg());
            Msg replymsg = Msg.createMsgPacket("eric", "eric", msga[i++]);
            out.writeObject(replymsg);
            out.flush();
            if(i == 3)
            	i = 0;
        }

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                ObjectInputStream in = null;
                    in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                while(true){
                    Msg msg = (Msg) in.readObject();
                    System.out.println(msg.getMsg());
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
//        Thread.sleep(1000);
    }
}
