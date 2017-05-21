package com.push.lazyir.modules.sendCommand;



import com.push.lazyir.Loggout;
import com.push.lazyir.pojo.Command;
import com.push.lazyir.devices.NetworkPackage;
import com.push.lazyir.managers.CommandManager;
import com.push.lazyir.modules.Module;
import com.push.lazyir.pojo.CommandsList;

import java.util.List;

/**
 * Created by buhalo on 05.03.17.
 */

public class SendCommand extends Module {
    public static final String SEND_COMMAND = "SendCommand";
    public static final String RECEIVED_COMMAND = "receivedCommand";
    public static final String EXECUTE = "execute";
    public static final String COMMAND = "command";


    @Override
    public void execute(NetworkPackage np) {

        if(np.getData().equals(EXECUTE))
        {
           executeCommand(np.getObject(NetworkPackage.N_OBJECT, CommandsList.class).getCommands());
        }

    }

    private void executeCommand(List<Command> commands) {
     //   List<String> commandsByNames = CommandManager.getInstance().getCommandsByNames(commands);
        for(Command command : commands)
        {
            try {
                Runtime.getRuntime().exec(command.getCommand());
            } catch (Exception e) {
                Loggout.e("SendCommand",e.toString());
            }
        }
    }


    private void saveCommand(List<Command> args)
    {
        CommandManager.getInstance().save(args);
    }
}
