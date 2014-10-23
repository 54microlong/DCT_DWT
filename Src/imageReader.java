package usc.csci576.assignment2.longchen;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;


public class imageReader {

    public int width = 512;
    public int height = 512;
    public BufferedImage img = null;
    public byte[][] bytes = null;


    public imageReader(File _imageFile, int _width, int _height)
    {
        this.img = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
        this.width = _width;
        this.height = _height;

        //Each pic contain 3 channels of R, G, B.
        this.bytes = new byte[this.height * 3][this.width];
        byte[] _bytes = null;


        try {

            InputStream is = new FileInputStream(_imageFile);

            long len = _imageFile.length();
            _bytes = new byte[(int)len];

            int offset = 0;
            int numRead = 0;
            while (offset < _bytes.length && (numRead=is.read(_bytes, offset, _bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Converter the 1-dimension array into 2-dimension bytes.
        for(int i=0; i<_bytes.length;i++){
            this.bytes[i/this.width][i%this.width] = _bytes[i];
        }
    }

    public void ImageSetNormalColor() {

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                byte r = this.bytes[y][x];
                byte g = this.bytes[y + this.height][x];
                byte b = this.bytes[y + this.height * 2][x];

                int pix = ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                img.setRGB(x, y, pix);
            }
        }
    }

    public void DisPlayPic(String _title, int _x, int _y)
    {
        JFrame frame = new JFrame();
        frame.setTitle(_title);
        frame.setLocation(_x, _y);
        JLabel label = new JLabel(new ImageIcon(img));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    //Using to display animation
    public static void DisPlayPicByFrame(JFrame _frame, BufferedImage _img, String _title, int _x, int _y)
    {
        //JFrame frame = new JFrame();
        _frame.setTitle(_title);
        _frame.setLocation(_x, _y);
        JLabel label = new JLabel(new ImageIcon(_img));
        _frame.getContentPane().removeAll();
        _frame.getContentPane().add(label, BorderLayout.CENTER);
        _frame.pack();
        _frame.setVisible(true);
    }

    public void DisplayPic(ArrayList<Block[][]> _rgbBlock, BufferedImage _buffImage)
    {
        for(int y = 0, ylocation=0; y < this.height/8; y++, ylocation +=8){
            for(int x = 0, xlocation=0; x < this.width/8; x++, xlocation +=8)
            {
                DisplayBlock(_rgbBlock.get(0)[x][y],_rgbBlock.get(1)[x][y],_rgbBlock.get(2)[x][y], _buffImage, ylocation, xlocation);
            }
        }
    }

    public void DisplayBlock(Block _r, Block _g, Block _b, BufferedImage _buffImage, int _y, int _x)
    {
        for(int i=0;i< Block.N;i++){
            for(int j=0;j<Block.N;j++)
            {
                int pix = (((int)(_r.block[i][j]) & 0xff) << 16) |
                        (((int)_g.block[i][j] & 0xff) << 8) | ((int)_b.block[i][j] & 0xff);
                _buffImage.setRGB(j+_x,i+_y, pix);
            }
        }

    }

    //Transfer to DCT and use front _numCoeffcient coefficient to inverse DCT
    public void ImageSetDCT_Color(int _numCoeffcient) {
        //1@ Split the picture to  8*8 blocks
        DCT _dct = new DCT();

        Block[][] picBlock = _dct.SplitChanneal(this.bytes, 3*this.height, this.width);

        //Traverse the blocks matrix.
        for(int i=0;i<this.height/Block.N;i++)
        {
            for(int j=0;j<this.width/Block.N;j++)
            {
                // R channel information
                picBlock[i][j].FDCT();
                picBlock[i][j].ZigZag(Block.N, _numCoeffcient);
                picBlock[i][j].IDCT();

                // G channel information
                picBlock[i+this.height/Block.N][j].FDCT();
                picBlock[i+this.height/Block.N][j].ZigZag(Block.N, _numCoeffcient);
                picBlock[i+this.height/Block.N][j].IDCT();

                // B channel information
                picBlock[i+2*this.height/Block.N][j].FDCT();
                picBlock[i+2*this.height/Block.N][j].ZigZag(Block.N, _numCoeffcient);
                picBlock[i+2*this.height/Block.N][j].IDCT();

                DisplayBlock(picBlock[i][j], picBlock[i+this.height/Block.N][j], picBlock[i+2*this.height/Block.N][j],
                                this.img, i*8, j*8);
            }
        }
        //Select the first m coefficients in a zig order for each 8x8 block.
    }



    //Split this.byte into 3 parts of R, G and B.
    public void ImageDWT_Color(int _numCoefficient)
    {
        // for color R
        double[][] _r = ImageSetDWT_Byte(_numCoefficient, 0, 0);
        // for color G
        double[][] _g = ImageSetDWT_Byte(_numCoefficient, this.height, 0);
        // for color B
        double[][] _b = ImageSetDWT_Byte(_numCoefficient, this.height*2, 0);

        for(int i=0;i< height;i++){
            for(int j=0;j<width;j++)
            {
                int pix = (((int)(_r[i][j]) & 0xff) << 16) |
                        (((int)_g[i][j] & 0xff) << 8) | ((int)_b[i][j] & 0xff);
                this.img.setRGB(j,i, pix);
            }
        }
    }

    //Transfer to DWT and use front _numCoeffcient coefficient to inverse DCT
    public double[][] ImageSetDWT_Byte(int _numCoeffcient, int _y, int _x)
    {

        //transfer This byte to arraylist
        double[][] _doubleByte = new double[this.height][this.width];
        for(int j=0;j<this.height;j++) {
            for (int i = _x; i < this.width; i++) {
                _doubleByte[j][i] = this.bytes[j+_y][i];
            }
        }

        DWT _dwt = new DWT();

        ArrayList<Double> _zigzag = _dwt.DwtZigzag(_doubleByte, this.height, 0, 0, 512*512);

        ArrayList<Double> _fdwt = _dwt.FDWT(_zigzag);

        //Fill other space with 0 to match the origin size of picture.
        for(int i=_numCoeffcient; i<_fdwt.size();i++)
        {
            _fdwt.set(i,0.0);
        }

        //IDWT transfer
        ArrayList<Double> _idwt =_dwt.IDWT(_fdwt);

        return _dwt.IDwtZigzag(_idwt, 512, 0);

    }

    public static void main(String[] args)
    {

        String fileName = args[0];
        int numCoefficient = Integer.parseInt(args[1]);

//        /*---------------Test DCT----------------*/
//        imageReader _image = new imageReader(new File(fileName), 512, 512);
//        _image.ImageSetNormalColor();
//        _image.DisPlayPic();
//        _image.ImageSetDCT_Color(16);
//        _image.DisPlayPic();
//        /*-------------End Test DCT----------------*/

        imageReader _imageDWT = new imageReader(new File(fileName), 512, 512);
//        _imageDWT.ImageSetNormalColor();
//        _imageDWT.DisPlayPic();

        //512*512 = 262144
        //512*128 = 131072
        //128*128 = 16384
        _imageDWT.ImageDWT_Color(16384);
        //_imageDWT.ImageSetNormalColor();
        _imageDWT.DisPlayPic("DWT", 100, 200);

    }
  
//   public static void main(String[] args) {
//
//
//	String fileName = args[0];
//    int numCoefficient = Integer.parseInt(args[1]);
//
//   	int width = 512;
//	int height = 512;
//
//    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//    byte[] bytes = null;
//
//    double[][] rChannel=new double[height][width];
//    double[][] gChannel=new double[height][width];
//    double[][] bChannel=new double[height][width];
//
//    try {
//	    File file = new File(args[0]);
//	    InputStream is = new FileInputStream(file);
//
//	    long len = file.length();
//	    bytes = new byte[(int)len];
//
//	    int offset = 0;
//        int numRead = 0;
//        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
//            offset += numRead;
//        }
//    } catch (FileNotFoundException e) {
//        e.printStackTrace();
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//
//    int ind = 0;
//	for(int y = 0; y < height; y++){
//
//		for(int x = 0; x < width; x++){
//
//			byte a = 0;
//			byte r = bytes[ind];
//			byte g = bytes[ind+height*width];
//			byte b = bytes[ind+height*width*2];
//
//			//int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
//			int pix = ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
//
//			//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
//            rChannel[x/8][x%8] = r;
//            gChannel[x/8][x%8] = g;
//            bChannel[y/8][x%8] = b;
//
//            img.setRGB(x,y,pix);
//	        ind++;
//			}
//		}
//
//    //split the picture into channels
//    DCT _dct = new DCT();
//    ArrayList<Block[][]> _blocks = new ArrayList<Block[][]>();
//    Block[][] _rBlock =  _dct.SplitChanneal(rChannel, width, height);
//    Block[][] _gBlock = _dct.SplitChanneal(rChannel, width, height);
//    Block[][] _bBlock = _dct.SplitChanneal(rChannel, width, height);
//
//    _blocks.add(_rBlock);
//    _blocks.add(_gBlock);
//    _blocks.add(_bBlock);
//
//    for(int k=0;k<3;k++){
//        //Traverse the _tem to DCT every block.
//        for(int i=0;i<height/8;i++){
//            for(int j=0;j<width/8;j++){
//                _blocks.get(k)[i][j].FDCT();
//
//                //select the first m coefficients in a zig order for each 8x8 block
//                _blocks.get(k)[i][j].block = _blocks.get(k)[i][j].ZigZag(8, numCoefficient);
//                _blocks.get(k)[i][j].IDCT();
//            }
//        }
//    }
//
//
//
//
//
//
//
//
//
//       // Use a label to display the image
//    JFrame frame = new JFrame();
//    JLabel label = new JLabel(new ImageIcon(img));
//    frame.getContentPane().add(label, BorderLayout.CENTER);
//    frame.pack();
//    frame.setVisible(true);
//
//   }
  
}
