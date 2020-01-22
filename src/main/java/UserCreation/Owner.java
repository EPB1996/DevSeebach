package UserCreation;

import org.telegram.telegrambots.meta.api.objects.User;

public class Owner implements LocalUser {
    private String firstName;
    private String lastName;
    private int chatID;

    public Owner(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.chatID = user.getId();
    }

    public int getUserChatId() {
        return this.chatID;
    }

    public String getUserName() {
        return this.getUserFirstName() + " " + this.getUserLastName();
    }

    public String getUserFirstName() {
        return this.firstName;
    }

    public String getUserLastName() {
        return this.lastName;
    }
}
