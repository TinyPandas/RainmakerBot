package panda.rainmaker.wiki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InfoItem {

    @JsonProperty("per_page")
    private int perPage;

    @JsonProperty("total_result_count")
    private int totalResultCount;

    @JsonProperty("query")
    private String query;

    @JsonProperty("num_pages")
    private int numPages;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("facets")
    private Facets facets;

    public void setPerPage(int perPage){
        this.perPage = perPage;
    }

    public int getPerPage(){
        return perPage;
    }

    public void setTotalResultCount(int totalResultCount){
        this.totalResultCount = totalResultCount;
    }

    public int getTotalResultCount(){
        return totalResultCount;
    }

    public void setQuery(String query){
        this.query = query;
    }

    public String getQuery(){
        return query;
    }

    public void setNumPages(int numPages){
        this.numPages = numPages;
    }

    public int getNumPages(){
        return numPages;
    }

    public void setCurrentPage(int currentPage){
        this.currentPage = currentPage;
    }

    public int getCurrentPage(){
        return currentPage;
    }

    public void setFacets(Facets facets){
        this.facets = facets;
    }

    public Facets getFacets(){
        return facets;
    }
}
