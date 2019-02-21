package Sample;

import java.io.File;
import java.io.IOException;
import java.awt.AWTException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

import javax.swing.*;
import javax.imageio.*;

public class convertImg extends JFrame implements ActionListener{
	
	JButton m_btOpen, m_btSave, m_btConvert, m_btSub, m_btRGBtoU, m_btDct; 
	IMGPanel  m_panelImgInput, m_panelImgOutput;
	BufferedImage m_imgInput, m_imgOutput;
	//Create a file chooser
	final JFileChooser m_fc = new JFileChooser();
	
	//setup some GUI stuff
	public JPanel createContentPane (){	    
	    
		// We create a bottom JPanel to place everything on.
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(null);
	    
        m_panelImgInput = new IMGPanel();        
        m_panelImgInput.setLocation(10, 10);
        m_panelImgInput.setSize(400, 300);
	    totalGUI.add(m_panelImgInput);
	    
	    // create a panel for buttons
	    JPanel panelButtons = new JPanel();
	    panelButtons.setLayout(null);
	    panelButtons.setLocation(420, 50);
	    panelButtons.setSize(100, 400);
        totalGUI.add(panelButtons);
        
        m_panelImgOutput = new IMGPanel();
        m_panelImgOutput.setLocation(530, 10);
        m_panelImgOutput.setSize(400, 300);
        totalGUI.add(m_panelImgOutput);
	    
	    m_btOpen = new JButton("OPEN");
	    m_btOpen.setLocation(0, 0);
	    m_btOpen.setSize(100, 40);
	    m_btOpen.addActionListener(this);
	    panelButtons.add(m_btOpen);
	    
	    m_btSave = new JButton("SAVE");
	    m_btSave.setLocation(0, 60);
	    m_btSave.setSize(100, 40);
	    m_btSave.addActionListener(this);
	    panelButtons.add(m_btSave);
	    
	    m_btConvert = new JButton("RGB->Y");
	    m_btConvert.setLocation(0, 120);
	    m_btConvert.setSize(100, 40);
	    m_btConvert.addActionListener(this);
	    panelButtons.add(m_btConvert);
		
		m_btRGBtoU = new JButton("RGB->U");
		m_btRGBtoU.setLocation(0,180);
		m_btRGBtoU.setSize(100,40);
		m_btRGBtoU.addActionListener(this);
		panelButtons.add(m_btRGBtoU);
		
		m_btSub = new JButton("Chroma Sub-sampling U");
		m_btSub.setLocation(0,240);
		m_btSub.setSize(100,40);
		m_btSub.addActionListener(this);
		panelButtons.add(m_btSub);

		m_btDct = new JButton("Chroma Sub-sampling U");
		m_btDct.setLocation(0,280);
		m_btDct.setSize(100,40);
		m_btDct.addActionListener(this);
		panelButtons.add(m_btDct);

	    totalGUI.setOpaque(true);
	    return totalGUI;
	}
	
    // This is the new ActionPerformed Method.
    // It catches any events with an ActionListener attached.
    // Using an if statement, we can determine which button was pressed
    // and change the appropriate values in our GUI.
    public void actionPerformed(ActionEvent evnt) {
        // button OPEN is clicked
    	if(evnt.getSource() == m_btOpen){
        	m_fc.addChoosableFileFilter(new ImageFilter());
        	m_fc.setAcceptAllFileFilterUsed(false);
        	int returnVal = m_fc.showOpenDialog(convertImg.this);
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
                 File file = m_fc.getSelectedFile();
                 try {
                	 m_imgInput = ImageIO.read(file);
                     m_panelImgInput.setBufferedImage(m_imgInput);	
                 }catch (IOException ex) {
                	 //...
                 }
            }
        }
        // convert RGB to Y 
        else if(evnt.getSource() == m_btConvert){
        	if(m_imgInput == null)
        		return;
        	int w = m_imgInput.getWidth(null);
        	int h = m_imgInput.getHeight(null);
        	
        	// calculate Y values
			int YValues[] = new int[w*h];
			int UValues[] = new int[w*h];
			int VValues[] = new int[w*h];
        	int inputValues[] = new int[w*h];
        	PixelGrabber grabber = new PixelGrabber(m_imgInput.getSource(), 0, 0, w, h, inputValues, 0, w);
            try{
              if(grabber.grabPixels() != true){
                try{
            	  throw new AWTException("Grabber returned false: " + grabber.status());
            	}catch (Exception e) {};
              }
            } catch (InterruptedException e) {};
            
            int red,green, blue; 
            for (int index = 0; index < h * w; ++index){
            	red = ((inputValues[index] & 0x00ff0000) >> 16);
            	green =((inputValues[index] & 0x0000ff00) >> 8);
            	blue = ((inputValues[index] & 0x000000ff) );
				YValues[index] = (int)((0.299 * (float)red) + (0.587 * (float)green) + (0.114 * (float)blue));
				UValues[index] = (int)(0.492*((float)blue - YValues[index]));
				VValues[index] = (int)(0.877*((float)red - YValues[index]));
            }
        	
            // write Y values to the output image
            m_imgOutput = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        	WritableRaster raster = (WritableRaster) m_imgOutput.getData();
        	raster.setPixels(0, 0, w, h, YValues);
        	m_imgOutput.setData(raster);
        	m_panelImgOutput.setBufferedImage(m_imgOutput);	
		}

