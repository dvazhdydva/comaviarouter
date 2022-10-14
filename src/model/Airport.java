package model;

import java.util.ArrayList;

public class Airport {
    private String icao;
    private String iata;
    private String name;
    private Coordinates coordinates; // 2 element array of doubles -- getLat getLon
    private double elevation;
    private ArrayList<Runway> runways = new ArrayList<>();

    public Airport(String icao, String iata, String name, Coordinates coordinates, double elevation,
                   char lengthUnits, char altitudeUnits) {
        setIcao(icao);
//        this.icao = icao;
        setIata(iata);
//        this.iata = iata;
        setName(name);
//        this.name = name;
        this.coordinates = coordinates;
        setElevation(elevation,altitudeUnits);
//        this.elevation = elevation;
        addRunwayDefault();
    }

    public Airport(String icao, String iata, String name, String rawCoordinates, double elevation,
                   char lengthUnits, char altitudeUnits) {
        this(icao, iata, name, new Coordinates(rawCoordinates), elevation, lengthUnits, altitudeUnits);
    }

    public Airport(String icao, String iata, String name, Object[] coords, double elev){
        this(icao, iata, name, new Coordinates(new double[]{(double) coords[0], (double) coords[1]}), elev, 'f', 'f');
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        try{
            if(icao.equals("")){
                throw new Exception("Empty ICAO");
            }else if(icao.length()!=4){
                throw new Exception("Invalid ICAO code length");
            }else if(!icao.matches("[a-zA-Z]+")){
                throw new Exception("ICAO code must contain only letters");
            }else {
                this.icao = icao.toUpperCase();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        try{
            if(iata.equals("")){
                throw new Exception("Empty IATA");
            }else if(iata.length()!=3){
                throw new Exception("Invalid IATA code length");
            }else if(!iata.matches("[a-zA-Z0-9]+")){
                throw new Exception("IATA code contains invalid characters");
            }else {
                this.iata = iata.toUpperCase();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCoordinates(String rawCoordinates) {
        this.coordinates = new Coordinates(rawCoordinates);
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation, char units) {
        try{
//            if(elevation < -1000){
//                throw new Exception("Elevation value out of scope");
//            }
            if(Character.toUpperCase(units)!='F'){
                elevation = Math.round(elevation / 0.3048);
            }
            if(elevation < -1000 || elevation > 27000){
                throw new Exception("The elevation value just doesn't make any sense");
            }else {
                this.elevation = elevation;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    // -------- AIRPORT -> RUNWAY --------------------------

    private void addRunwayDefault(){
        runways.add(new Runway(this.getIcao(), "99", this.getCoordinates(), 360.0, 9999.9, 999.9, this.getElevation(),
                this.getElevation(),'f','f'));
    }

    private void removeRunwayDefault(){
        if(runways.size()==0){
            return;
        }
        for(int i=0;i<runways.size();i++){
            if(runways.get(i)!=null && runways.get(i).getRwId().equals("99")){
                runways.remove(i);
                return;
            }
        }
    }

    public void addRunway(String rwId, Coordinates rwTHRcoordinates,
                          double rwHeading, double rwLength, double rwWidth,
                          double rwElevStart, double rwElevEnd, char lengthUnits, char altitudeUnits){
        runways.add(new Runway(this.getIcao(), rwId, rwTHRcoordinates, rwHeading, rwLength, rwWidth, rwElevStart,
                rwElevEnd,lengthUnits,altitudeUnits));
        removeRunwayDefault();
    }

    public void addRunway(String rwId, String rawRwTHRcoordinates,
                          double rwHeading, double rwLength, double rwWidth,
                          double rwElevStart, double rwElevEnd, char lengthUnits, char altitudeUnits){
        addRunway(rwId, new Coordinates(rawRwTHRcoordinates), rwHeading, rwLength, rwWidth, rwElevStart,
                rwElevEnd,lengthUnits,altitudeUnits);
    }

    // ---------RUNWAY GETTERS-----------

    public boolean rwExists(String rwId){
        if(!rwId.equals("")){
            for(Runway r : runways){
                if(rwId.equals(r.getRwId())){
                    return true;
                }
            }
        }
        return false;
    }

    private Runway rwSelector(String rwId){
        for(Runway r : runways){
            if(rwId.equals(r.getRwId())){
                return r;
            }
        }
        return null;
    }

    public double[] getRwTHRcoords(String rwId){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else {
                Runway runway = rwSelector(rwId);
                return runway.getRwTHRcoordinates().getLatLonArray();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new double[0];//????? how to avoid this? do not return anything if the exception has been caught
    }

    public String getRWcoords(String rwId){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else {
                Runway runway = rwSelector(rwId);
                return runway.getRwTHRcoordinates().getLatLon();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return rwId + " does not exist";//????? how to avoid this? do not return anything if the exception has been caught
    }

    public void printAllRwDetails(){
        StringBuilder sb = new StringBuilder();
        for(Runway r : runways){
            if(r!=null){
                sb.append("ICAO: " + this.getIcao()).append(" RW: " + r.getRwId()).append(" COORDS: " + r.getRwTHRcoordinates().getLatLon()).
                        append(" HEADING: " + r.getRwHeading()).append(" LENGTH: " + r.getRwLength()).append(" WIDTH: " + r.getRwWidth()).
                        append(" START ELEV: " + r.getRwElevStart()).append(" END ELEV: " + r.getRwElevEnd()).append("\n");
            }
        }
        System.out.println(sb.toString());
    }

    public void printRwDetails(String rwId){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else{
                Runway runway = rwSelector(rwId);
                System.out.print("ICAO: " + this.getIcao() + " RW: " + runway.getRwId() + " COORDS: " + runway.getRwTHRcoordinates().getLatLon()
                        + " HEADING: " + runway.getRwHeading() + " LENGTH: " + runway.getRwLength() + " WIDTH: " + runway.getRwWidth()
                        + " START ELEV: " + runway.getRwElevStart() + " END ELEV: " + runway.getRwElevEnd());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public double getRwHeading(String rwId){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else {
                Runway runway = rwSelector(rwId);
                return runway.getRwHeading();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return 0;//????? how to avoid this? do not return anything if the exception has been caught
    }

    public double getRwElevStart(String rwId){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else {
                Runway runway = rwSelector(rwId);
                return runway.getRwElevStart();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return 0;//????? how to avoid this? do not return anything if the exception has been caught
    }

    public double getRwElevEnd(String rwId){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else {
                Runway runway = rwSelector(rwId);
                return runway.getRwElevEnd();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return 0;//????? how to avoid this? do not return anything if the exception has been caught
    }

    public double getRwLength(String rwId){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else {
                Runway runway = rwSelector(rwId);
                return runway.getRwLength();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return 0; //????? how to avoid this? do not return anything if the exception has been caught
    }

    public double getRwWidth(String rwId){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        Runway runway = rwSelector(rwId);
        return runway.getRwWidth();
    }

    // -------------------- RUNWAY SETTERS -----------------------

    public void setRwId(String rwIdOld, String RwIdNew){
        try{
            if(!rwExists(rwIdOld)){
                throw new Exception(rwIdOld + " Runway does not exist at " + this.getIcao());
            }else {
                Runway runway = rwSelector(rwIdOld);
                runway.setRwId(RwIdNew);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setRwCoords(String rwId, String coordsNew){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else {
                Runway runway = rwSelector(rwId);
                runway.setRwTHRcoordinates(coordsNew);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setRwHeading(String rwId, double headingNew){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else {
                Runway runway = rwSelector(rwId);
                runway.setRwHeading(headingNew);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setRwLength(String rwId, double lengthNew, char lengthUnits){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else{
                Runway runway = rwSelector(rwId);
                runway.setRwLength(lengthNew, lengthUnits);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setRwWidth(String rwId, double widthNew, char lengthUnits){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else{
                Runway runway = rwSelector(rwId);
                runway.setRwWidth(widthNew, lengthUnits);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setRwElevStart(String rwId, double elevStartNew, char altitudeUnits){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else{
                Runway runway = rwSelector(rwId);
                runway.setRwElevStart(elevStartNew, altitudeUnits);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setRwElevEnd(String rwId, double elevEndNew, char altitudeUnits){
        try{
            if(!rwExists(rwId)){
                throw new Exception("Runway '" + rwId + "' does not exist at " + this.getIcao());
            }else{
                Runway runway = rwSelector(rwId);
                runway.setRwElevEnd(elevEndNew, altitudeUnits);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    // *********************** RUNWAY CLASS *********************************
    private class Runway{
        public String rwIcao;
        public String rwId;
        public double rwHeading;
        public double rwLength;
        public double rwWidth;
        public double rwElevStart;
        public double rwElevEnd;
        public Coordinates rwTHRcoordinates;

        public Runway(String rwIcao, String rwId, Coordinates rwTHRcoordinates,
                      double rwHeading, double rwLength, double rwWidth,
                      double rwElevStart, double rwElevEnd, char lengthUnits, char altitudeUnits) {
            this.rwIcao = rwIcao;
            setRwId(rwId);
//            this.rwId = rwId;
            setRwHeading(rwHeading);
//            this.rwHeading = rwHeading;
            setRwLength(rwLength, lengthUnits);
//            this.rwLength = rwLength;
            setRwWidth(rwWidth, lengthUnits);
//            this.rwWidth = rwWidth;
            setRwElevStart(rwElevStart, altitudeUnits);
//            this.rwElevStart = rwElevStart;
            setRwElevEnd(rwElevEnd, altitudeUnits);
//            this.rwElevEnd = rwElevEnd;
            this.rwTHRcoordinates = rwTHRcoordinates;
        }

        public Runway(String rwIcao, String rwId, String coordinates, double rwHeading, double rwLength,
                      double rwWidth, double rwElevStart, double rwElevEnd, char lengthUnits, char altitudeUnits){
            this(rwIcao, rwId, new Coordinates(coordinates),rwHeading,rwLength,rwWidth,
                    rwElevStart,rwElevEnd,lengthUnits,altitudeUnits);
        }

        public String getRwId() {
            return rwId;
        }

        public void setRwId(String rwId) {
            try{
                if(rwId == null || rwId.equals("") || rwId.length() < 2 || rwId.length() > 3){
                    throw new Exception("Illegal RW id");
                }else {
                    this.rwId = rwId;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

        public double getRwHeading() {
            return rwHeading;
        }

        public void setRwHeading(double rwHeading) {
            try{
                if(rwHeading < 0 || rwHeading > 360){
                    throw new Exception("Invalid heading value");
                }else {
                    this.rwHeading = rwHeading;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

        public double getRwLength() {
            return rwLength;
        }

        public void setRwLength(double rwLength, char units) {
            try{
//                if(rwLength < 0){
//                    throw new Exception("Length value cannot be negative");
//                }
                if(Character.toUpperCase(units)!='F'){
                    rwLength = Math.round(rwLength / 0.3048);
                }
                if(rwLength < 0 || rwLength > 25000){
                    throw new Exception("The length value just doesn't make any sense");
                }else {
                    this.rwLength = rwLength;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

        public double getRwWidth() {
            return rwWidth;
        }

        public void setRwWidth(double rwWidth, char units) {
            try{
//                if(rwWidth < 0){
//                    throw new Exception("Length value cannot be negative");
//                }
                if(Character.toUpperCase(units)!='F'){
                    rwWidth = Math.round(rwWidth / 0.3048);
                }
                if(rwWidth < 0 || rwWidth > 2000){
                    throw new Exception("The width value just doesn't make any sense");
                }else {
                    this.rwWidth = rwWidth;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

        public double getRwElevStart() {
            return rwElevStart;
        }

        public void setRwElevStart(double rwElevStart, char units) {
            try{
//                if(rwElevStart < 0){
//                    throw new Exception("Elevation value cannot be negative");
//                }
                if(Character.toUpperCase(units)!='F'){
                    rwElevStart = Math.round(rwElevStart / 0.3048);
                }
                if(rwElevStart < -1000 || rwElevStart > 27000){
                    throw new Exception("The elevation value just doesn't make any sense");
                }else {
                    this.rwElevStart = rwElevStart;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

        public double getRwElevEnd() {
            return rwElevEnd;
        }

        public void setRwElevEnd(double rwElevEnd, char units) {
            try{
//                if(rwElevEnd < 0){
//                    throw new Exception("Elevation value cannot be negative");
//                }
                if(Character.toUpperCase(units)!='F'){
                    rwElevEnd = Math.round(rwElevEnd / 0.3048);
                }
                if(rwElevEnd < -1000 || rwElevEnd > 27000){
                    throw new Exception("The elevation value just doesn't make any sense");
                }else {
                    this.rwElevEnd = rwElevEnd;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        public Coordinates getRwTHRcoordinates() {
            return rwTHRcoordinates;
        }

        public void setRwTHRcoordinates(Coordinates rwTHRcoordinates) {
            this.rwTHRcoordinates = rwTHRcoordinates;
        }

        public void setRwTHRcoordinates(String rawRwTHRcoordinates) {
            this.rwTHRcoordinates = new Coordinates(rawRwTHRcoordinates);
        }
    }
}
