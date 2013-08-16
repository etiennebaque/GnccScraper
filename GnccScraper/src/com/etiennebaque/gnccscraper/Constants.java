package com.etiennebaque.gnccscraper;

/**
 * Class of public constants, used for the project.
 * @author Etienne Baqué
 *
 */
public class Constants {

	/**
	 * Form name, used on the first page.
	 */
	public final static String FORM_NAME = "input";
	
	/** 
	 * Submit button value, used on the first page.
	 */
	public final static String SUBMIT_BUTTON_VALUE = "დასტური";
	
	/**
	 * Text field value, used on the first page.
	 */
	public final static String TEXT_FIELD_NAME = "company";
	
	/**
	 * Table tag name, used to isolate the result list table.
	 */
	public final static String TABLE_TAG_NAME = "table";
	
	/**
	 * Tbody tag name, used to isolate the result list.
	 */
	public final static String TBODY_TAG_NAME = "tbody";
	
	/**
	 * tr tag name, used to isolate the result list.
	 */
	public final static String TR_TAG_NAME = "tr";
	
	/**
	 * p tag name, used to isolate the broadcaster name and declaration url.
	 */
	public final static String P_TAG_NAME = "p";
	
	/**
	 * a tag name, used to isolate the declaration url.
	 */
	public final static String A_TAG_NAME = "a";
	
	/**
	 * href attribute of a tag, to retrieve the brodcaster document url.
	 */
	public final static String HREF_ATTRIBUTE = "href";
	
	/**
	 * CSV header, for broadcaster names.
	 */
	public final static String BROADCASTER_HEADER= "Broadcaster";
	
	/**
	 * CSV header, for broadcaster document url.
	 */
	public final static String DOC_URL_HEADER= "Document URL";

}
