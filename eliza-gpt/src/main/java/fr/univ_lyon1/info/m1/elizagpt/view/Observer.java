package fr.univ_lyon1.info.m1.elizagpt.view;

import fr.univ_lyon1.info.m1.elizagpt.model.payload.Update;

/**
 * The {@code Observer} interface defines methods to be implemented by classes
 * observing changes in the view of the Eliza GPT application. Observers can react to
 * updates related to message addition, deletion, search, and undo search operations.
 * <p>
 * Classes implementing this interface should handle updates received through the
 * specified methods to maintain synchronization with the application's model.
 *
 * @version 1.0
 */
public interface Observer {

     /**
      * Notifies the observer when a new message is added to the application.
      *
      * @param addUpdate The update object containing information about the added message.
      */
     void onMessageAddUpdate(Update addUpdate);

     /**
      * Notifies the observer when a message is deleted from the application.
      *
      * @param deleteUpdate The update object containing information about the deleted message.
      */
     void onDeleteUpdate(Update deleteUpdate);

     /**
      * Notifies the observer when a search operation is performed in the application.
      *
      * @param searchUpdate The update object containing search-related information.
      */
     void onSearchUpdate(Update searchUpdate);

     /**
      * Notifies the observer when the undo search operation is performed in the application.
      *
      * @param searchUpdate The update object containing information about the search result.
      */
     void onUndoSearchUpdate(Update searchUpdate);
}
