package Command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class PhotoCommandOperation implements CommandOperation {
    Command command;

    public PhotoCommandOperation(Command textComamnd) {
        this.command = textComamnd;
    }

    @Override
    public SendMessage execute() {
        return command.handlePhotoCommand();
    }

    @Override
    public String print() {
        return command.print();
    }
}
