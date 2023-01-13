package main;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class DataCrawler {
    private String apiKey;
    public DataCrawler(String api){
        this.apiKey = api;
    }

    public void crawl(){

        Gson gson = new Gson();


        String first_response;
        String second_response;
        JsonArray response = null;
        Folder folder = new Folder();
        folder.savefolder = "datalakes";
        try {
            File file = new File(folder.savefolder + "/" + LocalDate.now() + folder.suffix);
            String absolutepath = file.getAbsolutePath();
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                FileInputStream in
                        = new FileInputStream(absolutepath);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + System.lineSeparator());
                }
                response = gson.fromJson(sb.toString(), JsonArray.class);

            }else{
                response = new JsonArray();
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
        try {
            first_response = Jsoup.connect(folder.url)
                    .validateTLSCertificates(false)
                    .timeout(6000)
                    .ignoreContentType(true)
                    .header("accept", "application/json")
                    .header("api_key", apiKey)
                    .method(Connection.Method.GET)
                    .maxBodySize(0).execute().body();

            JsonElement jsonElement = gson.fromJson(first_response, JsonElement.class);
            String url_data = jsonElement.getAsJsonObject().get("datos").getAsString();
            second_response = Jsoup.connect(url_data)
                    .validateTLSCertificates(false)
                    .timeout(6000)
                    .ignoreContentType(true)
                    .header("accept", "application/json")
                    .header("api_key", apiKey)
                    .method(Connection.Method.GET)
                    .maxBodySize(0).execute().body();
            JsonArray response_unprocessed = gson.fromJson(second_response, JsonArray.class);

            try {
                Path path = Paths.get(folder.savefolder);
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for(JsonElement ele : response_unprocessed){
                JsonObject obj = ele.getAsJsonObject();
                Dato dato = new Dato();
                Localizacion localizacion = new Localizacion();
                localizacion.longitudMin = -16;
                localizacion.longitudMax = -15;
                localizacion.latitudMin = 27.5;
                localizacion.latitudMax = 28.4;
                dato.timestamp = obj.get("fint").getAsString();
                float lon = obj.get("lon").getAsFloat();
                float lat = obj.get("lat").getAsFloat();
                if (lon > localizacion.longitudMin && lon < localizacion.longitudMax
                        && lat > localizacion.latitudMin && lat < localizacion.latitudMax) {
                    dato.place = obj.get("lon").getAsString()
                            .concat(" ")
                            .concat(obj.get("lat").getAsString());
                    if (obj.has("ta"))
                        dato.temp = obj.get("ta").getAsFloat();
                    if (obj.has("tamax"))
                        dato.tempmax = obj.get("tamax").getAsFloat();
                    if (obj.has("tamin"))
                        dato.tempmin = obj.get("tamin").getAsFloat();
                    if (obj.has("ubi"))
                        dato.station = obj.get("ubi").getAsString();
                    if (obj.has("idema"))
                        dato.code = obj.get("idema").getAsString();
                    String element = gson.toJson(dato);
                    response.add(element);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            File file = new File(folder.savefolder + "/" + LocalDate.now() + folder.suffix);
            file.createNewFile();
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(gson.toJson(response));
            myWriter.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

}