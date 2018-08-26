package com.push.lazyir.modules;

import com.push.lazyir.devices.Device;
import com.push.lazyir.modules.battery.Battery;
import com.push.lazyir.modules.battery.BatteryDto;
import com.push.lazyir.modules.clipboard.ClipBoard;
import com.push.lazyir.modules.clipboard.ClipBoardDto;
import com.push.lazyir.modules.command.SendCommandDto;
import com.push.lazyir.modules.dbus.Mpris;
import com.push.lazyir.modules.dbus.MprisDto;
import com.push.lazyir.modules.memory.Memory;
import com.push.lazyir.modules.memory.MemoryDto;
import com.push.lazyir.modules.notifications.call.CallModuleDto;
import com.push.lazyir.modules.notifications.messengers.MessengersDto;
import com.push.lazyir.modules.notifications.notifications.ShowNotificationDto;
import com.push.lazyir.modules.notifications.call.CallModule;
import com.push.lazyir.modules.notifications.messengers.Messengers;
import com.push.lazyir.modules.notifications.notifications.ShowNotification;
import com.push.lazyir.modules.notifications.sms.SmsModule;
import com.push.lazyir.modules.command.SendCommand;
import com.push.lazyir.modules.notifications.sms.SmsModuleDto;
import com.push.lazyir.modules.ping.Ping;
import com.push.lazyir.modules.ping.PingDto;
import com.push.lazyir.modules.reminder.Reminder;
import com.push.lazyir.modules.reminder.ReminderDto;
import com.push.lazyir.modules.share.ShareModule;
import com.push.lazyir.modules.share.ShareModuleDto;
import com.push.lazyir.modules.touch.TouchControl;
import com.push.lazyir.modules.touch.TouchControlDto;
import com.push.lazyir.service.main.ModuleComponent;
import com.push.lazyir.utils.entity.Pair;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by buhalo on 05.03.17.
 */

public class ModuleFactory {
    private HashMap<String, Pair<Class,Class>> registeredModules = new HashMap<>();
    private ModuleComponent moduleComponent;
    private Method[] methods;
    private Lock lock = new ReentrantLock();

    public Module instantiateModule(Device dv, Class registeredModule)
    {
        lock.lock();
        try {
            Module module = null;
            Method method = getMethod(registeredModule);
            if(method == null)
                throw new NullPointerException("Such method doesn't exist  " + registeredModule.getSimpleName());
           method.setAccessible(true);
            module =(Module) method.invoke(moduleComponent);
            module.setDevice(dv);
            return module;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    private Method getMethod(Class registeredModule) {
        if (methods == null)
            methods = moduleComponent.getClass().getDeclaredMethods();
        Method method = null;
        for (Method mt : methods) {
            if(mt.getName().equals("provide"+registeredModule.getSimpleName())){
               method = mt;
               break;
            }
        }
        return method;
    }

    public void registerModulesInit() {
        if(registeredModules.isEmpty()) {
            registeredModules.put("SendCommand",new Pair<>(SendCommand.class, SendCommandDto.class));
            registeredModules.put("ShareModule",new Pair<>(ShareModule.class, ShareModuleDto.class));
            registeredModules.put("ShowNotification",new Pair<>(ShowNotification.class, ShowNotificationDto.class));
            registeredModules.put("SmsModule",new Pair<>(SmsModule.class, SmsModuleDto.class));
            registeredModules.put("Battery",new Pair<>(Battery.class, BatteryDto.class));
            registeredModules.put("Mpris",new Pair<>(Mpris.class, MprisDto.class));
            registeredModules.put("ClipBoard",new Pair<>(ClipBoard.class, ClipBoardDto.class));
            registeredModules.put("Messengers",new Pair<>(Messengers.class, MessengersDto.class));
            registeredModules.put("TouchControl",new Pair<>(TouchControl.class, TouchControlDto.class));
            registeredModules.put("CallModule",new Pair<>(CallModule.class, CallModuleDto.class));
            registeredModules.put("Reminder",new Pair<>(Reminder.class, ReminderDto.class));
            registeredModules.put("Memory",new Pair<>(Memory.class, MemoryDto.class));
            registeredModules.put("Ping",new Pair<>(Ping.class, PingDto.class));
        }
    }

    public Module instantiateModuleByName(Device dv,String name)
    {
        Pair<Class, Class> entry = registeredModules.get(name);
        Class moduleClass = entry.getLeft();
        return instantiateModule(dv, moduleClass);
    }


    public void setModuleComponent(ModuleComponent moduleComponent) {
        this.moduleComponent = moduleComponent;
    }

   public Class getModuleDto(String type){
        return registeredModules.get(type).getRight();
   }
}
