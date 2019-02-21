package Sample;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class ImageFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {
		if (arg0.isDirectory()) {
            return true;
        }
 
        String extension = Utils.getExtension(arg0);
        if (extension != null) {
            if (extension.equals(Utils.tiff) ||
                extension.equals(Utils.tif) ||
                extension.equals(Utils.gif) ||
                extension.equals(Utils.jpeg) ||
                extension.equals(Utils.jpg) ||
                extension.equals(Utils.png)) {
                    return true;
            } else {
                return false;
            }
        }
        return false;
	}

	@Override
	public String getDescription() {
		return "Just Images";	
	}
}