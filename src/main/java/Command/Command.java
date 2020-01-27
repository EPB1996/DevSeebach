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
    private Set<Integer> ownerSet = new HashSet<>();

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
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

            List<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow row = new KeyboardRow();
            if(ownerSet.contains(user.getId())) {
                row.add("New Group");
                row.add("Add/Delete Member");
                row.add("Member List");
                keyboard.add(row);
                row = new KeyboardRow();
                row.add("Show Memberstatus");
                row.add("Leave Group");
                row.add("Stats");
                keyboard.add(row);
            }else{
                row.add("Show Memberstatus");
                row.add("Leave Group");
                row.add("Stats");
                keyboard.add(row);
            }



            keyboardMarkup.setKeyboard(keyboard);

            sendMessage.setText("Here is your menu");
            sendMessage.setReplyMarkup(keyboardMarkup);

        }

        if(command.equals("test")){
            sendMessage = handlePhotoCommand();
        }


        return sendMessage;
    }

    SendMessage handlePhotoCommand() {
        /*
        TODO: set callback (do you want to upload this photo?)
         */
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
