package Command;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


public interface CommandOperation {
    SendMessage execute();

    String print();
}


