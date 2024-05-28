package rs.sbnz.service.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {
    public void saveImage(String imageBase64, String name) throws IOException {
        System.out.println("Saving image to " + ("./images/article/" + name + ".jpg"));
        String[] parts = imageBase64.split(",");
        byte[] bytes = Base64.getDecoder().decode(parts[parts.length - 1]);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        File file = new File("./images/article/" + name + ".jpg");
        ImageIO.write(image, "jpg", file);
    }
}
