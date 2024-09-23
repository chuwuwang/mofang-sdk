package com.morefun.ysdk.sample.utils;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class TcpService {
    private final String TAG = "TcpService";
    private static TcpService instance = null;
    private Socket mSocket;
    private Thread mThread = null;
    private static Object lock = new Object();

    public static TcpService getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new TcpService();
                }
            }
        }
        return instance;
    }

    private void initSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        mSocket = new Socket();
    }


    private static void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean connect(String ip, int port) {
        int connectTimeout = 5 * 1000;
        int receiveTimeout = 5 * 1000;
        long startTime = System.currentTimeMillis();

        Log.d(TAG, "服务器连接IP:" + ip + ",端口:" + port);
        while (true) {
            try {
                initSocket();
                InetSocketAddress remoteAddress = new InetSocketAddress(ip, port);
                mSocket.connect(remoteAddress, connectTimeout);
                try {
                    mSocket.setSoTimeout(receiveTimeout);
                } catch (SocketException e) {
                    // TODO Auto-generated catch block
                    disconnect();
                    e.printStackTrace();
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                disconnect();
                sleep(1000);
            }

            if (isTimeout(startTime, connectTimeout)) {
                disconnect();
                return false;
            }
        }
    }

    private boolean isTimeout(long beginTime, int timeout) {
        return System.currentTimeMillis() - beginTime > timeout;
    }

    public void disconnect() {
        Log.d(TAG, "断开连接");
        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (mThread != null) {
            try {
                mThread.interrupt();
                mThread = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnect() {
        try {
            if (mSocket != null) {
                return mSocket.isConnected();
            }
        } catch (Exception e) {
            disconnect();
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean send(byte[] sendMessage) {
        Log.d(TAG, "发送报文:" + BytesUtil.bytes2HexString(sendMessage));

        if (isConnect()) {
            OutputStream mOutputStream;
            try {
                mOutputStream = mSocket.getOutputStream();

                mOutputStream.write(sendMessage);
                mOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
                disconnect();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public synchronized byte[] receive() {
        if (TcpService.getInstance().isConnect()) {
            try {
                byte[] buffer = new byte[512];
                InputStream inputStream = mSocket.getInputStream();

                if (inputStream.read(buffer) > 0) {
                    Log.d(TAG, "报文:" + BytesUtil.bytes2HexString(buffer));
                    return buffer;
                }
            } catch (Exception e) {
                e.printStackTrace();
                TcpService.getInstance().disconnect();
                return null;
            }
        }
        return null;
    }

}
