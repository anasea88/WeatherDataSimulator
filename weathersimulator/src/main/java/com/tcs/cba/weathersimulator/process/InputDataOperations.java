/**
 * The InputDataOperations class contains all history weather data related operations.
 *  History weather data is loaded for each location specified in input file.
 *
 * @author Anas E A
 * @version 0.0.1
 * @since November 17, 2017
 * Copyright (c) 2017,Anas E A. All rights reserved.
 */
package com.tcs.cba.weathersimulator.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.tcs.cba.weathersimulator.bean.WeatherForcastParameters;
import com.tcs.cba.weathersimulator.model.Location;
import com.tcs.cba.weathersimulator.utils.Constants;

public class InputDataOperations {

	private String historyDataPath;
	private String locationCode;
	private String locationName;

	private Double latitude;
	private Double longitude;
	private String altitude;
	private Calendar calender;
	private SimpleDateFormat simpleDateFormat;

	/*
	 * This method load the history weather data parameters to a map.
	 * 
	 * @param location
	 * 
	 * @throws IOException
	 * 
	 * @throws ParseException
	 */
	public Map<Integer, List<WeatherForcastParameters>> getHistoryWeatherData(Location location,String rootDir)
			throws IOException, ParseException {

		Map<Integer, List<WeatherForcastParameters>> historyWeatherParametersMap = new TreeMap<Integer, List<WeatherForcastParameters>>();
		locationCode = location.getLocationCode();
		locationName = location.getLocationName();
		latitude = Double.parseDouble(location.getLatlong().split(Constants.COMMA)[0]);
		longitude = Double.parseDouble(location.getLatlong().split(Constants.COMMA)[1]);
		altitude = location.getAltitude().toString();
		historyDataPath = rootDir+location.getWeatherData().getLoc();
		WeatherForcastParameters weatherParametersBean = new WeatherForcastParameters();
		String weatherInfo = null;
		calender = new GregorianCalendar();
		simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.setLenient(false);

		File historyDataPathLoc = new File(historyDataPath);
		File[] weatherDataFiles = null;
		if (historyDataPathLoc.exists() && historyDataPathLoc.isDirectory()) {
			weatherDataFiles = historyDataPathLoc.listFiles();
			for (int i = 0; i < weatherDataFiles.length; i++) {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(weatherDataFiles[i]));
				while (bufferedReader.ready()) {
					weatherInfo = bufferedReader.readLine();
					weatherParametersBean = loadWeatherParameters(weatherInfo);

					List<WeatherForcastParameters> weatherParametersBeanList = null;
					int dayOfYear = weatherParametersBean.getDayOfYear();
					if (historyWeatherParametersMap.containsKey(dayOfYear)) {
						weatherParametersBeanList = historyWeatherParametersMap.get(dayOfYear);
					} else {
						weatherParametersBeanList = new ArrayList<WeatherForcastParameters>();
					}
					weatherParametersBeanList.add(weatherParametersBean);
					historyWeatherParametersMap.put(dayOfYear, weatherParametersBeanList);

				}

				if (bufferedReader != null) {
					bufferedReader.close();
				}

			}
		}

		return historyWeatherParametersMap;

	}

	/*
	 * This method will extract the parameters from each of line of history data and
	 * set to a bean.
	 * 
	 * @param weatherInfo
	 * 
	 * @throws IOException
	 * 
	 * @throws ParseException
	 */
	private WeatherForcastParameters loadWeatherParameters(String weatherInfo) throws IOException, ParseException {

		WeatherForcastParameters weatherParametersBean = new WeatherForcastParameters();

		String[] lineSplit = weatherInfo.split(Constants.COMMA, -1);

		Date formattedDate = getFormattedDate(lineSplit[0]);

		weatherParametersBean.setDayOfYear(getDayOfYear(formattedDate));
		weatherParametersBean.setLocationCode(locationCode);
		weatherParametersBean.setLatitude(latitude);
		weatherParametersBean.setLongitude(longitude);
		weatherParametersBean.setLocationName(locationName);
		weatherParametersBean.setAltitude(altitude);
		weatherParametersBean.setTemperature(Double.parseDouble(lineSplit[1]));
		weatherParametersBean.setHumidity(Double.parseDouble(lineSplit[3]));
		weatherParametersBean.setPressure(Double.parseDouble(lineSplit[2]));
		weatherParametersBean.setWeatherType(lineSplit[4].trim());
		weatherParametersBean.setYear(getYear(formattedDate));

		return weatherParametersBean;
	}

	/*
	 * This method will format the input date to a specified pattern.
	 * 
	 * @param date
	 */

	public Date getFormattedDate(String date) throws ParseException {
		simpleDateFormat.applyPattern(Constants.INPUT_DATE_PATTERN);
		Date dateInstance = simpleDateFormat.parse(date);
		return dateInstance;

	}

	/*
	 * This method will extract the year part of input date
	 * 
	 * @param dateInstance
	 */
	public int getYear(Date dateInstance) {
		simpleDateFormat.applyPattern(Constants.YEAR_PATTERN);
		return Integer.parseInt(simpleDateFormat.format(dateInstance));
	}

	/*
	 * This method will extract the day part from the input date
	 * 
	 * @param dateInstance
	 */
	public int getDayOfYear(Date dateInstance) throws ParseException {
		calender.setTime(dateInstance);
		return calender.get(Calendar.DAY_OF_YEAR);
	}

}
