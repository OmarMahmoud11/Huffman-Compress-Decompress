import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Pattern;



public class DeCompressFile {
    private static ArrayList<byte[]> C = new ArrayList<>();
    private static HashMap<String, String> deserializedHashMap = null;
    private static long size=0;
    private static void deCompressFile(String fileName){
        ByteArrayOutputStream ous = null;
        try  {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            /*******************************************************************/
            deserializedHashMap = (HashMap<String, String>) ois.readObject();


            // Where save the decompressed file
            String[] directories = fileName.split(Pattern.quote("\\"));
            directories[directories.length-1] = "extracted." +directories[directories.length-1].substring(0,directories[directories.length-1].length()-3);
            String newFileName = "";
            for(int i=0 ; i<directories.length ; i++){
                newFileName+=directories[i];
                if(i!= directories.length-1)
                    newFileName+='\\';
            }
            /********************************************************************/
            //****//
            OutputStream fos = new FileOutputStream(newFileName);
            BufferedOutputStream bof = new BufferedOutputStream(fos);

            String buffer2 = "";
            boolean temp = false;
            ous = new ByteArrayOutputStream();
            int read = 0;
            byte[] buffer = new byte[1024*1024];
            while ((read = fis.read(buffer)) != -1) {
                int m = 0;
                if(!temp){
                    ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
                    for(int j=m ; j<read ; j++){
                        m++;
                        if(buffer[j] != '\n') {
                            byteBuffer.put(buffer[j]);
                        }
                        else {
                            byteBuffer.flip();
                            size = byteBuffer.getLong();
                            temp = true;
                            break;
                        }
                    }
                }
                ous.reset();
                for(int j=m ; j<read ; j++){
                    byte data = buffer[j];
                    for (int k = 7; k >= 0; k--) {
                        int bit = (data >> k) & 1;
                        buffer2+=Integer.toString(bit);
                        if(deserializedHashMap.get(buffer2) != null && size!=0){
                            String in = deserializedHashMap.get(buffer2);
                            byte[] bytesIn =Base64.getDecoder().decode(in);
                            bof.write(bytesIn);
                            size-=bytesIn.length;
                            buffer2="";
                        }
                    }
                }
            }
            bof.close();
            ois.close();
            System.out.println("Successfully"
                    + " Decompressed");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void handleDecompression(String fileName){
        deCompressFile(fileName);
    }
}
