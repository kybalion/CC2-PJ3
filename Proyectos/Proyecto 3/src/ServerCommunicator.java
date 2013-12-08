import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ServerCommunicator extends Thread {
    private Socket socket = null;
 
    public ServerCommunicator(Socket socket) {
        super("ServerCommunicator");
        this.socket = socket;
        /*try {
			this.socket.setSoTimeout(10*1000);
		} catch (SocketException e) {
			System.out.println("tiempo de conexion expirados");
		}*/
    }
 
    public void run() {
        try {
			System.out.println("Conexion establecida con server "+this.socket.getRemoteSocketAddress());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String receivedMsg, serverCmd, from = "", subject = "", body = "";
            String [] cmd_args;
            while ((receivedMsg = in.readLine()) != null) {
            	serverCmd = getServerCmd(receivedMsg);
            	switch(serverCmd){
            		case "SEND MAIL":
            			while ((receivedMsg = in.readLine()) != null) {
            				serverCmd = getServerCmd(receivedMsg);
            				if (serverCmd.equals("MAIL FROM")){
            					cmd_args = receivedMsg.split(" ");
            					from = cmd_args[2];
            				}
            				else if (serverCmd.equals("MAIL SUBJECT")){
            					cmd_args = receivedMsg.split(" ");
            					subject = cmd_args[2];
            				}
            				else if (serverCmd.equals("MAIL BODY")){
            					cmd_args = receivedMsg.split(" ");
            					body = cmd_args[2];
            				}
            				else if (serverCmd.equals("END SEND MAIL")){
            					break;
            				}
            			}
            			out.println("Yo no voy a enviar ningun correo de verga");
            			break;
            		case "CHECK CONTACT":
            			out.println("what app biatch!!!");
            			break;
            	}
            }
            out.close();
            in.close();
            socket.close();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	private static String getServerCmd(String msg){
		if(msg.matches("^SEND MAIL .*")){
			return "SEND MAIL";
		}
		if(msg.matches("^MAIL FROM .*")){
			return "MAIL FROM";
		}
		if(msg.matches("^MAIL SUBJECT .*")){
			return "MAIL SUBJECT";
		}
		if(msg.matches("^MAIL BODY .*")){
			return "MAIL BODY";
		}
		if(msg.matches("END SEND MAIL")){
			return "END SEND MAIL";
		}
		if(msg.matches("^CHECK CONTACT .*")){
			return "CHECK CONTACT";
		}
		return "INVALID COMMAND ERROR";
	}
}