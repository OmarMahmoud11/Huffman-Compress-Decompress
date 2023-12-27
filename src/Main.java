import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    public static CompressFile compressFile;
    public static DeCompressFile deCompressFile;

    private static String compressedFileName(String fileName,int n){
        String[] directories = fileName.split(Pattern.quote("\\"));
        directories[directories.length-1] = "20011027." + n +"." +directories[directories.length-1]+".hc";
        String newFileName = "";
        for(int i=0 ; i<directories.length ; i++){
            newFileName+=directories[i];
            if(i!= directories.length-1)
                newFileName+='\\';
        }
        return newFileName;
    }
    public static void main(String[] args) {
//        “I acknowledge that I am aware of the academic integrity guidelines of this course,
//        and that I worked on this assignment independently without any unauthorized help”

        double compressionRatio = 0;
        double runningTime = 0;
        long startTime = 0;
        long endTime = 0;
        System.out.println();
        if(args.length < 2 || args.length > 3){
            System.out.println("Error in Segments!");
        }
        if(args[0].equalsIgnoreCase("c")){
            // Original File Size
            File originalFile = new File(args[1]);
            long originalFileSize = originalFile.length();

            //Compress
            startTime = System.currentTimeMillis();
            compressFile.handleCompression(args[1] , Integer.parseInt(args[2]));
            endTime = System.currentTimeMillis();
            runningTime = (double) (endTime-startTime)/1000;

            // Compressed File Size
            String compressedFileName = compressedFileName(args[1],Integer.parseInt(args[2]));
            File compressedFile = new File(compressedFileName);
            long compressedFileSize = compressedFile.length();

            compressionRatio = ((double)compressedFileSize/(double) originalFileSize) * 100;
            System.out.println("Compression Ratio: " + compressionRatio + "%");
            System.out.println("Running Time : " + runningTime + " sec");
        }
        else{
            startTime = System.currentTimeMillis();
            deCompressFile.handleDecompression(args[1]);
            endTime = System.currentTimeMillis();
            runningTime = (double) (endTime-startTime)/1000;
            System.out.println("Running Time : " + runningTime + " sec");
        }
        System.out.println();
    }
}
