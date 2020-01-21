package Command;

import Storage.Connect;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
            c.insertUnregeisteredUser(user.getId(), user.getUserName());
            sendMessage.setText("Successfully Registered");
        }

        if (command.equals("Manage Group")) {
            int groupId = c.getGroupId(user.getId());
            System.out.println(groupId);

            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            InlineKeyboardButton showGroupMember = new InlineKeyboardButton().setText("Group Member")
                    .setCallbackData("Memberlist");
            InlineKeyboardButton deleteMember = new InlineKeyboardButton().setText("Delete Member")
                    .setCallbackData("DeleteProcess");
            InlineKeyboardButton addMember = new InlineKeyboardButton().setText("Add Member")
                    .setCallbackData("AddProcess");

            rowInline.add(showGroupMember);
            rowInline.add(deleteMember);
            rowInline.add(addMember);


            rowsInline.add(rowInline);

            markupInline.setKeyboard(rowsInline);
            sendMessage.setText("Actions:");
            sendMessage.setReplyMarkup(markupInline);
        }

        if (command.equals("/menu")) {

            //TODO: Menu only availible for owner && class for makrups?
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

            List<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow row = new KeyboardRow();

            row.add("New Group");
            row.add("Manage Group");
            row.add("Report Problem");

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
