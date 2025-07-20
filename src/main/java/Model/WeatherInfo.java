package Model;

public class WeatherInfo {
    private String location;
    private double temperature;
    private String condition;

    public WeatherInfo(String location, double temperature, String condition) {
        this.location = location;
        this.temperature = temperature;
        this.condition = condition;
    }

    public String getLocation() {
        return location;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }
}