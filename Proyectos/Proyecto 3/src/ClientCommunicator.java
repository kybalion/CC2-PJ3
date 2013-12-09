import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import data.DataProvider;

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
		}
		return "INVALID COMMAND ERROR";
	}
}