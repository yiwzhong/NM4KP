
import java.util.Arrays;


public class DataStatisticalUtils {
 
    /**
     * Median
     *
     * @param arr
     * @return
     */
    public static double getMedian(double[] arr) {
        double[] tempArr = Arrays.copyOf(arr, arr.length);
        Arrays.sort(tempArr);
        if (tempArr.length % 2 == 0) {
            return (tempArr[tempArr.length >> 1] + tempArr[(tempArr.length >> 1) - 1]) / 2;
        } else {
            return tempArr[(tempArr.length >> 1)];
        }
    }


    /**
     * Standard Deviation
     * 
     * @param arr
     * @return
     */
    public static double getStandardDeviation(double[] arr) {
        double sum = 0;
        double mean = 0;
        
        for (double num : arr) {
            mean += num;
        }
        mean = mean / arr.length;
 
        for (int i = 0; i < arr.length; i++) {
            sum += Math.sqrt((arr[i] - mean) * (arr[i] - mean));
        }
        return (sum / (arr.length - 1));
    }


}