package GroundZero;


import Callback.Callback;
import Callback.CallbackOperation;
import Callback.CallbackOperationExecuter;
import Command.Command;
import Command.CommandOperationExecuter;
import Command.PhotoCommandOperation;
import Command.TextCommandOperation;
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
        EditMessageReplyMarkup change;



        if (update.hasCallbackQuery()) {
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                int msgId = update.getCallbackQuery().getMessage().getMessageId();
                change = callbackOperationExecuter.reactToCallback(new CallbackOperation(new Callback(update)));

                try {
                    if(change == null){
                        SendMessage msg = new SendMessage();
                        msg.setReplyToMessageId(msgId);
                        msg.setText("Process finished");
                        msg.setChatId(chatId);
                        execute(msg);
                    }else
                    execute(change);

                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }



        } else {
            if (message.hasText()) {
                response = commandOperationExecuter.reactToIncomingMessage(new TextCommandOperation(new Command(message)));
                if(response.getText().contains("register")){
                    notifyOp(response.getText());
                }
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

    public void notifyOp(String s) {
        /*
            TODO: i) weiteren Bot, für die Neustartung etc des eigentlichen Bots.
         */

        SendMessage msg = new SendMessage();
        msg.setText(s);
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
