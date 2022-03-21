package panda.rainmaker.wiki;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FetchFields{

	@JsonProperty("recipes")
	private List<String> recipes;

	@JsonProperty("api-reference")
	private List<String> apiReference;

	@JsonProperty("videos")
	private List<String> videos;

	@JsonProperty("articles")
	private List<String> articles;

	public void setRecipes(List<String> recipes){
		this.recipes = recipes;
	}

	public List<String> getRecipes(){
		return recipes;
	}

	public void setApiReference(List<String> apiReference){
		this.apiReference = apiReference;
	}

	public List<String> getApiReference(){
		return apiReference;
	}

	public void setVideos(List<String> videos){
		this.videos = videos;
	}

	public List<String> getVideos(){
		return videos;
	}

	public void setArticles(List<String> articles){
		this.articles = articles;
	}

	public List<String> getArticles(){
		return articles;
	}
}