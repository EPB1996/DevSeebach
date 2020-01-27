package Callback;

import KeyboardLayout.InlineKeyboardLayout;
import Storage.Connect;
import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

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

        this.sendMessage = new EditMessageReplyMarkup().setChatId(chatId).setMessageId((int) message_id);
    }

    EditMessageReplyMarkup handleCallback() {
        Connect c = new Connect();
        InlineKeyboardLayout inlineKeyboardLayout = new InlineKeyboardLayout();
        String callbackString = callback.getData();

        /**
         * manages group callbacks
         */
        if (callbackString.equals("Manage Group")) {
            inlineKeyboardLayout.setInlineKeyboardMarkup(inlineKeyboardLayout.getManageGroupMenu(), "", "destroy");
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if (callbackString.equals("DeleteProcess")) {

            HashMap<String, String> memberList = c.getMemberlist(chatId);
            Set<Pair<String, String>> altmemberList = new HashSet<>();
            for (String key : memberList.keySet()) {
                altmemberList.add(new Pair<>(memberList.get(key), key));
            }

            inlineKeyboardLayout.setInlineKeyboardMarkup(altmemberList, "deleteUser:", "Manage Group");
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if (callbackString.contains("deleteUser:")) {
            String userToLookup = callbackString.split(":")[1];

            HashMap<String, String> userInformation = c.getGroupMember(chatId, Integer.valueOf(userToLookup));


            List<List<InlineKeyboardButton>> rowsInLine;
            List<InlineKeyboardButton> rowInline = new ArrayList<>();


            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            String strDate = "unknown";
            if(userInformation.get("MemberSince")!= "null")
                strDate = dateFormat.format(Date.valueOf(userInformation.get("MemberSince")));

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(userInformation.get("UserName")
                    + "(" + strDate + ")"
                    + "(" + userInformation.get("PostNumber") +
                    ")");
            button.setCallbackData(callbackString);

            rowInline.add(button);


            Set<Pair<String, String>> yesNoSetup = new HashSet<>();
            yesNoSetup.add(new Pair<>("Yes", "Yes:" + userToLookup));


            inlineKeyboardLayout.setInlineKeyboardMarkup(yesNoSetup
                    , "DeleteDecision:", "DeleteProcess");

            rowsInLine = inlineKeyboardLayout.getRowsInline();
            rowsInLine.add(0, rowInline);


            sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(rowsInLine));


        }

        if (callbackString.contains("DeleteDecision:")) {
            String decision = callbackString.split(":")[1];
            if (decision.equals("Yes")) {
                c.deleteMemberFromGroup(chatId, callbackString.split(":")[2]);

                Set<Pair<String, String>> emptySet = new HashSet();
                emptySet.add(new Pair<>("Member has been deleted", "DeleteProcess"));
                inlineKeyboardLayout.setInlineKeyboardMarkup(emptySet, "", "DeleteProcess");
            }

            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if(callbackString.equals("AddProcess")){

            HashMap<String, String> registeredUser = c.getAvailableUsers(chatId);

            Set<Pair<String, String>> UnionUsers = new HashSet<>();

            for (String key : registeredUser.keySet()) {
                if(!key.equals(String.valueOf(chatId)))
                UnionUsers.add(new Pair<>(registeredUser.get(key), registeredUser.get(key) + "!" +key));
            }

            inlineKeyboardLayout.setInlineKeyboardMarkup(UnionUsers, "addUser:", "Manage Group");
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if (callbackString.contains("addUser:")) {
            String userToLookup = callbackString.split(":")[1];

            List<List<InlineKeyboardButton>> rowsInLine;


            Set<Pair<String, String>> yesNoSetup = new HashSet<>();
            yesNoSetup.add(new Pair<>("Yes", "Yes:" + userToLookup));


            inlineKeyboardLayout.setInlineKeyboardMarkup(yesNoSetup
                    , "AddDecision:", "AddProcess");

            rowsInLine = inlineKeyboardLayout.getRowsInline();


            sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(rowsInLine));
        }

        if (callbackString.contains("AddDecision:")) {
            String decision = callbackString.split(":")[1];
            if (decision.equals("Yes")) {
                c.addMemberToGroup(chatId, callbackString.split(":")[2]);

                Set<Pair<String, String>> emptySet = new HashSet();
                emptySet.add(new Pair<>("Member has been added", "AddProcess"));
                inlineKeyboardLayout.setInlineKeyboardMarkup(emptySet, "", "AddProcess");
            }

            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if(callbackString.equals("destroy")){
            Set<Pair<String,String>> destroy = new HashSet<>();
            inlineKeyboardLayout.setInlineKeyboardMarkup(destroy,"",null);
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());

            return null;
        }

        /**
         * manages Photo callbacks
         */
        if(callbackString.equals("UploadProcess")) {
            HashMap<String, String> memberOfGroup = c.getAssociatedGroups(chatId);

            Set<Pair<String, String>> availableGroups = new HashSet<>();

            for (String key : memberOfGroup.keySet()) {

                    availableGroups.add(new Pair<>(memberOfGroup.get(key)+"'s Group", key));

            }

            inlineKeyboardLayout.setInlineKeyboardMarkup(availableGroups,
                    "sendPhoto:", null);
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }

        if(callbackString.contains("sendPhoto")){
            String sendTo = callbackString.split(":")[1];

            //TODO: datatransfer logic here

            c.updateUserPosts(chatId,sendTo);

            return null;
        }


        return this.sendMessage;
    }


    String print() {
        return date + " :\t " + user.getUserName() + " \t " + callback.getData();
    }
}
