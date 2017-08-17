package com.push.lazyir.devices;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.push.lazyir.managers.tcp.ConnectionThread;
import com.push.lazyir.modules.Module;
import com.push.lazyir.modules.ModuleFactory;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by buhalo on 19.02.17.
 */

public class Device {
    public final static ConcurrentHashMap<String,Device> connectedDevices = new ConcurrentHashMap<>();
    private ConnectionThread thread;
    private String id;
    private String name;
    private InetAddress ip;
    private volatile boolean paired;
    private volatile boolean listening;
    private volatile boolean pinging;
    private volatile boolean answer;
    private HashMap<String, Module> enabledMdules = new HashMap<>();

    public Device(String id, String name, InetAddress ip, ConnectionThread runnableThread) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.thread = runnableThread;
        this.paired = false;
        this.listening = true;
        this.pinging = false;
        this.answer = false;
        for (Class registeredModule : ModuleFactory.getRegisteredModules()) {
            enabledMdules.put(registeredModule.getSimpleName(), ModuleFactory.instantiateModule(this,registeredModule));
        }
    }

    public void printToOut(String message)
    {
        thread.printToOut(message);
    }

    public boolean isConnected()
    {
        return thread != null && thread.isConnected();
    }

    public void closeConnection()
    {
        thread.closeConnection();
    }

    public static Map<String, Device> getConnectedDevices() {
        return connectedDevices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    public boolean isPinging() {
        return pinging;
    }

    public void setPinging(boolean pinging) {
        this.pinging = pinging;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public void disableModules()
    {
        enabledMdules.clear();
    }

    public void enableModule(String name)
    {
       enabledMdules.put(name,ModuleFactory.instantiateModuleByName(this,name));
    }

    public HashMap<String,Module> getEnabledModules()
    {
        return enabledMdules;
    }

}
