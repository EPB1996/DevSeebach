package Command;

import Storage.Connect;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;


import java.time.LocalDateTime;
import java.util.List;




public class Command {
    private String command;
    private long chatId;
    private User user;
    private LocalDateTime date;
    private SendMessage sendMessage;
    private List<PhotoSize> photo;


    public Command(Message message) {
        if(message.hasText()){
            this.command = message.getText();
            this.photo = null;
        }else{
            this.command = "photo";
            this.photo = message.getPhoto();
        }
        this.user = message.getFrom();
        this.chatId = message.getChatId();
        this.date = java.time.LocalDateTime.now();
        prepareAnswer(chatId);

    }

    private void prepareAnswer(long chatId){
        this.sendMessage = new SendMessage();
        this.sendMessage.setChatId(chatId);
    }

    public SendMessage handleTextCommand(){

        Connect c = new Connect();
        if(command.equals("dbtest")){
            c.insertNewGroup(user.getId());

            sendMessage.setText("Group Created");
        }

        if(command.equals("register")){
            c.insertUnregeisteredUser(user.getId(),user.getUserName());
            sendMessage.setText("Successfully Registered");
        }


        return sendMessage;
    }

    public SendMessage handlePhotoCommand(){
        /*
        TODO: set callback (do you want to upload this photo?)
         */
        this.sendMessage.setText("Photo uploading...");
        return sendMessage;
    }

    public String print(){
        return date + " :\t " + user.getUserName() + " \t " + command;
    }
}
