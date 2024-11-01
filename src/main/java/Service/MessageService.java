package Service;

import java.util.*;

import Model.Message;
import DAO.AccountDAO;
import DAO.MessageDAO;

public class MessageService {
    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    // Constructor for MessageService
    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO;
    }

    public boolean usernameExistsById(int accountId) {
        return accountDAO.usernameExistsById(accountId);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageByID(int messageId) {
        return messageDAO.getMessageByID(messageId);
    }

    public void deleteMessageByID(int messageID) {
        messageDAO.deleteMessageByID(messageID);
    }

    public Message createMessage(Message message) {
        return messageDAO.createMessage(message);
    }

    public void updateMessage(Message message) {
        messageDAO.updateMessage(message);
    }

    public List<Message> getMessagesByAccountID(int accountID) {
        return messageDAO.getMessagesByAccountID(accountID);
    }
}
