package com.push.lazyir.modules.notifications.sms;


import com.push.lazyir.devices.NetworkPackage;
import com.push.lazyir.gui.GuiCommunicator;
import com.push.lazyir.modules.Module;
import com.push.lazyir.service.BackgroundService;

/**
 * Created by buhalo on 26.03.17.
 */

public class SmsModule extends Module {

    public static final String SMS_TYPE = "SmsModule";
    private static final String SEND = "send";
    public static final String RECEIVE = "receive";
    private static final String RESPONSE = "response";

    @Override
    public void execute(NetworkPackage np) {
        if(np.getData().equals(RECEIVE))
        {
            Sms sms = np.getObject(NetworkPackage.N_OBJECT, Sms.class);
            GuiCommunicator.show_sms(device.getId(),sms);
        }
    }

    @Override
    public void endWork() {

    }

    public static void send_sms(String name,String text,String dvId) {
        NetworkPackage np =  NetworkPackage.Cacher.getOrCreatePackage(SMS_TYPE,SEND);
        Sms message = new Sms(name,name,text,null,null);
        np.setObject(NetworkPackage.N_OBJECT,message);
        BackgroundService.sendToDevice(dvId,np.getMessage());
    }



}