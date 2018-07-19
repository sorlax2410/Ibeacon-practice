package com.kibuno.kenshi.beacon.Core;

public final class DistanceCalculation {

    public static double calculateDist(int txPower, double rssi) {
        return Math.pow(10.0, ((double)txPower - rssi) / (10.0 * 2));
    }

    public static double calculateDistance(int measuredPower, int rssi) {
        if (rssi == 0.0D) {
            return -1.0D; // if we cannot determine distance, return -1.
        }

        double ratio = (double)rssi*1.0D/ (double) measuredPower;
        if (ratio < 1.0D) {
//            return Math.pow(ratio, 8.0D);
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
//            double accuracy =  (0.69976D)*Math.pow(ratio,7.7095D) + 0.111D;
//            double accuracy =  (0.42093)*Math.pow(ratio,6.9476) + 0.54992;
            return accuracy;
        }
    }

    public static double computeAccuracy(int txpower, double rssi) {
        double RSSI = Math.abs(rssi);

//	   if (beacon.getRssi() == 0)
//	   {
//		   return -1.0D;
//	   }
//
//	   double ratio = beacon.getRssi() / beacon.getMeasuredPower();
//	   double rssiCorrection = 0.96D + Math.pow(Math.abs(beacon.getRssi()), 3.0D) % 10.0D / 150.0D;
//
//	   if (ratio <= 1.0D)
//	   {
//		   return Math.pow(ratio, 9.98D) * rssiCorrection;
//	   }
//	   return (0.103D + 0.89978D * Math.pow(ratio, 7.71D)) * rssiCorrection;

        if (RSSI == 0.0D) {
            return -1.0D;
        }

        double ratio = RSSI * 1.0D / (double)txpower;
        if (ratio < 1.0D) {
            return Math.pow(ratio, 8.0D);
        }

        double accuracy = 0.69976D * Math.pow(ratio, 7.7095D) + 0.111D;
        return accuracy;
    }

    public static double calculateAccuracy(int var1) {
        if(var1 == 0) {
            return -1.0D;
        } else {
            double var2 = (double)var1 * 1.0D / -55.0D;
            if(var2 < 1.0D) {
                return changeTwoDecimal_f(Math.pow(var2, 10.0D));
            } else {
                double var4 = 0.89976D * Math.pow(var2, 7.7095D) + 0.111D;
                return changeTwoDecimal_f(var4);
            }
        }
    }

    private static double changeTwoDecimal_f(double var1) {
        double var3 = (double)Math.round(var1 * 100.0D);
        double var5 = var3 / 100.0D;
        String var7 = var5 + "";
        int var8 = var7.indexOf(46);
        if(var8 < 0) {
            var8 = var7.length();
            var7 = var7 + '.';
        }

        while(var7.length() <= var8 + 2) {
            var7 = var7 + "0";
        }

        return Double.parseDouble(var7);
    }
}
