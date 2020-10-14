package datProviders;

import managers.FileReaderManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class WeatherBitConfigFileReader {

    private Properties properties;
    private final String propertyFilePath= FileReaderManager.getInstance().getConfigReader().getWeatherBitConfigPath();


    public WeatherBitConfigFileReader(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(propertyFilePath));
            properties = new Properties();
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("configurations file not found at " + propertyFilePath);
        }
    }

    public String getWeatherBitBaseURI() {
        String weatherBitBaseURI = properties.getProperty("weatherBitBaseURI");
        if(weatherBitBaseURI != null) return weatherBitBaseURI;
        else throw new RuntimeException("WeatherBit BaseURI is not available at "+propertyFilePath);
    }

    public String getWeatherBitAPIKey() {
        String weatherBitAPIKey = properties.getProperty("weatherBitAPIKey");
        if(weatherBitAPIKey != null) return weatherBitAPIKey;
        else throw new RuntimeException("WeatherBit API key is not available at "+propertyFilePath);
    }

}