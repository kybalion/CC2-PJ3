package data;

import data.DB;

import java.util.Stack;

import entities.IncomingEmail;
import entities.User;
import communication.ServerRequestor;

public class DataProvider {
	private static final String USER_LOGIN = "SELECT ID, USERNAME, PASSWORD FROM USERS WHERE USERNAME =";
	private static final String USER_CONTACTS = "SELECT USERNAME FROM CONTACTS WHERE USER_ID = ";
  	private static final String USER_MAILS = "SELECT SUBJECT, BODY, SENTBY FROM EMAILS WHERE READ = 0 AND TO_USER_ID = ";
	private static final String CHECK_CONTACT = "SELECT ID, USERNAME FROM USERS WHERE USERNAME = ";
    private static final String INSERT_MAIL = "INSERT INTO INCOMINGMAILS (BODY, SUBJECT, FROM_USERNAME, READ, RECEIVED_ON, TO_USER_ID) VALUES ";
    
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
			connection.executeQuery(USER_LOGIN+"'"+data[1]+"'");
			if(connection.next()){
				if (data[1].equals(connection.getString("USERNAME"))) {
					connectedUser = new User();
					connectedUser.setUserName((String) connection.getString("USERNAME"));
					connectedUser.setID((long)(Integer.parseInt((String) connection.getString("ID"))));
					return data[2].equals(connection.getString("PASSWORD"))? "OK LOGIN" : "LOGIN ERROR 102";  
				}
			}
			connection.close();
		} catch (Exception e) {
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
	
	public Stack<IncomingEmail> getEmails() {
    	try {
			connection.connect();
			connection.executeQuery(USER_MAILS + connectedUser.getID());
			Stack<IncomingEmail> emails = new Stack<IncomingEmail>();

			while (connection.next()) {
				IncomingEmail email = new IncomingEmail((String) connection.getString("subject"),
						(String) connection.getString("body"),
						(String) connection.getString("from_username"),
						connectedUser.getID());
				emails.push(email);
			}
			connection.close();
			return emails;
		} catch (Exception e) {
			System.out.println("Ocurrio un error al tratar de conectarse.");
		}
    	return null;
    }
	
		public String ServerIncommingEmail(String mailTo, String mailFrom, String mailSubject, String mailBody) {
		String[] to = mailTo.split(" ");
		String[] from = mailFrom.split(" ");
		Pattern patronSubject = Pattern.compile("^MAIL SUBJECT \"(.*)\"");
		Matcher subjectMatcher = patronSubject.matcher(mailSubject);
		subjectMatcher.find();
		Pattern patronBody = Pattern.compile("^MAIL BODY \"(.*)\"");
		Matcher bodyMatcher = patronBody.matcher(mailBody);
		bodyMatcher.find();
		
		if(subjectMatcher.group(1).equals("")){
			return "SEND ERROR 203";
		}
		if(bodyMatcher.group(1).equals("")){
			return "SEND ERROR 204";
		}
		try {
			connection.connect();
			connection.executeQuery(CHECK_CONTACT+"'"+to[2].toLowerCase()+"'");
			if(connection.next()){
				IncomingEmail newEmail = new IncomingEmail (subjectMatcher.group(1),bodyMatcher.group(1),from[2].toLowerCase(),(long)(Integer.parseInt((String) connection.getString("ID"))));
				connection.close();
				connection.connect();
				connection.executeNonQuery(INSERT_MAIL+"('"+newEmail.getBody()+"','"+newEmail.getSubject()+"','"+newEmail.getSentBy()+"',"+newEmail.getRead()+",'"+newEmail.getReceivedOn()+"',"+newEmail.getToUserID()+")");
				connection.close();
				return "OK SEND MAIL";
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Ocurrio un error al tratar de conectarse.");
		} finally {
			try {
				connection.close();
			} catch (UGDBDisconnectException e) {
				e.printStackTrace();
			}
		}
		return "SEND ERROR 201 "+to[2];
	}
	
	public boolean existContact(String contact) {
		try {
			connection.connect();
			connection.executeQuery(CHECK_CONTACT + "'" + contact + "'");
			return connection.next(); 
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Ocurrio un error al tratar de conectarse.");
		} finally {
			try {
				connection.close();
			} catch (UGDBDisconnectException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public String serverCheckContact(String message) { 
		String[] data = message.split(" ");
		return existContact(data[2])? "OK CHECK CONTACT " + data[2] : "CHECK ERROR 205";
	}
	
	public String checkContact(String message) {
		String[] data = message.split(" ");
		if (existContact(data[2])) {
			return "OK NEWCONT";
		} else if (data) {
			return "OK NEWCONT";
		}
		return "NEWCONT ERROR " + data[2];
	}
	
	public boolean insertNewUser(String username, String password) throws Exception
    {
        connection.connect();
        connection.executeQuery(USER_LOGIN +"'"+ username.toLowerCase()+"'");
        if(connection.next())
        {
            throw new Exception("Ya existe el usuario que desea agregar");
        }
        return connection.executeNonQuery("INSERT INTO users values (NULL, '"+username.toLowerCase()+"', '"+ password.toLowerCase() +"');");
        
    }
}
