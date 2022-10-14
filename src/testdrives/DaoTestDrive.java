package testdrives;

import dao.NavigationDatabaseBroker;
import model.Waypoint;

import java.util.ArrayList;

public class DaoTestDrive {

    public static void main(String[] args) {
//        Airport egaa = new Airport("EGAA", "BFS", "BELFAST / ALDERGROVE", "543927N 0061257W", 268, 'f','f');
//        egaa.addRunway("07","543908.12N \n" +
//                "\n" +
//                "0061406.87W",67,2780,45,205.8,267.5,'m','f');
//        SQLinsertRecordTest addEgaa = new SQLinsertRecordTest();
//
//        try {
//            addEgaa.insertRunwayRecord(egaa,"07");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        NavigationDatabaseBroker navigationDatabaseBroker = new NavigationDatabaseBroker();
        ArrayList<Waypoint> wpts = navigationDatabaseBroker.getWaypoints(new String[] {"OCK"});

        for(Waypoint w : wpts){
            System.out.println(w.getCoordinates());
        }

    }
}
