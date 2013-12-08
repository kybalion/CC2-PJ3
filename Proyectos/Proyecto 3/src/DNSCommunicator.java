import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class DNSCommunicator{
	String serverName, serverIP, dnsIP;
	int dnsPort;
	static Socket dnsSocket;
	static BufferedReader in;
	static PrintWriter out;

	public DNSCommunicator(String name, String ip, String dns, int port){
		this.serverName = name;
		this.serverIP = ip;
		this.dnsIP = dns;
		this.dnsPort = port;
		try {
			connectToDNSServer(this.dnsIP,this.dnsPort);
		} catch (UnknownHostException e) {
			System.out.println("No ha sido posible conectarse al DNS server "+this.dnsIP+":"+this.dnsPort);
		} catch (IOException e) {
			System.out.println("No ha sido posible conectarse al DNS server "+this.dnsIP+":"+this.dnsPort);
		}
	}
	
	public void sendOnline() throws IOException{
		out.println("ONLINE "+this.serverName+" "+this.serverIP);
		String reply = in.readLine();
		System.out.println(reply);
		
	}
	
	public void sendOffline() throws IOException{
		out.println("OFFLINE "+this.serverName);
		String reply = in.readLine();
		System.out.println(reply);
		
	}
	
	public void getIPTable() throws IOException{
		String reply = "";
		int regs = 0;
		String[] args;
		out.println("GETIPTABLE");
		while ((reply = in.readLine()) != null){
			if(reply.contains("OK IPTABLE")){
				regs++;
				if (regs == 1) MailServer.serversIPTable = new Hashtable<String,String>();
				args = reply.split(" ");
				MailServer.serversIPTable.put(args[2], args[3]);
				if (reply.contains("*")) break;
			}
			else{
				System.out.println(reply);
			}
		}
	}
	
	private static void connectToDNSServer(String ip, int port) throws UnknownHostException, IOException{
		dnsSocket = new Socket(ip , port);
		in = new BufferedReader(new InputStreamReader(dnsSocket.getInputStream()));
		out = new PrintWriter(dnsSocket.getOutputStream(),true);
	}
}