package tw.pago.pagobackend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.exception.BadRequestException;

@Component
public class ExchangeRateProvider {

    @Value("${currency-exchange-rate-api-url}")
    private String API_URL;
    
    @Value("${currency-exchange-rate-api-key}")
    private String API_KEY;

    private Map<String, BigDecimal> exchangeRateMap = new HashMap<>();

    public Map<String, BigDecimal> fetchExchangeRateFromApi() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            conn.setRequestMethod("GET");
// Replace with your actual API key
            conn.setRequestProperty("x-api-key", API_KEY);

            // Read the API response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                parseJsonResponse(response.toString());
            } else {
                throw new BadRequestException("HTTP error: " + responseCode);
            }

            conn.disconnect();
            return exchangeRateMap;
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        
    }

    public void parseJsonResponse(String response) throws JsonMappingException, JsonProcessingException {
        // Parse the JSON response using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.toString());

        // Extract the rate object from the JSON response
        JsonNode rateNode = jsonNode.get("rate");
        if (rateNode != null && rateNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> rateFields = rateNode.fields();
            while (rateFields.hasNext()) {
                Map.Entry<String, JsonNode> entry = rateFields.next();
                String currencyCode = entry.getKey();
                BigDecimal exchangeRate = entry.getValue().decimalValue();
                exchangeRateMap.put(currencyCode, exchangeRate);
            }
        }

        // Check for missing currencies
        for (CurrencyEnum currencyEnum : CurrencyEnum.values()) {
            String currencyCode = currencyEnum.name();
            if (!exchangeRateMap.containsKey(currencyCode)) {
                // Handle missing currency (e.g., set a default rate or log a warning)
                exchangeRateMap.put(currencyCode, BigDecimal.ONE); // Default to 1 if missing
            }
        }
    }
}