		// convert RGB to YUV with sub sampling
        else if(evnt.getSource() == m_btSub){
        	if(m_imgInput == null)
        		return;
        	int w = m_imgInput.getWidth(null);
			int h = m_imgInput.getHeight(null);
			
			if(w % 2 == 1){
				w = w - 1;
			}
			if(h % 2 == 1){
				h = h - 1;
			}
        	
        	// calculate Y values
			int YValues[] = new int[w*h];
			int UValues[] = new int[w*h];
			int VValues[] = new int[w*h];
			int SubSampledUValues[] = new int[w*h/4];
			int SubSampledVValues[] = new int[w*h/4];
        	int inputValues[] = new int[w*h];
        	PixelGrabber grabber = new PixelGrabber(m_imgInput.getSource(), 0, 0, w, h, inputValues, 0, w);
            try{
              if(grabber.grabPixels() != true){
                try{
            	  throw new AWTException("Grabber returned false: " + grabber.status());
            	}catch (Exception e) {};
              }
            } catch (InterruptedException e) {};
            
            int red,green, blue; 
            for (int index = 0; index < h * w; ++index){
            	red = ((inputValues[index] & 0x00ff0000) >> 16);
            	green =((inputValues[index] & 0x0000ff00) >> 8);
            	blue = ((inputValues[index] & 0x000000ff) );
				YValues[index] = (int)((0.299 * (float)red) + (0.587 * (float)green) + (0.114 * (float)blue));  
				UValues[index] = (int)(0.492*((float)blue - YValues[index]));
				VValues[index] = (int)(0.877*((float)red - YValues[index]));
			}
			for(int index = 0; index < h*w/4; ++index){
				int row = (index*2)/w;
				int subSampledindex = index * 2 + row * w;
				if(index < w*h/4){
					if(subSampledindex < w*h){
						SubSampledUValues[index] = UValues[subSampledindex];
						SubSampledVValues[index] = VValues[subSampledindex];
					}
				}
				
			}
        	
            // write Y values to the output image
            m_imgOutput = new BufferedImage(w/2,h/2, BufferedImage.TYPE_BYTE_GRAY);
        	WritableRaster raster = (WritableRaster) m_imgOutput.getData();
        	raster.setPixels(0, 0, w/2, h/2, SubSampledUValues);
        	m_imgOutput.setData(raster);
        	m_panelImgOutput.setBufferedImage(m_imgOutput);	
		}
		// convert RGB to U
        else if(evnt.getSource() == m_btRGBtoU){
        	if(m_imgInput == null)
        		return;
        	int w = m_imgInput.getWidth(null);
			int h = m_imgInput.getHeight(null);
			
			if(w % 2 == 1){
				w = w - 1;
			}
			if(h % 2 == 1){
				h = h - 1;
			}
        	
        	// calculate Y values
			int YValues[] = new int[w*h];
			int UValues[] = new int[w*h];
			int VValues[] = new int[w*h];
			int SubSampledUValues[] = new int[w*h/4];
			int SubSampledVValues[] = new int[w*h/4];
        	int inputValues[] = new int[w*h];
        	PixelGrabber grabber = new PixelGrabber(m_imgInput.getSource(), 0, 0, w, h, inputValues, 0, w);
            try{
              if(grabber.grabPixels() != true){
                try{
            	  throw new AWTException("Grabber returned false: " + grabber.status());
            	}catch (Exception e) {};
              }
            } catch (InterruptedException e) {};
            
            int red,green, blue; 
            for (int index = 0; index < h * w; ++index){
            	red = ((inputValues[index] & 0x00ff0000) >> 16);
            	green =((inputValues[index] & 0x0000ff00) >> 8);
            	blue = ((inputValues[index] & 0x000000ff) );
				YValues[index] = (int)((0.299 * (float)red) + (0.587 * (float)green) + (0.114 * (float)blue));  
				UValues[index] = (int)(0.492*((float)blue - YValues[index]));
				VValues[index] = (int)(0.877*((float)red - YValues[index]));
			}
			for(int index = 0; index < h*w/4; ++index){
				int row = (index*2)/w;
				int subSampledindex = index * 2 + row * w;
				if(index < w*h/4){
					if(subSampledindex < w*h){
						SubSampledUValues[index] = UValues[subSampledindex];
						SubSampledVValues[index] = VValues[subSampledindex];
					}
				}
				
			}
        	
            // write Y values to the output image
            m_imgOutput = new BufferedImage(w,h, BufferedImage.TYPE_BYTE_GRAY);
        	WritableRaster raster = (WritableRaster) m_imgOutput.getData();
        	raster.setPixels(0, 0, w, h, UValues);
        	m_imgOutput.setData(raster);
        	m_panelImgOutput.setBufferedImage(m_imgOutput);	
		}
		// perform DCT
		else if(evnt.getSource() == m_btDct){
        	if(m_imgInput == null)
        		return;
        	int w = m_imgInput.getWidth(null);
			int h = m_imgInput.getHeight(null);
			
			if(w % 8 != 0){
				w = w - w%8;
			}
			if(h % 8 != 0){
				h = h - h%8;
			}
        	
        	// calculate Y values
			int YValues[] = new int[w*h];
			int UValues[] = new int[w*h];
			int VValues[] = new int[w*h];
			int SubSampledUValues[] = new int[w*h/4];
			int SubSampledVValues[] = new int[w*h/4];
			int inputValues[] = new int[w*h];
			int dctArray[][] = new int[64][w*h/64];
        	PixelGrabber grabber = new PixelGrabber(m_imgInput.getSource(), 0, 0, w, h, inputValues, 0, w);
            try{
              if(grabber.grabPixels() != true){
                try{
            	  throw new AWTException("Grabber returned false: " + grabber.status());
            	}catch (Exception e) {};
              }
            } catch (InterruptedException e) {};
            
            int red,green, blue; 
            for (int index = 0; index < h * w; ++index){
            	red = ((inputValues[index] & 0x00ff0000) >> 16);
            	green =((inputValues[index] & 0x0000ff00) >> 8);
            	blue = ((inputValues[index] & 0x000000ff) );
				YValues[index] = (int)((0.299 * (float)red) + (0.587 * (float)green) + (0.114 * (float)blue));  
				UValues[index] = (int)(0.492*((float)blue - YValues[index]));
				VValues[index] = (int)(0.877*((float)red - YValues[index]));
			}
			for(int index = 0; index < h*w/4; ++index){
				int row = (index*2)/w;
				int subSampledindex = index * 2 + row * w;
				if(index < w*h/4){
					if(subSampledindex < w*h){
						SubSampledUValues[index] = UValues[subSampledindex];
						SubSampledVValues[index] = VValues[subSampledindex];
					}
				}
				
			}
			int temp2d[][] = new int[h][w];
			int dctMatrix[][][] = new int[8][8][h*w/64];
			for(int i = 0; i < h; i++){
				for(int j = 0; j < w; j++){
					temp2d[i][j] = YValues[j+i*w];
				}
			}
			

			for(int i = 0; i < h; i++ ){
				for(int j = 0; j < w; j++){
					dctMatrix[i%8][j%8][i/8 * w/8 + j/8] = temp2d[i][j];
				}
			}
			for(int i =0; i< 8; i++){
				for(int j =0; j <8; j++){
					System.out.print(dctMatrix[i][j][0]);
					System.out.print(","); 
				}
				System.out.println("");
			}
			
			for(int i =0; i< 8; i++){
				for(int j =0; j <8; j++){
					System.out.print(temp2d[i][j]);
				}
				System.out.println("");
			}

            // write Y values to the output image
            m_imgOutput = new BufferedImage(w,h, BufferedImage.TYPE_BYTE_GRAY);
        	WritableRaster raster = (WritableRaster) m_imgOutput.getData();
        	raster.setPixels(0, 0, w, h, UValues);
        	m_imgOutput.setData(raster);
        	m_panelImgOutput.setBufferedImage(m_imgOutput);	
		}
        // button SAVE is clicked
        else if(evnt.getSource() == m_btSave){
        	if(m_imgOutput == null)
        		return;
        	m_fc.addChoosableFileFilter(new ImageFilter());
        	m_fc.setAcceptAllFileFilterUsed(false);
        	int returnVal = m_fc.showSaveDialog(convertImg.this);
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
        		File file = m_fc.getSelectedFile();	
        		try {
            	    ImageIO.write(m_imgOutput, "jpg", file);
            	} catch (IOException e) {
            		//...
            	}
        	}
        }
    }
	
    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Convert Image");

        //Create and set up the content pane.
        convertImg demo = new convertImg();
        frame.setContentPane(demo.createContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(940, 360);
        frame.setVisible(true);
    }
    
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
