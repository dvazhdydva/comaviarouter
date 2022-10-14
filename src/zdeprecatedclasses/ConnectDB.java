package zdeprecatedclasses;

import java.sql.*;
import java.util.Scanner;

public class ConnectDB {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)){
            ResultSet resultSet;
            int failedAttemptsCounter = 0;
            while(true) {
                try {
                    System.out.print("Enter username: ");
                    String user = scanner.next();
                    System.out.print("Enter password: ");
                    String pswd = scanner.next();
                    Connection connection = DriverManager.
//                            getConnection("jdbc:postgresql://aviarouter-prod.cm0iszl97jcf.us-east-1.rds.amazonaws.com:5432/aviarouter",
//                            user, pswd);
                    getConnection("jdbc:postgresql://localhost:5432/postgres",
                            user, pswd);
                    Statement statement = connection.createStatement();
//                    resultSet = statement.executeQuery("select * from wpt_table where wpt_id = 'VAPID';");
                    resultSet = statement.executeQuery("select * from wpt_table_test;");
                    connection.close();
                    break;
                } catch (Exception eWhile) {
                    System.out.println(eWhile.getMessage());
                    System.out.println("Incorrect User or Wrong password");
                    failedAttemptsCounter++;
                    if(failedAttemptsCounter>3){
                        System.out.println("You've entered incorrect credentials more than 3 times. Program will quit.");
                        System.exit(-1);
                    }
                }
            }

            // test - print to console
            while(resultSet.next()){
                System.out.println(resultSet.getString("wpt_id"));
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }




    }
}
