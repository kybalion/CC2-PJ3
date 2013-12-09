import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerRequestor {
	
	
	public String checkUser(String serverName) {
		try {
			Socket serverSocket = new Socket(MailServer.serversIPTable.get(serverName), 1500);
			BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
