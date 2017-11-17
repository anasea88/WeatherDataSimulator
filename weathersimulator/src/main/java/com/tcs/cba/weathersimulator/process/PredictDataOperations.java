/**
 * The PredictDataOperations class contains weather forecasting operations and calculations.
 *  
 * @author Anas E A
 * @version 0.0.1
 * @since November 17, 2017
 * Copyright (c) 2017,Anas E A. All rights reserved.
 */

package com.tcs.cba.weathersimulator.process;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.tcs.cba.weathersimulator.bean.WeatherForcastParameters;
import com.tcs.cba.weathersimulator.utils.Constants;
import com.tcs.cba.weathersimulator.utils.Utils;

public class PredictDataOperations {
	private SimpleDateFormat simpleDateFormat;
	private Calendar calender;
	Map<Integer, List<WeatherForcastParameters>> historyWeatherParametersMap = new TreeMap<Integer, List<WeatherForcastParameters>>();

	/*
	 * This method will invoke the weather prediction calculations for each dates in
	 * the given date range
	 * 
	 * @params historyWeatherParametersMap
	 * 
	 * @params start date
	 * 
	 * @params end date
	 * 
	 * @throws ParseException
	 * 
	 * @throws InterruptedException
	 * 
	 * @throws IOException
	 */
	public void generateWeatherForcastReport(Map<Integer, List<WeatherForcastParameters>> historyWeatherParametersMap,
			String startDate, String endDate) throws ParseException, InterruptedException, IOException {

		simpleDateFormat = new SimpleDateFormat();
		calender = new GregorianCalendar();
		simpleDateFormat.applyPattern("yyyy-M-d");
		Date beginDate = simpleDateFormat.parse(startDate);
		Date stopDate = simpleDateFormat.parse(endDate);
		Date processingDate = beginDate;
		Calendar c = Calendar.getInstance();
		c.setTime(beginDate);
		this.historyWeatherParametersMap = historyWeatherParametersMap;

		while (true) {
			int theDay = getDayOfYear(processingDate);
			int year = getYear(processingDate);
			forcastWeatherForOneDay(theDay, year, processingDate);

			c.add(Calendar.DATE, 1);
			processingDate = c.getTime();
			if (stopDate.before(processingDate)) {
				break;
			}
		}

	}

