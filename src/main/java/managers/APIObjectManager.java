package managers;

import apiObjects.*;

public class APIObjectManager {

    private WeatherBit weatherBit;
    private DailyForecast dailyForecast;

    public WeatherBit getWeatherBit() {
        return (weatherBit == null) ? weatherBit = new WeatherBit() : weatherBit;
    }

    public DailyForecast getDailyForecast() {
        return (dailyForecast == null) ? dailyForecast = new DailyForecast() : dailyForecast;
    }
}
