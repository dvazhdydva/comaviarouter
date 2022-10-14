package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public final class Coordinates {

	private double[] coordinates = new double[2];

	// constructors
	public Coordinates() {
	}

	public Coordinates(String[] str) throws Exception { // {"45", "02 ", "20.35N 002 15 18.98E"}
		String tempstr = "";
		// remove spaces and build a single string latlon
		for (int i = 0; i < str.length; i++) {
			for (int j = 0; j < str[i].length(); j++) {
				if (str[i] != " ") {
					tempstr += str[i];
				}
			}
		}

		String[] latlon = coordinatesFormatter(tempstr);
		coordinates = coordinatesConverter(latlon);
	}

	public Coordinates(String latStr, String lonStr) throws Exception { // {"45 02 20.35N", "002 15 18.98E"}
		String tempstr = "";
		// remove spaces
		for (int i = 0; i < latStr.length(); i++) {
			if (latStr.charAt(i) != ' ') {
				tempstr += String.valueOf(latStr.charAt(i));
			}
		}
		for (int i = 0; i < lonStr.length(); i++) {
			if (lonStr.charAt(i) != ' ') {
				tempstr += String.valueOf(lonStr.charAt(i));
			}
		}

		String[] latlon = coordinatesFormatter(tempstr);
		coordinates = coordinatesConverter(latlon);
	}

	public Coordinates(double[] coords) {
		coordinates = coords;
	}

	public Coordinates(Double[] coords){
		coordinates = new double[]{coords[0], coords[1]};
	}

	public Coordinates(String str)  {

		str = removeUnwantedCharsFromCoordinates(str);

		String[] coords = new String[0];
		try {
			coords = coordinatesFormatter(str);
		} catch (Exception e) {
			System.out.println("Illegal coordinates format");
		}
		coordinates = coordinatesConverter(coords);

	}

	public Coordinates(double lat, double lon) throws Exception {// throws CoordinatesValueExceedsMaximum {

		try {
			if (Math.abs(lat) > 90.0) {
				throw new Exception("LAT value is greater than 90");
			} else if (Math.abs(lon) > 180.0) {
				throw new Exception("LON value is greater than 180");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		coordinates[0] = lat;
		coordinates[1] = lon;
	}	// done

	// private methods
	private static double[] coordinatesConverter(String[] latlon) {// throws CoordinatesValueExceedsMaximum {
		double[] result = new double[2];
		// N15:24:13.986 (152413.986)-> 15 + (24 + (13.986/60)/60) = 15.403885
		double lat = 1;
		double lon = 1;
		if (latlon[0].charAt(0) == '-') {
			lat = -1;
			latlon[0] = latlon[0].replaceAll("-", "");
		}

		lat = lat * ((Double.parseDouble(latlon[0].substring(0, 2)) + ((Double.parseDouble(latlon[0].substring(2, 4))
				+ (Double.parseDouble(latlon[0].substring(4, latlon[0].length())) / 60))) / 60));

		if (latlon[1].charAt(0) == '-') {
			lon = -1;
			latlon[1] = latlon[1].replaceAll("-", "");
		}

//		System.out.println("test: " + latlon[1]);

		lon = lon * ((Double.parseDouble(latlon[1].substring(0, 3)) + ((Double.parseDouble(latlon[1].substring(3, 5))
				+ (Double.parseDouble(latlon[1].substring(5, latlon[1].length())) / 60))) / 60));

		try {
			if (Math.abs(lat) > 90.0) {
				throw new Exception("LAT value is greater than 90");
			} else if (Math.abs(lon) > 180.0) {
				throw new Exception("LON value is greater than 180");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		result[0] = lat;
		result[1] = lon;

		return result;
	}	// done

	private static String removeUnwantedCharsFromCoordinates(String str) {
		String result = "";
		str = str.toUpperCase();
		for (char c : str.toCharArray()) {
			if (c == 'N' || c == 'S' || c == 'E' || c == 'W' || (c >= 48 && c <= 57) || c == ',' || c == '.'
					|| c == '-') {
				result = result + String.valueOf(c);
			}
		}
		return result;
	}

	private static String[] coordinatesFormatter(String str) throws Exception {// throws CoordinatesReferenceLetterMissing {
		String[] result = new String[2];
		str = str.toUpperCase();

		try {
			if (str.indexOf("N") < 0 && str.indexOf("S") < 0) {
				throw new Exception("N/S is missing");
			} else if (str.indexOf("E") < 0 && str.indexOf("W") < 0) {
				throw new Exception("E/W is missing");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		if (str.indexOf("S") >= 0) {
			result[0] = "-";
		} else {
			result[0] = "";
		}
		if (str.indexOf("W") >= 0) {
			result[1] = "-";
		} else {
			result[1] = "";
		}
		// lat
		if (str.indexOf("N") == 0 || str.indexOf("S") == 0) { // N001122E0001122
			if (str.indexOf("E") > 0) {
				result[0] += str.substring(1, str.indexOf("E"));
			} else {
				result[0] += str.substring(1, str.indexOf("W"));
			}
		} else {// 001122N0001122E
			if (str.indexOf("S") > 0) {
				result[0] += str.substring(0, str.indexOf("S"));
			} else {
				result[0] += str.substring(0, str.indexOf("N"));
			}
		}
		// lon
		if (str.indexOf("E") == str.length() - 1 || str.indexOf("W") == str.length() - 1) { // 001122N0001122E
			if (str.indexOf("N") > 0) {
				result[1] += str.substring(str.indexOf("N") + 1, str.length() - 1);
			} else {
				result[1] += str.substring(str.indexOf("S") + 1, str.length() - 1);
			}
		} else {// N001122E0001122
			if (str.indexOf("E") > 0) {
				result[1] += str.substring(str.indexOf("E") + 1, str.length());
			} else {
				result[1] += str.substring(str.indexOf("W") + 1, str.length());
			}
		}
		return result;
	}	// done

	// object methods
	public void setCoordinates(double lat, double lon) throws Exception {// throws CoordinatesValueExceedsMaximum {
		try {
			if (Math.abs(lat) > 90.0) {
				throw new Exception("LAT value is greater than 90");
			} else if (Math.abs(lon) > 180.0) {
				throw new Exception("LON value is greater than 180");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		coordinates[0] = lat;
		coordinates[1] = lon;
	}	// done

	public void setCoordinates(String rawCoordinates) throws Exception{
		rawCoordinates = removeUnwantedCharsFromCoordinates(rawCoordinates);
		String[] coords = coordinatesFormatter(rawCoordinates);
		coordinates = coordinatesConverter(coords);
	}

	public String getCoordinates() {
//		double[] result = new double[2];
//		result[0] = coordinates[0];
//		result[1] = coordinates[1];
		return coordinates[0] + " " + coordinates[1];
	}

	public String getTextCoordinates() { //nicety 
		String lat = "";
		String lon = "";
		if (this.coordinates[0] < 0) {
			lat = Constants.SOUTH;
		} else {
			lat = Constants.NORTH;
		}

		if (this.coordinates[1] < 0) {
			lon = Constants.WEST;
		} else {
			lon = Constants.EAST;
		}

		lat = lat
				+ String.valueOf("0" + getDegrees(this.getLat()))
						.substring(String.valueOf("0" + getDegrees(this.getLat())).length() - 2)
				+ Constants.DEGREE
				+ String.valueOf("0" + getMinutes(this.getLat()))
						.substring(String.valueOf("0" + getMinutes(this.getLat())).length() - 2)
				+ Constants.MINUTES
				+ String.valueOf("0" + (int) getSeconds(this.getLat()))
						.substring(String.valueOf("0" + (int) getSeconds(this.getLat())).length() - 2)
				+ (String.valueOf(getSeconds(this.getLat()) - (int) getSeconds(this.getLat())) + "000000").substring(1,
						6);

		lon = lon
				+ String.valueOf("00" + getDegrees(this.getLon()))
						.substring(String.valueOf("00" + getDegrees(this.getLon())).length() - 3)
				+ Constants.DEGREE
				+ String.valueOf("0" + getMinutes(this.getLon()))
						.substring(String.valueOf("0" + getMinutes(this.getLon())).length() - 2)
				+ Constants.MINUTES
				+ String.valueOf("0" + (int) getSeconds(this.getLon()))
						.substring(String.valueOf("0" + (int) getSeconds(this.getLon())).length() - 2)
				+ (String.valueOf(getSeconds(this.getLon()) - (int) getSeconds(this.getLon())) + "000000").substring(1,
						6);

		return lat + " " + lon;
	}

	public String getDecimalDegreeCoordinates() {
		String[] result = new String[2];
		if (coordinates[0] < 0) {
			result[0] = Constants.SOUTH + String.valueOf(coordinates[0]).substring(1, String.valueOf(coordinates[0]).length());
		} else {
			result[0] = Constants.NORTH + String.valueOf(coordinates[0]);
		}
		if (coordinates[1] < 0) {
			result[1] = Constants.WEST + String.valueOf(coordinates[1]).substring(1, String.valueOf(coordinates[1]).length());
		} else {
			result[1] = Constants.EAST + String.valueOf(coordinates[1]);
		}
		return Arrays.toString(result);
	}// done

	public String[] getDecimalMinuteCoordinates() {
		String[] result = new String[2];

		// for easy reading
		double latmin = (Math.abs(coordinates[0]) - Math.abs((int) coordinates[0])) * 60;

//		System.out.println("test: " + latmin);

		// lat N/S
		if (coordinates[0] < 0) {
			result[0] = "S";
		} else {
			result[0] = "N";
		}
		// lat DEGREES
		if (Math.abs((int) coordinates[0]) < 10) {
			result[0] += "0" + String.valueOf(Math.abs((int) coordinates[0])) + ":";
		} else {
			result[0] += String.valueOf(Math.abs((int) coordinates[0])) + ":";
		}
		// lat MINUTES
		if ((int) latmin < 10) {
			result[0] += "0" + String.format("%.3f", latmin);
		} else {
			result[0] += String.format("%.3f", latmin);
		}

		// for easy reading
		double lonmin = (Math.abs(coordinates[1]) - Math.abs((int) coordinates[1])) * 60;

		// lon W/E
		if (coordinates[1] < 0) {
			result[1] = "W";
		} else {
			result[1] = "E";
		}
		// lon DEGREES
		if (Math.abs((int) coordinates[1]) < 10) {
			result[1] += "00" + String.valueOf(Math.abs((int) coordinates[1])) + ":";
		} else if (Math.abs((int) coordinates[1]) < 100) {
			result[1] += "0" + String.valueOf(Math.abs((int) coordinates[1])) + ":";
		} else {
			result[1] += String.valueOf(Math.abs((int) coordinates[1])) + ":";
		}
		// lon MINUTES
		if ((int) lonmin < 10) {
			result[1] += "0" + String.format("%.3f", lonmin);
		} else {
			result[1] += String.format("%.3f", lonmin);
		}
		return result;
	}// done

	public String[] getDecimalSecondsCoordinates() {
		String[] result = getDecimalMinuteCoordinates();

		if ((int) (Double.parseDouble(result[0].substring(result[0].indexOf("."), result[0].length())) * 60) < 10) {
			result[0] = result[0].substring(0, result[0].indexOf(".")) + ":0" + String.format("%.3f",
					(Double.parseDouble(result[0].substring(result[0].indexOf("."), result[0].length())) * 60));
		} else {
			result[0] = result[0].substring(0, result[0].indexOf(".")) + ":" + String.format("%.3f",
					(Double.parseDouble(result[0].substring(result[0].indexOf("."), result[0].length())) * 60));
		}

		if ((int) (Double.parseDouble(result[1].substring(result[1].indexOf("."), result[1].length())) * 60) < 10) {
			result[1] = result[1].substring(0, result[1].indexOf(".")) + ":0" + String.format("%.3f",
					(Double.parseDouble(result[1].substring(result[1].indexOf("."), result[1].length())) * 60));
		} else {
			result[1] = result[1].substring(0, result[1].indexOf(".")) + ":" + String.format("%.3f",
					(Double.parseDouble(result[1].substring(result[1].indexOf("."), result[1].length())) * 60));
		}
		return result;
	}// done

	public String getRawDecimalCoordinates() {
		String[] result = new String[2];
		result[0] = String.format("%.10f", coordinates[0]);
		result[1] = String.format("%.10f", coordinates[1]);
		return Arrays.toString(result);
	}

	// getters and object methods
	public String getLatDecimalMinute() {
		String[] str = getDecimalMinuteCoordinates();
		return str[0];
	}

	public String getLonDecimalMinute() {
		String[] str = getDecimalMinuteCoordinates();
		return str[1];
	}

	public double[] getLatLonArray(){
		return coordinates;
	}

	public String getLatLon(){
		return String.valueOf(this.getLat()) + ", " + String.valueOf(this.getLon());
	}

	public double getLat() {
		return coordinates[0];
	}

	public double getLon() {
		return coordinates[1];
	}

	public boolean isARINC424() {
		// NXX00XXEYYY00YY or NXX0030EYYY00YY
		if ((Math.abs(coordinates[0]) - Math.abs((int) coordinates[0]) == 0
				|| (Math.abs(coordinates[0]) - Math.abs((int) coordinates[0])) * 60 == 30)
				&& (Math.abs(coordinates[1]) - Math.abs((int) coordinates[1]) == 0))
			return true;
		return false;
	}// done

	public Coordinates getGrid(Coordinates coords, int grid) {
		Coordinates gridCoords = new Coordinates();
		double lat = (int)coords.getLat();
		double lon = (int)coords.getLon();
		grid = 5; // temp force to 5 min grid
		return gridCoords;
	}

	public boolean isFullDegree() {
		if (Math.abs(coordinates[0]) - Math.abs((int) coordinates[0]) == 0
				&& (Math.abs(coordinates[1]) - Math.abs((int) coordinates[1]) == 0))
			return true;
		return false;
	}	// done

	public boolean isHalfDegree() {
		if (((Math.abs(coordinates[0]) - Math.abs((int) coordinates[0])) * 60 == 30)
				&& (Math.abs(coordinates[1]) - Math.abs((int) coordinates[1]) == 0))
			return true;
		return false;
	}	// done

	public int westOf(Coordinates coords) { // 1 = yes; -1 = no; 0 = equal; 2 = 180degrees apart
		double c1 = this.coordinates[1];
		double c2 = coords.getLon();

		if (Math.abs(c1) + Math.abs(c2) == 180) {
			return 2;
		}

		if (c1 == c2) {
			return 0;
		}

		if (c1 >= 0 && c2 >= 0) {
			if (c1 < c2) {
				return 1;
			} else {
				return -1;
			}
		} else if (c1 <= 0 && c2 <= 0) {
			if (Math.abs(c1) > Math.abs(c2)) {
				return 1;
			} else {
				return -1;
			}
		} else if (Math.abs(c1) + Math.abs(c2) > 180) {
			if (c1 > 0) {
				return 1;
			} else {
				return -1;
			}
		} else if (Math.abs(c1) + Math.abs(c2) < 180) {
			if (c1 < 0) {
				return 1;
			} else {
				return -1;
			}
		}
		return 0;
	}	// done

	public int northOf(Coordinates coords) { // 1 = yes; -1 = no; 0 = equal;
		double c1 = this.coordinates[0];
		double c2 = coords.getLat();

		if (c1 > c2) {
			return 1;
		} else if (c1 < c2) {
			return -1;
		}

		return 0;
	}

	// static methods
	public static double getDistanceBetweenCoordinates(double[] coords1, double[] coords2) {

//		double f1 = coords1[0] * Math.PI / 180;
//		double f2 = coords2[0] * Math.PI / 180;
//		double df = (coords1[0] - coords2[0]) * Math.PI / 180;
//		double dl = (coords1[1] - coords2[1]) * Math.PI / 180;
//		double a = Math.sin(df / 2) * Math.sin(df / 2) + Math.cos(f1) * Math.cos(f2) * Math.sin(dl / 2) * Math.sin(dl / 2);
//		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//		double d = EARTH_RADIUS * c; // in meteres

		// all above in one var
		double d = Constants.EARTH_RADIUS * (2 * Math.atan2(
				Math.sqrt(Math.sin(((coords1[0] - coords2[0]) * Math.PI / 180) / 2)
						* Math.sin(((coords1[0] - coords2[0]) * Math.PI / 180) / 2)
						+ Math.cos(coords1[0] * Math.PI / 180) * Math.cos(coords2[0] * Math.PI / 180)
								* Math.sin(((coords1[1] - coords2[1]) * Math.PI / 180) / 2)
								* Math.sin(((coords1[1] - coords2[1]) * Math.PI / 180) / 2)),
				Math.sqrt(1 - (Math.sin(((coords1[0] - coords2[0]) * Math.PI / 180) / 2)
						* Math.sin(((coords1[0] - coords2[0]) * Math.PI / 180) / 2)
						+ Math.cos(coords1[0] * Math.PI / 180) * Math.cos(coords2[0] * Math.PI / 180)
								* Math.sin(((coords1[1] - coords2[1]) * Math.PI / 180) / 2)
								* Math.sin(((coords1[1] - coords2[1]) * Math.PI / 180) / 2)))));

		return d / 1000 / 1.852; // in nautical miles
	} //done

	public static double getBearingBetweenCoordinates(double[] coords1, double[] coords2) {
//		double l1 = coords1[1]*Math.PI/180;
//		double l2 = coords2[1]*Math.PI/180;
//		double f1 = coords1[0]*Math.PI/180;
//		double f2 = coords2[0]*Math.PI/180;
//		double y = Math.sin(l2-l1)*Math.cos(f2);
//		double x = Math.cos(f1)*Math.sin(f2)-Math.sin(f1)*Math.cos(f2)*Math.cos(l2-l1);
//		double t = Math.atan2(y, x);

		// "concise" formula in one var
		double t = Math.atan2(
				(Math.sin((coords2[1] * Math.PI / 180) - (coords1[1] * Math.PI / 180))
						* Math.cos(coords2[0] * Math.PI / 180)),
				(Math.cos(coords1[0] * Math.PI / 180) * Math.sin(coords2[0] * Math.PI / 180)
						- Math.sin(coords1[0] * Math.PI / 180) * Math.cos(coords2[0] * Math.PI / 180)
								* Math.cos((coords2[1] * Math.PI / 180) - (coords1[1] * Math.PI / 180))));

		double brng = (t * 180 / Math.PI + 360) % 360; // in degrees

		return brng;
	}// done

	public static double[] getMidPoint(double[] coords1, double[] coords2) {
		double[] result = new double[2];

//		double l1 = coords1[1] * Math.PI / 180;
//		double l2 = coords2[1] * Math.PI / 180;
//		double f1 = coords1[0] * Math.PI / 180;
//		double f2 = coords2[0] * Math.PI / 180;
//		double bx = Math.cos(f2) * Math.cos(l2 - l1);
//		double by = Math.cos(f2) * Math.sin(l2 - l1);
//		double f3 = Math.atan2(Math.sin(f1) + Math.sin(f2),
//				Math.sqrt((Math.cos(f1) + bx) * (Math.cos(f1) + bx) + by * by));
//		double l3 = l1 + Math.atan2(by, Math.cos(f1) + bx);

		// all above in two vars
		double f3 = Math
				.atan2(Math.sin(coords1[0] * Math.PI / 180) + Math.sin(coords2[0] * Math.PI / 180),
						Math.sqrt((Math.cos(coords1[0] * Math.PI / 180) + (Math.cos(coords2[0] * Math.PI / 180)
								* Math.cos((coords2[1] * Math.PI / 180) - (coords1[1] * Math.PI / 180))))
								* (Math.cos(coords1[0] * Math.PI / 180) + (Math.cos(coords2[0] * Math.PI / 180)
										* Math.cos((coords2[1] * Math.PI / 180) - (coords1[1] * Math.PI / 180))))
								+ Math.pow(
										Math.cos(coords2[0] * Math.PI / 180)
												* Math.sin((coords2[1] * Math.PI / 180) - (coords1[1] * Math.PI / 180)),
										2)));
		double l3 = (coords1[1] * Math.PI / 180) + Math.atan2(
				Math.cos(coords2[0] * Math.PI / 180)
						* Math.sin((coords2[1] * Math.PI / 180) - (coords1[1] * Math.PI / 180)),
				Math.cos(coords1[0] * Math.PI / 180) + (Math.cos(coords2[0] * Math.PI / 180)
						* Math.cos((coords2[1] * Math.PI / 180) - (coords1[1] * Math.PI / 180))));

		result[0] = f3 * 180 / Math.PI; // degrees
		result[1] = l3 * 180 / Math.PI; // degrees
		return result;
	}

	public static double[] getBearingDistanceCoordinates(double[] coords, double distanceKM, double bearing) {// distance
																												// in km
																												// //
																												// true
																												// bearing!
		double[] result = new double[2];

		distanceKM *= 1000; // to meteres

		/*
		 * const φ2 = Math.asin( Math.sin(φ1)*Math.cos(d/R) +
		 * Math.cos(φ1)*Math.sin(d/R)*Math.cos(brng) ); const λ2 = λ1 +
		 * Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(φ1),
		 * Math.cos(d/R)-Math.sin(φ1)*Math.sin(φ2));
		 */
		double l1 = coords[1] * Math.PI / 180;
		double f1 = coords[0] * Math.PI / 180;

		double f2 = Math.asin(Math.sin(f1) * Math.cos(distanceKM / Constants.EARTH_RADIUS)
				+ Math.cos(f1) * Math.sin(distanceKM / Constants.EARTH_RADIUS) * Math.cos(bearing));
		double l2 = l1 + Math.atan2(Math.sin(bearing) * Math.sin(distanceKM / Constants.EARTH_RADIUS) * Math.cos(f1),
				Math.cos(distanceKM / Constants.EARTH_RADIUS) - Math.sin(f1) * Math.sin(f2));

		result[0] = f2 * 180 / Math.PI; // degrees
		result[1] = l2 * 180 / Math.PI; // degrees
		return result;
	}

	public static int getDegrees(double coords) {
		return Math.abs((int) coords);
	}

	public static int getMinutes(double coords) {
		// N011518 -> 18/60
		return Math.abs((int) ((coords - (int) coords) * 60));
	}

	public static double getSeconds(double coords) {
		// ((D4-INT(D4))*60-INT((D4-INT(D4))*60))*60

		return roundDecimal(Math.abs(((coords - (int) coords) * 60 - (int) ((coords - (int) coords) * 60))) * 60);
	}

	private static double roundDecimal(double value) {
		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(4, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

//	public static double toDecimalMinute(int i) { //Temporarily disabled
//		return 99999.999991;//Math.abs;
//	}

	public static boolean isARINC424(double[] coords) {
		// NXX00XXEYYY00YY or NXX0030EYYY00YY
		if ((Math.abs(coords[0]) - Math.abs((int) coords[0]) == 0
				|| (Math.abs(coords[0]) - Math.abs((int) coords[0])) * 60 == 30)
				&& (Math.abs(coords[1]) - Math.abs((int) coords[1]) == 0))
			return true;
		return false;
	}	// done

	public static boolean isFullDegree(double[] coords) {
		if (Math.abs(coords[0]) - Math.abs((int) coords[0]) == 0
				&& (Math.abs(coords[1]) - Math.abs((int) coords[1]) == 0))
			return true;
		return false;
	}	// done

	public static boolean isHalfDegree(double[] coords) {
		if (((Math.abs(coords[0]) - Math.abs((int) coords[0])) * 60 == 30)
				&& (Math.abs(coords[1]) - Math.abs((int) coords[1]) == 0))
			return true;
		return false;
	}	// done

	// constants
	private static class Constants {
		static public final int EARTH_RADIUS = 6371000; // meters
		static public final double KM_IN_NAUTICAL_MILES = 1.852; //
		static public final double FEET_IN_METERES = 0.3048;
		// StM
		static public final String DEGREE = "\u00B0"; // proper degree symbol
		static public final String MINUTES = "'";
		//	static public final char DEGREE2 = 167;//248; // crossed degree sign
		static public final String COLON = String.valueOf((char)58);
		static public final String DECIMAL = String.valueOf((char)46);
		static public final String NORTH = String.valueOf((char)78);
		static public final String SOUTH = String.valueOf((char)83);
		static public final String WEST = String.valueOf((char)87);
		static public final String EAST = String.valueOf((char)69);
		static public final String LAT = "Latitude: ";
		static public final String LON = "Longitude: ";
		static public final String KM = "km";
		static public final String NM = "nm";
		static public final String M = "m"; // should it be char?
		static public final String F = "feet"; // plural // should it be char (')?

	}

}
