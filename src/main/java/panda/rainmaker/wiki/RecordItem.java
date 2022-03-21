package panda.rainmaker.wiki;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecordItem {

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("_index")
    private String index;

    @JsonProperty("display_title")
    private String displayTitle;

    @JsonProperty("_type")
    private String type;

    @JsonProperty("sort")
    private Object sort;

    @JsonProperty("_score")
    private double score;

    @JsonProperty("url")
    private String url;

    @JsonProperty("highlight")
    private Highlight highlight;

    @JsonProperty("api_type")
    private String apiType;

    @JsonProperty("segment")
    private String segment;

    @JsonProperty("_explanation")
    private Object explanation;

    @JsonProperty("id")
    private String id;

    @JsonProperty("category")
    private String category;

    @JsonProperty("_version")
    private Object version;

    // Extended
    @JsonProperty("description")
    private String description;

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("sections")
    private List<String> sections;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("published_at")
    private String publishedAt;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("body")
    private String body;

    public void setSummary(String summary){
        this.summary = summary;
    }

    public String getSummary(){
        return summary;
    }

    public void setIndex(String index){
        this.index = index;
    }

    public String getIndex(){
        return index;
    }

    public void setDisplayTitle(String displayTitle){
        this.displayTitle = displayTitle;
    }

    public String getDisplayTitle(){
        return displayTitle;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public void setSort(Object sort){
        this.sort = sort;
    }

    public Object getSort(){
        return sort;
    }

    public void setScore(double score){
        this.score = score;
    }

    public double getScore(){
        return score;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl(){
        return url;
    }

    public void setHighlight(Highlight highlight){
        this.highlight = highlight;
    }

    public Highlight getHighlight(){
        return highlight;
    }

    public void setApiType(String apiType){
        this.apiType = apiType;
    }

    public String getApiType(){
        return apiType;
    }

    public void setSegment(String segment){
        this.segment = segment;
    }

    public String getSegment(){
        return segment;
    }

    public void setExplanation(Object explanation){
        this.explanation = explanation;
    }

    public Object getExplanation(){
        return explanation;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getCategory(){
        return category;
    }

    public void setVersion(Object version){
        this.version = version;
    }

    public Object getVersion(){
        return version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
