package entities;

import java.text.SimpleDateFormat;

public class IncomingEmail {
	private String subject;
	private String body;
	private String sentBy;
	private long toUserID;
	private String receivedOn;
	private byte read;
	
	public IncomingEmail(String subject, String body, String sentBy, long toUserID) {
		this.subject = subject;
		this.body = body;
		this.sentBy = sentBy;
		this.toUserID = toUserID;
		this.read = 0;
		java.util.Date now = new java.util.Date();
		this.receivedOn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSentBy() {
		return sentBy;
	}
	public void setSentBy(String sentBy) {
		this.sentBy = sentBy;
	}
	public long getToUserID() {
		return toUserID;
	}
	public void setToUserID(long toUserID) {
		this.toUserID = toUserID;
	}
	
	public byte getRead() {
		return read;
	}
	public void setRead(byte read) {
		this.read = read;
	}
	
	public void setReceivedOn(String receivedOn) {
		this.receivedOn = receivedOn;
	}
	public String getReceivedOn() {
		return receivedOn;
	}
}
