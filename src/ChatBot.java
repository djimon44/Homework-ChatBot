import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ChatBot {

    private static final String WEATHER_API_KEY = "96edc6151dac2b2d3c07a543fdca1ce9";
    private static final String EXCHANGE_API_KEY = "9addf35667ec4f63ff65d4e4";

    public static void main(String[] args) {
        ChatBot bot = new ChatBot();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the city you are interested in: ");
        String city = scanner.nextLine();
        System.out.println(bot.getWeather(city));

        System.out.print("Enter the currency you want to convert from: ");
        String fromCurrency = scanner.nextLine();
        System.out.print("Enter the currency you want to convert to: ");
        String toCurrency = scanner.nextLine();
        System.out.println(bot.getExchangeRate(fromCurrency, toCurrency));

        scanner.close();
    }

    public String getWeather(String city) {
        try {
            String urlString = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                    city, WEATHER_API_KEY);
            String response = sendHttpRequest(urlString);
            JSONObject json = new JSONObject(response);
            String weatherDescription = json.getJSONArray("weather").getJSONObject(0).getString("description");
            double temperature = json.getJSONObject("main").getDouble("temp");
            return String.format("The weather in %s is currently %s with a temperature of %.2f°C", city, weatherDescription, temperature);
        } catch (Exception e) {
            e.printStackTrace();
            return "Could not retrieve weather data.";
        }
    }

    public String getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            String urlString = String.format(
                    "https://v6.exchangerate-api.com/v6/%s/latest/%s",
                    EXCHANGE_API_KEY, fromCurrency);
            String response = sendHttpRequest(urlString);
            JSONObject json = new JSONObject(response);
            double rate = json.getJSONObject("conversion_rates").getDouble(toCurrency);
            return String.format("The exchange rate from %s to %s is %.4f", fromCurrency, toCurrency, rate);
        } catch (Exception e) {
            e.printStackTrace();
            return "Could not retrieve exchange rate data.";
        }
    }

    private String sendHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return content.toString();
        } else {
            throw new RuntimeException("HTTP request failed with response code " + responseCode);
        }
    }
}
