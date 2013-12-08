import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import entities.Email;


public class ClientProcessor {
	
	Stack<String> messages = new Stack<String>();
	private PrintWriter out;
	private BufferedReader in;
	
	public ClientProcessor(PrintWriter out, BufferedReader in) {
		this.out = out;
		this.in = in;
	}
	
	public void sendMails(Stack<Email> emails) {
		if (!emails.isEmpty()) {
			while (!emails.isEmpty()) {
				Email email = emails.pop();
				out.println("OK GETNEWMAILS " + email.getSentBy() + 
							" " + email.getSubject() + 
							" " + email.getBody() + 
							(emails.isEmpty()? " *" : ""));
			}
		} else {
			out.println("OK GETNEWMAILS NOMAILS");
		}
	}
	
	public void sendContacts(Stack<String> contacts) {
		if (!contacts.isEmpty()) {
			while (!contacts.isEmpty()) {
				String contact = contacts.pop();
				out.println("OK CLIST " + contact + (contacts.isEmpty()? " *" : ""));
			}
		} else {
			out.println("CLIST ERROR 103");
		}
	}
	
	public String newContact(String contact) {
		
		return null;
	}
	
	public void send(String message) {
		out.println(message);
	}
	
	public boolean hasMessages() {
		String message = null;
		try {
			if (messages.isEmpty()) {
				message = in.readLine();
				if (message != null)
					messages.add(message);
			}
			return messages.isEmpty()? false : true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getMessage() {
		return messages.isEmpty()? null : messages.pop();
	}
}
