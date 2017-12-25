package com.push.lazyir.service;


import com.push.lazyir.Loggout;
import com.push.lazyir.gui.GuiCommunicator;
import com.push.lazyir.modules.share.ShareModule;
import com.push.lazyir.devices.Device;
import com.push.lazyir.devices.NetworkPackage;
import com.push.lazyir.service.tcp.ConnectionThread;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.*;


/**
 * Created by buhalo on 19.02.17.
 */

public class TcpConnectionManager {
    public final static String TCP_INTRODUCE = "tcpIntroduce";
    public final static String TCP_PING = "ping pong";
    public final static String TCP_PAIR_RESULT = "pairedresult";
    public final static String RESULT = "result";
    public final static String OK = "ok";
    public final static String REFUSE = "refuse";
    public final static String TCP_PAIR = "pair";
    public final static String TCP_UNPAIR = "unpair";
    public final static String TCP_SYNC = "sync";

    private int port = 5667;
    private boolean tls;

    private ServerSocket myServerSocket;

    public boolean isServerOn() {
        return ServerOn;
    }

    private volatile boolean ServerOn = false;

    public TcpConnectionManager() {
    }

    private SSLContext createSSLContext(){
        try{
            KeyStore keyStore = KeyStore.getInstance("jks");
            ClassLoader classLoader =getClass().getClassLoader();
            URL bimka = classLoader.getResource("bimka");
            keyStore.load( bimka.openStream(),"bimkaSamokat".toCharArray());
            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "bimkaSamokat".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();
            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();
            // Initialize SSLContext
             SSLContext sslContext = SSLContext.getInstance("TLS");
             sslContext.init(km  ,  tm, null);
            return sslContext;
        } catch (Exception e){
            Loggout.e("Tcp", "getContext",e);
        }
        return null;
    }


    void startServer()
    {
        boolean trying = true;
        int firstTryPort = port;
       // tls = Boolean.parseBoolean(BackgroundService.getSettingManager().get("TLS"));
        tls = true;
        while(trying && firstTryPort < 5777) { //todo внимание проверь остальные порты чтоб не пересекались
            try {
                if(tls)
                {
                    SSLServerSocketFactory sslServerSocketFactory = createSSLContext().getServerSocketFactory();
                    if(sslServerSocketFactory == null)
                        BackgroundService.crushed("startServer");
                    myServerSocket = sslServerSocketFactory.createServerSocket(firstTryPort);
                }
                else
                    myServerSocket = new ServerSocket(firstTryPort);
                this.port = firstTryPort;
                trying = false;
            } catch (Exception e) {
                Loggout.e("Tcp", "startServer with port " + firstTryPort + " failed",e);
                firstTryPort++;
                if(firstTryPort >= 5777)
                {
                    BackgroundService.crushed("startServer");
                }
                trying = false; // for testing purposes /todo
            }
        }
    }



    void StopListening(Device dv) {
        if(dv == null)
        return;
        dv.closeConnection();
    }

    void startListening() {
      BackgroundService.submitNewTask(() -> {
            if(isServerOn()) {
                Loggout.d("Tcp","Server already working");
                return;
            }
            ServerOn = true;
            while(isServerOn()) {
                try {
                Socket socket = myServerSocket.accept();
                    BackgroundService.submitNewTask((new ConnectionThread(socket)));
            } catch (IOException e) {
                    Loggout.e("Tpc","Exception on accept connection ignoring +",e);
                    if(myServerSocket.isClosed())
                        ServerOn = false;
            }}try {
                myServerSocket.close();
                ServerOn = false;
                GuiCommunicator.tcpClosed();
                Loggout.d("Tcp","Closing server");
            }catch (IOException e) {
                Loggout.e("Tcp","error in closing server",e);
            }
        });
    }


    void sendRequestPairDevice(String id)
    {
        NetworkPackage np =  NetworkPackage.Cacher.getOrCreatePackage(TCP_PAIR,"REQUEST");
        BackgroundService.sendToDevice(id,np.getMessage());
    }

    void sendPairResult(String id,String result) {
        try {
            NetworkPackage np =  NetworkPackage.Cacher.getOrCreatePackage(TCP_PAIR_RESULT,String.valueOf(InetAddress.getLocalHost().getHostName().hashCode()));
            np.setValue(RESULT,result);
            BackgroundService.sendToDevice(id,np.getMessage());
            if(result.equals(OK))
            ShareModule.sendSetupServerCommand(id);
        } catch (UnknownHostException e) {
            Loggout.e("Tcp","sendPairResult",e);
        }

    }

    void sendUnpair(String id)
    {
        BackgroundService.sendToDevice(id, NetworkPackage.Cacher.getOrCreatePackage(TCP_UNPAIR,TCP_UNPAIR).getMessage());
        Device device = Device.getConnectedDevices().get(id);
        if(device != null)
        device.unpair();
    }

}