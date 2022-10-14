package model;

public class Navaid extends Waypoint {
    private double elevation;
    private double frequency;
    private double VORdistance;

    public Navaid(String name, String fullName, Coordinates coordinates,
                  double elevation, double frequency, char type){
        super(name, coordinates, type);
        this.setFullName(fullName.toUpperCase());
        this.frequency = frequency;
        this.elevation = elevation;
    }

    public Navaid(String name, String fullName, String rawCoordinates,
                  double elevation, double frequency) {
        this(name, fullName, new Coordinates(rawCoordinates), elevation, frequency, 'D'); // B - DB
    }

    public Navaid(String name, Coordinates coordinates,String type, String fullName,
                  double elevation, double frequency) {
        this(name, fullName, coordinates, elevation, frequency, 'D'); // B - DB
    }

    public Navaid(String wptId, Object[] coords, String type, String fullName, double elevation, double frequency){
        super(wptId, new Coordinates(new double[]{(double) coords[0], (double) coords[1]}), String.valueOf(type.trim().charAt(0)));
        this.setFullName(fullName);
        this.elevation = elevation;
        this.frequency = frequency;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getVORdistance(double altitude){
        if(this.getType()=='D'){
//            return 1.23 * Math.sqrt(this.elevation);
            return 1.23 * Math.sqrt(altitude);
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return super.toString() + " Navaid{" +
                "elevation=" + elevation +
                ", frequency=" + frequency +
                '}';
    }
}
