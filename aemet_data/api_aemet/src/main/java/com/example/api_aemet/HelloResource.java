package com.example.api_aemet;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Path("/places")
public class HelloResource {
    private static final String dbname = "datamart.db";
    private static final String savefolder = "datamarts";

    public HelloResource() throws SQLException {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemperaturas() throws SQLException, ClassNotFoundException {
        List<String> lugares = new ArrayList<>();
        Class.forName("org.sqlite.JDBC");
        String dbPath_max = savefolder + "/" + dbname;
        String url = "jdbc:sqlite:C:/Users/jcubt/IdeaProjects/aemet_data/" + dbPath_max;
        Connection connection_max = DriverManager.getConnection(url);
        connection_max.setAutoCommit(false);
        Statement statement_1 = connection_max.createStatement();
        ResultSet temperaturas_max = statement_1.executeQuery("SELECT PLACE FROM max_temp");
        while (temperaturas_max.next()) {
            String temperatura = temperaturas_max.getString("PLACE");
            lugares.add(temperatura);
        }
        String dbPath_min = savefolder + "/" + dbname;
        String url_2 = "jdbc:sqlite:C:/Users/jcubt/IdeaProjects/aemet_data/" + dbPath_min;
        Connection connection = DriverManager.getConnection(url_2);
        connection.setAutoCommit(false);
        Statement statement_2 = connection.createStatement();
        ResultSet temperaturas_min = statement_2.executeQuery("SELECT PLACE FROM min_temp");
        while (temperaturas_min.next()){
            String temperatura_2 = temperaturas_min.getString("PLACE");
            lugares.add(temperatura_2);
        }
        return Response.ok(lugares).build();
    }

    @GET
    @Path("/with-max-temperature")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemperatura_max(
            @QueryParam("from") String from,
            @QueryParam("to") String to) throws SQLException, ClassNotFoundException {
        List<String> lugares_max = new ArrayList<>();
        Class.forName("org.sqlite.JDBC");
        String dbPath_max = savefolder + "/" + dbname;
        String url = "jdbc:sqlite:C:/Users/jcubt/IdeaProjects/aemet_data/" + dbPath_max;
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        ResultSet temperaturas_max = statement.executeQuery(
                "SELECT PLACE FROM max_temp WHERE DATE BETWEEN '" + from + "' AND '" + to + "'");
        while (temperaturas_max.next()) {
            String temperatura = temperaturas_max.getString("PLACE");
            lugares_max.add(temperatura);
        }
        if (lugares_max.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(lugares_max).build();
    }

    @GET
    @Path("/with-min-temperature")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemperatura_min(
            @QueryParam("from") String from,
            @QueryParam("to") String to) throws SQLException, ClassNotFoundException {
        List<String> lugares_min = new ArrayList<>();
        Class.forName("org.sqlite.JDBC");
        String dbPath_min = savefolder + "/" + dbname;
        String url = "jdbc:sqlite:C:/Users/jcubt/IdeaProjects/aemet_data/" + dbPath_min;
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        ResultSet temperaturas_min = statement.executeQuery(
                "SELECT PLACE FROM min_temp WHERE DATE BETWEEN '" + from + "' AND '" + to + "'");
        while (temperaturas_min.next()) {
            String temperatura = temperaturas_min.getString("PLACE");
            lugares_min.add(temperatura);
        }
        if (lugares_min.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(lugares_min).build();
    }
}