import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import entities.Email;


public class ServerProcessor {
	Stack<String> messages = new Stack<String>();
	private PrintWriter out;
	private BufferedReader in;
	
	public ServerProcessor(PrintWriter out, BufferedReader in) {
		this.out = out;
		this.in = in;
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
