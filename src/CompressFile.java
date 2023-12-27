import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Pattern;

public class CompressFile {
    private static long size = 0;
    private static HashSet<String> BufferSet = new HashSet<>();
    private static HashMap<String,Long> freq = new HashMap<>();
    private static HashMap<String,String> stringToBits = new HashMap<>();
    private static HashMap<String,String> bitsToString = new HashMap<>();

    public static void convertToBytes(String fileName,int n){
        FileInputStream ios = null;
        try {
            byte[] buffer = new byte[1024*1024];
            ios = new FileInputStream(fileName);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(ios);
            int read = 0;
            while ((read = bufferedInputStream.read(buffer)) != -1) {
                size += read;
                int ll = read;
                for(int i=0 ; i<read ; i+=n){
                    int k=0;
                    if(n>=ll) k=ll;else k=n;
                    ll-=k;
                    if(k<1)
                        System.out.println(k);
                    ByteBuffer buffer2 = ByteBuffer.allocate(k);
                    for(int j=i ; j<i+k && j<read; j++){
                        buffer2.put(buffer[j]);
                    }
                    byte[] b = buffer2.array();
                    // to convert byte[] to string
                    String str = Base64.getEncoder().encodeToString(b);
                    long fr = freq.get(str)==null?0:freq.get(str);
                    fr += 1;
                    freq.put(str,fr);
                    BufferSet.add(str);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
    }
    public static node Huffman(){
        //this function can solve any type of node, but we only change the C and the node value
        int n = BufferSet.size();
        PriorityQueue<node> PQ =new PriorityQueue<>(new nodeComparator());
        Iterator<String> it = BufferSet.iterator();
        while (it.hasNext()){
            String buffer = it.next();
            PQ.add(new node(buffer, Math.toIntExact(freq.get(buffer)),null,null));
        }
        if(PQ.size()==1)
            return PQ.poll();
        for(int i=0 ; i<n-1 ; i++){
            node z = new node(null,0,null,null);
            z.left = PQ.poll();
            z.right = PQ.poll();
            z.freq = z.right.freq + z.left.freq;
            PQ.add(z);
        }
        return PQ.poll();
    }
    public static void getHashes(node root){
        getHashesHelper(root, "");
    }
    public static void getHashesHelper(node hashNode,String code){
        if (hashNode == null) {
            return;
        }

        if (hashNode.value != null) {
            stringToBits.put(hashNode.value, code);
            bitsToString.put(code,hashNode.value);
        }

        getHashesHelper(hashNode.left, code + "0");
        getHashesHelper(hashNode.right, code + "1");
    }

    private static byte[] binaryStringToBytes(String binaryString) {
        int length = binaryString.length();
        int numBytes = (length + 7) / 8; // Calculate the number of bytes needed

        byte[] result = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            int start = i * 8;
            int end = Math.min(start + 8, length);
            String eightBits = binaryString.substring(start, end);

            // Parse the 8-bit substring to a byte
            result[i] = (byte) Integer.parseInt(eightBits, 2);
        }

        return result;
    }
    private static byte[] binaryStringToBytes2(String binaryString) {
        int length = binaryString.length();
        int numZeros = 8-length;
        for(int j=0 ; j<numZeros ; j++){
            binaryString+='0';
        }
        length = binaryString.length();
        int numBytes = (length + 7) / 8; // Calculate the number of bytes needed

        byte[] result = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            int start = i * 8;
            int end = Math.min(start + 8, length);
            String eightBits = binaryString.substring(start, end);

            // Parse the 8-bit substring to a byte
            result[i] = (byte) Integer.parseInt(eightBits, 2);
        }

        return result;
    }
    public static void compressFile(String fileName,int n){
        try {
            File fileRead = new File(fileName);
            InputStream ios = new FileInputStream(fileRead);
            byte[] bufferRead = new byte[1024*1024];
            int read = 0;
            /*******************************************************************/
            // Where save the compressed file
            String[] directories = fileName.split(Pattern.quote("\\"));
            directories[directories.length-1] = "20011027." + n +"." +directories[directories.length-1]+".hc";
            String newFileName = "";
            for(int i=0 ; i<directories.length ; i++){
                newFileName+=directories[i];
                if(i!= directories.length-1)
                    newFileName+='\\';
            }
            /********************************************************************/

            // Starting writing the header
            OutputStream fos = new FileOutputStream(newFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            BufferedOutputStream bof = new BufferedOutputStream(fos);
            oos.writeObject(bitsToString);


            // Add the size to the file
            ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
            byteBuffer.putLong(size);
            bof.write(byteBuffer.array());
            bof.write((byte) '\n');

            // Starting writing the bytes in it
            String buffer="";
            while ((read = ios.read(bufferRead)) != -1){
                int ll = read;
                for(int i=0 ; i<read ; i+=n){
                    int k=0;
                    if(n>=ll) k=ll;
                    else k=n;
                    ll-=k;
                    if(k<1)
                        System.out.println(k);
                    ByteBuffer buffer2 = ByteBuffer.allocate(k);
                    for(int j=i ; j<i+k && j<read; j++){
                        buffer2.put(bufferRead[j]);
                    }
                    String str = Base64.getEncoder().encodeToString(buffer2.array());
                    String bits = stringToBits.get(str);
                    buffer += bits;
                    int end=(buffer.length()/8)*8;
                    if(end==0)continue;
                    String in = buffer.substring(0,end);
                    buffer = buffer.substring(end);
                    byte[] b = binaryStringToBytes(in);
                    bof.write(b);
                }
            }
            if(buffer.length()!=0) {
                byte[] b = binaryStringToBytes2(buffer);
                bof.write(b);
            }
            bof.close();
            System.out.println("Successfully"
                    + " Compressed");
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
    public static void handleCompression(String fileName ,int n) {
        convertToBytes(fileName,n);
        node root = Huffman();
        getHashes(root);
        compressFile(fileName,n);
    }
}
