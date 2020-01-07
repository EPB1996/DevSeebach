package GroundZero;


import Command.CommandOperationExecuter;
import Command.Command;
import Command.TextCommandOperation;
import Command.PhotoCommandOperation;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageHandler extends TelegramLongPollingBot {
    long op = 198057550;
    CommandOperationExecuter commandOperationExecuter = new CommandOperationExecuter();


    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        SendMessage response = new SendMessage();

        if(message.hasText()) {
                response = commandOperationExecuter.reactToIncomingMessage(new TextCommandOperation(new Command(message)));
        }else if(message.hasPhoto()){
                response = commandOperationExecuter.reactToIncomingMessage(new PhotoCommandOperation(new Command(message)));
        }




        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void notifyOp(TelegramApiException e){
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