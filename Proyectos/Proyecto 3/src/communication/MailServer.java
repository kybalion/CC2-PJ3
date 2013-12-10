package communication;
import java.io.IOException;
import java.util.Hashtable;

public class MailServer  {
	public static Hashtable<String,String> serversIPTable;
	public static DNSCommunicator dnsCommunicator = new DNSCommunicator("the.lord.of.the.mails2.com", "192.168.1.101", "192.168.1.100", 1200);
	
    public static void main(String[] args) throws IOException {
    	Listener clientListener = new Listener(1400);
    	Listener serverListener = new Listener(1500);
    	clientListener.start();
    	serverListener.start();
    	dnsCommunicator.sendOnline();
    	dnsCommunicator.getIPTable();
    	System.out.println(serversIPTable);
    }
}