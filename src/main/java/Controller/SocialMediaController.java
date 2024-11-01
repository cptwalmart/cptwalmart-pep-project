package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.AccountDAO;
import DAO.MessageDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.*;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    // Default constructor with default service instantiation
    public SocialMediaController() {
        this.accountService = new AccountService(new AccountDAO());
        this.messageService = new MessageService(new MessageDAO(), new AccountDAO());
    }

    // Constructor for dependency injection
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }


    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postAccountHandler);
        app.post("/login", this::postLoginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIDHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler);

        //app.start(8080);
        return app;
    }

    private void postAccountHandler(Context ctx) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Account account = mapper.readValue(ctx.body(), Account.class);

            // Validate username is not blank
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
                ctx.status(400).result("");
                return;
            }

            // Validate password length is at least 4 characters
            if (account.getPassword() == null || account.getPassword().length() < 4) {
                ctx.status(400).result("");
                return;
            }

            // Check if an account with the username already exists
            if (accountService.usernameExists(account.getUsername())) {
                ctx.status(400).result("");
                return;
            }

            // Persist the account to the database if all conditions are met
            Account addedAccount = accountService.addAccount(account);
            if (addedAccount != null) {
                ctx.json(mapper.writeValueAsString(addedAccount));
                ctx.status(200); // OK
            } else {
                ctx.status(400).result("");
            }
        } catch (JsonProcessingException e) {
            ctx.status(400).result("");
        } catch (Exception e) {
            ctx.status(400).result("");
        }
    }

    private void postLoginHandler(Context ctx) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Account account = mapper.readValue(ctx.body(), Account.class);

            // Access database and retrieve account & PW
            Account pwAccount = accountService.getAccount(account);

            // Compare username & PW
            if (pwAccount != null && pwAccount.getUsername().equals(account.getUsername())
                    && pwAccount.getPassword().equals(account.getPassword())) {
                // Send as JSON
                ctx.status(200).json(pwAccount);
            } else {
                // Mismatch credentials
                ctx.status(401).result("");
            }

        } catch (JsonProcessingException e) {
            ctx.status(400).result("");
        } catch (Exception e) {
            ctx.status(400).result("");
        }
    }

    private void postMessageHandler(Context ctx) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse the request body into a Message object (without message_id)
            Message message = mapper.readValue(ctx.body(), Message.class);

            // Validate message_text length and content
            if (message.getMessage_text() == null || message.getMessage_text().isBlank()
                    || message.getMessage_text().length() > 255) {
                ctx.status(400).result("");
                return;
            }

            // Check if posted_by refers to an existing user
            if (!messageService.usernameExistsById(message.getPosted_by())) {
                ctx.status(400).result("");
                return;
            }

            // Send the new message to the database and retrieve it with the generated
            // message_id
            Message createdMessage = messageService.createMessage(message);

            // Return the created message with a 200 status
            ctx.status(200).json(createdMessage);

        } catch (JsonProcessingException e) {
            ctx.status(400).result("");
        } catch (Exception e) {
            ctx.status(400).result("");
            e.printStackTrace();
        }
    }

    private void getAllMessagesHandler(Context ctx) {
        try {
            // Retrieve all messages from DB using message service
            List<Message> messages = messageService.getAllMessages();

            // Respond with the messages as a JSON array
            ctx.status(200).json(messages);
        } catch (Exception e) {
            ctx.status(400).result("");
        }
    }

    private void getMessageByIDHandler(Context ctx) {
        // Get message_id
        String messageIDStr = ctx.pathParam("message_id");

        try {
            int messageID = Integer.parseInt(messageIDStr);

            Message message = messageService.getMessageByID(messageID);

            if (message!= null){
                ctx.status(200).json(message);
            }else {
                ctx.status(200);
            }
            
        } catch (NumberFormatException e) {
            ctx.status(400).result("");
        } catch (Exception e) {
            ctx.status(400).result("");
        }

    }

    private void deleteMessageHandler(Context ctx) {
        String messageIDStr = ctx.pathParam("message_id");
        try {
            int messageID = Integer.parseInt(messageIDStr);

            // get message
            Message message = messageService.getMessageByID(messageID);

            if (message != null) {
                messageService.deleteMessageByID(messageID);
                ctx.status(200).json(message);
            } else {
                ctx.status(200).result("");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("");
        } catch (Exception e) {
            ctx.status(400).result("");
        }
    }

    private void updateMessageHandler(Context ctx) {
        String messageIDStr = ctx.pathParam("message_id");
        try {
            int messageID = Integer.parseInt(messageIDStr);

            // get new message_text
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> requestBody = mapper.readValue(ctx.body(), Map.class);
            String newMessageText = requestBody.get("message_text");
            // Validate message_text length and content
            if (newMessageText == null || newMessageText.isBlank()
                    || newMessageText.length() > 255) {
                ctx.status(400).result("");
                return;
            }

            // get message from db

            Message message = messageService.getMessageByID(messageID);

            if (message != null) {
                message.setMessage_text(newMessageText);
                messageService.updateMessage(message);

                ctx.status(200).json(message);
            } else {
                ctx.status(400).result("");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("");
        } catch (JsonProcessingException e) {
            ctx.status(400).result("");
        } catch (Exception e) {
            ctx.status(400).result("");
        }
    }

    public void getMessagesByUserHandler(Context ctx) {
        String accountIDStr = ctx.pathParam("account_id");

        try {
            int accountID = Integer.parseInt(accountIDStr);

            // Get messages from specified user
            List<Message> messages = messageService.getMessagesByAccountID(accountID);

            // Respond with JSON of messages (empty if none exist)
            ctx.status(200).json(messages);
        } catch (NumberFormatException e) {
            ctx.status(400).result("");
        } catch (Exception e) {
            ctx.status(400).result("");
        }
    }
}