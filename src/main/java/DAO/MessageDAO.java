package DAO;

import Model.Message;
import Util.ConnectionUtil;
import io.javalin.http.Context;

import java.sql.*;
import java.util.*;

public class MessageDAO {
    public Message createMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            int affectedRows = preparedStatement.executeUpdate();

            ResultSet resultSet = null;
            if (affectedRows > 0) {
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int generatedId = resultSet.getInt(1);
                    message.setMessage_id(generatedId);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return message;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<Message>();

        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                        rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return messages;
    }

    public Message getMessageByID(int messageID) {
        Message message = null;

        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, messageID);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException e) {
            System.out.println("Error");
        }
        return message;
    }

    public void deleteMessageByID(int messageID) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, messageID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting message by id: " + e.getMessage());
        }
    }

    public void updateMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, message.getMessage_text());
            preparedStatement.setInt(2, message.getMessage_id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating message by id: " + e.getMessage());
        }
    }

    public List<Message> getMessagesByAccountID(int accountID) {
        List<Message> messages = new ArrayList<Message>();
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, accountID);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                        rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message); // adds message to list
            }
        } catch (SQLException e) {
            System.out.println("Error getting messages by account ID: " + e.getMessage());
        }
        return messages; // Return the list of messages
    }
}
