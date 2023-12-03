package fr.univ_lyon1.info.m1.elizagpt.model;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageProcessor {

    private final ArrayList<Message> messages;
    private final Random random = new Random();

    public MessageProcessor() {
        this.messages = new ArrayList<>();
        //this.messages.add(new Message("Bonjour" , Message.Sender.ELIZA));
    }


    public String normalize(final String text) {
        return text.replaceAll("\\s+", " ")
                .replaceAll("^\\s+", "")
                .replaceAll("\\s+$", "")
                .replaceAll("[^\\.!?:]$", "$0.");
    }

    public static class Verb {
        private final String firstSingular;
        private final String secondPlural;

        public String getFirstSingular() {
            return firstSingular;
        }

        public String getSecondPlural() {
            return secondPlural;
        }

        Verb(final String firstSingular, final String secondPlural) {
            this.firstSingular = firstSingular;
            this.secondPlural = secondPlural;
        }
    }

    protected static final List<Verb> VERBS = Arrays.asList(
            new Verb("suis", "êtes"),
            new Verb("vais", "allez"),
            new Verb("dis", "dites"),
            new Verb("ai", "avez"),
            new Verb("fais", "faites"),
            new Verb("sais", "savez"),
            // Question 02
            new Verb("peux", "pouvez"),
            new Verb("dois", "devez")
    );



    public Message addMessage(String text, Message.Sender sender){
        String message = normalize(text);
        Message newMessage = new Message(message , sender);
        this.messages.add(newMessage);
        return newMessage;
    }

    public void deleteMessage(int messageId){
        this.messages.removeIf(message -> message.getId() == messageId);
    }

    public ArrayList<Message> search(String text){
        ArrayList<Message> result = new ArrayList<>();
        Pattern pattern;
        Matcher matcher;
        for(Message message : messages){
            pattern = Pattern.compile(text , Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(message.getText());
            if(matcher.find()){
                result.add(message);
            }
        }
        return result;
    }


    public String generateElizaResponse(String userMessage) {

        String normalizedText = normalize(userMessage);

        Pattern pattern;
        Matcher matcher;

        // First, try to answer specifically to what the user said
        pattern = Pattern.compile(".*Je m'appelle (.*)\\.", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(normalizedText);
        if (matcher.matches()) {
            return "Bonjour " + matcher.group(1) + ".";
        }

        pattern = Pattern.compile("Quel est mon nom \\?", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(normalizedText);
        if (matcher.matches()) {
            if (getUserName() != null) {
                return "Votre nom est " + getUserName() + ".";
            } else {
                return "Je ne connais pas votre nom.";
            }
        }

        pattern = Pattern.compile("Qui est le plus (.*) \\?", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(normalizedText);
        if (matcher.matches()) {
            return "Le plus " + matcher.group(1) + " est bien sûr votre enseignant de MIF01!";
        }

        pattern = Pattern.compile("(Je .*)\\.", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(normalizedText);
        if (matcher.matches()) {
            final String startQuestion = pickRandom(new String[]{
                    "Pourquoi dites-vous que ",
                    "Pourquoi pensez-vous que ",
                    "Êtes-vous sûr que ",
            });
            return startQuestion + firstToSecondPerson(matcher.group(1)) + " ?";
        }

        // Questions Detector
        pattern = Pattern.compile(".*\\?");
        matcher = pattern.matcher(normalizedText);
        if (matcher.matches()) {
            final String randomResponse = pickRandom(new String[]{
                    "Je vous renvoie la question.",
                    "Ici, c'est moi qui pose les questions.",
            });
            return randomResponse;
        }

        // Nothing clever to say, answer randomly
        if (random.nextBoolean()) {
            return "Il fait beau aujourd'hui, vous ne trouvez pas ?";
        }
        if (random.nextBoolean()) {
            return "Je ne comprends pas.";
        }
        if (random.nextBoolean()) {
            return "Hmmm, hmm ...";
        }

        // Default answer
        if (getUserName() != null) {
            return "Qu'est-ce qui vous fait dire cela, " + getUserName() + " ?";
        } else {
            return "Qu'est-ce qui vous fait dire cela ?";
        }
    }


    public String getUserName(){
        String result = null;
        Pattern pattern = Pattern.compile("Je m'appelle (.*)\\." , Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        ArrayList<Message> userMessages = (ArrayList<Message>) messages.stream().filter(
                message -> message.getSender() == Message.Sender.USER
        ).collect(Collectors.toList());

        for(Message message : userMessages){
            matcher = pattern.matcher(message.getText());
            if(matcher.matches()){
                result = matcher.group(1);
            }
        }
        return result;
    }

    public String firstToSecondPerson(final String text) {
        String processedText = text
                .replaceAll("[Jj]e ([a-z]*)e ", "vous $1ez ");
        for (Verb v : VERBS) {
            processedText = processedText.replaceAll(
                    "[Jj]e " + v.getFirstSingular(),
                    "vous " + v.getSecondPlural());
        }
        processedText = processedText
                .replaceAll("[Jj]e ([a-z]*)s ", "vous $1ssez ")
                .replace("mon ", "votre ")
                .replace("ma ", "votre ")
                .replace("mes ", "vos ")
                .replace("moi", "vous");
        return processedText;
    }


    public <T> T pickRandom(final T[] array) {
        return array[random.nextInt(array.length)];
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }




}
