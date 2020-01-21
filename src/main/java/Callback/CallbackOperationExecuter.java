package Callback;

import Command.CommandOperation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;

import java.util.ArrayList;
import java.util.List;

public class CallbackOperationExecuter {
    private final List<CallbackOperation> commandOperationList = new ArrayList<>();

    public EditMessageReplyMarkup reactToCallback(CallbackOperation callbackOperation){
        /***
         * TODO: producing log from incoming commands
         */
        commandOperationList.add(callbackOperation);
        for(CallbackOperation cmd : commandOperationList){
            System.out.println(cmd.print());
        }
        return callbackOperation.execute();
    }
}
