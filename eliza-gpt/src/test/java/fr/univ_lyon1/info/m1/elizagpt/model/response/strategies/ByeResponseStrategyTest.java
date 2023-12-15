package fr.univ_lyon1.info.m1.elizagpt.model.response.strategies;

import fr.univ_lyon1.info.m1.elizagpt.model.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ByeResponseStrategyTest {

    ByeResponseStrategy bey_obj;
    ArrayList<Message> Messages;
    @BeforeEach
    public void setUp(){
        bey_obj = new ByeResponseStrategy();
        Messages = new ArrayList<>();
        Messages.add(new Message("a test text !", Message.Sender.USER));
        Messages.add(new Message("a new test text !", Message.Sender.ELIZA));
        Messages.add(new Message("another one !", Message.Sender.ELIZA));
    }

    @Test
    void generateResponse() {
        String input = "Au revoir.";
        String expected_res  = "Oh non, c'est trop triste de se quitter !";
        String result = bey_obj.generateResponse(Messages,input);

        assertEquals(result,expected_res);
    }
    @Test
    void generateResponseWithName() {
        Messages.add(new Message("Je m'appelle abdennour.", Message.Sender.USER));
        String input = "Au revoir.";
        String expected_res  = "Oh non, c'est trop triste de se quitter abdennour !";
        String result = bey_obj.generateResponse(Messages,input);
        assertEquals(expected_res,result);
    }
}