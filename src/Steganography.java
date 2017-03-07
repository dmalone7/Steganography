/**
 *
 * @authors David, Raunak
 */

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Steganography {

    /********************************************
    ******************* Main ********************
    *********************************************/

    public static void main(String[] args) throws IOException, FileNotFoundException {
        if (args.length != 3) {
            System.err.println("Args uh oh.");
            return;
        }
        
        // assign encrypt or decrypt
        boolean write = false;
        switch(args[0]){
            case "-E":
                write = true;
            case "-D":
                break;
            default:
                System.err.println("Tag uh oh.");
                return;
        }
        
        String imgName = args[1];
        String fileName = args[2];
        
        // get image
        BufferedImage img = null;
        img = ImageIO.read(new File(imgName));
        
        // get message
        if (write) {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
        
            // convert message to bytes
            LinkedList<Integer> message = new LinkedList<>();
            for(String line; (line = br.readLine()) != null; )
                for (Byte b : line.getBytes())
                    message.add((int)b);
            
            img = encryptImage(img, message);
            createImage(img, imgName);
        }
        // write decrypted message to output file
        else  { 
            String dMessage = decrypt(img);
            outputMessage(dMessage, fileName);
        }

        System.out.println("Name:\t" + imgName + "\n" 
                + "Height:\t" +img.getHeight() + "\n" 
                + "Width:\t" + img.getWidth() + "\n"
                + "Pixels:\t" + img.getHeight() * img.getWidth() + "\n");
    }

    /********************************************
    ***************** Functions *****************
    *********************************************/
    
    // creates a new file named fileName to output message to
    static private void outputMessage(String message, String fileName) {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(message);
        } 
        catch (IOException e) {
            System.err.println("Write uh oh.");
            System.exit(-1);
        }
    }
    
    // creates a new image file of img named fileName
    static private void createImage(BufferedImage img, String fileName) throws IOException{
        String[] temp = fileName.split("[.]");
        File outputfile = new File(temp[0] + "-steg.png");
        ImageIO.write(img, "png", outputfile);
    }

    // encrypts message into image
    static private BufferedImage encryptImage (BufferedImage img, LinkedList<Integer> message) {
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

    // decrypts message from image
    static private String decrypt (BufferedImage img) {
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

    // extracts rgb bytes from rgb int
    static private int[] int_to_rgbArray(int i) {
        int alpha = (i >> 24) & 0xFF;
        int red =   (i >> 16) & 0xFF;
        int green = (i >>  8) & 0xFF;
        int blue =   i & 0xFF;
        
        return(new int[]{red, green, blue, alpha});
    }

    // combines rgb bytes into rgb int
    static private int rgbArray_to_int(int[] rgbArray) {
        int value = 0;
        value ^= rgbArray[3] << 24; // alpha
        value ^= rgbArray[0] << 16; // red
        value ^= rgbArray[1] <<  8; // green
        value ^= rgbArray[2];       // blue
        
        return value;
    }

    // returns the bit at index
    public static int getBit(int b, int index) {
        return ((b >> index) & 1);
    }
}