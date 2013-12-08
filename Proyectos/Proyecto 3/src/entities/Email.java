package entities;

public class Email {
	private String subject;
	private String body;
	private String sentBy;
	private long toUserID;
	
	public Email(String subject, String body, String sentBy, long toUserID) {
		this.subject = subject;
		this.body = body;
		this.sentBy = sentBy;
		this.toUserID = toUserID;
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
}
