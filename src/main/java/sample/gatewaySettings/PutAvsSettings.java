package sample.gatewaySettings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sample.domain.settings.AvsFilters;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class PutAvsSettings {
    private static final String API_URI = "https://cladevgsops02.clearent.net:8490/rest/v2/settings/terminal/avs";
    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String ACCEPT_HEADER_KEY = "Accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String POST_METHOD = "PUT";
    private static final boolean OUTPUT_TRUE = true;

    public static void main(String[] args) throws Exception {
        String response;
        System.out.println("Beginning updating terminal");
        response = updateTerminal();
        System.out.println("Response: " + response);

    }

    private static String updateTerminal() throws Exception {
        AvsFilters filters = new AvsFilters();
        Gson gson = new GsonBuilder().create();
        String jsonCustomer = gson.toJson(filters);
        System.out.println("Request: " + jsonCustomer);
        return requestTransaction(jsonCustomer);
    }
    private static String requestTransaction(String requestBody)
            throws IOException {

        HttpURLConnection httpConnection = null;
        try {
            httpConnection = setupHttpConnection(API_URI);
            setRequestParameters(httpConnection, API_URI);
            sendRequest(requestBody, httpConnection.getOutputStream());
            return getResponse(httpConnection.getInputStream());
        } catch (IOException e) {
            return getResponse(httpConnection.getErrorStream());
        } catch (Exception e) {
            return getResponse(httpConnection.getErrorStream());
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }

    private static HttpURLConnection setupHttpConnection(final String apiEndpoint) {
        final URL url = createUrl(apiEndpoint);
        return openHttpConnection(url);
    }

    private static HttpURLConnection openHttpConnection(final URL url) {
        try {
            final HttpURLConnection httpConnection = (HttpURLConnection) url
                    .openConnection();
            httpConnection.setDoOutput(OUTPUT_TRUE);
            return httpConnection;
        } catch (IOException ioe) {
            throw new IllegalStateException(
                    "Unable to open connection with endpoint", ioe);
        }
    }

    private static URL createUrl(final String apiEndpoint) {
        try {
            return new URL(apiEndpoint);
        } catch (MalformedURLException mue) {
            throw new IllegalStateException("Improperly formed URL", mue);
        }
    }

    private static String getResponse(final InputStream inputStream) {
        final StringBuilder response = new StringBuilder();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String output;
            while ((output = bufferedReader.readLine()) != null) {
                response.append(output);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to read from endpoint", ioe);
        }
        return response.toString();
    }

    private static void sendRequest(final String requestBody,
                                    final OutputStream outputStream) {
        try {
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to write to endpoint", ioe);
        }
    }

    private static void setRequestParameters(
            final HttpURLConnection httpConnection, String apiKey)
            throws ProtocolException {
        httpConnection.setRequestMethod(POST_METHOD);
        httpConnection.setRequestProperty(CONTENT_TYPE_KEY, APPLICATION_JSON);
        httpConnection.setRequestProperty(ACCEPT_HEADER_KEY, APPLICATION_JSON);
        httpConnection.setRequestProperty("api-key", API_KEY);
    }




}