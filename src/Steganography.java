import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Steganography {

    public static void main(String[] args) throws IOException, FileNotFoundException {
        if (args.length != 3) {
            System.err.println("Usage: Sternography [-option] [image-file] [text-file]");
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
                System.err.println("Error: " + args[0]);
                return;
        }
        
        String imgName = args[1];
        String fileName = args[2];

        Steganography me = new Steganography();
        Encrypt encoder = new Encrypt();
        
        // get image
        BufferedImage img = null;
        img = ImageIO.read(new File(imgName));

        // write boolean decides whether encrypting or decrypting
        if (write) {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            LinkedList<Integer> message = me.fileToIntArray(br);
            
            img = encoder.encryptImage(img, message);
            encoder.createImage(img, imgName);
        }
        else  { 
            String dMessage = encoder.decrypt(img);
            encoder.outputMessage(dMessage, fileName);
        }

        System.out.println("Name:\t" + imgName + "\n" 
                + "Height:\t" +img.getHeight() + "\n" 
                + "Width:\t" + img.getWidth() + "\n"
                + "Pixels:\t" + img.getHeight() * img.getWidth() + "\n");
    }

    // convert message to bytes
    public LinkedList<Integer> fileToIntArray(BufferedReader br) throws IOException {
        LinkedList<Integer> message = new LinkedList<>();
        for(String line; (line = br.readLine()) != null; )
            for (Byte b : line.getBytes())
                message.add((int)b);

        return message;
    }
}
