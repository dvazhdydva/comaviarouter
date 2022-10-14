package zdeprecatedclasses;

import model.Airport;

import java.sql.*;
import java.util.Scanner;

public class SQLinsertRecordTest {
    private final static String awsUrl = "jdbc:postgresql://aviarouter-prod.cm0iszl97jcf.us-east-1.rds.amazonaws.com:5432/aviarouter";
    private static String userName = "";
    private static String userPswd = "";

    static{
        synchronized (SQLinsertRecordTest.class) {
            if (userName.equals("")) {
                while (true) {
                    try (Scanner scanner = new Scanner(System.in)) {
                        System.out.print("Enter username: ");
                        userName = scanner.next();
                        System.out.print("Enter password: ");
                        userPswd = scanner.next();
                        Connection connection = DriverManager.
                                getConnection(awsUrl, userName, userPswd);
                        connection.close();
                        System.out.println("Hurray! You're in");
                        break;
                    } catch (Exception eWhile) {
                        System.out.println("Incorrect User or Wrong password");
                    }
                }
            }
        }
    }

    private static final String INSERT_AIRPORT_SQL = "INSERT INTO apt_table " +
            "VALUES (?, ?, ?, ?, ?);";

    private static final String INSERT_RUNWAY_SQL = "INSERT INTO rw_table " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    public void insertAirportRecord(Airport airport) throws SQLException {
        System.out.println(INSERT_AIRPORT_SQL);
        // Step 1: Establishing a Connection
        try (Connection connection = DriverManager.getConnection(awsUrl, userName, userPswd);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_AIRPORT_SQL)) {
            preparedStatement.setString(1, airport.getIcao());
            preparedStatement.setString(2, airport.getIata());
            preparedStatement.setString(3, airport.getName());
            preparedStatement.setArray(4, connection.createArrayOf("double", new Object[] {airport.getCoordinates().getLatLonArray()}));
            preparedStatement.setDouble(5, airport.getElevation());

            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {

            // print SQL exception information
            System.out.println(e.getMessage());
        }

        // Step 4: try-with-resource statement will auto close the connection.
    }

    public void insertRunwayRecord(Airport airport, String rwId) throws SQLException {
        System.out.println(INSERT_RUNWAY_SQL);
        // Step 1: Establishing a Connection
        try (Connection connection = DriverManager.getConnection(awsUrl, userName, userPswd);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RUNWAY_SQL)) {
            if(!airport.rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + airport.getIcao());
            }
            preparedStatement.setString(1, airport.getIcao());
            preparedStatement.setString(2, rwId);
            preparedStatement.setDouble(3, airport.getRwElevStart(rwId));
            preparedStatement.setDouble(4, airport.getRwElevEnd(rwId));
            preparedStatement.setDouble(5, airport.getRwLength(rwId));
            preparedStatement.setDouble(6, airport.getRwHeading(rwId));
            preparedStatement.setDouble(7, airport.getRwWidth(rwId));
            preparedStatement.setArray(8, connection.createArrayOf("double", new Object[] {airport.getRwTHRcoords(rwId)}));

            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {

            // print SQL exception information
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Step 4: try-with-resource statement will auto close the connection.
    }


}
