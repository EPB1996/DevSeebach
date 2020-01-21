package Callback;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;

public class CallbackOperation {
    Callback callback;

    public CallbackOperation(Callback callback) {
        this.callback = callback;
    }

    public EditMessageReplyMarkup execute() {
        return callback.handleCallback();
    }

    public String print() {
        return callback.print();
    }
}
