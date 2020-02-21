package Command;


import KeyboardLayout.InlineKeyboardLayout;
import Storage.Connect;
import Storage.PhotoQueue;
import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;


public class Command {
    private String command;
    private long chatId;
    private User user;
    private LocalDateTime date;
    private SendMessage sendMessage;
    private List<PhotoSize> photo;
    private Set<Integer> ownerSet = new HashSet<>();

    public Command(Message message) {
        if (message.hasText()) {
            this.command = message.getText();
            this.photo = null;
        } else {
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

        if(ownerSet.isEmpty()){
           ownerSet = c.getOwnerSet();
        }

        if (command.equals("New Group")) {

            boolean success = c.insertNewGroup(user.getId(),user.getFirstName());
            if(success) {
                c.addMemberToGroup(user.getId(), user.getFirstName() + "!" + String.valueOf(user.getId()));
                sendMessage.setText("Group Created");
            }
            else
                sendMessage.setText("Problem with creating Group. Group not created.");

        }

        if (command.equals("register")) {
            c.insertUnregeisteredUser(user.getId(), user.getFirstName());
            sendMessage.setText( user.getFirstName() + " was successfully registered");

        }

        if (command.equals("Add/Delete Member")) {

            InlineKeyboardLayout inlineKeyboardLayout = new InlineKeyboardLayout();
            inlineKeyboardLayout.setInlineKeyboardMarkup(inlineKeyboardLayout.getManageGroupMenu(), "", "destroy");

            sendMessage.setText("Actions:");
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if(command.equals("Member List")){

            Set<String[]> memberList = c.getFullMemberInformation(chatId);
            String msg = "";
            for(String[] infos: memberList){
                msg += "Name:\t_" + infos[0] + "_\n" +
                        "Since:\t_" + infos[1] +"_\n" +
                        "Posts:\t_" + infos[2] + "_\n\n";
            }
            sendMessage.setParseMode("markdown");

            sendMessage.setText(msg);

        }

        if (command.equals("/menu")) {
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

            List<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow row = new KeyboardRow();
            if(ownerSet.contains(user.getId())) {
                row.add("New Group");
                row.add("Add/Delete Member");
                row.add("Member List");
                keyboard.add(row);
                row = new KeyboardRow();
                row.add("Leave Group");
                row.add("Stats");
                keyboard.add(row);
            }else{
                row.add("Leave Group");
                row.add("Stats");
                keyboard.add(row);
            }



            keyboardMarkup.setKeyboard(keyboard);

            sendMessage.setText("Here is your menu");
            sendMessage.setReplyMarkup(keyboardMarkup);

        }

        if(command.equals("Leave Group")){
            HashMap<String, String> memberOfGroup = c.getAssociatedGroups(chatId);

            Set<Pair<String, String>> availableGroups = new HashSet<>();

            for (String key : memberOfGroup.keySet()) {
                if(!key.equals(String.valueOf(chatId)))
                availableGroups.add(new Pair<>(memberOfGroup.get(key)+"'s Group", key));

            }

            InlineKeyboardLayout inlineKeyboardLayout = new InlineKeyboardLayout();
            inlineKeyboardLayout.setInlineKeyboardMarkup(availableGroups,
                    "LeaveProcess:", "destroy");
            sendMessage.setText("Leave Group Process:");
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if(command.equals("Stats")){
            Set<String[]> memberList = c.getFullUserInformation(chatId);
            String msg = "";
            for(String[] infos: memberList){
                msg += "Name:\t" + infos[0] + "\n" +
                        "Since:\t" + infos[1] +"\n" +
                        "Posts:\t" + infos[2] + "\n \n";

            }
            sendMessage.setText(msg);
        }


        //maybe at some time implement this
        if(command.equals("Join Request")){
            HashMap<String, String> openGroups = c.getOwnerGroupPairs(chatId);

            Set<Pair<String, String>> availableGroups = new HashSet<>();


            for (String key : openGroups.keySet()) {

                availableGroups.add(new Pair<>(openGroups.get(key)+"'s Group", key));

            }

            InlineKeyboardLayout inlineKeyboardLayout = new InlineKeyboardLayout();
            inlineKeyboardLayout.setInlineKeyboardMarkup(availableGroups,
                    "JoinRequest:", "destroy");
            sendMessage.setText("Request to join:");
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if(command.equals("test")){
            sendMessage = handlePhotoCommand();
        }


        return sendMessage;
    }

    SendMessage handlePhotoCommand() {

        Set<Pair<String,String>> inlineButtons = new HashSet<>();
        inlineButtons.add(new Pair<>("Upload","UploadProcess"));
        inlineButtons.add(new Pair<>("Cancel","destroy"));

        InlineKeyboardLayout inlineKeyboardLayout = new InlineKeyboardLayout();
        inlineKeyboardLayout.setInlineKeyboardMarkup(inlineButtons,"",null);

        this.sendMessage.setText("Photo process:");
        sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        return sendMessage;
    }

    String print() {

        return date + " :\t " + user.getUserName() + " \t " + command;
    }
}