	/*
	 * This method performs the mathematical calculations for weather predictions.
	 * This involves 3 stages of calculations- 1.Moving average(MA)
	 * 2.Oscillation(OSC) 3.Rate of Change(ROC)
	 * 
	 * @params processDayHistoryList
	 * 
	 * @params processingDate
	 */
	public String calculateWeatherParameters(List<WeatherForcastParameters> processDayHistoryList,
			Date processingDate) {

		WeatherForcastParameters weatherForcastParameters = null;
		simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern("yyyy-MM-dd'T'hh:mm:ss'Z'");
		Random r = new Random();
		int sizeOfHistoryList = processDayHistoryList.size();
		List<Double> temperatureHistoryList = new ArrayList<Double>(sizeOfHistoryList);
		List<Double> pressureHistoryList = new ArrayList<Double>(sizeOfHistoryList);
		List<Double> humidityHistoryList = new ArrayList<Double>(sizeOfHistoryList);

		String locationCode = Constants.EMPTY_STRING;
		String latitude = Constants.EMPTY_STRING;
		String longitude = Constants.EMPTY_STRING;
		String altitude = Constants.EMPTY_STRING;

		// lower window having value less than half of size of data list
		int windowperiod1 = r.nextInt((sizeOfHistoryList / 2) + 1 - sizeOfHistoryList / 3) + sizeOfHistoryList / 3;

		// upper window having value greater than half of size of data list
		int windowperiod2 = r.nextInt(sizeOfHistoryList - (sizeOfHistoryList / 2) + 1) + (sizeOfHistoryList / 2) + 1;

		// value equals or less than size of data
		int offset = r.nextInt(sizeOfHistoryList+1);

		for (int i = 0; i < sizeOfHistoryList; i++) {
			weatherForcastParameters = processDayHistoryList.get(i);
			temperatureHistoryList.add(weatherForcastParameters.getTemperature());
			pressureHistoryList.add(weatherForcastParameters.getPressure());
			humidityHistoryList.add(weatherForcastParameters.getHumidity());
			if (i == 0) {
				locationCode = weatherForcastParameters.getLocationCode();
				latitude = weatherForcastParameters.getLatitude().toString();
				longitude = weatherForcastParameters.getLongitude().toString();
				altitude = weatherForcastParameters.getAltitude();
			}
		}

		// calculating the moving average of temperature list of values
		double temperatureMovingAverageValue1 = calculateMovingAverage(windowperiod1, offset, temperatureHistoryList);
		double temperatureMovingAverageValue2 = calculateMovingAverage(windowperiod2, offset, temperatureHistoryList);

		// calculating the moving average of pressure list of values
		double pressureMovingAverageValue1 = calculateMovingAverage(windowperiod1, offset, pressureHistoryList);
		double pressureMovingAverageValue2 = calculateMovingAverage(windowperiod2, offset, pressureHistoryList);

		// calculating the moving average of humidity list of values
		double humidityMovingAverageValue1 = calculateMovingAverage(windowperiod1, offset, humidityHistoryList);
		double humidityMovingAverageValue2 = calculateMovingAverage(windowperiod2, offset, humidityHistoryList);

		// calculating the oscillation value of temperature
		double temperatureOscillationValue = calculateOscillationValue(temperatureMovingAverageValue1,
				temperatureMovingAverageValue2);

		// calculating the oscillation value of pressure
		double pressureOscillationValue = calculateOscillationValue(pressureMovingAverageValue1,
				pressureMovingAverageValue2);

		// calculating the oscillation value of humidity
		double humidityOscillationValue = calculateOscillationValue(humidityMovingAverageValue1,
				humidityMovingAverageValue2);

		int windowEdge = Utils.caltculateIndexOnWindowEdges(windowperiod1 + offset - 1, sizeOfHistoryList);
		// timestep minimum between 0 and 2
		int timeStep = r.nextInt(Utils.calculateIndexOfVariance(windowEdge, offset, sizeOfHistoryList) + 2) + offset;

		int resultantTimeStep = Utils.caltculateIndexOnWindowEdges(timeStep, sizeOfHistoryList);

		// calculate the rate of change of temperature
		double temperatureRateOfChange = calculateRateOfChange(temperatureHistoryList.get(windowEdge),
				temperatureHistoryList.get(resultantTimeStep));

		// calculate the rate of change of pressure
		double pressureRateOfChange = calculateRateOfChange(pressureHistoryList.get(windowEdge),
				pressureHistoryList.get(resultantTimeStep));

		// calculate the rate of change of humidity
		double humidityRateOfChange = calculateRateOfChange(humidityHistoryList.get(windowEdge),
				humidityHistoryList.get(resultantTimeStep));

		// calculate the final value of temperature
		double predictedTemperature = Utils.roundedValue(temperatureHistoryList.get(temperatureHistoryList.size() - 1)
				+ (temperatureRateOfChange * temperatureOscillationValue));

		// calculate the final value of pressure
		double predictedPressure = Utils.roundedValue(pressureHistoryList.get(pressureHistoryList.size() - 1)
				+ (pressureRateOfChange * pressureOscillationValue));

		// calculate the final value of humidity
		int predictedHumidity = (int) Utils.roundedValue(humidityHistoryList.get(humidityHistoryList.size() - 1)
				+ (humidityRateOfChange * humidityOscillationValue));

		String predictedWeatherType = getWeatherType(predictedTemperature, predictedHumidity);

		String weatherPredictOutput = locationCode + Constants.PIPE_DELIMITER + latitude + Constants.COMMA + longitude
				+ Constants.COMMA + altitude + Constants.PIPE_DELIMITER + simpleDateFormat.format(processingDate)
				+ Constants.PIPE_DELIMITER + predictedWeatherType + Constants.PIPE_DELIMITER + predictedTemperature
				+ Constants.PIPE_DELIMITER + predictedPressure + Constants.PIPE_DELIMITER + predictedHumidity;

		System.out.println(weatherPredictOutput + "\n");
		return weatherPredictOutput;
	}

