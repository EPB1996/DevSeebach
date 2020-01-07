package Command;

import groovy.ui.SystemOutputInterceptor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

public class CommandOperationExecuter {
    private final List<CommandOperation> commandOperationList = new ArrayList<>();

    public SendMessage reactToIncomingMessage(CommandOperation commandOperation){
        /***
         * TODO: producing log from incoming commands
         */
        commandOperationList.add(commandOperation);
        for(CommandOperation cmd : commandOperationList){
            System.out.println(cmd.print());
        }
        return commandOperation.execute();
    }
}
