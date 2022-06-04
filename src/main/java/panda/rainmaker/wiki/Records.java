package panda.rainmaker.wiki;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Records{

	@JsonProperty("recipes")
	private List<RecordItem> recipes;

	@JsonProperty("api-reference")
	private List<RecordItem> apiReference;

	@JsonProperty("resources")
	// Extended
	private List<RecordItem> resources;

	@JsonProperty("videos")
	private List<RecordItem> videos;

	@JsonProperty("articles")
	private List<RecordItem> articles;

	@JsonProperty("learn-roblox")
	// Extended
	private List<RecordItem> learnRoblox;

	public void setRecipes(List<RecordItem> recipes){
		this.recipes = recipes;
	}

	public List<RecordItem> getRecipes(){
		return recipes;
	}

	public void setApiReference(List<RecordItem> apiReference){
		this.apiReference = apiReference;
	}

	public List<RecordItem> getApiReference(){
		return apiReference;
	}

	public void setResources(List<RecordItem> resources){
		this.resources = resources;
	}

	public List<RecordItem> getResources(){
		return resources;
	}

	public void setVideos(List<RecordItem> videos){
		this.videos = videos;
	}

	public List<RecordItem> getVideos(){
		return videos;
	}

	public void setArticles(List<RecordItem> articles){
		this.articles = articles;
	}

	public List<RecordItem> getArticles(){
		return articles;
	}

	public void setLearnRoblox(List<RecordItem> learnRoblox){
		this.learnRoblox = learnRoblox;
	}

	public List<RecordItem> getLearnRoblox(){
		return learnRoblox;
	}

	public Map<String, RecordItem> getFirstOfEach() {
		Map<String, RecordItem> firstOfEach = new HashMap<>();

		if (apiReference.size() > 0)
			firstOfEach.put("API Reference", apiReference.get(0));

		if (articles.size() > 0)
			firstOfEach.put("Articles", articles.get(0));

		// Extended
		if (learnRoblox.size() > 0)
			firstOfEach.put("Learn Roblox", learnRoblox.get(0));

		if (recipes.size() > 0)
			firstOfEach.put("Recipes", recipes.get(0));

		// Extended
		if (resources.size() > 0)
			firstOfEach.put("Resources", resources.get(0));

		if (videos.size() > 0)
			firstOfEach.put("Videos", videos.get(0));

		return firstOfEach;
	}

	public List<RecordItem> getCategory(String category) {
		List<RecordItem> topResults = null;

		switch (category) {
			case "API Reference":
				topResults = apiReference.subList(0, getEnd(apiReference));
				break;
			case "Articles":
				topResults = articles.subList(0, getEnd(articles));
				break;
			case "Learn Roblox":
				topResults = learnRoblox.subList(0, getEnd(learnRoblox));
				break;
			case "Recipes":
				topResults = recipes.subList(0, getEnd(recipes));
				break;
			case "Resources":
				topResults = resources.subList(0, getEnd(resources));
				break;
			case "Videos":
				topResults = videos.subList(0, getEnd(videos));
				break;
		}

		return topResults;
	}

	private int getEnd(List<RecordItem> recordItemList) {
		System.out.println(recordItemList.size());
		return Math.min(recordItemList.size(), 5);
	}
}