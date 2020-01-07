package UserCreation;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageHandler extends TelegramLongPollingBot {
    long op = 198057550;



    public void onUpdateReceived(Update update) {

        if(update.getMessage().hasText())
            handleTextCommand(update);






    }

    private void handleTextCommand(Update update){
        Message message = update.getMessage();
        String command = message.getText();
        long chatId = message.getChatId();

        SendMessage answerMessage = new SendMessage();
        answerMessage.setChatId(new Long(0));

        if(command.equals("test")){
            answerMessage.setText("Test");
        }

        try {
            execute(answerMessage);
        } catch (TelegramApiException e) {
            notifyOp(e);
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
