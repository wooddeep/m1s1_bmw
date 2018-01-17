package com.bmw.M1S1.utils;

import com.bmw.M1S1.model.Login_info;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2017/2/17.
 */

public class UdpSocketUtil {

    private DatagramSocket socket;
    private DatagramPacket datagramPacket;
    private DatagramPacket datagramPacketRead;
    private ExecutorService fixThreadPool;
    private boolean isStop;

    public UdpSocketUtil() {

        try {
            LogUtil.log("udp: socket连接中");
            if(socket == null) {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(Login_info.base_socket_port));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            InetAddress address = InetAddress.getByName(Login_info.base_socket_ip);
            datagramPacket = new DatagramPacket(new byte[1], 1, address, Login_info.base_socket_port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[94];
        datagramPacketRead = new DatagramPacket(bytes, bytes.length);
        fixThreadPool = Executors.newFixedThreadPool(50);

        LogUtil.log("udp: 包准备就绪");
        read();

    }

    private void initSocket() {
        if (fixThreadPool != null) {
            fixThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    while (socket == null && !isStop) {

                        if(socket != null) {
                            LogUtil.log("udp: socket连接成功");
                            break;
                        }
                    }
                }
            });
        }
    }

    public void send(final byte[] commands, String name) {
        if (socket == null || datagramPacket == null) {
            LogUtil.error("发送：socket为空！");
            return;
        }
        fixThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                datagramPacket.setData(commands);
                datagramPacket.setLength(commands.length);
                try {
                    socket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
    }

    public void read() {
        fixThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    if (socket != null && datagramPacketRead != null)
                        try {
                            socket.receive(datagramPacketRead);
                            byte[] bytes = datagramPacketRead.getData();
                            if (listener != null)
                                listener.read(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        });
    }

    public void release() {
        fixThreadPool.shutdownNow();
        isStop = true;
        if (socket != null) {
            socket.close();
        }
        LogUtil.log("socket连接：释放内存！");

    }

    OnDataReadListener listener;

    public void setOnDataReadListener(OnDataReadListener listener) {
        this.listener = listener;
    }

    public interface OnDataReadListener {
        void read(byte[] bytes);
    }
}
