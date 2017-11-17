package com.tcs.cba.weather.simulator.weathersimulator;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.tcs.cba.weathersimulator.bean.WeatherForcastParameters;
import com.tcs.cba.weathersimulator.model.Location;
import com.tcs.cba.weathersimulator.model.WeatherData;
import com.tcs.cba.weathersimulator.process.InputDataOperations;

import junit.framework.TestCase;

public class TestInputDataOperations extends TestCase {

	Location location = new Location();
	String rootDir="D:/anas/eclipse_workspace/eclipse_nov14/weathersimulator";
	Map<Integer, List<WeatherForcastParameters>> historyWeatherParametersMapSample = new TreeMap<Integer, List<WeatherForcastParameters>>();

	@Test
	public void testGetHistoryWeatherData() throws IOException, ParseException {
		initilizeLocationValues();
		InputDataOperations inputDataOperations = new InputDataOperations();
		Map<Integer, List<WeatherForcastParameters>> historyWeatherParametersMap = inputDataOperations
				.getHistoryWeatherData(location,rootDir);
		List<WeatherForcastParameters> weatherParametersList = historyWeatherParametersMap.get(2);
		for (WeatherForcastParameters weatherParameters : weatherParametersList) {
			assertEquals(Double.parseDouble("27"), weatherParameters.getTemperature());
			assertEquals(Double.parseDouble("41"), weatherParameters.getPressure());
			assertEquals(Double.parseDouble("1012"), weatherParameters.getHumidity());
			assertEquals("Fog", weatherParameters.getWeatherType());
			assertEquals(2015, weatherParameters.getYear());
			assertEquals(2, weatherParameters.getDayOfYear());
		}

	}

	private void initilizeLocationValues() {
		location.setLocationCode("MEL");
		location.setLocationName("Melbourne");
		location.setAltitude(BigInteger.valueOf(7));
		location.setLatlong("-37.83,144.98");
		WeatherData weatherDataLoc = new WeatherData();
		weatherDataLoc.setLoc(
				"/src/test/resources/sampleInput");
		location.setWeatherData(weatherDataLoc);

	}

}
