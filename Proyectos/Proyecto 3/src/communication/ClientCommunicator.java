package communication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.DataProvider;
import entities.IncomingEmail;

public class ClientCommunicator extends Thread {
    private Socket socket = null;
    
    public ClientCommunicator(Socket socket) {
        super("ClientCommunicator");
        this.socket = socket;
    }
    //comentario
    public void run() {
        System.out.println("Conexion establecida con cliente " + this.socket.getRemoteSocketAddress());
        try {
			attendClient(new ClientProcessor(new PrintWriter(socket.getOutputStream(), true), 
					new BufferedReader(new InputStreamReader(socket.getInputStream()))));
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void attendClient(ClientProcessor cp) {
    	DataProvider dataProvider = new DataProvider();
    	IncomingEmail email = new IncomingEmail();
    	while(cp.hasMessages()) {
			String message = cp.getMessage();
			System.out.println("Mensaje Recibido: " + message);
			switch (getExpression(message)) {
				case "LOGIN":
					cp.send(dataProvider.login(message));
					break;
					
				case "CLIST": 
					cp.sendContacts(dataProvider.getContacts());
					break;
					
				case "GETNEWMAILS": 
					cp.sendMails(dataProvider.getEmails());
					break;
					
				case "NEWCONT":
					cp.send(dataProvider.checkContact(message));
					break;
				
				case "SEND MAIL":
					email = new IncomingEmail();
					break;
					
				case "MAIL TO":
					email.addRecipient(message.split(" ")[2]);
					break;
					
				case "MAIL SUBJECT":
					Matcher subjectMatcher = Pattern.compile("^MAIL SUBJECT \"(.*)\"").matcher(message);
					subjectMatcher.find();
					email.setSubject(subjectMatcher.group(1));
					break;
					
				case "MAIL BODY":
					Matcher bodyMatcher = Pattern.compile("^MAIL BODY \"(.*)\"").matcher(message);
					bodyMatcher.find();
					email.setSubject(bodyMatcher.group(1));
					break;
					
				case "END SEND MAIL":
//					cp.send(dataProvider.ServerIncommingEmail(mailTo, mailFrom, mailSubject, mailBody));
					break;
					
				default:
					cp.send("INVALID COMMAND ERROR");
					break;
			}
		}
    }
    
	
	private static String getExpression(String message) {
		if (message.matches("^LOGIN .*")) {
			return "LOGIN";
		} else if (message.matches("^CLIST .*")) {
			return "CLIST";
		} else if (message.matches("^GETNEWMAILS .*")) {
			return "GETNEWMAILS";
		} else if (message.matches("^NEWCONT .*")) {
			return "NEWCONT";
		} else if(message.matches("^SEND MAIL .*")) {
			return "SEND MAIL";
		} else if(message.matches("(?i)^MAIL TO [_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")) {
			return "MAIL TO";
		} else if(message.matches("(?i)^MAIL SUBJECT \".*\"")) {
			return "MAIL SUBJECT";
		} else if(message.matches("(?i)^MAIL BODY \".*\"")) {
			return "MAIL BODY";
		} else if(message.matches("(?i)^END SEND MAIL")) {
			return "END SEND MAIL";
		} else if(message.matches("(?i)^CHECK CONTACT [_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")) {
			return "CHECK CONTACT";
		}
		return message;
	}
}