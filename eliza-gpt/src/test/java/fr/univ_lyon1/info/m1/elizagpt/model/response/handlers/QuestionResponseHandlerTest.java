package fr.univ_lyon1.info.m1.elizagpt.model.response.handlers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionResponseHandlerTest {

    private QuestionResponseHandler questionResponseHandler;

    @BeforeEach
    public void setUp() {
        questionResponseHandler = new QuestionResponseHandler();
    }

    @Test
    void generateResponse() {
        String input = "a question???";
        String expectedResponse1  = "Je vous renvoie la question.";
        String expectedResponse2  = "Ici, c'est moi qui pose les questions.";
        String result = questionResponseHandler.handleResponse(input);

        assertTrue(result.equals(expectedResponse1) || result.equals(expectedResponse2));
    }
}
