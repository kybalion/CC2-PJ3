import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class MailClient {
	static Socket serverSocket;
	static BufferedReader in;
	static PrintWriter out;
	static boolean logged = false;
	static BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
	static Hashtable<String,String> errors = new Hashtable<String,String>(); 
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		connectToMailServer();
		/*if(!logged){
			login();
		}
		out.println("CLIST username");
		while ((reply = in.readLine()) != null){
			System.out.println(reply);
			if (reply.contains("*")) break;
		}*/
		/*out.println("SEND MAIL daniel@the.lord.of.the.mails.com");
		out.println("MAIL FROM alejandro@the.fellowship.of.mails.com");
		out.println("MAIL SUBJECT test");
		out.println("MAIL BODY estoesunaprueba");
		out.println("END SEND MAIL");*/
		out.println("CHECK CONTACT danie@the.lord.of.the.mails.com");
		System.out.println(in.readLine());
		in.close();
		out.close();
	}
	
	public static void login() throws IOException {
		initializateErrorsTable();
		String username,password;
		System.out.print("Ingrese el usuario: ");
		username = userInput.readLine();
		System.out.print("Ingrese el password: ");
		password = userInput.readLine();
		out.println("LOGIN "+username+" "+password);
	}
	
	public static void initializateErrorsTable() {
		errors.put("LOGIN ERROR 101", "unknown user");
		errors.put("LOGIN ERROR 102", "invalid password");
		errors.put("CLIST ERROR 103", "no contacts found");
		errors.put("SEND ERROR 104", "unknown contact");
		errors.put("SEND ERROR 105", "unknown server");
		errors.put("SEND ERROR 106", "no recipient(s)");
		errors.put("SEND ERROR 107", "no subject");
		errors.put("SEND ERROR 108", "no body");
		errors.put("LOGIN ERROR 101", "unknown user");
		errors.put("LOGIN ERROR 101", "unknown user");
		errors.put("LOGIN ERROR 101", "unknown user");
	}
	
	public static void connectToMailServer() throws UnknownHostException, IOException {
		serverSocket = new Socket("192.168.1.100", 1500);
		in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		out = new PrintWriter(serverSocket.getOutputStream(),true);
	}
}