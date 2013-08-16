package com.etiennebaque.gnccscraper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.etiennebaque.gnccscraper.model.Broadcaster;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Main class that runs the GNCC scraper.
 * @author Etienne Baqué
 *
 */
public class Scraper {

	/**
	 * gncc.ge url.
	 */
	private final static String GNCC_URL = "http://gncc.ge/";

	/**
	 * gncc.ge url to use as a starting point for scraping.
	 */
	private final static String GNCC_URL_SCRAP = GNCC_URL + "index.php?lang_id=GEO&sec_id=50051";

	/**
	 * Method that run the GNCC scraper. No argument needs to be passed.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("****************************");
			System.out.println("* GNCC Broadcaster scraper *");
			System.out.println("****************************\n");

			System.out.println("** Retrieving search form now...\n");

			// We retrieve the first page, then the second page (the search result page), based on user input.
			HtmlPage resultPage = retrieveSearchResultPage();

			// We retrieve the tables contained in the search result page,
			DomNodeList<HtmlElement> resultListTables = resultPage.getBody().getElementsByTagName(Constants.TABLE_TAG_NAME);

			// We now parse the result page, one of the table contains the list of broadcasters.
			List<Broadcaster> broadcasterList = parseResultPage(resultListTables);

			if (broadcasterList != null){
				// If we found some broadcasters, we output now their information in a CSV file.
				writeCSVFile(broadcasterList);
			}

			// End of the program.
			System.out.println("\nBye!");

		} catch (IOException e) {
			System.out.println("ERROR: Problem occured while parsing a web page");
		} 

	}

	/**
	 * This method writes the list of broadcasters in a CSV file.
	 * @param broadcasterList List of broadcasters
	 */
	private static void writeCSVFile(List<Broadcaster> broadcasterList){
		FileWriter writer;
		try {
			writer = new FileWriter(System.getProperty("user.home") + "/ListOfBroadcasters.csv");
			// Headers
			writer.append(Constants.BROADCASTER_HEADER);
			writer.append(',');
			writer.append(Constants.DOC_URL_HEADER);
			writer.append('\n');

			for (Broadcaster broadcaster : broadcasterList){
				//Data
				writer.append(broadcaster.getName());
				writer.append(',');
				writer.append(broadcaster.getUrl());
				writer.append('\n');
			}

			writer.flush();
			writer.close();

			System.out.println("** Information about " + broadcasterList.size() + 
					" broadcaster(s) has been written into a CSV file, which can be found here: " + 
					System.getProperty("user.home") + "/ListOfBroadcasters.csv");

		} catch (IOException e) {
			System.out.println("ERROR: Problem occured while writing the data into the output CSV file.");
		}

	}


	/**
	 * This method parse the search result page, in order to retrieve the list of broadcasters.
	 * @param resultListTables
	 * @return list of Broadcaster
	 */
	private static List<Broadcaster> parseResultPage(DomNodeList<HtmlElement> resultListTables){
		List<Broadcaster> broadcasterList = null;
		if (resultListTables != null){
			for (HtmlElement table : resultListTables){
				// It happens that the list we want to parse is contained in 
				// a <table> without attributes. That's the only one on the page.
				if (table.getAttributesMap().size() == 0){
					DomNodeList<HtmlElement> resultList = table.getElementsByTagName(Constants.TBODY_TAG_NAME);

					if (resultList != null && resultList.size() >= 1){
						HtmlElement list = resultList.get(0);
						DomNodeList<HtmlElement> broadcasterResultList = list.getElementsByTagName(Constants.TR_TAG_NAME);
						broadcasterList = new ArrayList<Broadcaster>();
						for (HtmlElement broadcasterResult : broadcasterResultList){
							DomNodeList<HtmlElement> broadcasterNameAndUrlList = broadcasterResult.getElementsByTagName(Constants.P_TAG_NAME);
							HtmlElement broadcasterNameAndUrl = broadcasterNameAndUrlList.get(0);

							// We retrieve the broadcaster name here.
							DomNode child = broadcasterNameAndUrl.getFirstChild();
							String broadcasterName = child.asText();

							// We retrieve the PDF url here.
							DomNodeList<HtmlElement> urlTagList = broadcasterNameAndUrl.getElementsByTagName(Constants.A_TAG_NAME);
							HtmlElement urlTag = urlTagList.get(0);
							String url = urlTag.getAttribute(Constants.HREF_ATTRIBUTE);

							if (broadcasterName != null && url != null){
								broadcasterList.add(new Broadcaster(broadcasterName, GNCC_URL+url));
							}
						}
						break;
					}else{
						// No result have been found
						System.out.println("** No result found. No output file will be generated.");
						return null;
					}
				}
			}
		}
		return broadcasterList;

	}
	
	/**
	 * This method scrapes the first page, then the second based on user input.
	 * @return HtmlPage instance
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private static HtmlPage retrieveSearchResultPage() throws IOException{

		Scanner userInput = new Scanner (System.in);

		WebClient webClient = new WebClient();

		// Even though this setter is deprecated, it has been added anyway, because
		// parsing the main page using the WebClient instance was throwing some errors. 
		// (errors related to parsing some static files).
		webClient.setThrowExceptionOnScriptError( false ) ;

		// Get the first page, the one that has the POST form.
		HtmlPage page1 = webClient.getPage(GNCC_URL_SCRAP);

		// In order to submit the form, we look for it, as well as its
		// elements we need (ie text field, submit button...). 
		HtmlForm form = page1.getFormByName(Constants.FORM_NAME);
		HtmlSubmitInput button = form.getInputByValue(Constants.SUBMIT_BUTTON_VALUE);
		HtmlTextInput textField = form.getInputByName(Constants.TEXT_FIELD_NAME);

		System.out.println("** Search form retrieved.");
		//String query = console.readLine("** Enter a search word (no value here will return full list of broadcasters): ");
		System.out.println("** Enter a search word (no value here will return full list of broadcasters): ");
		String query = userInput.nextLine();

		if (query == null || (query != null && query.trim().isEmpty())){
			query = "";
		}

		// Change the value of the text field
		textField.setValueAttribute(query);

		System.out.println("** Retrieving the result list now...");

		// We submit the form here, and get the result page content in return.
		HtmlPage resultPage = button.click();
		
		return resultPage;
	}
}
