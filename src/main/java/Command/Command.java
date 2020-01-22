package Command;

import KeyboardLayout.InlineKeyboardLayout;
import Storage.Connect;
import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDateTime;
import java.util.*;


public class Command {
    private String command;
    private long chatId;
    private User user;
    private LocalDateTime date;
    private SendMessage sendMessage;
    private List<PhotoSize> photo;


    public Command(Message message) {
        if (message.hasText()) {
            this.command = message.getText();
            this.photo = null;
        } else {
            this.command = "photo";
            this.photo = message.getPhoto();
        }
        this.user = message.getFrom();
        this.chatId = message.getChatId();
        this.date = java.time.LocalDateTime.now();
        this.sendMessage = new SendMessage();
        this.sendMessage.setChatId(chatId);

    }


    SendMessage handleTextCommand() {
        sendMessage.setText("Unknown command.");
        Connect c = new Connect();

        if (command.equals("New Group")) {


            c.insertNewGroup(user.getId());

            sendMessage.setText("Group Created");
        }

        if (command.equals("register")) {
            c.insertUnregeisteredUser(user.getId(), user.getFirstName());
            sendMessage.setText("Successfully Registered");
        }

        if (command.equals("Add/Delete Member")) {
            int groupId = c.getGroupId(user.getId());
            System.out.println(groupId);

            InlineKeyboardLayout inlineKeyboardLayout = new InlineKeyboardLayout();
            inlineKeyboardLayout.setInlineKeyboardMarkup(inlineKeyboardLayout.getManageGroupMenu(), "", "destroy");

            sendMessage.setText("Actions:");
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if(command.equals("Member List")){

            Set<String[]> memberList = c.getFullMemberInformation(chatId);
            String msg = "";
            for(String[] infos: memberList){
                msg += "Name:\t" + infos[0] + "\n" +
                        "Since:\t" + infos[1] +"\n" +
                        "Posts:\t" + infos[2] + "\n" +
                        "---------------------------------------------------\n";
            }
            sendMessage.setText(msg);

        }

        if (command.equals("/menu")) {

            //TODO: Menu only availible for owner && class for makrups?
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

            List<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow row = new KeyboardRow();

            row.add("New Group");
            row.add("Add/Delete Member");
            row.add("Member List");

            keyboard.add(row);

            keyboardMarkup.setKeyboard(keyboard);

            sendMessage.setText("Here is your menu");
            sendMessage.setReplyMarkup(keyboardMarkup);

        }


        return sendMessage;
    }

    SendMessage handlePhotoCommand() {
        /*
        TODO: set callback (do you want to upload this photo?)
         */
        this.sendMessage.setText("Photo uploading...");
        return sendMessage;
    }

    String print() {
        return date + " :\t " + user.getUserName() + " \t " + command;
    }
}
