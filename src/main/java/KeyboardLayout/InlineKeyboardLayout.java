package KeyboardLayout;

import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class InlineKeyboardLayout {
    InlineKeyboardMarkup inlineKeyboardMarkup;


    Set<Pair<String, String>> manageGroupMenu;
    List<List<InlineKeyboardButton>> rowsInline;


    public InlineKeyboardLayout() {
        this.inlineKeyboardMarkup = new InlineKeyboardMarkup();
                this.rowsInline = new ArrayList<>();


    }


    public List<List<InlineKeyboardButton>> getRowsInline() {
        return rowsInline;
    }

    public Set<Pair<String, String>> getManageGroupMenu() {
        this.manageGroupMenu = new HashSet<>();
        manageGroupMenu.add(new Pair<>("Delete Member", "DeleteProcess"));
        manageGroupMenu.add(new Pair<>("Add Member", "AddProcess"));
        return manageGroupMenu;
    }

    public InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return inlineKeyboardMarkup;
    }

    public void setInlineKeyboardMarkup(Set<Pair<String, String>> buttons, String addCallbackKey, String previous) {
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        int anzahlEleProZeilen = 0;
        for (Pair<String, String> keyValuePair : buttons) {
            InlineKeyboardButton button = new InlineKeyboardButton().setText(keyValuePair.getFirst())
                    .setCallbackData(addCallbackKey + keyValuePair.getSecond());
            rowInline.add(button);
            anzahlEleProZeilen += 1;
            if (anzahlEleProZeilen >= 3) {

            }
            rowsInLine.add(rowInline);
            rowInline = new ArrayList<>();
            anzahlEleProZeilen = 0;
        }

        if (previous != null) {
            InlineKeyboardButton backButton = new InlineKeyboardButton().setText("Back")
                    .setCallbackData(previous);
            rowInline.add(backButton);
        }


        rowsInLine.add(rowInline);
        this.rowsInline = rowsInLine;
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
    }


}
