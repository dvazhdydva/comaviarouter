package testdrives;

import dao.NavigationDatabaseBroker;
import model.Airport;
import model.Coordinates;
import model.Navaid;
import model.Waypoint;

import java.sql.*;
import java.util.ArrayList;

public class DbTestDrive {
    private static final String INSERT_AIRPORT_QUERY = "INSERT INTO apt_table_test VALUES (?, ?, ?, ?, ?);";
    private static final String INSERT_WAYPOINT_QUERY = "INSERT INTO wpt_table_test VALUES (?, ?, ?, ?, ?, ?);";
    private static final String INSERT_RUNWAY_QUERY = "INSERT INTO rw_table_test VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String SELECT_ALL_FROM_WPT_TABLE = "SELECT * FROM  wpt_table_test;";
    private static final String SELECT_ALL_FROM_APT_TABLE = "SELECT * FROM  apt_table_test;";
    private static final String SELECT_ALL_FROM_RW_TABLE = "SELECT * FROM  rw_table_test;";

    private static final String SELECT_FROM_RW_TABLE_BY_ID = "SELECT * FROM rw_table_test WHERE rw_id = ?;";
    private static final String SELECT_FROM_AIRPORT_TABLE_BY_ICAO = "SELECT * FROM apt_table_test WHERE apt_icao = ?;";
    private static final String SELECT_FROM_WAYPOINT_TABLE_BY_WPTID = "SELECT * FROM wpt_table_test WHERE wpt_id = ?;";
    private static final String SELECT_FROM_WAYPOINT_TABLE_BY_WPTTYPE = "SELECT * FROM wpt_table_test WHERE wpt_type = ?;";

    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "glavnyjHuy");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String[] formatToArray(String line) {
        String[] result = line.split("\t");
        return result;
    }

    public static String[] formatInsertWaypointString(String wptId, double[] coords, String wptType, String wptName,
                                               double wptElev, double wptFreq) throws Exception {
        String[] result = new String[6];
        if(wptId == null || "".equals(wptId)){
            throw new Exception("Waypoint id must not be null or empty");
        }else{
            result[0] = wptId.toUpperCase();
        }
        if(coords == null || (coords[0]==0.0 && coords[1]==0.0)){
            throw new Exception("Coordinates must not be null or empty (0.0 0.0)");
        }else{
            result[1] = String.valueOf(coords[0]) + "," + String.valueOf(coords[1]);
        }
        if(wptType == null || "".equals(wptType)){
            throw new Exception("Waypoint type must be provided");
        }if(!"E".equals(wptType) && !"B".equals(wptType) && !"D".equals(wptType)){
            throw new Exception("Only B (NDB), D (VOR) or E (5LNC) types are supported");
        }else{
            result[2] = wptType.toUpperCase();
        }
        if(wptName==null || "".equals(wptName)){
            result[3] = result[0];
        }else {
            result[3] = wptName.toUpperCase();
        }

        if("D".equals(result[2])){
            if(wptElev==0.0){
                throw new Exception("D-type (VOR VORDME TACAN VORTAC etc) waypoints must have elevation property");
            }else if(wptFreq==0.0){
                throw new Exception("Frequency must be provided for D-type (VOR VORDME TACAN VORTAC etc) waypoints");
            }else {
                result[4] = String.valueOf(wptElev);
                result[5] = String.valueOf(wptFreq);
            }
        }else if("B".equals(result[2])){
            if(wptFreq==0.0){
                throw new Exception("Frequency must be provided for B-type (NDB) waypoints");
            }else {
                result[4] = "NULL";
                result[5] = String.valueOf(wptFreq);
            }
        }else{
            result[4] = "NULL";
            result[5] = "NULL";
        }
        return result;
    }

    public static String[] formatInsertWaypointString(Waypoint waypoint){
        String[] result = new String[6];
        result[0] = waypoint.getName();
        result[1] = String.valueOf(waypoint.getCoordinates().getLat()) + "," +
                String.valueOf(waypoint.getCoordinates().getLon());
        result[2] = String.valueOf(waypoint.getType());
        result[3] = waypoint.getFullName();
        if(waypoint instanceof Navaid){
            if("D".equals(result[2])){
                result[4] = String.valueOf(((Navaid) waypoint).getElevation());
                result[5] = String.valueOf(((Navaid) waypoint).getFrequency());
            }else{
                result[4] = "NULL";
                result[5] = String.valueOf(((Navaid) waypoint).getFrequency());
            }
        }else{
            result[4] = "NULL";
            result[5] = "NULL";
        }
        return result;
    }

    public static void insertWaypoint(String[] inputData) {
        try {



            //implement a cehck if  waypoint already laoded



            PreparedStatement ps = connection.prepareStatement(INSERT_WAYPOINT_QUERY);
            //wpt_id
            ps.setString(1, inputData[0]);
            //wpt_coords
            ps.setArray(2, connection.createArrayOf("float", inputData[1].trim().split(",")));
            //wpt_type
            ps.setString(3, inputData[2]);
            //wpt_name
            ps.setString(4, inputData[3]);
            //wpt_elev & wpt_freq
            if (inputData[2].trim().equals("D")) {
                ps.setDouble(5, Double.parseDouble(inputData[4]));
                ps.setDouble(6, Double.parseDouble(inputData[5]));
            } else if (inputData[2].trim().equals("B")) {
                ps.setNull(5, java.sql.Types.NULL);
                ps.setDouble(6, Double.parseDouble(inputData[5]));
            } else {
                ps.setNull(5, java.sql.Types.NULL);
                ps.setNull(6, java.sql.Types.NULL);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertWaypoint(Waypoint waypoint) {
        try {



            //implement a cehck if  waypoint already laoded



            PreparedStatement ps = connection.prepareStatement(INSERT_WAYPOINT_QUERY);
            //wpt_id
            ps.setString(1, waypoint.getName());
            //wpt_coords
            ps.setArray(2, connection.createArrayOf("float", waypoint.getCoordinates().getLatLon().split(",")));
            //wpt_type
            ps.setString(3, String.valueOf(waypoint.getType()));
            //wpt_name
            ps.setString(4, waypoint.getFullName());
            //wpt_elev & wpt_freq
            if (waypoint instanceof Navaid && "D".equals(String.valueOf(waypoint.getType()))) {
                ps.setDouble(5, ((Navaid)waypoint).getElevation());
                ps.setDouble(6, ((Navaid)waypoint).getFrequency());
            } else if (waypoint instanceof Navaid &&  "B".equals(String.valueOf(waypoint.getType()))) {
                ps.setNull(5, java.sql.Types.NULL);
                ps.setDouble(6, ((Navaid)waypoint).getFrequency());
            } else {
                ps.setNull(5, java.sql.Types.NULL);
                ps.setNull(6, java.sql.Types.NULL);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertAirport(String[] inputData) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_AIRPORT_QUERY);
            //apt_icao
            ps.setString(1, inputData[0]);
            //apt_iata
            if (inputData[1].trim().equals("NULL")) {
                ps.setNull(2, java.sql.Types.NULL);
            } else {
                ps.setString(2, inputData[1]);
            }
            //apt_name
            ps.setString(3, inputData[2]);
            //apt_coords
            ps.setArray(4, connection.createArrayOf("float", inputData[3].trim().split(",")));
            //apt_elev
            ps.setDouble(5, Double.parseDouble(inputData[4]));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertAirport(Airport airport) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_AIRPORT_QUERY);
            //apt_icao
            ps.setString(1, airport.getIcao());
            //apt_iata
            if ("NULL".equals(airport.getIata())) {
                ps.setNull(2, java.sql.Types.NULL);
            } else {
                ps.setString(2, airport.getIata());
            }
            //apt_name
            ps.setString(3, airport.getName());
            //apt_coords
            ps.setArray(4, connection.createArrayOf("float", airport.getCoordinates().getLatLon().split(",")));
            //apt_elev
            ps.setDouble(5, airport.getElevation());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertRunway(String[] inputData) {
        try {
            PreparedStatement ps = connection.prepareStatement(INSERT_RUNWAY_QUERY);
            //rw_icao
            ps.setString(1, inputData[0]);
            //rw_id
            ps.setString(2, inputData[1]);
            //rw_elev_start
            ps.setDouble(3, Double.parseDouble(inputData[2]));
            //rw_elev_end
            ps.setDouble(4, Double.parseDouble(inputData[3]));
            //rw_length
            ps.setDouble(5, Double.parseDouble(inputData[4]));
            //rw_bearing
            ps.setDouble(6, Double.parseDouble(inputData[5]));
            //rw_width
            ps.setDouble(7, Double.parseDouble(inputData[6]));
            //rw_coords
            ps.setArray(8, connection.createArrayOf("float", inputData[7].trim().split(",")));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArrayList<Object>> selectAllAirportsToArray(){
        ArrayList<ArrayList<Object>> result = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_FROM_APT_TABLE);
            while(rs.next()) {

                ArrayList<Object> data = new ArrayList<>();
                data.add(rs.getString(1));
                data.add(rs.getString(2));
                data.add(rs.getString(3));
                data.add(rs.getArray(4));
                data.add(rs.getDouble(5));
                result.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<ArrayList<Object>> selectAllRunwaysToArray(){
        ArrayList<ArrayList<Object>> result = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_FROM_RW_TABLE);
            while(rs.next()) {
                ArrayList<Object> data = new ArrayList<>();
                data.add(rs.getString(1));
                data.add(rs.getString(2));
                data.add(rs.getDouble(3));
                data.add(rs.getDouble(4));
                data.add(rs.getDouble(5));
                data.add(rs.getDouble(6));
                data.add(rs.getDouble(7));
                data.add(rs.getArray(8));
                result.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<ArrayList<Object>> selectAllWaypointsToArray(){
        ArrayList<ArrayList<Object>> result = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_FROM_WPT_TABLE);
            while(rs.next()) {
                ArrayList<Object> data = new ArrayList<>();
                data.add(rs.getString(1));
                data.add(rs.getArray(2));
                data.add(rs.getString(3));
                data.add(rs.getString(4));
                data.add(rs.getDouble(5));
                data.add(rs.getDouble(6));
                result.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Object> selectRunway(String icao, String rwId) throws Exception{
        ArrayList<Object> result = new ArrayList<>();
            PreparedStatement ps = connection.prepareStatement(SELECT_FROM_RW_TABLE_BY_ID);
            ps.setString(1,rwId.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if(rs.next() && icao.toUpperCase().equals(rs.getString(1))) {
                    result.add(rs.getString(1));
                    result.add(rs.getString(2));
                    result.add(rs.getDouble(3));
                    result.add(rs.getDouble(4));
                    result.add(rs.getDouble(5));
                    result.add(rs.getDouble(6));
                    result.add(rs.getDouble(7));
                    result.add(rs.getArray(8));
            }else{
                throw new IllegalArgumentException("RW" + rwId.toUpperCase() + " doesn't exist at " + icao.toUpperCase());
            }
        return result;
    }

    public static ArrayList<Object> selectAirport(String icao) throws Exception{
        ArrayList<Object> result = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_AIRPORT_TABLE_BY_ICAO);
        ps.setString(1,icao.toUpperCase());
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            result.add(rs.getString(1));
            result.add(rs.getString(2));
            result.add(rs.getString(3));
            result.add(rs.getArray(4));
            result.add(rs.getDouble(5));
        }else{
            throw new IllegalArgumentException("ICAO " + icao.toUpperCase() + " doesn't exist");
        }
        if(result.size()>5){
            throw new Exception("Multiple records for ICAO " + icao.toUpperCase());
        }
        return result;
    }

    public static ArrayList<Object> selectWaypointId(String wptId) throws Exception{
        ArrayList<Object> result = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_WAYPOINT_TABLE_BY_WPTID);
        ps.setString(1,wptId.toUpperCase());
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            result.add(rs.getString(1));
            result.add(rs.getArray(2));
            result.add(rs.getString(3));
            result.add(rs.getString(4));
            result.add(rs.getDouble(5));
            result.add(rs.getDouble(6));
        }else{
            throw new IllegalArgumentException("Waypoint " + wptId.toUpperCase() + " doesn't exist");
        }
        if(result.size()>6){
            throw new Exception("Multiple records for Waypoinnnt " + wptId.toUpperCase());
        }
        return result;
    }

    public static ArrayList<ArrayList<Object>> selectWaypointType(String wptType) throws Exception{
        ArrayList<ArrayList<Object>> result = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(SELECT_FROM_WAYPOINT_TABLE_BY_WPTTYPE);
        ps.setString(1,wptType.toUpperCase());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            ArrayList<Object> data = new ArrayList<>();
            data.add(rs.getString(1));
            data.add(rs.getArray(2));
            data.add(rs.getString(3));
            data.add(rs.getString(4));
            data.add(rs.getDouble(5));
            data.add(rs.getDouble(6));

            result.add(data);
        }

        if(result.size()==0){
            throw new IllegalArgumentException("No type " + wptType.toUpperCase() + " waypoints loaded");
        }

        return result;
    }

    //debugging
    public static void main(String[] args) {
        //AIRPORT
//        insertAirport(new Airport("EGMH", "MSE", "MANSTON", new Coordinates("51°20′32″N 001°20′46″E"),
//                178.0, 'm', 'f'));
        //NAVAID
//        insertWaypoint(new Navaid("DET","DETLING",new Coordinates("511814.41N\n" +
//                "0003550.19E"),645,117.3, 'D'));
        //WAYPOINT
        insertWaypoint(new Waypoint("ADASI","591315.16N 0061731.47W"));
//
//        Waypoint waypointEA = new Waypoint("ABKAT", "540853.12N 0015846.69W");
//        Waypoint waypointD = new Navaid("ADN", "ABERDEEN", "571837.62N\n" +
//                "0021601.95W",600,114.3);
//
//        insertWaypoint(formatInsertWaypointString(waypointEA));
//        insertWaypoint(formatInsertWaypointString(waypointD));
//


//        try {
//            ArrayList<Object> arr = selectRunway("eglk","25");
//            for(Object o : arr) {
//                System.out.print(o + "\t");
//            }
//            System.out.println();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        try{
//            ArrayList<Object> wptArr = selectWaypointId("cpt");
//            for(Object o : wptArr){
//                System.out.print(o + "\t");
//            }
//            System.out.println();
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//
//        try{
//            ArrayList<Object> aptArr = selectAirport("eglk");
//            for(Object o : aptArr){
//                System.out.print(o + "\t");
//            }
//            System.out.println();
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//
//        try{
//            ArrayList<ArrayList<Object>> arr = selectWaypointType("t");
//            for(ArrayList<Object> a : arr){
//                for(Object o : a){
//                    System.out.print(o + "\t");
//                }
//                System.out.println();
//            }
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }



        //===================================================================================================
//        ArrayList<ArrayList<Object>> arr = selectAllWaypointsToArray();
//
//        for(ArrayList<Object> al : arr){
//            for(Object o : al){
//                if(o==null){
//                    System.out.print("null" + "\t");
//                }else {
//                    System.out.print(o.toString() + "\t");
//                }
//            }
//            System.out.println();
//        }

        //===================================================================================================

//        ArrayList<String> allLines = new ArrayList<>();
//        try (BufferedReader bf = new BufferedReader(new FileReader("C:\\Users\\Alexander.Naumov\\OneDrive\\java\\aviarouter\\rds\\rw_table.txt"))) {
//            String currLine;
//            while ((currLine = bf.readLine()) != null) {
//                currLine = currLine.replace("{", "").replace("}", "");
//                allLines.add(currLine);
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
//
//        for (String s : allLines) {
//            String[] str = formatToArray(s);
//            insertRunway(str);
//        }
    }
}
