import java.nio.ByteBuffer;

class node{
    public String value;
    public int freq;
    public node left;
    public node right;

    public node(String value,int freq, node left, node right) {
        this.value = value;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }
}
