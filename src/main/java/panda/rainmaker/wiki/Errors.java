package panda.rainmaker.wiki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Errors{

	@JsonProperty("fetch_fields")
	private FetchFields fetchFields;

	public void setFetchFields(FetchFields fetchFields){
		this.fetchFields = fetchFields;
	}

	public FetchFields getFetchFields(){
		return fetchFields;
	}
}