package panda.rainmaker.wiki;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RobloxResponse{

	@JsonProperty("record_count")
	private int recordCount;

	@JsonProperty("records")
	private Records records;

	@JsonProperty("errors")
	private Errors errors;

	@JsonProperty("info")
	private Info info;

	public void setRecordCount(int recordCount){
		this.recordCount = recordCount;
	}

	public int getRecordCount(){
		return recordCount;
	}

	public void setRecords(Records records){
		this.records = records;
	}

	public Records getRecords(){
		return records;
	}

	public void setErrors(Errors errors){
		this.errors = errors;
	}

	public Errors getErrors(){
		return errors;
	}

	public void setInfo(Info info){
		this.info = info;
	}

	public Info getInfo(){
		return info;
	}
}