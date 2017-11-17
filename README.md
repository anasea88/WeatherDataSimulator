>    Weather Data Simulator program which predicts the weather conditions for different locations based on historical data.

----
## Pre-Requisites
* JDK 1.7 or higher (JAVA_HOME and PATH set) for compile and execution
* Apache Maven 3.3 or higher (MVN_HOME and PATH set) for build

----
## Project setup

Go to the project root directory weatherdatasimulator/, the directory where pom.xml for weatherdatasimulator is present.

>Execute the command to build the jar:

    mvn clean install



>Execute the command to build the jar with skip test:

    mvn clean install -Dmaven.test.skip=true

The following jar will be generated and used for execution.
**weathersimulator-0.0.1-jar-with-dependencies.jar**


* The past weather data needs to be downloaded to provide input ([link to data](https://www.wunderground.com/)) or the same is available within the project in below mentioned path 
 "weathersimulator/src/main/resources/input_data".
The data between 2005 and 2015 (both years inclusive) for 4 locations(Sydney,Melbourne,Adelaide,Brisbane) considered for this analysis.
* Update the configuration file based on the instructions below.

----
## Program Execution
**This program takes 3 arguments.**

* Start date
* End date
* Configuration root path

>Command to execute:

    java -jar weathersimulator-0.0.1-jar-with-dependencies.jar 
	<start_date> 
	<end_date>
    <configuration rootpath>

>Example:

    java -jar weathersimulator-0.0.1-jar-with-dependencies.jar 
	2017-7-21 
	2017-7-25
    D:/WeatherData-master/weathersimulator/

**Note**: The date format is in YYYY-M-d


>**Main class:**

     
	com.tcs.cba.weathersimulator.process.WeatherForcastInit



**Configuration:**

The configuration file "locationsinfo.xml" holds the static information regarding various geographic locations (weather stations) which are subject to weather forecasting. The configuration file reads as below:

PATH: 


>weathersimulator/src/main/resources/config/locationsinfo.xml

    <locations>
	<location locationName="Sydney" locationCode="SYD"  latlong="-33.86,151.21" altitude="39">
		<weatherData loc="input_data/Sydney" />
	</location></locations>

----
## Functional Description
When a query is made to forecast the weather for any future date range, the historical data available for those days in that range from previous years is fetched as reference data. This data is then shuffled to begin with the oscillation model.

[Note: For leap years, the 366th and 365th days are both considered, as historical data for 2 leap years is only available]

Oscillation model finds the moving averages from the above shuffled data within two randomly selected windows for each weather factor (temperature, pressure, humidity).


Moving Average1 (MA1) = ∑(Xn)/N1

Moving Average2 (MA2) = ∑(Xm)/N2


where N1 is the size of window 1, N2 is the size of window 2.

where n ranges from offset value 'f' to sum of offset and window 1 size (f + N1). Similarly m ranges from offset value 'f' to sum of offset and window 2 size (f + N2).

Here,

* N2 > N1
* Both window1 and window2 start from the same offset 'f'

The oscillation is then calculated using the two moving averages.
>

     
	Oscillation (OSC) = |MA1 - MA2|

where the absolute value is taken.

The rate of change is calculated by considering the first window (Window 1) of the moving average calculations. The last entry of the window 'Xe', along with another entry selected randomly 'Xa' from the initial shuffled list of weather parameters is used.


>

     
	Rate of Change(ROC) = 1-(Xe/Xa);


where Xe is the final entry in the first window selected. Xa can be any random entry from the shuffled list of weather factor. Here,

* f < a < N1

The rate of change(ROC) obtained can be positive or negative, indicating a rise or drop in the weather factors value. It can be considered as a means of identifying, whether there is a rise or fall in the weather factor over the years. The oscillation can give the amount of the change that has been observed over the years.

The weather prediction is always done based on the historical data available for edge case of the initial list. The final prediction is done by evaluating the effect of the rate of change and oscillation values on the previous recorded data for the said date.


>

     
	WeatherFactor(Wf) = We + (OSC * ROC)

Here,

*  We => Weather factor at the edge case.
* OSC => Oscillation factor
* ROC => Rate of change factor

Finally, based on the values forecasted for the weather factors, the weather type is categorized as Cloudy, Rainy, Cool, Cold, Warm and Hot.


----
## Technical Description


* WeatherForcastInit.java - This class is the starting point of the application. It loads the configuration file using JAXB and invoke the history data loading and weather prediction operations.

* InputDataOperations.java - This class involves all history data related operations.

* PredictDataOperations.java - This class does the core functional implementation. It does the processing to predict the weather factors for any date.
* Util.java - The reusable functions and logics are embedded in this class.
WeatherForcastParameters.java - This is the model (DTO) class carrying the weather information for any day of any year.

**Process Flow**

The process flow starts with the WeatherForcastInit.java.The configuration file is then loaded into the JAXB class instance. This class ensures the loading of history data and invokes the methods for mathematical operation for weather predictions.

The main logic of the application is to calculate the weather factor values for a single day only and this lies in the PredictDataOperations.java which also contains the mathematical logic of the application. 

Util.java is called in, to handle certain common functions.

The final weather factors predicted are collected and classified based on the 6 weather types mentioned earlier. Finally the result emitted by the thread of string format to the standard output media(console by default) is of format:

>

     
	MEL|-37.83,144.98,7|2017-02-13T12:00:00Z|COOL|15.7|67.0|1022



The output can be read in the following format:

>

     
	<statCode>|latitude,longitude,altitude|date_time|weatherType|temperature|humidity|pressure
  
  
  # Author / Contribution
Anas E A
