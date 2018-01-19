package com.push.gui.controllers;

import com.push.gui.basew.Popup;
import com.push.gui.entity.NotificationDevice;
import com.push.gui.entity.PhoneDevice;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import java.util.List;
import java.util.Optional;


// Class wich actually update gui
public class ApiController {

    private static MainController mainController;
    private static ApiController apiController;

    private ApiController(){}

    public static MainController getMainController() {
        return mainController;
    }

    public static void setMainController(MainController mainController) {
        ApiController.mainController = mainController;
    }

    public static ApiController getInstance() {
        if(apiController == null)
            apiController = new ApiController();
        return apiController;
    }

    public void newDeviceConnected(PhoneDevice phoneDevice) {
        Platform.runLater(() -> mainController.getMainApp().getConnectedDevices().add(phoneDevice));
    }

    public void deviceDisconnected(String id){
        Platform.runLater(
                () -> {  PhoneDevice deviceById = getDeviceById(id);
                        mainController.getMainApp().getConnectedDevices().remove(deviceById);});
    }

    public void setBatteryStatus(String id,int battery,boolean charging){
       Platform.runLater(()-> {
           PhoneDevice deviceById = getDeviceById(id);
           deviceById.setBattery(battery);
           deviceById.setCharging(charging);
           refreshSelection(id);
       });
    }

    public void setDevicePaired(String id,boolean paired){
        Platform.runLater(()->{
            PhoneDevice deviceById = getDeviceById(id);
            deviceById.setPaired(paired);
            refreshSelection(id);
        });
    }

    public void setDeviceMounted(String id,boolean mounted){
        Platform.runLater(()->{
            PhoneDevice deviceById = getDeviceById(id);
            deviceById.setPaired(mounted);
            refreshSelection(id);
        });
    }

    public void setDeviceNotifications(String id, List<NotificationDevice> notifications){
        Platform.runLater(()->{
            PhoneDevice deviceById = getDeviceById(id);
            deviceById.setNotifications(FXCollections.observableArrayList(notifications));
            refreshSelection(id);
        });
    }

    private void refreshSelection(String id){
        int selectedIndex = mainController.getPersonList().getSelectionModel().getSelectedIndex();
        mainController.getPersonList().getSelectionModel().select(-1);
        mainController.getPersonList().getSelectionModel().select(selectedIndex);
    }

    private PhoneDevice getDeviceById(String id){
        Optional<PhoneDevice> first = mainController.getMainApp().getConnectedDevices().stream().filter(device -> device.getId().equals(id)).findFirst();
        return first.orElse(null);
    }

    public void showNotification(String id, NotificationDevice notification) {
        Popup.show(id,notification,mainController);
    }

    public void removeNotificationCallEnd(String id, String callerNumber){
        Popup.callEnd(id,callerNumber);
    }

    public void requestPair(String id,NotificationDevice notificationDevice) {
       showNotification(id,notificationDevice);
    }
}
