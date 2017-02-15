package com.example.dennisjohansson8.tracker;

import android.location.Location;
import android.util.Log;

import java.util.Arrays;

class LapLogic {

    LapLogic() {

    }

    /**
     * @param location @description current location
     * @return distance in m
     * calculates distance in m
     */
    double calcDistance(Location oldLocation, Location location) {
        double deltaAlt = location.getAltitude() - oldLocation.getAltitude();
        double deltaLat = Math.toRadians(location.getLatitude() - oldLocation.getLatitude());
        double deltaLong = Math.toRadians(location.getLongitude() - oldLocation.getLongitude());

        double a = (Math.pow(Math.sin(deltaLat / 2), 2) + Math.cos(Math.toRadians(location.getLatitude())) * Math.cos(Math.toRadians(oldLocation.getLatitude())) * Math.pow((Math.sin(deltaLong / 2)), 2));
        double angle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        int RADIUS = 6371;
        return Math.sqrt(Math.pow((angle * RADIUS), 2) + (deltaAlt * deltaAlt)) * 1000;
    }

    /**
     * add everything from array together
     *
     * @param speed array that contains speed values
     * @return total of all speeds (total distance)
     */
    double simpleDistanceFormat(String[] speed) {
        double distance = 0;
        for (String aSpeed : speed) {
            distance += Double.parseDouble(aSpeed);
        }
        return distance;
    }

    /**
     * checks if newer time is within bounds of the older time
     *
     * @param oldSpeed       the older speed
     * @param newSpeed       the newer speed
     * @param timeDifference the time difference of the two speeds
     * @return boolean
     */
    boolean inBounds(double oldSpeed, double newSpeed, int timeDifference) {
        return (newSpeed <= oldSpeed + (timeDifference * 9.82 * 2) || newSpeed >= (oldSpeed - timeDifference * 9.82 * 2));
    }

    /**
     * formats time to "HH hours mm minutes ss sec"
     *
     * @param length time in seconds
     * @return formatted version of time
     */
    String timeFormat(int length) {
        int hour = (int) Math.floor(length / (60 * 60));
        int minute = (int) (Math.floor(length / 60) - hour * 60);
        int second = (int) Math.floor(length % 60);
        return hour + "h " + minute + "min " + second + "sec";
    }

    double findMaxSpeed(String[] speedArray) {
        double max = 0;
        for (String aSpeedArray : speedArray) {
            if (Double.parseDouble(aSpeedArray) > max) {
                max = Double.parseDouble(aSpeedArray);
            }
        }
        return max;
    }

    double findMinSpeed(String[] speedArray) {
        double min = 0;
        for (String aSpeedArray : speedArray) {
            if (Double.parseDouble(aSpeedArray) > min) {
                min = Double.parseDouble(aSpeedArray);
            }
        }
        return min;
    }

    double averageSpeed(String[] speedArray) {
        return (simpleDistanceFormat(speedArray) / speedArray.length);
    }

    double averageSpeedZero(String[] speedArray) {
        int length = speedArray.length;
        for (String aSpeedArray : speedArray) {
            if (Double.parseDouble(aSpeedArray) == 0) {
                length--;
            }
        }
        return (simpleDistanceFormat(speedArray) / length);
    }

    double medianSpeed(String[] speedArray) {
        double median;
        Arrays.sort(speedArray);
        if ((speedArray.length & 1) == 0) {
            //even
            median = (Double.parseDouble(speedArray[((speedArray.length) / 2) - 1]) + Double.parseDouble(speedArray[((speedArray.length) / 2)])) / 2;
        } else {
            median = Double.parseDouble(speedArray[(speedArray.length / 2)]);
        }
        return median;
    }

    double medianSpeedZero(String[] speedArray) {
        double median;
        int nrZero = 0;
        Arrays.sort(speedArray);
        for (String aSpeed : speedArray) {
            if (aSpeed.equals("0")) {
                nrZero++;
            } else {
                break;
            }
        }
        if ((speedArray.length - nrZero & 1) == 0) {
            //even
            median = (Double.parseDouble(speedArray[((speedArray.length + nrZero) / 2) + 1]) + Double.parseDouble(speedArray[((speedArray.length + nrZero) / 2)])) / 2;
        } else {
            median = Double.parseDouble(speedArray[((speedArray.length + nrZero) / 2)]);
        }
        return median;
    }
}
