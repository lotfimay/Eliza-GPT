package fr.univ_lyon1.info.m1.elizagpt.view;

import fr.univ_lyon1.info.m1.elizagpt.model.payload.AddUpdate;
import fr.univ_lyon1.info.m1.elizagpt.model.payload.DeleteUpdate;
import fr.univ_lyon1.info.m1.elizagpt.model.payload.SearchUpdate;
import fr.univ_lyon1.info.m1.elizagpt.model.payload.Update;
import fr.univ_lyon1.info.m1.elizagpt.controller.Controller;
import fr.univ_lyon1.info.m1.elizagpt.model.message.Message;
import fr.univ_lyon1.info.m1.elizagpt.model.search.SearchStrategy;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ComboBox;


import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

/**
 * The JfxView class represents the JavaFX-based
 * graphical user interface for the Eliza GPT application.
 * It provides a chat-like interface where users
 * can interact with Eliza and view messages.
 */
public class JfxView implements Observer {
    private final VBox dialog;
    private TextField text = null;
    private TextField searchText = null;
    private Label searchTextLabel = null;
    private final Controller controller;
    private ComboBox<SearchStrategy> searchComboBox = null;


    private final Map<Integer, HBox> messageToHbox = new HashMap<>();

    /**
     * Creates the main view of the application.
     *
     * @param stage      The JavaFX stage.
     * @param width      The width of the stage.
     * @param height     The height of the stage.
     * @param controller The controller managing the application logic.
     */
    public JfxView(final Stage stage, final int width,
                   final int height, final Controller controller) {
        this.controller = controller;
        controller.registerObserver(this);

        stage.setTitle("Eliza GPT");

        final VBox root = new VBox(10);

        final Pane search = createSearchWidget();
        root.getChildren().add(search);

        ScrollPane dialogScroll = new ScrollPane();
        dialog = new VBox(10);
        dialogScroll.setContent(dialog);
        // scroll to bottom by default:
        dialogScroll.vvalueProperty().bind(dialog.heightProperty());
        root.getChildren().add(dialogScroll);
        dialogScroll.setFitToWidth(true);

        final Pane input = createInputWidget();
        root.getChildren().add(input);
        controller.undoSearch();
        // Everything's ready: add it to the scene and display it
        final Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        text.requestFocus();
        stage.show();
    }

    static final String BASE_STYLE = "-fx-padding: 8px; "
            + "-fx-margin: 5px; "
            + "-fx-background-radius: 5px;";
    static final String USER_STYLE = "-fx-background-color: #A0E0A0; " + BASE_STYLE;
    static final String ELIZA_STYLE = "-fx-background-color: #A0A0E0; " + BASE_STYLE;

    private void sendMessage(final String text) {
        controller.addUserMessage(text);
    }

    /**
     * Extract the name of the user from the dialog.
     * TODO: this totally breaks the MVC pattern, never, ever, EVER do that.
     *
     * @return The extracted name of the user (not working properly, to be fixed later).
     */
    private Pane createSearchWidget() {
        final HBox firstLine = new HBox();
        final HBox secondLine = new HBox();
        firstLine.setAlignment(Pos.BASELINE_LEFT);
        firstLine.setSpacing(10); // Adjust spacing between components

        searchText = new TextField();
        searchText.setOnAction(e -> searchText());

        searchComboBox = createComboBox();

        final Button searchButton = new Button("Search");
        searchButton.getStylesheets()
                .add(Objects.requireNonNull(getClass()
                        .getResource("/styles/buttons.css")).toExternalForm());
        searchButton.getStyleClass().add("search-button");
        searchButton.setOnAction(e -> searchText());
        searchButton.setOnMouseEntered(event -> searchButton.setCursor(Cursor.HAND));


        final Button undoSearchButton = new Button("Undo search");
        undoSearchButton.getStylesheets()
                .add(Objects.requireNonNull(getClass()
                        .getResource("/styles/buttons.css")).toExternalForm());
        undoSearchButton.getStyleClass().add("undo-search-button");
        undoSearchButton.setOnAction(e -> controller.undoSearch());
        undoSearchButton.setOnMouseEntered(event -> undoSearchButton.setCursor(Cursor.HAND));

        firstLine.getChildren().addAll(searchText, searchButton, undoSearchButton, searchComboBox);

        searchTextLabel = new Label();
        secondLine.getChildren().addAll(searchTextLabel);

        final VBox input = new VBox();
        input.getChildren().addAll(firstLine, secondLine);

        return input;
    }

