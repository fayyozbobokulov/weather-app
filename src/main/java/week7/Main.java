package week7;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Get city names
        System.out.println("Enter city names (separated by commas):");
        String cityInput = scanner.nextLine().trim();
        List<String> cities = Arrays.stream(cityInput.split(","))
                                  .map(String::trim)
                                  .filter(s -> !s.isEmpty())
                                  .toList();

        if (cities.isEmpty()) {
            System.out.println("Error: No valid city names entered.");
            return;
        }

        // Get unit preference
        System.out.println("Choose units (metric for Celsius, imperial for Fahrenheit, or standard for Kelvin):");
        String unit = scanner.nextLine().trim().toLowerCase();
        
        if (!unit.equals("metric") && !unit.equals("imperial") && !unit.equals("standard")) {
            System.out.println("Error: Invalid unit. Please use 'metric', 'imperial', or 'standard'.");
            return;
        }

        try {
            WeatherService weatherService = new WeatherService();
            List<Weather> weatherList = weatherService.getWeatherForCities(cities, unit);

            if (!weatherList.isEmpty()) {
                // Print weather data to console
                System.out.println("\nWeather Data:");
                weatherList.forEach(System.out::println);

                // Find cities with highest and lowest temperatures
                Weather highestTemp = weatherService.findCityWithHighestTemp(weatherList);
                Weather lowestTemp = weatherService.findCityWithLowestTemp(weatherList);

                System.out.println("\nAnalysis:");
                System.out.printf("- City with the highest temperature: %s (%s)%n",
                        highestTemp.getCity(), highestTemp.getTemperatureWithUnit());
                System.out.printf("- City with the lowest temperature: %s (%s)%n",
                        lowestTemp.getCity(), lowestTemp.getTemperatureWithUnit());

                // Save to file
                String fileName = "weather_report.txt";
                try (FileWriter writer = new FileWriter(fileName)) {
                    writer.write("Weather Report\n\n");
                    for (Weather weather : weatherList) {
                        writer.write(weather.toString());
                        writer.write("\n");
                    }
                    writer.write("\nAnalysis:\n");
                    writer.write(String.format("- City with the highest temperature: %s (%s)%n",
                            highestTemp.getCity(), highestTemp.getTemperatureWithUnit()));
                    writer.write(String.format("- City with the lowest temperature: %s (%s)%n",
                            lowestTemp.getCity(), lowestTemp.getTemperatureWithUnit()));
                    
                    System.out.printf("%nWeather report has been saved to %s%n", fileName);
                }
            } else {
                System.out.println("No weather data was retrieved. Please check your city names and try again.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Caused by: " + e.getCause().getMessage());
            }
        } finally {
            scanner.close();
        }
    }
}