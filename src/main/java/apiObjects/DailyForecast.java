package apiObjects;

import java.util.Map;

public class DailyForecast {

    private String datetime;
    private Float low_temp;
    private Float max_temp;
    private Map<String, String> weather;

    public String getDatetime() {
        return datetime;
    }

    public Float getlow_temp() {
        return low_temp;
    }

    public Float getmax_temp() {
        return max_temp;
    }

    public Map<String, String> getWeatherDetails() {
        return weather;
    }

}
