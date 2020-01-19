package GroundZero;

import Storage.Connect;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GroundZero {
    public static void main(String[] args) {


        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new MessageHandler());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