	/**
	 * Method calculates the rate of change. It can be positive or negative
	 * 
	 * @param factorT
	 * @param factorTMinusA
	 * @return rate of change
	 */
	public double calculateRateOfChange(Double factorT, Double factorTMinusA) {
		double rateOfChange = 1 - (factorT / factorTMinusA);
		return rateOfChange;
	}

	/**
	 * Method calculates the oscillation based on the window selected
	 * 
	 * @param factorMovingAverage1
	 * @param factorMovingAverage2
	 * @return oscillation
	 */
	private double calculateOscillationValue(double factorMovingAverage1, double factorMovingAverage2) {
		return Math.abs(factorMovingAverage1 - factorMovingAverage2);
	}

	/**
	 * Method used to find the moving averages of the specific weather parameter
	 * based on the window selected
	 * 
	 * @param window
	 * @param offset
	 * @param weatherParametersList
	 * @return moving average
	 */
	public double calculateMovingAverage(int window, int offset, List<Double> weatherParametersList) {
		List<Double> weatherParametersSubList = new ArrayList<Double>();
		int sizeOfHistoryList = weatherParametersList.size();
		if (offset + window < sizeOfHistoryList) {
			weatherParametersSubList = weatherParametersList.subList(offset, offset + window);
		} else {
			weatherParametersSubList.addAll(weatherParametersList.subList(offset, sizeOfHistoryList));
			if(sizeOfHistoryList>Utils.caltculateIndexOnWindowEdges(offset + window, sizeOfHistoryList)) {
			weatherParametersSubList.addAll(weatherParametersList.subList(0,
					Utils.caltculateIndexOnWindowEdges(offset + window, sizeOfHistoryList)));}
		}
		return Utils.calculateAverage(weatherParametersSubList);
	}

	/**
	 * Method helps predict the weather type based on the weather factors.
	 *
	 * @param temperature
	 * @param humidity
	 * @return weatherType as String
	 */
	private String getWeatherType(double temperature, int humidity) {
		String weatherType = null;

		// conditions governing weatherType.
		if (temperature > 20 && humidity > 70 && humidity < 80) {
			weatherType = Constants.WEATHER_CLOUDY;
		} else if (temperature > 20 && humidity >= 80) {
			weatherType = Constants.WEATHER_RAINY;
		} else if (temperature >= 20 && temperature <= 24) {
			weatherType = Constants.WEATHER_WARM;
		} else if (temperature < 20 && temperature >= 14) {
			weatherType = Constants.WEATHER_COOL;
		} else if (temperature < 14) {
			weatherType = Constants.WEATHER_COLD;
		} else {
			weatherType = Constants.WEATHER_HOT;
		}

		return weatherType;

	}

	/*
	 * This method will invoke the calculation for each day of prediction
	 * 
	 * @param day
	 * 
	 * @param year
	 * 
	 * @param processingDate
	 */
	public String forcastWeatherForOneDay(int day, int year, Date processingDate) {
		List<WeatherForcastParameters> processDayHistoryList = historyWeatherParametersMap.get(day);
		if (day == 366) {
			processDayHistoryList.addAll(historyWeatherParametersMap.get(day - 1));
		}
		Collections.shuffle(processDayHistoryList);

		// process weather data
		return calculateWeatherParameters(processDayHistoryList, processingDate);

	}

	/*
	 * This method will extract the day of input year
	 * 
	 * @param dateInstance
	 */
	public int getDayOfYear(Date dateInstance) throws ParseException {
		calender.setTime(dateInstance);
		return calender.get(Calendar.DAY_OF_YEAR);
	}

	/*
	 * This method will extract the year part of input year
	 * 
	 * @param dateInstance
	 */
	public int getYear(Date dateInstance) {
		simpleDateFormat.applyPattern(Constants.YEAR_PATTERN);
		return Integer.parseInt(simpleDateFormat.format(dateInstance));
	}

}
