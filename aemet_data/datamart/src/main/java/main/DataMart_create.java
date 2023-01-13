package main;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DataMart_create {
    private static String dbname = "datamart.db";
    private static String savefolder = "datamarts";
    private static String folder = "datalakes";
    private static String suffix = ".events";


    public DataMart_create(){

    }

    public void run(String tablename){
        try(Connection connection = connect(savefolder + "/" + dbname)){
            Statement statement = connection.createStatement();
            File f = new File(folder);
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(suffix);
                }
            };
            createTable(statement, tablename);
            String[] pathnames;
            pathnames = f.list(filter);

            for (String pathname : pathnames){
                StringBuilder sb = new StringBuilder();
                File file = new File(folder + "/" + pathname);
                FileInputStream in = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null){
                    sb.append(line + System.lineSeparator());
                }
                Gson gson = new Gson();
                JsonArray response = gson.fromJson(sb.toString(), JsonArray.class);

                Dato temp = new Dato();
                if (tablename == "min_temp"){
                    temp.temp = 100;
                    for (JsonElement el : response){
                        String obj = "{" + el.getAsString().substring(1, el.getAsString().length()-1) + "}";
                        Dato dato = gson.fromJson(obj, Dato.class);
                        if (temp.temp > dato.tempmin){
                            temp.timestamp = dato.timestamp;
                            temp.station = dato.station;
                            temp.code = dato.code;
                            temp.temp = dato.tempmin;
                        }
                    }
                } else {
                    temp.temp = -100;
                    for (JsonElement el : response){
                        String obj = "{" + el.getAsString().substring(1, el.getAsString().length()-1) + "}";
                        Dato dato = gson.fromJson(obj, Dato.class);
                        if (temp.temp < dato.tempmax){
                            temp.timestamp = dato.timestamp;
                            temp.station = dato.station;
                            temp.code = dato.code;
                            temp.temp = dato.tempmax;
                        }
                    }
                }
                String dates = temp.timestamp;
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime dateTime = LocalDateTime.parse(dates, formatter);
                LocalDate date = dateTime.toLocalDate();
                LocalTime time = dateTime.toLocalTime();
                java.sql.ResultSet result = statement.executeQuery("SELECT * FROM " + tablename
                        + " WHERE DATE = '" + date.toString() + "' AND TIME = '" + time.toString() + "'");
                if (!result.next()){
                    insert(statement, temp, date, time, tablename, connection);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void insert(Statement statement, Dato temp, LocalDate date, LocalTime time, String tablename, Connection connection) {
        String sql = "INSERT INTO " + tablename + " (Time, Date, Place, Station, Temperature) VALUES (?,?,?,?,?);";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, String.valueOf(time));
            ps.setString(2, String.valueOf(date));
            ps.setString(3, temp.station);
            ps.setString(4, temp.code);
            ps.setDouble(5, temp.temp);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTable(Statement statement, String tablename) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS " + tablename + " (" +
                "Time TEXT NOT NULL, \n" +
                "Date TEXT NOT NULL, \n" +
                "Place TEXT NOT NULL, \n" +
                "Station TEST NOT NULL, \n" +
                "Temperature REAL DEFAULT 0" +
                ");");
    }

    private Connection connect(String dbPath) {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + dbPath;
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLITE has been established");
        } catch (SQLException e) {
            System.out.println("Connection to SQLITE has not been established: " + e.getMessage());
        }
        return conn;
    }
}
