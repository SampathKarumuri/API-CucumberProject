package stepDefinitions;

import apiObjects.DailyForecast;
import apiObjects.WeatherBit;
import context.TestContext;
import utils.CommonFunctions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.Assert;
import java.time.LocalDate;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;

public class weatherForecast_steps {

    private LocalDate nextDate;
    private Response response;

    private WeatherBit weatherBit;
    private TestContext testContext;
    private List<DailyForecast> dailyForecastList;

    public weatherForecast_steps(TestContext context) {
        testContext = context;
        weatherBit = testContext.getApiObjectManager().getWeatherBit();
    }

    @Given("^I like to holiday in \"(.*?)\"$")
    public void i_like_to_holiday_in(String city) {
        weatherBit.addCityAsParameter(city);
    }

    @Given("^I only like to holiday on \"([^\"]*)\"$")
    public void i_only_like_to_holiday_on(String dayOfWeek) {
        nextDate = CommonFunctions.calculateNextDate(dayOfWeek);

        Assert.assertNotNull(nextDate,"In correct format for day of week is given as data, Ex: Monday/Tuesday");
    }

    @When("^I look up the the weather forecast for the next (\\d+)$")
    public void i_look_up_the_the_weather_forecast_for_the_next(int noOfDays) {
        weatherBit.addNoOfDaysAsParameter(noOfDays);

        weatherBit.createDailyForecastAPIRequest();

        response =weatherBit.InvokeDailyForecastAPIRequest();

        assertThat("Verify response status code",response.getStatusCode(), Matchers.is(HttpStatus.SC_OK));

        assertThat("The response is valid JSON",response.headers().get("content-type").toString(), Matchers.is("Content-Type=application/json; charset=utf-8"));

        weatherBit.verifyCityInResponse(response);
    }

    @When("^Check if it has rained previous days$")
    public void check_if_it_has_rained_previous_days() {

        dailyForecastList = weatherBit.getDailyForecastDetails(response);

        weatherBit.checkIfItHasRainedPreviousDays(dailyForecastList,nextDate);
    }

    @Then("^I can see the temperature is between (\\d+) to (\\d+) degrees degrees in Bondi Beach$")
    public void i_can_see_the_temperature_is_between_to_degrees_degrees_in_Bondi_Beach(int minTemp, int maxTemp) {

        weatherBit.verifyTemperatureRange(dailyForecastList, minTemp, maxTemp);

    }

    @Then("^I can see that it won't be snowing for the next (\\d+)$")
    public void i_can_see_that_it_won_t_be_snowing_for_the_next(int noOfDays) {

        weatherBit.verifySnowConditions(dailyForecastList, noOfDays);
    }
}
