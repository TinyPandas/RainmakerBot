package panda.rainmaker.rda_article;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleResponse{

	@JsonProperty("ArticleResponse")
	private List<ArticleResponseItem> articleResponse;

	public void setArticleResponse(List<ArticleResponseItem> articleResponse){
		this.articleResponse = articleResponse;
	}

	public List<ArticleResponseItem> getArticleResponse(){
		return articleResponse;
	}

	public List<ArticleResponseItem> getArticlesFromQuery(String query, String author) {
		if (articleResponse == null) return null;
		if (articleResponse.size() == 0) return new ArrayList<>();

		List<ArticleResponseItem> results = articleResponse.stream()
				.filter(item -> item.getTitle().toLowerCase().contains(query.toLowerCase()))
				.collect(Collectors.toList());

		if (author != null && author.length() > 0) {
			results = results.stream()
					.filter(item -> item.getAuthor().toLowerCase().contains(author.toLowerCase()))
					.collect(Collectors.toList());
		}

		return results;
	}
}