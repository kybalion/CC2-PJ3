package communication;
import java.io.IOException;
import java.net.ServerSocket;

public class Listener extends Thread  {
    private int port;
 
    public Listener(int port) {
        super("ClientListener");
        this.port = port;
    }
 
    public void run() {
        ServerSocket serverSocket = null;
        boolean listening = true;
 
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            System.err.println("No se puede escuchar en el puerto 1400");
            System.exit(0);
        }
 
        System.out.println("Escuchando en puerto "+this.port+"...");
        while (listening) { 
        	try {
        		if(this.port == 1400){
        			new ClientCommunicator(serverSocket.accept()).start();
        		}
        		else if(this.port == 1500){
        			new ServerCommunicator(serverSocket.accept()).start();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			} 
        }
 
        try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}