    private ComboBox<SearchStrategy> createComboBox() {
        ComboBox<SearchStrategy> searchBox = new ComboBox<>();
        searchBox.getItems().addAll(controller.getSearchStrategies());
        searchBox.setOnAction(e -> {
            SearchStrategy strategy = searchBox.getSelectionModel().getSelectedItem();
            controller.setSearchStrategy(strategy);
        });
        searchBox.setPromptText("Select search strategy");
        searchBox.getStylesheets().add(Objects.requireNonNull(getClass()
                                        .getResource("/styles/combo-box.css"))
                                        .toExternalForm());
        return searchBox;
    }
    /**
     * Handles the user's action when initiating a search.
     * <p>
     * If the search text is not empty, this method triggers a search operation
     * through the controller, updates the search label, and clears the search text field.
     * If the search text is empty, it updates the search label to indicate no active search.
     */
    private void searchText() {
        String currentSearchText = this.searchText.getText();
        if (currentSearchText == null) {
            searchTextLabel.setText("No active search");
        } else {
            controller.search(currentSearchText);
            this.searchText.setText("");
        }
    }

    /**
     * Creates and returns a user input widget, consisting of a text field and a "Send" button.
     * <p>
     * The "Send" button triggers the sendMessage method when pressed, sending the text
     * from the input field to the controller. After sending, the input field is cleared.
     * <p>
     * This method encapsulates the creation and configuration of the user input components.
     *
     * @return A Pane containing the user input widget.
     */
    private Pane createInputWidget() {
        final HBox input = new HBox();
        text = new TextField();

        text.setPromptText("Type your message here");

        // Set an action event for the text field to handle pressing "Enter"
        text.setOnAction(e -> {
            sendMessage(text.getText());
            text.setText("");
        });

        final Button sendButton = new Button("Send");

        sendButton.getStylesheets()
                .add(Objects.requireNonNull(getClass()
                        .getResource("/styles/buttons.css")).toExternalForm());
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnMouseEntered(event -> sendButton.setCursor(Cursor.HAND));
        // Set an action event for the "Send" button to handle clicks
        sendButton.setOnAction(e -> {
            sendMessage(text.getText());
            text.setText("");
        });

        // Set Hgrow for the text field to make it take as much space as possible
        HBox.setHgrow(text, Priority.ALWAYS);

        // Set spacing between the text field and the button
        input.setSpacing(10); // Adjust the spacing as needed

        // Add the text field and the "Send" button to the input pane
        input.getChildren().addAll(text, sendButton);

        return input;
    }




    /**
     * Handles the update when a new message is added.
     * Adds the new message to the user interface.
     *
     * @param update The update object containing information about the added message.
     * @throws IllegalArgumentException If the provided update is not of type {@link AddUpdate}.
     */
    @Override
    public void onMessageAddUpdate(final Update update) throws IllegalArgumentException {
        try {
            AddUpdate addUpdate = (AddUpdate) update;
            HBox newHbox = createHBoxFromMessage(addUpdate.getNewMessage());
            messageToHbox.put(addUpdate.getNewMessage().getId(), newHbox);
            dialog.getChildren().add(newHbox);
        } catch (ClassCastException exception) {
            throw new IllegalArgumentException("Expected AddUpdate object but found another");
        }
    }



    /**
     * Handles the update when a message is deleted.
     * Removes the corresponding message from the user interface.
     *
     * @param update The update object containing information about the deleted message.
     * @throws IllegalArgumentException If the provided update is not of type {@link DeleteUpdate}.
     */
    @Override
    public void onDeleteUpdate(final Update update) throws IllegalArgumentException {
            try {
                DeleteUpdate deleteUpdate = (DeleteUpdate) update;
                int messageId = deleteUpdate.getDeletedMessageId();
                HBox toBeDeleted = messageToHbox.get(messageId);
                dialog.getChildren().remove(toBeDeleted);
                messageToHbox.remove(messageId);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException("Expected DeleteUpdate object but find another");
            }
    }

