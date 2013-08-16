package com.etiennebaque.gnccscraper.model;

/**
 * Model class to store information about a broadcaster.
 * @author Etienne Baqué
 *
 */
public class Broadcaster {

	private String name;
	private String url;
	
	public Broadcaster(){}
	
	public Broadcaster(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
