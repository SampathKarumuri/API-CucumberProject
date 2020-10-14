package apiObjects;

import io.restassured.RestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import managers.FileReaderManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherBit {

    private Map<String,String> params = new HashMap<>();
    private RequestSpecification request;


    public WeatherBit() {
        RestAssured.baseURI = FileReaderManager.getInstance().getWeatherBitConfigFileReader().getWeatherBitBaseURI();
    }

    public void addCityAsParameter(String city) {
        params.put("city",city); //Adding city and country code as parameters inorder to pass to rest assured
    }

    public void addNoOfDaysAsParameter(int noOfDays) {
        params.put("days",String.valueOf(noOfDays)); //Adding number of days as parameter inorder to pass to rest assured
    }

    //weather-forecast related methods
    public void weatherForecastByCity() {
        params.put("key", FileReaderManager.getInstance().getWeatherBitConfigFileReader().getWeatherBitAPIKey()); //Adding apiKey as parameter inorder to pass to rest assured

        request = RestAssured.given().log().all().queryParams(params); //adding all parameters to rest assured and log request

    }

    public Response getForecast() {

        return request.given().log().all().given().filter(new ResponseLoggingFilter()).when().get("forecast/daily"); //calling api request
    }

    public List<DailyForecast> getDailyForecastDetails(Response response) {

        return response.jsonPath().getList("data", DailyForecast.class);
    }


}
