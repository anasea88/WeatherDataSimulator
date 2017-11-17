/**
 * The Main class is the main method which invoked while jar run
 * The class contains the complete flow of weather data prediction
 *
 * @author Anas E A
 * @version 0.0.1
 * @since November 17, 2017
 * Copyright (c) 2017,Anas E A. All rights reserved.
 */
package com.tcs.cba.weathersimulator.process;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.tcs.cba.weathersimulator.bean.WeatherForcastParameters;
import com.tcs.cba.weathersimulator.model.Location;
import com.tcs.cba.weathersimulator.utils.Constants;
import com.tcs.cba.weathersimulator.utils.Utils;

/* WeatherForcastInit is the main class of the application.
 * The main method is invoked while jar/executable run.
 * This invokes the processing of predicting weather parameters of days in specified date range.
 * The application takes 3 arguments.
 * 1.Start date in yyyy-mm-dd format.
 * 2.End date in yyyy-mm-dd format.
 * 3.Root directory.
 * historyWeatherParametersMap is a map which holds the history weather data parameters. This map key contains the 'day of year' and value contains 
 *     the list of beans 'WeatherForcastParameters' which holds the value of different weather parameters.
 *locations is a list contains the different state informations.
 *  * 		
 */
public class WeatherForcastInit {

	public static void main(String[] args)
			throws NumberFormatException, IOException, ParseException, InterruptedException {

		// Map contains the history weather data parameters. Key contains the 'day of
		// year' and value contains the list of weather data parameters.
		Map<Integer, List<WeatherForcastParameters>> historyWeatherParametersMap;

		// InputDataOperations class contains all history data related operations.
		InputDataOperations dataOperations = new InputDataOperations();

		// PredictDataOperations class contains all weather forecasting operations and
		// calculations.
		PredictDataOperations predictDataOperations = new PredictDataOperations();

		// Input arguments for date range (start date and end date)
		String startDate = args[0];
		String endDate = args[1];
		String rootDir = args[2];
		String locationPath = rootDir + Constants.LOCATION_DETAILS_PATH;

		// Loading location details
		List<Location> locations = Utils.loadLocationDetails(locationPath);

		if (locations.size() > 0 && locations != null) {
			for (Location location : locations) {
				// Fetching the history weather data for each location from a specified input
				// path.
				historyWeatherParametersMap = dataOperations.getHistoryWeatherData(location, rootDir);

				// Generate the weather forecasting report for the days in specified date range.
				predictDataOperations.generateWeatherForcastReport(historyWeatherParametersMap, startDate, endDate);

			}
		}
	}

}
