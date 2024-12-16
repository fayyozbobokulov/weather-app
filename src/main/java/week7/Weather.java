package week7;

public class Weather {
    private String city;
    private double temperature;
    private String description;
    private String unit;

    public Weather(String city, double temperature, String description, String unit) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.unit = unit;
    }

    public String getCity() {
        return city;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public String getTemperatureWithUnit() {
        String symbol = switch (unit) {
            case "metric" -> "°C";
            case "imperial" -> "°F";
            default -> "K";
        };
        return String.format("%.1f%s", temperature, symbol);
    }

    @Override
    public String toString() {
        return String.format("City: %s%nTemperature: %s%nDescription: %s%n",
                city, getTemperatureWithUnit(), description);
    }
}
