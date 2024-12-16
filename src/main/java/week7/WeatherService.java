package week7;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WeatherService {
    private final String apiKey;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public WeatherService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("OPENWEATHER_API_KEY");
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<Weather> getWeatherForCities(List<String> cities, String unit) throws IOException {
        List<Weather> weatherList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (String city : cities) {
            try {
                Weather weather = getWeatherForCity(city.trim(), unit);
                weatherList.add(weather);
            } catch (IOException e) {
                errors.add(String.format("Error fetching weather for %s: %s", city, e.getMessage()));
            }
        }

        if (!errors.isEmpty()) {
            System.err.println("Encountered the following errors:");
            errors.forEach(System.err::println);
        }

        return weatherList;
    }

    private Weather getWeatherForCity(String city, String unit) throws IOException {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=%s",
                city, apiKey, unit);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    throw new IOException("City not found");
                }
                throw new IOException("API call failed with code: " + response.code());
            }

            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);

            double temperature = root.path("main").path("temp").asDouble();
            String description = root.path("weather").get(0).path("description").asText();

            return new Weather(city, temperature, description, unit);
        }
    }

    public Weather findCityWithHighestTemp(List<Weather> weatherList) {
        return weatherList.stream()
                .max((w1, w2) -> Double.compare(w1.getTemperature(), w2.getTemperature()))
                .orElse(null);
    }

    public Weather findCityWithLowestTemp(List<Weather> weatherList) {
        return weatherList.stream()
                .min((w1, w2) -> Double.compare(w1.getTemperature(), w2.getTemperature()))
                .orElse(null);
    }
}
