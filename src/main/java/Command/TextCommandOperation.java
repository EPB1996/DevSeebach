package Command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TextCommandOperation implements  CommandOperation{
    Command command;

    public TextCommandOperation(Command textComamnd) {
       this.command = textComamnd;
    }

    @Override
    public SendMessage execute() {
        return command.handleTextCommand();
    }

    @Override
    public String print() {
        return command.print();
    }


}
