package com.tcs.cba.weather.simulator.weathersimulator;

import static junit.framework.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.tcs.cba.weathersimulator.bean.WeatherForcastParameters;
import com.tcs.cba.weathersimulator.process.PredictDataOperations;

public class TestPredictDataOperations {

	public List<WeatherForcastParameters> processDayHistoryList = new ArrayList<WeatherForcastParameters>();
	String resultWeatherParameters = null;

	@Before
	public void intializeValues() throws Exception {
		processDayHistoryList = new ArrayList<WeatherForcastParameters>();
		resultWeatherParameters = "SYD|-33.86,151.21,39|2015-01-12T12:00:00Z|RAINY|23.0|1082.0|80";
		initializeListOfHistory();

	}

	@Test
	public void testCalculateWeatherForcastParameters() throws ParseException {
		PredictDataOperations predictDataOperations = new PredictDataOperations();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern("yyyy-M-d");
		Date processingDate = simpleDateFormat.parse("2015-1-12");
		String predictedWeatherParameters = predictDataOperations.calculateWeatherParameters(processDayHistoryList,
				processingDate);
		assertEquals(resultWeatherParameters, predictedWeatherParameters);
	}

	/**
	 * Sample values for history data parameters.
	 */
	private void initializeListOfHistory() {

		WeatherForcastParameters weatherParameters1 = new WeatherForcastParameters();

		weatherParameters1.setLatitude(Double.parseDouble("-33.86"));
		weatherParameters1.setLongitude(Double.parseDouble("151.21"));
		weatherParameters1.setAltitude("39");
		weatherParameters1.setDayOfYear(12);
		weatherParameters1.setLocationCode("SYD");
		weatherParameters1.setHumidity(73d);
		weatherParameters1.setPressure(1120d);
		weatherParameters1.setTemperature(26d);
		weatherParameters1.setWeatherType("CLOUDY");
		weatherParameters1.setYear(2010);

		WeatherForcastParameters weatherParameters2 = new WeatherForcastParameters();

		weatherParameters2.setLatitude(Double.parseDouble("-33.86"));
		weatherParameters2.setLongitude(Double.parseDouble("151.21"));
		weatherParameters2.setAltitude("39");
		weatherParameters2.setDayOfYear(12);
		weatherParameters2.setLocationCode("SYD");
		weatherParameters2.setHumidity(64d);
		weatherParameters2.setPressure(1012d);
		weatherParameters2.setTemperature(24d);
		weatherParameters2.setWeatherType("WARM");
		weatherParameters2.setYear(2013);

		WeatherForcastParameters weatherParameters3 = new WeatherForcastParameters();

		weatherParameters3.setLatitude(Double.parseDouble("-33.86"));
		weatherParameters3.setLongitude(Double.parseDouble("151.21"));
		weatherParameters3.setAltitude("39");
		weatherParameters3.setDayOfYear(12);
		weatherParameters3.setLocationCode("SYD");
		weatherParameters3.setHumidity(80d);
		weatherParameters3.setPressure(1082d);
		weatherParameters3.setTemperature(23d);
		weatherParameters3.setWeatherType("RAINY");
		weatherParameters3.setYear(2015);

		processDayHistoryList.add(weatherParameters1);
		processDayHistoryList.add(weatherParameters2);
		processDayHistoryList.add(weatherParameters3);

	}

	/*@Test
	public void testMovingAverageCalculation() {
		PredictDataOperations predictDataOperations = new PredictDataOperations();
		List<Double> tempList = new ArrayList<Double>();
		for (WeatherForcastParameters factorList : processDayHistoryList) {
			tempList.add(factorList.getTemperature());
		}
		assertEquals(23.5, predictDataOperations.calculateMovingAverage(2, 1, tempList));
	}*/

	@Test
	public void testRateOfChangeCalculation() {
		PredictDataOperations predictDataOperations = new PredictDataOperations();
		assertEquals(-0.08370044052863457, predictDataOperations.calculateRateOfChange(24.6, 22.7));
	}

}
