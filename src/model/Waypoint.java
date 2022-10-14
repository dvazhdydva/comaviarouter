package model;

public class Waypoint {
    private String name; //id
    private String fullName;
    private Coordinates coordinates;
    private final char type; //

    public Waypoint(String name, Coordinates coordinates, char type){
        this.name = name.toUpperCase();
        if(type=='E') {
            this.setFullName(name.toUpperCase());
        }
        this.type = type; // E - en-route
        this.coordinates = coordinates;
    }

    public Waypoint(String name, String rawCoordinates){
        this(name, new Coordinates(rawCoordinates),'E');
    }

    public Waypoint(String name, double[] dbCoordinates){
        this(name, new Coordinates(dbCoordinates),'E');
    }

    public Waypoint(String name, Coordinates coordinates){
        this(name, coordinates,'E');
    }

    public Waypoint(String wptId, double[] dbCoordinates, String type){
        this(wptId, new Coordinates(dbCoordinates), type.trim().charAt(0));
        this.fullName = name;
    }

    public Waypoint(String wptId, Object[] dbCoordinates, String type){
        this(wptId, new Coordinates(new double[]{(double) dbCoordinates[0], (double) dbCoordinates[1]}), type.trim().charAt(0));

    }

    public Waypoint(String wptId, Coordinates coordinates, String type) {
        this(wptId, coordinates, type.charAt(0));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        try{
            if(name.length()>5){
                throw new Exception("Invalid waypoint name");
            }else {
                this.name = name.toUpperCase();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String name){
        if(name.equals("")){
            this.fullName = this.name;
        }else {
            this.fullName = name.toUpperCase();
        }
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCoordinates(String rawCoordinates){
        try {
            this.coordinates = new Coordinates(rawCoordinates);
        } catch (Exception e) {
            System.out.println("Illeagal coordinates format");
        }
    }

    public char getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", coordinates=" + coordinates.getLatLon() +
                ", type=" + type +
                '}';
    }
}
