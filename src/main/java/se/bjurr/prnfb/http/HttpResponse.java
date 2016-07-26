package se.bjurr.prnfb.http;

public class HttpResponse {
	public HttpResponse(int status, String content) {
		this.status = status;
		this.content = content;
	}
	
	private int status;
	private String content;
	
	public int getStatus() {
		return status;
	}
	
	public String getContent() {
		return content;
	}
}
