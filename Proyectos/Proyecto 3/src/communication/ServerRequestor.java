package communication;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import entities.IncomingEmail;


public class ServerRequestor {
	
	
	public static boolean checkUser(String user) {
		Socket serverSocket = null;
		try {
			serverSocket = new Socket(MailServer.serversIPTable.get(user.split("@")[1]), 1500);
			BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),true);
			
			out.println("CHECK CONTACT " + user);
			return in.readLine().startsWith("OK CHECK CONTACT");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static void sendMail(IncomingEmail email) {
		Socket serverSocket = null;
		try {
			serverSocket = new Socket(MailServer.serversIPTable.get(email.getToUserName().split("@")[1]), 1500);
			PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),true);
			
			out.println("SEND MAIL " + email.getToUserName());
			out.println("MAIL FROM " + email.getSentBy());
			out.println("MAIL SUBJECT " + email.getSubject());
			out.println("MAIL BODY " + email.getBody());
			out.println("END MAIL");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
