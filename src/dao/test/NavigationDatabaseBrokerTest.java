package dao.test;

import dao.NavigationDatabaseBroker;
import model.Navaid;
import model.Waypoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NavigationDatabaseBrokerTest {
    @Test
    void getWaypoints(){
        NavigationDatabaseBroker navigationDatabaseBroker = new NavigationDatabaseBroker();
        ArrayList<Waypoint> waypoints = navigationDatabaseBroker.getWaypoints(new String[]{"AVANT", "NELKO", "HAZEL", "BPK"});
        assertEquals(waypoints.get(2).getName(),"HAZEL");
    }


//    @Test
//    void getWaypoints() {
//        NavigationDatabaseBroker navigationDatabaseBroker = new NavigationDatabaseBroker();
//        ArrayList<Waypoint> waypoints = navigationDatabaseBroker.getWaypoints(new String[]{"OCK"});
//        assertNotNull(waypoints, "Expecting not null even if nothing found");
//        assertEquals(waypoints.size(), 1, "Expecting one waypoint returned") ;
//        Waypoint testWaypoint = waypoints.get(0);
//        assertEquals(testWaypoint.getName(), "OCK");
//        assertEquals(testWaypoint.getType(), "D");
//        Navaid testNavaid = (Navaid) testWaypoint;
//        assertEquals(testNavaid.getElevation(),200);
//    }
//
//    @Test
//    void getWaypointsNotExist() {
//        NavigationDatabaseBroker navigationDatabaseBroker = new NavigationDatabaseBroker();
//        ArrayList<Waypoint> waypoints = navigationDatabaseBroker.getWaypoints(new String[]{"DIM"});
//        assertNotNull(waypoints, "Expecting not null even if nothing found");
//        assertEquals(waypoints.size(),0, "Expecting empty array because this waypoint doesnt exist");
//    }
//
//    @Test
//    void getWaypointsMulti() {
//        NavigationDatabaseBroker navigationDatabaseBroker = new NavigationDatabaseBroker();
//        ArrayList<Waypoint> waypoints = navigationDatabaseBroker.getWaypoints(new String[]{"OCK","BIG"});
//        assertNotNull(waypoints, "Expecting not null even if nothing found");
//        assertEquals(waypoints.size(),2, "Expecting two elements in the results");
//    }

}