    /**
     * Handles the update when a search operation is performed.
     * Updates the search label with the search text and processes the search result.
     *
     * @param update The update object containing search-related information.
     * @throws IllegalArgumentException If the provided update is not of type {@link SearchUpdate}.
     */
    @Override
    public void onSearchUpdate(final Update update) throws IllegalArgumentException {
        try {
            SearchUpdate searchUpdate = (SearchUpdate) update;
            searchTextLabel.setText("Searching for: " + searchUpdate.getSearchText());
            List<Message> searchResult = searchUpdate.getSearchResult();
            processSearchResult(searchResult);
        } catch (ClassCastException exception) {
             throw new IllegalArgumentException("Expected SearchUpdate object but found another");
        }
    }


    /**
     * Handles the update when the user undoes a search operation.
     * Restores the original list of messages in the user interface.
     *
     * @param update The update object containing information for undoing the search.
     * @throws IllegalArgumentException If the provided update is not of type {@link SearchUpdate}.
     */
    @Override
    public void onUndoSearchUpdate(final Update update) throws IllegalArgumentException {
            try {
                SearchUpdate undoSearchUpdate = (SearchUpdate) update;
                searchTextLabel.setText(null);
                List<Message> allMessages = undoSearchUpdate.getSearchResult();
                processSearchResult(allMessages);
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException("Expected SearchUpdate object"
                        + " but found another");
            }
    }


    /**
     * Creates an HBox from a message for display in the dialog.
     *
     * @param message The message to be displayed.
     * @return An HBox containing the message label with appropriate styling.
     */
    private HBox createHBoxFromMessage(final Message message) {

        String messageText = message.getText();
        HBox parentHBox = new HBox();
        HBox hBox = new HBox();

        final Label label = new Label(messageText);


        parentHBox.setAlignment(
                message.getSender() == Message.Sender.ELIZA
                        ? Pos.BASELINE_LEFT : Pos.BASELINE_RIGHT
        );

        hBox.setStyle(
                message.getSender() == Message.Sender.ELIZA
                        ? ELIZA_STYLE : USER_STYLE
        );

        final Button button = createDeleteButton(message.getSender());
        button.setOnMouseClicked(e -> {
            controller.deleteMessage(message.getId());
        });
        if (message.getSender() == Message.Sender.ELIZA) {
            hBox.getChildren().addAll(label, button);
        } else {
            hBox.getChildren().addAll(button, label);
        }

        parentHBox.getChildren().add(hBox);
        return parentHBox;
    }

    private Button createDeleteButton(final Message.Sender sender) {
        final ImageView iconImageView = new ImageView(
                new Image(Objects.requireNonNull(getClass()
                        .getResourceAsStream("/icons/remove-icon.png"))));
        iconImageView.setFitHeight(16);
        iconImageView.setFitWidth(16);
        final Button button = new Button();
        button.setGraphic(iconImageView);
        button.setStyle("-fx-background-color: transparent;");
        button.setOnMouseEntered(event -> button.setCursor(Cursor.HAND));
        if (sender == Message.Sender.ELIZA) {
            button.setAlignment(Pos.TOP_RIGHT);
        } else {
            button.setAlignment(Pos.TOP_LEFT);
        }

        return button;
    }


    /**
     * Processes the search result messages and displays them in the dialog.
     * Clears the existing mapping of message IDs to HBoxes, creates new HBoxes
     * from the search result messages, and adds them to the dialog.
     *
     * @param messages The list of messages resulting from a search operation.
     */
    private void processSearchResult(final List<Message> messages) {
        messageToHbox.clear();

        ArrayList<HBox> result = new ArrayList<>();
        for (Message message : messages) {
            HBox hBox = createHBoxFromMessage(message);
            result.add(hBox);
            messageToHbox.put(message.getId(), hBox);
        }

        dialog.getChildren().clear();
        dialog.getChildren().addAll(result);
    }

}
