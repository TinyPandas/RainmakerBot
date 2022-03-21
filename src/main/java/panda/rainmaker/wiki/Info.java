package panda.rainmaker.wiki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Info{

	@JsonProperty("recipes")
	private InfoItem recipes;

	@JsonProperty("api-reference")
	private InfoItem apiReference;

	@JsonProperty("resources")
	private InfoItem resources;

	@JsonProperty("videos")
	private InfoItem videos;

	@JsonProperty("articles")
	private InfoItem articles;

	@JsonProperty("learn-roblox")
	private InfoItem learnRoblox;

	public void setRecipes(InfoItem recipes){
		this.recipes = recipes;
	}

	public InfoItem getRecipes(){
		return recipes;
	}

	public void setApiReference(InfoItem apiReference){
		this.apiReference = apiReference;
	}

	public InfoItem getApiReference(){
		return apiReference;
	}

	public void setResources(InfoItem resources){
		this.resources = resources;
	}

	public InfoItem getResources(){
		return resources;
	}

	public void setVideos(InfoItem videos){
		this.videos = videos;
	}

	public InfoItem getVideos(){
		return videos;
	}

	public void setArticles(InfoItem articles){
		this.articles = articles;
	}

	public InfoItem getArticles(){
		return articles;
	}

	public void setLearnRoblox(InfoItem learnRoblox){
		this.learnRoblox = learnRoblox;
	}

	public InfoItem getLearnRoblox(){
		return learnRoblox;
	}
}