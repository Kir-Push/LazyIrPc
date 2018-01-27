package com.push.gui.basew;

import com.notification.Notification;
import com.notification.NotificationFactory;
import com.notification.manager.QueueManager;
import com.push.gui.controllers.MainController;
import com.push.gui.entity.CustomNotification;
import com.push.gui.entity.NotificationDevice;
import com.push.lazyir.modules.notifications.call.callTypes;
import com.push.lazyir.service.BackgroundService;
import com.theme.ThemePackagePresets;
import com.utils.Time;
import javafx.stage.Screen;

import java.util.HashMap;
import java.util.List;


public class Popup {

     private volatile static boolean initialized;
     private static NotificationFactory factory;
     private static QueueManager manager;
     private static double notTime;
     private static double callNotTime;

     private static HashMap<String,CustomNotification> callNotifs = new HashMap<>();
     private static int countScreens;


    public static void show(String id, NotificationDevice notification, MainController mainController,Object... arg) {


        int numberOfMonitors = Screen.getScreens().size();
        if(numberOfMonitors != countScreens){
            countScreens = numberOfMonitors;
            initialized = false;
        }

        if(!initialized){
            // register the custom builder with the factory
            factory = new NotificationFactory(ThemePackagePresets.cleanLight());
            factory.addBuilder(CustomNotification.class, new CustomNotification.CustomBuilder());
            if(manager != null)
                manager.stop();
            manager = new QueueManager(NotificationFactory.Location.SOUTHEAST);
            // add the Notification
            manager.setScrollDirection(QueueManager.ScrollDirection.NORTH);
            initialized = true;
            notTime = Double.parseDouble(BackgroundService.getSettingManager().get("Notif-time"));
            callNotTime = Double.parseDouble(BackgroundService.getSettingManager().get("Call-Notif-time"));
        }
        // if you have many than 5 notif on screen remove all oldest
        List<Notification> notifications = manager.getNotifications();
        if(notifications.size() >= 5){
            for(int i = 5;i<notifications.size();i++) {
                notifications.get(i).removeFromManager();
            }
        }
        CustomNotification build = factory.build(CustomNotification.class, notification, id, mainController,arg == null ? null : arg[0]);
        boolean incoming = notification.getType().equalsIgnoreCase(callTypes.incoming.name());
        boolean missed = notification.getType().equalsIgnoreCase(callTypes.missedIn.name());
        boolean outgoing = notification.getType().equalsIgnoreCase(callTypes.outgoing.name());
        boolean answer = notification.getType().equalsIgnoreCase(callTypes.answer.name());
        if(incoming || missed || outgoing){
            callNotifs.put(notification.getTitle(),build);
        }
        Time time = Time.seconds(notTime);
        if(incoming){
           time = Time.seconds(callNotTime);
        }
        manager.addNotification(build, time);

    }

    public static void callEnd(String id, String callerNumber) {
        if(callNotifs.containsKey(callerNumber)){
            CustomNotification customNotification = callNotifs.get(callerNumber);
            customNotification.removeFromManager();
            callNotifs.remove(callerNumber);
        }
    }
}
