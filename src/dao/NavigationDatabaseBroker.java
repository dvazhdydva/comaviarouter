package dao;

import model.Airport;
import model.Coordinates;
import model.Navaid;
import model.Waypoint;
import testdrives.DbTestDrive;

import java.sql.*;
import java.util.ArrayList;

public class NavigationDatabaseBroker {
    private static Connection connection = DbConnectionPool.connection;
    private static final String SELECT_FROM_WAYPOINT_TABLE_MULTIPLE_WPTIDS = "SELECT * FROM wpt_table_test WHERE wpt_id IN(?);"; // bloody single quote!!!!! this String is not used
    private static final String SELECT_FROM_WAYPOINT_TABLE_BY_WPTID = "SELECT * FROM wpt_table_test WHERE wpt_id = ?;";
    private static final String SELECT_FROM_AIRPORT_TABLE_BY_ICAO = "SELECT * FROM apt_table_test WHERE apt_icao = ?;";

    public static ArrayList<Waypoint> getWaypoints(String[] waypointIDs) {
        ArrayList<Waypoint> result = new ArrayList<>();
        for (String wptId : waypointIDs) {
            result.add(getWaypoint(wptId));
        }
        return result;
    }

    public static Waypoint getWaypoint(String wptId) {
        Waypoint wpt = null;
        try {
            PreparedStatement ps = connection.prepareStatement(SELECT_FROM_WAYPOINT_TABLE_BY_WPTID);
            ps.setString(1, wptId.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if ("D".equals(rs.getString(3)) || "DB".equals(rs.getString(3))) {
                    wpt = new Navaid(rs.getString(1), (Object[]) rs.getArray(2).getArray(),
                            rs.getString(3), rs.getString(4), rs.getDouble(5), rs.getDouble(6));
                } else {
                    wpt = new Waypoint(rs.getString(1), (Object[]) rs.getArray(2).getArray(), rs.getString(3));
                }
                return wpt;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wpt;
    }

    public static Airport getAirport(String icao) {
        Airport apt = null;
        try {
            PreparedStatement ps = connection.prepareStatement(SELECT_FROM_AIRPORT_TABLE_BY_ICAO);
            ps.setString(1, icao.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                apt = new Airport(rs.getString(1), rs.getString(2),
                        rs.getString(3), (Object[]) rs.getArray(4).getArray(), rs.getDouble(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return apt;
    }

    private static void analyzer1(ArrayList<Waypoint> waypoints) {
        double altitude = 2200; // feet


    }

    private static boolean analyzer2(ArrayList<Object> route) { // {ALT, Airport, Waypoint, Waypoint, Waypoint, Airport}
        int abendCounter = 0;
        double altitude = 0.0;
        ArrayList<Airport> airportPair = new ArrayList<>();
        ArrayList<Waypoint> waypoints = new ArrayList<>();




        for (Object o : route) {
            if (o instanceof Airport) {
                airportPair.add((Airport) o);
            } else if (o instanceof Waypoint) {
                waypoints.add((Waypoint) o);
            } else if (o instanceof Double) {
                altitude = (double) o;
            }
//            } else if (o == null) {
//                try {
//                    throw new Exception("NULLLLLLLLLLLLLLLLLLLLLLLLLLL in " + o.getClass());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }



        try {
            if(airportPair.size()!=2){
                throw new Exception("***TEST0*** Missing airport");
            }

            if (waypoints.get(0) instanceof Navaid && // VOR
                    Coordinates.getDistanceBetweenCoordinates(airportPair.get(0).getCoordinates().getLatLonArray(),
                            waypoints.get(0).getCoordinates().getLatLonArray()) > ((Navaid) waypoints.get(0)).getVORdistance(altitude)) {
                abendCounter++;
                throw new Exception("***TEST1*** VOR doesn't cover the whole segment between origin and VOR");


            } else if (Coordinates.getDistanceBetweenCoordinates(airportPair.get(0).getCoordinates().getLatLonArray(), // en-route
                    waypoints.get(0).getCoordinates().getLatLonArray()) > 50) {
                abendCounter++;
                throw new Exception("***TEST2*** Distance from origin to the first En-route point is greater than 50nm");

            }else{
                System.out.println("Segment " + airportPair.get(0).getIcao() + " " + waypoints.get(0).getName().trim() + " is fine");
            }

            for (int i = 1; i < waypoints.size(); i++) {

                if (waypoints.get(i - 1) instanceof Navaid && waypoints.get(i) instanceof Navaid && // 2 VORs
                        Coordinates.getDistanceBetweenCoordinates(waypoints.get(i - 1).getCoordinates().getLatLonArray(),
                                waypoints.get(i).getCoordinates().getLatLonArray()) > ((Navaid) waypoints.get(i - 1)).getVORdistance(altitude) +
                                ((Navaid) waypoints.get(i)).getVORdistance(altitude)) {
                    abendCounter++;
                    throw new Exception("***TEST3*** VORs do not cover the whole segment between " + waypoints.get(i - 1).getName() +
                            " and " + waypoints.get(i).getName());

                } else if ((waypoints.get(i - 1) instanceof Navaid || waypoints.get(i) instanceof Navaid)) { // 1 VOR
                    double distDiff = 0;
                    String vor = null;
                    String w = null;
                    if (waypoints.get(i - 1) instanceof Navaid) {
                        distDiff = ((Navaid) waypoints.get(i - 1)).getVORdistance(altitude);
                        vor = waypoints.get(i - 1).getName();
                        w = waypoints.get(i).getName();
                    } else {
                        distDiff = ((Navaid) waypoints.get(i)).getVORdistance(altitude);
                        vor = waypoints.get(i).getName();
                        w = waypoints.get(i-1).getName();
                    }
                    if (Coordinates.getDistanceBetweenCoordinates(waypoints.get(i - 1).getCoordinates().getLatLonArray(),
                            waypoints.get(i).getCoordinates().getLatLonArray()) > distDiff) {
                        abendCounter++;
                        throw new Exception("***TEST4*** VOR " + vor.trim() + " doesn't cover the whole segment to " + w);

                    }else{
                        System.out.println("Segment " + waypoints.get(i-1).getName().trim() + " " + waypoints.get(i).getName().trim() + " is fine");
                    }
                } else if (Coordinates.getDistanceBetweenCoordinates(waypoints.get(i - 1).getCoordinates().getLatLonArray(), // all other cases
                        waypoints.get(i).getCoordinates().getLatLonArray()) > 50) {
                    abendCounter++;
                    throw new Exception("***TEST5*** Distance is greater than 50nm between " + waypoints.get(i - 1).getName() + " and " +
                            waypoints.get(i).getName());

                }else{
                    System.out.println("Segment " + waypoints.get(i-1).getName().trim() + " " + waypoints.get(i).getName().trim() + " is fine");
                }
            }

            if (waypoints.get(waypoints.size()-1).getType() == 'D' && // VOR
                    Coordinates.getDistanceBetweenCoordinates(airportPair.get(0).getCoordinates().getLatLonArray(),
                            waypoints.get(0).getCoordinates().getLatLonArray()) > ((Navaid) waypoints.get(1)).getVORdistance(altitude)) {
                abendCounter++;
                throw new Exception("***TEST6*** VOR doesn't cover the whole segment between VOR and destination");


            } else if (Coordinates.getDistanceBetweenCoordinates(airportPair.get(1).getCoordinates().getLatLonArray(), // en-route
                    waypoints.get(waypoints.size()-1).getCoordinates().getLatLonArray()) > 50) {
                abendCounter++;
                throw new Exception("***TEST7*** Distance from the last En-route point and destination is greater than 50nm");

            }else{
                System.out.println("Segment " + waypoints.get(waypoints.size()-1).getName().trim() + " and " + airportPair.get(1).getIcao() + " is fine");
            }
        } catch (Exception e) {
            abendCounter++;
            System.out.println(e.getMessage());
        }
        return abendCounter > 0? false:true;
    }

    // debugging
    public static void main(String[] args) {
        ArrayList<Object> route = new ArrayList<>();
        route.add(19000.0); // altitude
        route.add(getAirport("EGLK"));
        route.add(getWaypoint("ock"));
        route.add(getWaypoint("big"));
//        route.add(getWaypoint("adasi"));
        route.add(getWaypoint("det"));
        route.add(getWaypoint("cpt"));
        route.add(getAirport("EGMH"));

        System.out.println(analyzer2(route));


//        NavigationDatabaseBroker navigationDatabaseBroker = new NavigationDatabaseBroker();
//        ArrayList<Waypoint> wpts = navigationDatabaseBroker.getWaypoints(new String[]{"AVANT", "NELKO", "HAZEL", "BPK"});
//
//        for (Waypoint w : wpts) {
//            System.out.println(w.toString());
//        }
//
//        Waypoint one = getWaypoint("OCK");
//        Waypoint two = getWaypoint("BIG");
//
//        double dist = Coordinates.getDistanceBetweenCoordinates(one.getCoordinates().getLatLonArray(), two.getCoordinates().getLatLonArray());
//
//        System.out.println(dist);

    }
}
