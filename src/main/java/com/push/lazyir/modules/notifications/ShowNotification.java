package com.push.lazyir.modules.notifications;

import com.push.lazyir.Loggout;
import com.push.lazyir.devices.NetworkPackage;
import com.push.lazyir.gui.Communicator;
import com.push.lazyir.managers.SettingManager;
import com.push.lazyir.managers.TcpConnectionManager;
import com.push.lazyir.modules.Module;
import com.push.lazyir.modules.dbus.Mpris;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by buhalo on 21.03.17.
 */
public class ShowNotification extends Module {
    public static final String SHOW_NOTIFICATION = "ShowNotification";
    public static final String RECEIVE_NOTIFICATION = "receiveNotification";
    public static final String DELETE_NOTOFICATION = "deleteNotification";
    public static final String SEND = "sendMsg";

    public static final String NOTIFICATION_CLASS = "notificationClass";

    public static final String SMS = "com.android.mms";
    public static final String CALL = "com.android.call";
    public static final String ENDCALL = "com.android.endCall";
    public static final String UPDATES = "updates";
    public static final String BATTERY = "battery";
    public static final String WHATSAPP = "whatsapp";
    public static final String EMAIL = "email";
    public static final String OTHERS = "others";

    private static volatile boolean CALLING = false;

    private Lock lock =new ReentrantLock();

    @Override
    public void execute(NetworkPackage np) {
                    try {
                            if (np.getData().equals(RECEIVE_NOTIFICATION)) {
                                Communicator.getInstance().sendToOut(np.getMessage());
                            }
                            else if (np.getData().equals(DELETE_NOTOFICATION)) {
                               //TODO
                            }
                            else if(np.getData().equals("ALL NOTIFS"))
                            {
                                Notifications notifications = np.getObject(NetworkPackage.N_OBJECT, Notifications.class);
                                if(notifications.getNotifications().size() > 0)
                                sendNotifsToOut(notifications);
                            }
                            else if(np.getData().equals(CALL))
                            {
                                CALLING = true;
                                Mpris.pauseAll(np.getId());
                                Communicator.getInstance().sendToOut(np.getMessage());
                            }
                            else if(np.getData().equals(ENDCALL))
                            {
                                CALLING = false;
                                Mpris.playAll(np.getId());
                            }

                    } catch(NullPointerException e){
                            Loggout.e("ShowNotification", e.toString());
                        }
}


    public void sendNotifsToOut(Notifications allNotifications)
    {
        NetworkPackage toOut = new NetworkPackage(SHOW_NOTIFICATION, "NOTIF TO ID");
        toOut.setObject(NetworkPackage.N_OBJECT, allNotifications);
        Communicator.getInstance().sendToOut(toOut.getMessage());
    }


    public void requestNotificationsFromDevice() {
        NetworkPackage np = new NetworkPackage(SHOW_NOTIFICATION,"ALL NOTIFS");
        TcpConnectionManager.getInstance().sendCommandToServer(device.getId(),np.getMessage());
    }
}
