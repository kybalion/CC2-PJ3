import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import data.DataProvider;

public class ServerCommunicator extends Thread {
    private Socket socket = null;
    
    public ServerCommunicator(Socket socket) {
        super("ServerCommunicator");
        this.socket = socket;
    }
    
    public void run() {
        System.out.println("Conexion establecida con servidor " + this.socket.getRemoteSocketAddress());
        try {
			attendServer(new ServerProcessor(new PrintWriter(socket.getOutputStream(), true), 
					new BufferedReader(new InputStreamReader(socket.getInputStream()))));
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void attendServer(ServerProcessor sp) {
    	DataProvider dataProvider = new DataProvider();
    	String mailTo ="", mailFrom = "", mailSubject = "", mailBody = "";
    	while(sp.hasMessages()) {
			String message = sp.getMessage();
			System.out.println("Mensaje Recibido: " + message);
			switch (getExpression(message)) {
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
					sp.send(dataProvider.ServerIncommingEmail(mailTo, mailFrom, mailSubject, mailBody));
					break;
				case "CHECK CONTACT":
					sp.send(dataProvider.ServerCheckContact(message));
					break;
				case "SEND ERROR 202":
					sp.send("SEND ERROR 202");
					break;
				default:
					sp.send("INVALID COMMAND ERROR");
					break;
			}
		}
    }
    
	
	private static String getExpression(String message) {
		if(message.matches("(?i)^SEND MAIL [_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")){
			return "SEND MAIL";
		}
		else if(message.matches("(?i)^MAIL FROM [_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")){
			return "MAIL FROM";
		}
		if(message.matches("(?i)^SEND MAIL\\s*")){
			return "SEND ERROR 202";
		}
		else if(message.matches("(?i)^MAIL SUBJECT \".*\"")){
			return "MAIL SUBJECT";
		}
		else if(message.matches("(?i)^MAIL BODY \".*\"")){
			return "MAIL BODY";
		}
		else if(message.matches("(?i)^END SEND MAIL")){
			return "END SEND MAIL";
		}
		else if(message.matches("(?i)^CHECK CONTACT [_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})")){
			return "CHECK CONTACT";
		}
		return "INVALID COMMAND ERROR";
	}
}