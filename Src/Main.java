package usc.csci576.assignment2.longchen;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here

        String fileName = args[0];
        int numCoefficient = Integer.parseInt(args[1]);
        numCoefficient = -1;

        if(numCoefficient > 0)
        {
            //512*512 = 262144
            //512*128 = 131072
            //128*128 = 16384
            //numCoefficient = 262144;

            //DCT
            imageReader _dctImage = new imageReader(new File(fileName), 512, 512);
            _dctImage.ImageSetDCT_Color((int)(numCoefficient/4096));
            _dctImage.DisPlayPic("DCT: " + numCoefficient, 100, 100);

            //DWT
            imageReader _dwtImageDWT = new imageReader(new File(fileName), 512, 512);
            _dwtImageDWT.ImageDWT_Color(numCoefficient);
            _dwtImageDWT.DisPlayPic("DWT: " + numCoefficient, 800, 100);

        }
        else
        {
            //Create the animation
            ArrayList<imageReader> arrayDctImageReader = new ArrayList<imageReader>();
            ArrayList<imageReader> arrayDwtImageReader = new ArrayList<imageReader>();

            //Create all 64 frame of picture
            for(int i=1;i<=64;i++)
            {
                System.out.println("==> Current Deal With: " + i +  " Frame" );
                imageReader _tempDCT = new imageReader(new File(fileName), 512, 512);
                _tempDCT.ImageSetDCT_Color((int)(i));
                arrayDctImageReader.add(_tempDCT);

                imageReader _tempDWT = new imageReader(new File(fileName), 512, 512);
                _tempDWT.ImageDWT_Color(i*4096);
                arrayDwtImageReader.add(_tempDWT);
            }

            long recordTime = System.nanoTime();
            int frameNum = 0;
            JFrame DctFrame = new JFrame();
            JFrame DwtFrame = new JFrame();


            while(frameNum<64)
            {
                //30 Frame every second
                while (System.nanoTime() - recordTime - 2*1000000000.0D < 0.0D)
                {
                    try
                    {
                        Thread.sleep(5L);
                    }
                    catch (InterruptedException localInterruptedException2) {
                        Thread.currentThread().interrupt();
                    }
                }

                imageReader.DisPlayPicByFrame(DctFrame, arrayDctImageReader.get(frameNum).img, "DCT: Animation: " + (1+frameNum)*4096,  100, 100);

                imageReader.DisPlayPicByFrame(DwtFrame, arrayDwtImageReader.get(frameNum).img, "DWT: Animation: " + (1+frameNum)*4096,  800, 100);

                frameNum++;
                recordTime = System.nanoTime();
            }
        }
    }

}
