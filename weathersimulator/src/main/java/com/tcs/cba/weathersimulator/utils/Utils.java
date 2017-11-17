/**
 * The Utils Class has generic or commonly used functions.
 *  
 * @author Anas E A
 * @version 0.0.1
 * @since November 17, 2017
 * Copyright (c) 2017,Anas E A. All rights reserved.
 */
package com.tcs.cba.weathersimulator.utils;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.ArrayUtils;

import com.tcs.cba.weathersimulator.model.Location;
import com.tcs.cba.weathersimulator.model.Locations;

public class Utils {

	
	/*
	 * This method will fetch the location details from input file.
	 * @param locationPath
	 */	
	@SuppressWarnings("restriction")
	public static List<Location> loadLocationDetails(String locationPath) {
		File configFile = null;
		Locations locDetailsList = null;

		try {
			configFile = new File(locationPath);

			final JAXBContext jaxbContext = JAXBContext.newInstance(Locations.class);
			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			locDetailsList = (Locations) jaxbUnmarshaller.unmarshal(configFile);

		} catch (JAXBException exception) {
			exception.printStackTrace();
		}
		return (locDetailsList == null) ? null : locDetailsList.getLocation();
	}

	

    /**
     * This method helps rounding the double value to precision 1 (1 decimal space).
     * @param forecastedValue
     */
    public static double roundedValue(double forecastedValue) {
        double roundValue = (double) Math.round(forecastedValue * 10) / 10;
        return roundValue; 
    }

    /**
     * This method helps identifying the window edges whenever the offset moves towards end of list.
     * @param index
     * @param sizeOfList
     */
    public static int caltculateIndexOnWindowEdges(int index, int sizeOfList) {
        if(index >= sizeOfList) {
            index = index - sizeOfList;
        }
        return index;
    }
    

    /**
     *  This method helps identifying the window edges whenever the offset moves towards end of list.
     * @param windowEdge
     * @param offset
     * @param sizeOfList
     */
    public static int calculateIndexOfVariance(int windowEdge, int offset, int sizeOfList) {
        if(windowEdge - offset >=0) {
            return windowEdge - offset;
        } else {
            return sizeOfList + (windowEdge - offset);
        }
    }
    
    
    /**
     * Calculates and returns the mean value of a list of double values.
     * @param parametersList
     */
    public static double calculateAverage(List<Double> parametersList) {
        double sum = 0;
        for(Double factor: parametersList) {
            sum += factor;
        }
        return sum/parametersList.size();
    }

}
