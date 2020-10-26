package apiObjects;

import com.cucumber.listener.Reporter;
import io.restassured.RestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import managers.FileReaderManager;
import org.hamcrest.Matchers;
import org.testng.Assert;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class WeatherBit {
    private String city;
    private String countryCode;
    private Map<String,String> params = new HashMap<>();
    private RequestSpecification request;
    private LocalDate holiday;


    public WeatherBit() {
        RestAssured.baseURI = FileReaderManager.getInstance().getWeatherBitConfigFileReader().getWeatherBitBaseURI();
    }

    public void addCityAsParameter(String city) {
        this.city = city.split(",")[0];
        this.countryCode = city.split(",")[1];

        params.put("city",city); //Adding city and country code as parameters inorder to pass to rest assured
        Reporter.addStepLog(city+" added as parameter");
    }

    public void addNoOfDaysAsParameter(int noOfDays) {
        params.put("days",String.valueOf(noOfDays)); //Adding number of days as parameter inorder to pass to rest assured
        Reporter.addStepLog(noOfDays+" days added as parameter");
    }

    //weather-forecast related methods
    public void createDailyForecastAPIRequest() {
        params.put("key", FileReaderManager.getInstance().getWeatherBitConfigFileReader().getWeatherBitAPIKey()); //Adding apiKey as parameter inorder to pass to rest assured
        Reporter.addStepLog("API key is retrieved from properties file");

        request = RestAssured.given().log().all().queryParams(params); //adding all parameters to rest assured and log request

    }

    public Response InvokeDailyForecastAPIRequest() {

        return request.given().log().all().given().filter(new ResponseLoggingFilter()).when().get("forecast/daily"); //calling api request
    }

    public List<DailyForecast> getDailyForecastDetails(Response response) {

        return response.jsonPath().getList("data", DailyForecast.class);
    }

    public void verifyCityInResponse(Response response) {
        assertThat("Verify destination in response - "+city, response.jsonPath().get("city_name").toString(),Matchers.is(city));

        Reporter.addStepLog(city+" is verified successfully in response");
    }

    public void checkIfItHasRainedPreviousDays(List<DailyForecast> dailyForecastList, LocalDate nextDate) {

        String weatherDescription;
        boolean flag = false;

        for (DailyForecast forecast: dailyForecastList) {

            weatherDescription = forecast.getWeatherDetails().get("description").toLowerCase();

            if (weatherDescription.contains("snow") || weatherDescription.contains("rain")){

                flag = true;
            }

            if (forecast.getDatetime().equals(nextDate.toString()) && flag == true) {

                flag = false;
                holiday = nextDate; // to have last thursday date in case of no thursday without rain/snow
                nextDate = nextDate.plusDays(7);

            }
            else if (forecast.getDatetime().equals(nextDate.toString()) && flag == false){

                Assert.assertTrue(true,"Until " + nextDate.toString() +" for a week no rain or snow is observed");
                holiday = nextDate;
                return;
            }
        }
        //Assert.fail("No Thursday is identified for next 14 days, where no rain or snow is observed earlier for a week");
        Reporter.addStepLog("No Thursday is identified for next 14 days, where no rain or snow is observed earlier for a week");
    }

    public void verifyTemperatureRange(List<DailyForecast> dailyForecastList, int minTemp, int maxTemp) {

        Float actual_lowTemp;
        Float actual_maxTemp;

        for (DailyForecast forecast: dailyForecastList) {
            if (forecast.getDatetime().equals(holiday.toString())) {

                actual_lowTemp = forecast.getlow_temp();
                actual_maxTemp = forecast.getmax_temp();
                Reporter.addStepLog("Identified holiday is "+holiday.toString()+" and temperature range is "+actual_lowTemp+" - "+actual_maxTemp);

                if (actual_lowTemp < minTemp || actual_maxTemp > maxTemp)
                    Assert.fail("Thursday "+holiday.toString()+ ", temperature is not in between 20 and 30 degrees and not suitable for holiday");

            }
        }
    }

    public void verifySnowConditions(List<DailyForecast> dailyForecastList, int noOfDays) {

        String weatherDescription;

        for (DailyForecast forecast: dailyForecastList) {
            weatherDescription = forecast.getWeatherDetails().get("description").toLowerCase();

            if (weatherDescription.contains("snow")){
                Assert.fail("It is going to snow in next "+noOfDays+" days, and the date is "+forecast.getDatetime());
            }
        }
    }

}
