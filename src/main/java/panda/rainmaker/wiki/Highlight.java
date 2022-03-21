package panda.rainmaker.wiki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Highlight{

	@JsonProperty("body")
	private String body;

	@JsonProperty("sections")
	private String sections;

	@JsonProperty("display_title")
	private String displayTitle;

	@JsonProperty("title")
	private String title;

	public void setBody(String body){
		this.body = body;
	}

	public String getBody(){
		return body;
	}

	public void setSections(String sections){
		this.sections = sections;
	}

	public String getSections(){
		return sections;
	}

	public void setDisplayTitle(String displayTitle){
		this.displayTitle = displayTitle;
	}

	public String getDisplayTitle(){
		return displayTitle;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}
}