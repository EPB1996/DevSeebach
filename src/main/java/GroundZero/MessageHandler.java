package GroundZero;


import Command.*;
import Callback.*;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageHandler extends TelegramLongPollingBot {
    private long op = 198057550;
    private CommandOperationExecuter commandOperationExecuter = new CommandOperationExecuter();
    private CallbackOperationExecuter callbackOperationExecuter = new CallbackOperationExecuter();


    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        SendMessage response = new SendMessage();
        EditMessageReplyMarkup change= new EditMessageReplyMarkup();

        if(update.hasCallbackQuery()){
                change = callbackOperationExecuter.reactToCallback(new CallbackOperation(new Callback(update)));

                try {
                    execute(change);
                }catch (TelegramApiException e){
                    e.printStackTrace();
                }
        }else {
            if (message.hasText()) {
                response = commandOperationExecuter.reactToIncomingMessage(new TextCommandOperation(new Command(message)));
            } else if (message.hasPhoto()) {
                response = commandOperationExecuter.reactToIncomingMessage(new PhotoCommandOperation(new Command(message)));
            }
            
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }




    }

    public void notifyOp(Exception e) {
        /*
            TODO: i) weiteren Bot, für die Neustartung etc des eigentlichen Bots.
         */

        SendMessage msg = new SendMessage();
        msg.setText(e.toString());
        msg.setChatId(op);
        try {
            execute(msg);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
    public String getBotUsername() {

        return "FOTOWALL_Seebach_bot";
    }


    public String getBotToken() {
        // TODO
        return Secrets.TelegramBotKey;
    }

}
