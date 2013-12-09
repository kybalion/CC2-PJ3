package communication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import data.DataProvider;
import entities.IncomingEmail;

public class ClientCommunicator extends Thread {
    private Socket socket = null;
    
    public ClientCommunicator(Socket socket) {
        super("ClientCommunicator");
        this.socket = socket;
    }
    
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
    	IncomingEmail email;
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
					mailTo = ""; mailFrom =""; mailSubject = ""; mailBody="";
					mailTo = message;
					break;
				case "MAIL FROM":
					mailFrom = message;
					break;
				case "MAIL SUBJECT":
					mailSubject = message;
					break;
				case "MAIL BODY":
					mailBody = message;
					break;
				case "END SEND MAIL":
					cp.send(dataProvider.ServerIncommingEmail(mailTo, mailFrom, mailSubject, mailBody));
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
		} else if(message.matches("(?i)^SEND MAIL [_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")) {
			return "SEND MAIL";
		} else if(message.matches("(?i)^MAIL FROM [_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")) {
			return "MAIL FROM";
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