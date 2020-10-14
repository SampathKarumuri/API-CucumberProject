package stepDefinitions;

import apiObjects.DailyForecast;
import apiObjects.WeatherBit;
import context.TestContext;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.Assert;
import utils.CommonFunctions;
import java.time.LocalDate;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;


public class weatherForecast_steps {

    private LocalDate nextDay;

    private WeatherBit weatherBit;
    private TestContext testContext;
    private List<DailyForecast> forecastDailyList;
    private LocalDate holiday;

    private Response response;

    public weatherForecast_steps(TestContext context) {
        testContext = context;
        weatherBit = testContext.getApiObjectManager().getWeatherBit();
    }

    @Given("^I like to holiday in \"(.*?)\"$")
    public void i_like_to_holiday_in(String city) {
        weatherBit.addCityAsParameter(city);
    }

    @Given("^I only like to holiday on \"([^\"]*)\"$")
    public void i_only_like_to_holiday_on(String day) {
        nextDay = CommonFunctions.calculateNextDay(day);
        Assert.assertNotNull(nextDay,"In correct format for day is given as parameter, Ex: Monday/Tuesday");
    }

    @When("^I look up the the weather forecast for the next (\\d+)$")
    public void i_look_up_the_the_weather_forecast_for_the_next(int noOfDays) throws Throwable {
        weatherBit.addNoOfDaysAsParameter(noOfDays);

        weatherBit.weatherForecastByCity();

        response =weatherBit.getForecast();

        assertThat("Verify response status code",response.getStatusCode(), Matchers.is(HttpStatus.SC_OK));

        assertThat("The response is valid JSON",response.headers().get("content-type").toString(), Matchers.is("Content-Type=application/json; charset=utf-8"));

        //assertThat("Verify destination in response - "+city, response.jsonPath().get("city_name").toString(),Matchers.is(city));

    }

    @When("^Check if it has rained previous days$")
    public void check_if_it_has_rained_previous_days() throws Throwable {

        String weatherDescription;
        boolean flag = false;

        forecastDailyList = weatherBit.getDailyForecastDetails(response);

        for (DailyForecast forecast: forecastDailyList) {

            weatherDescription = forecast.getWeatherDetails().get("description").toLowerCase();

            if (weatherDescription.contains("snow") || weatherDescription.contains("rain")){

                flag = true;
            }

            if (forecast.getDatetime().equals(nextDay.toString()) && flag == true) {

                flag = false;
                holiday = nextDay; // to have last thursday value in case of no thursday without rain/snow
                nextDay = nextDay.plusDays(7);

            }
            else if (forecast.getDatetime().equals(nextDay.toString()) && flag == false){

                assertThat("Until " + nextDay.toString() +" Thursday for a week no rain or snow is observed",true);
                System.out.println("Until " + nextDay.toString() +" Thursday for a week no rain or snow is observed");
                holiday = nextDay;
                return;

            }

        }
        System.out.println("No Thursday is identified where no rain or snow is observed for a week");

        Assert.fail("No Thursday is identified for next 14 days, where no rain or snow is observed earlier for a week");
    }

    @Then("^I can see the temperature is between (\\d+) to (\\d+) degrees degrees in Bondi Beach$")
    public void i_can_see_the_temperature_is_between_to_degrees_degrees_in_Bondi_Beach(int arg1, int arg2) throws Throwable {

        Float low_temp;
        Float max_temp;

        System.out.println("Identified holiday is "+holiday.toString());

        for (DailyForecast forecast: forecastDailyList) {
            if (forecast.getDatetime().equals(holiday.toString())) {
                low_temp = forecast.getlow_temp();
                max_temp = forecast.getmax_temp();

                System.out.println("For Thursday "+holiday.toString()+ ", temperature range is "+low_temp+" and "+max_temp);

                if (low_temp < arg1 || max_temp > arg2)
                    Assert.fail("Thursday "+holiday.toString()+ ", temperature is not in between 20 and 30 degrees and suitable for holiday");

            }
        }
    }

    @Then("^I can see that it won't be snowing for the next (\\d+)$")
    public void i_can_see_that_it_won_t_be_snowing_for_the_next(int arg1) throws Throwable {

        String weatherDescription;

        for (DailyForecast forecast: forecastDailyList) {

            weatherDescription = forecast.getWeatherDetails().get("description").toLowerCase();

            if (weatherDescription.contains("snow")){
                Assert.fail("It is going to snow in next 14 days, and the date is "+forecast.getDatetime());

            }
        }
    }
}
