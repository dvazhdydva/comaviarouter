package testdrives;

import model.Airport;

public class ModelTestDrive {
    public static void main(String[] args) {
        Airport eglk = new Airport("EGLK", "BBS", "BLACKBUSHE", " 511926N   0005051W",325,'f','f');
        eglk.addRunway("07", "511921.17N \n" +
                "0005116.68W", 071,1335,46,322,324,'M','f');
        eglk.addRunway("25","511931.40N \n" +
                "0005027.64W",251,1335,46,324,322,'m','f');

        eglk.printAllRwDetails();
        System.out.println("*****************************************************");
        eglk.printRwDetails("24");
    }
}
