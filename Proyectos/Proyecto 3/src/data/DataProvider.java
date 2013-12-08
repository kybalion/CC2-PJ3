package data;

import data.DB;

import java.util.Stack;

import entities.User;
import entities.Email;

public class DataProvider {
	private static final String USER_LOGIN = "SELECT ID, USERNAME, PASSWORD FROM USERS WHERE USERNAME =";
//    private static final String USER_LOGIN_KEY = "USER_LOGIN";
    private static final String USER_CONTACTS = "SELECT USERNAME FROM CONTACTS WHERE USER_ID = ";
//    private static final String USER_CONTACTS_KEY = "USER_CONTACTS";
    private static final String USER_MAILS = "SELECT SUBJECT, BODY, SENTBY FROM EMAILS WHERE READ = 0 AND TO_USER_ID = ";
//    private static final String USER_MAILS_KEYS = "USER_MAIL";
    
    DB connection;
    User connectedUser;
    
    public DataProvider() {
    	connection = new DB("server.db");
    }
    
    public User getConnectedUser() {
		return connectedUser;
	}

	public void setConnectedUser(User connectedUser) {
		this.connectedUser = connectedUser;
	}

	public String login(String message) {
		String[] data = message.split(" ");
		try {
			connection.connect();
			connection.executeQuery(USER_LOGIN + "'" + data[1] + "'");
			if(connection.next()) {
				if (data[1].equals(connection.getString("USERNAME"))) {
					connectedUser = new User();
					connectedUser.setUserName((String) connection.getString("USERNAME"));
					connectedUser.setID((long)(Integer.parseInt((String) connection.getString("ID"))));
					return data[2].equals(connection.getString("PASSWORD"))? "OK LOGIN" : "LOGIN ERROR 102";  
				}
			}
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Ocurrio un error al tratar de conectarse.");
		}
		return "LOGIN ERROR 101";
	}
    
	public Stack<String> getContacts() {
    	try {
			connection.connect();
			connection.executeQuery(USER_CONTACTS + connectedUser.getID());
			Stack<String> contacts = new Stack<String>();
			if (connection.getString("USERNAME") != null) {
				while (connection.next()) {
					contacts.push((String) connection.getString("USERNAME"));
				}
			}
			connection.close();
			return contacts;
		} catch (Exception e) {
			System.out.println("Ocurrio un error al tratar de conectarse.");
		}
    	return null;
    }
	
	public Stack<Email> getEmails() {
    	try {
			connection.connect();
			connection.executeQuery(USER_MAILS + connectedUser.getID());
			Stack<Email> emails = new Stack<Email>();
			if (connection.getString("USERNAME") != null) {
				Email email = new Email((String) connection.getString("USERNAME"),
						(String) connection.getString("USERNAME"),
						(String) connection.getString("USERNAME"),
						connectedUser.getID());
				emails.push(email);
				while (connection.next()) {
					email = new Email((String) connection.getString("USERNAME"),
							(String) connection.getString("USERNAME"),
							(String) connection.getString("USERNAME"),
							connectedUser.getID());
					emails.push(email);
				}
			}
			connection.close();
			return emails;
		} catch (Exception e) {
			System.out.println("Ocurrio un error al tratar de conectarse.");
		}
    	return null;
    }
}
