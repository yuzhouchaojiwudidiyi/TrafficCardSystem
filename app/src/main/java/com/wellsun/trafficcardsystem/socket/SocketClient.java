package com.wellsun.trafficcardsystem.socket;

import android.os.SystemClock;
import android.util.Log;

import com.wellsun.trafficcardsystem.util.BytesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * date     : 2023-04-03
 * author   : ZhaoZheng
 * describe :
 */
public class SocketClient {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private boolean isRunning = false;
    private boolean isConnecting = false;
    private String host;
    private int port;
    private SocketListener socketListener;
    ExecutorService receivePool = Executors.newSingleThreadExecutor();
    ExecutorService sendPool = Executors.newSingleThreadExecutor();

    public SocketClient(String host, String port) {
        this.host = host;
        this.port = Integer.parseInt(port);
    }

    public void connect() {
        Log.v("socket", "connect开始=" + isRunning);
        if (isRunning) {
            return;
        }
        receivePool.execute(new Runnable() {
            @Override
            public void run() {
                while (!isConnecting) {
                    try {
                        Log.v("socket", "创建socket");
                        socket = new Socket();
                        SocketAddress address = new InetSocketAddress(host, port);
                        socket.connect(address, 3000);
                        socket.setKeepAlive(true);
                        is = socket.getInputStream();
                        os = socket.getOutputStream();
                        isRunning = true;
                        isConnecting = true;
                        Log.v("socket", "socket连接成功");
                        if (socketListener != null) {
                            socketListener.onConnected();
                        }
                        startReceiveThread();
                    } catch (IOException e) {
                        Log.v("socket", "socket连接异常" + e.toString());
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private byte[] bArr;
    byte[] b = new byte[1];

    //接收
    private void startReceiveThread() {
        Log.v("socket", "socket开始接收消息");
        while (isRunning) {
            int available = 0;
            try {
                Log.v("socket", "接收=");
                //下面为socket断开监听
                int bytesRead = is.read(b);
                if (bytesRead == -1) {
                    isRunning = false;
                    break;
                }
                Log.v("socket", "接收=" + bytesRead);
                available = is.available();
                Log.v("socket", "available=" + available + "");
                if (available > 0) {
                    bArr = new byte[available];
                    int read = is.read(bArr);
                    String readHex = bytesToHex(bArr);                                     //字节数组转16紧着字符串
                    String strChinese_gbk = new String(bArr, "GBK");
                    Log.v("socket", "接收message=" + strChinese_gbk);
                    if (socketListener != null) {
                        socketListener.onReceived(strChinese_gbk);
                    }
                }
                SystemClock.sleep(200);
            } catch (IOException e) {
                isRunning = false;
                Log.v("socket", "接收message异常=" + e.toString());
            }
        }
        disconnect();
    }

    //发送
    public void send(String message) {
        Log.v("socket", "发送是否运行isRunning" + isRunning);
        if (!isRunning) {
            return;
        }
        sendPool.execute(new Runnable() {
            @Override
            public void run() {
//                String yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//                byte[] cardBytes = yyyyMMddHHmmss.getBytes();
                byte[] cardBytes = BytesUtil.hexString2Bytes(message);
                try {
                    os.write(cardBytes);
                    if (socketListener != null) {
                        socketListener.onSent(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //断开
    public void disconnect() {
        Log.v("socket", "socket=" + "关闭");
        isRunning = false;
        isConnecting = false;
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (socketListener != null) {
            socketListener.onDisconnected();
        }
        //重新连接
        connect();
    }

    //监听
    public void setOnConnectListener(SocketListener listener) {
        socketListener = listener;
    }

    public interface SocketListener {
        void onConnected(); //连接成功

        void onDisconnected();//断开

        void onReceived(String message);//接收

        void onSent(String message);    //发送
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }


}
