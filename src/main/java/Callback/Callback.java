package Callback;

import KeyboardLayout.InlineKeyboardLayout;
import Storage.Connect;
import Storage.PhotoQueue;
import org.glassfish.grizzly.utils.Pair;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;


import java.io.*;
import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class Callback  {
    String photoFolder = "/home/epb1996/Pictures/FotoWall/";
    private CallbackQuery callback;
    private long chatId;
    private User user;
    private LocalDateTime date;
    private EditMessageReplyMarkup sendMessage;
    private long message_id;
    private Set<String> alreadySentTo = new HashSet<>();

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
        System.out.println(alreadySentTo.size());

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
            if(userInformation.get("MemberSince").equals("null"))
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

        /*
        if(callbackString.equals("destroy")){
            Set<Pair<String,String>> destroy = new HashSet<>();
            inlineKeyboardLayout.setInlineKeyboardMarkup(destroy,"",null);
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }
        */

        /**
         * manages Photo callbacks
         */
        if(callbackString.equals("UploadProcess")) {
            HashMap<String, String> memberOfGroup = c.getAssociatedGroups(chatId);

            Set<Pair<String, String>> availableGroups = new HashSet<>();

            for (String key : memberOfGroup.keySet()) {

                    availableGroups.add(new Pair<>(memberOfGroup.get(key)+"'s Group", key));

            }
            availableGroups.add(new Pair<>("Done","destroy"));

            inlineKeyboardLayout.setInlineKeyboardMarkup(availableGroups,
                    "sendPhoto:", null);
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }


        if(callbackString.contains("sendPhoto")){

            PhotoQueue queue = PhotoQueue.getStreamInstance();

            String sendTo = callbackString.split(":")[1];

            if(sendTo.equals("destroy")){
                queue.removePhotoFromQueue(chatId);

                return null;
            }

            java.io.File photoToSend = queue.getPhotoOfId(chatId);

            DateFormat dateFormat = new SimpleDateFormat("HH_mm_ss");
            java.util.Date date = new java.util.Date();


            try {
                byte[] data = getBytesFromFile(photoToSend);
                System.out.println(data);
                OutputStream out = new FileOutputStream(new File(photoFolder + sendTo +"/"+ callback.getFrom().getFirstName()
                        +dateFormat.format(date)+".jpg"));
                out.write(data);
                out.close();
            }catch (IOException e){
                e.printStackTrace();
            }

            c.updateUserPosts(chatId,sendTo);
            queue.addToAlreadySentSet(chatId,sendTo);


            Pair<String,String> ownerIp = c.getOwnerIp(sendTo);
            sendToScreen(ownerIp,sendTo);


            HashMap<String, String> memberOfGroup = c.getAssociatedGroups(chatId);

            Set<Pair<String, String>> availableGroups = new HashSet<>();

            for (String key : memberOfGroup.keySet()) {
                if(!queue.getAlreadySentSet(chatId).contains(key)){
                    availableGroups.add(new Pair<>(memberOfGroup.get(key)+"'s Group", key));
                }

            }


            availableGroups.add(new Pair<>("Done","destroy"));


            inlineKeyboardLayout.setInlineKeyboardMarkup(availableGroups,
                    "sendPhoto:", null);
            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());

        }

        if(callbackString.contains("LeaveProcessBack")){
            HashMap<String, String> memberOfGroup = c.getAssociatedGroups(chatId);

            Set<Pair<String, String>> availableGroups = new HashSet<>();

            for (String key : memberOfGroup.keySet()) {

                availableGroups.add(new Pair<>(memberOfGroup.get(key)+"'s Group", key));

            }

            inlineKeyboardLayout = new InlineKeyboardLayout();
            inlineKeyboardLayout.setInlineKeyboardMarkup(availableGroups,
                    "LeaveProcess:", "destroy");

            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());
        }
        if(callbackString.contains("LeaveProcess:")){
            String groupToLeave = callbackString.split(":")[1];
            List<List<InlineKeyboardButton>> rowsInLine;
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            Set<Pair<String, String>> yesNoSetup = new HashSet<>();
            yesNoSetup.add(new Pair<>("Yes", "Yes:" + groupToLeave));


            inlineKeyboardLayout.setInlineKeyboardMarkup(yesNoSetup
                    , "LeaveDecision:", "LeaveProcessBack");

            rowsInLine = inlineKeyboardLayout.getRowsInline();
            rowsInLine.add(0, rowInline);


            sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(rowsInLine));
        }

        if(callbackString.contains("LeaveDecision:")){
            String decision = callbackString.split(":")[1];
            if (decision.equals("Yes")) {

                c.deleteMemberFromGroup(Long.parseLong(callbackString.split(":")[2]),String.valueOf(chatId));

                Set<Pair<String, String>> emptySet = new HashSet();
                emptySet.add(new Pair<>("Group has been left.", "LeaveProcessBack"));
                inlineKeyboardLayout.setInlineKeyboardMarkup(emptySet, "", "LeaveProcessBack");
            }

            sendMessage.setReplyMarkup(inlineKeyboardLayout.getInlineKeyboardMarkup());

        }




        return this.sendMessage;
    }

    private void sendToScreen(Pair<String,String> ownerIp,String sendTo){
        Process p;

        try {

            List<String> cmdList = new ArrayList<String>();
            // adding command and args to the list
            cmdList.add("sh");
            cmdList.add("uploader.sh");
            cmdList.add(ownerIp.getFirst()+"@"+ownerIp.getSecond());
            cmdList.add(sendTo);
            ProcessBuilder pb = new ProcessBuilder(cmdList);
            p = pb.start();

            p.waitFor();
            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

    }

    private static byte[] getBytesFromFile(File file) throws IOException {

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new IOException("File is too large!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        return bytes;
    }

    String print() {
        return date + " :\t " + user.getUserName() + " \t " + callback.getData();
    }
}
