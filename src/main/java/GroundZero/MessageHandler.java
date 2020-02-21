package GroundZero;


import Callback.Callback;
import Callback.CallbackOperation;
import Callback.CallbackOperationExecuter;
import Command.Command;
import Command.CommandOperationExecuter;
import Command.PhotoCommandOperation;
import Command.TextCommandOperation;
import Storage.PhotoQueue;
import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class MessageHandler extends TelegramLongPollingBot {
    private long op = 198057550;
    private CommandOperationExecuter commandOperationExecuter = new CommandOperationExecuter();
    private CallbackOperationExecuter callbackOperationExecuter = new CallbackOperationExecuter();
    int msgId = 0;




    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        SendMessage response = new SendMessage();
        EditMessageReplyMarkup change;





        if (update.hasCallbackQuery()) {

            long chatId = update.getCallbackQuery().getMessage().getChatId();
            int callbackmsgId = update.getCallbackQuery().getMessage().getMessageId();
            String callback = update.getCallbackQuery().getData();

            if(callback.contains("AddDecision:Yes") || callback.contains("DeleteDecision:Yes")){

                if(callback.contains("AddDecision:Yes")) {

                    response.setChatId( Long.parseLong(callback.split(":")[2].split("!")[1]));
                    response.setText("Welcome to " + update.getCallbackQuery().getFrom().getFirstName() + "'s Group. \n \n" +
                            "Please provide pictures in PORTRAIT ORIENTATION (if not instructed otherwise by the owner " +
                            "of this FotoWall)");
                }else{

                    response.setChatId(Long.parseLong(callback.split(":")[2]));
                    response.setText("You have been deleted from  " + update.getCallbackQuery().getFrom()
                            .getFirstName() + "'s Group. \n");

                }
                try {
                    execute(response);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }

            change = callbackOperationExecuter.reactToCallback(new CallbackOperation(new Callback(update)));

            try {

                if(callback.equals("destroy") || callback.contains("sendPhoto:destroy") || change == null ) {

                    DeleteMessage deleteCallback = new DeleteMessage();
                    DeleteMessage deleteCommand = new DeleteMessage();


                    deleteCommand.setMessageId(msgId);
                    deleteCommand.setChatId(chatId);

                    deleteCallback.setMessageId(callbackmsgId);
                    deleteCallback.setChatId(chatId);

                    execute(deleteCallback);
                    execute(deleteCommand);
                }else{
                    execute(change);
                }
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }



        } else {

            msgId = update.getMessage().getMessageId();
            if (message.hasText()) {
                response = commandOperationExecuter.reactToIncomingMessage(new TextCommandOperation(new Command(message)));
                if(response.getText().contains("register")){
                    notifyOp(response.getText());
                }
            } else if (message.hasPhoto()) {
                msgId = update.getMessage().getMessageId();
                PhotoQueue photoQueue = PhotoQueue.getStreamInstance();
                String filePath = getFilePath(message.getPhoto().stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null));

                //download the File
                java.io.File img = downloadPhotoByFilePath(filePath);

                photoQueue.add(new Pair<Pair<Long,java.io.File>, Set<String>>(new Pair<>(message.getChatId(),img),new HashSet<String>()));

                response = commandOperationExecuter.reactToIncomingMessage(new PhotoCommandOperation(new Command(message)));
            }

            try {
                response.setReplyToMessageId(msgId);
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

    private String getFilePath(final PhotoSize photo) {
        if (photo.hasFilePath()) {
            return photo.getFilePath();
        }
        final GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(photo.getFileId());
        try {
            final org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);
            return file.getFilePath();
        } catch (final TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }
    private java.io.File downloadPhotoByFilePath(String filePath) {
        try {
            // Download the file calling AbsSender::downloadFile method
            return downloadFile(filePath);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String getBotUsername() {

        return "FOTOWALL_Seebach_bot";
    }


    public String getBotToken() {
        // TODO
        return Secrets.TelegramBotKey;
    }

}
