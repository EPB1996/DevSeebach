package Callback;

import Storage.Connect;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Callback {

    private CallbackQuery callback;
    private long chatId;
    private User user;
    private LocalDateTime date;
    private EditMessageReplyMarkup sendMessage;
    private long message_id;

    public Callback(Update update) {
        this.callback = update.getCallbackQuery();
        this.user = callback.getFrom();
        this.chatId = user.getId();
        this.date = java.time.LocalDateTime.now();
        this.message_id = callback.getMessage().getMessageId();

        this.sendMessage = new EditMessageReplyMarkup().setChatId(chatId).setMessageId((int)message_id);
    }

    EditMessageReplyMarkup handleCallback() {
        Connect c = new Connect();

        if(callback.getData().equals("Memberlist")) {

            HashMap<Long, String> memberList = c.getMemberlist(chatId);


            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            int anzahlEleProZeilen = 0;
            for (Long key : memberList.keySet()) {
                System.out.println(memberList.get(key));
                InlineKeyboardButton button = new InlineKeyboardButton().setText(memberList.get(key))
                        .setCallbackData("UserLookup:"+ key);
                rowInline.add(button);
                anzahlEleProZeilen += 1;
                if (anzahlEleProZeilen >= 2) {
                    rowsInLine.add(rowInline);
                    anzahlEleProZeilen = 0;
                    rowInline = new ArrayList<>();
                }else{

                }
            }

            inlineKeyboardMarkup.setKeyboard(rowsInLine);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        return this.sendMessage;
    }

    String print() {
        return date + " :\t " + user.getUserName() + " \t " + callback.getData();
    }
}
