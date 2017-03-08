import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Encrypt {

	/********************************************
    ***************** Encrypt *******************
    *********************************************/

	    // encrypts message into image
    public BufferedImage encryptImage (BufferedImage img, LinkedList<Integer> message) {
        int height = img.getHeight();
        int width = img.getWidth();
        int bit = 7;

        // encrypt message into image pixel by pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgbInt = img.getRGB(x, y);
                int[] rgbBytes = int_to_rgbArray(rgbInt);
                
                // compare RBG values to message bits, change value to even or odd
                for (int ndx = 0; ndx < 3; ndx++) {
                    int mByte;
                    try {
                        mByte = message.getFirst();
                    }
                    catch (NoSuchElementException e) {
                        rgbBytes[ndx] = 0x0;
                        rgbInt = rgbArray_to_int(rgbBytes);
                        img.setRGB(x, y, rgbInt);
                        return img;
                    }
                    
                    int rgbByte = rgbBytes[ndx];
                    
                    if (getBit(mByte, bit) == 1) {  // bit == 1, make it odd
                        if (rgbByte % 2 == 0) {
                            rgbByte += 1;
                        }
                    }
                    else {                      // bit == 0, make it even
                        if (rgbByte % 2 != 0) {
                            if (rgbByte == 255) {
                                rgbByte -= 2;
                            }
                            rgbByte += 1;
                        }
                    }
                    rgbBytes[ndx] = rgbByte;
                    
                    // remove message byte once we've read all the bits, reset bit count
                    if (bit == 0) {
                        message.removeFirst();
                        bit = 8;
                    }
                    bit--;
                }
                // convert rgbBytes to int
                rgbInt = rgbArray_to_int(rgbBytes);
                img.setRGB(x, y, rgbInt);
            }
        }
        return img;
    }

    /* Creates a new image file of img named fileName.
     * Image created is a png to preserve pixel information
     * without compression. 
     */
    public void createImage(BufferedImage img, String fileName) throws IOException{
        String[] temp = fileName.split("[.]");
        File outputfile = new File(temp[0] + "-steg.png");
        ImageIO.write(img, "png", outputfile);
    }

	/********************************************
    **************** Dencrypt *******************
    *********************************************/

    // decrypts message from image
    public String decrypt (BufferedImage img) {
        String message = "";
        
        BitSet bitArray = new BitSet(8);
        int bitIndex = 7;
        
        int height = img.getHeight();
        int width = img.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgbInt = img.getRGB(x, y);                
                int[] rgbBytes = int_to_rgbArray(rgbInt);
                
                for (int ndx = 0; ndx < 3; ndx++) {
                    int rgbByte = rgbBytes[ndx];
                    if (rgbByte % 2 != 0) {
                        bitArray.set(bitIndex, true);
                    }
                    
                    bitIndex--;
                    // add char to message, reset bit index and bitset
                    if (bitIndex < 0) {
                        String bitStr = new String(bitArray.toByteArray());
                        message += bitStr;
                        bitIndex = 7;
                        bitArray.clear();
                    }
                    
                    if (rgbByte == 0)
                        return message;
                }
            }
        }
        return message;
    }

    // creates a new file named fileName to output message to
    public void outputMessage(String message, String fileName) {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(message);
        } 
        catch (IOException e) {
            System.err.println(e);
            System.exit(-1);
        }
    }

	/********************************************
    ****************** Helper *******************
    *********************************************/

    // extracts rgb bytes from rgb int
    private int[] int_to_rgbArray(int i) {
        int alpha = (i >> 24) & 0xFF;
        int red =   (i >> 16) & 0xFF;
        int green = (i >>  8) & 0xFF;
        int blue =   i & 0xFF;
        
        return(new int[]{red, green, blue, alpha});
    }

    // combines rgb bytes into rgb int
    private int rgbArray_to_int(int[] rgbArray) {
        int value = 0;
        value ^= rgbArray[3] << 24; // alpha
        value ^= rgbArray[0] << 16; // red
        value ^= rgbArray[1] <<  8; // green
        value ^= rgbArray[2];       // blue
        
        return value;
    }

    // returns the bit at index
    private int getBit(int b, int index) {
        return ((b >> index) & 1);
    }
}
