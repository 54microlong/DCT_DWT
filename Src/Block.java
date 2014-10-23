package usc.csci576.assignment2.longchen;

/**
 * Created by chenlong on 10/16/14.
 */
public class Block {
    public static final int N = 8;  // number of Block
    public double[][] block;  //data of block

    Block(){
        this.block = new double[8][8];
    }

    Block(double[][] _block){
        this.block = _block;
    }

    Block(double[][] _graph, int _x, int _y){
        this.block = new double[8][8];
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++){
                this.block[i][j] = _graph[_x+i][_y+j];
            }
    }

    Block(byte[][] _graph, int _x, int _y){
        this.block = new double[8][8];
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++){
                //byte value is assigned to 8 bytes double and is type casted implicitly
                this.block[i][j] = _graph[_x+i][_y+j];
            }
    }

    public void Clear(){
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                this.block[i][j] = 0;
            }
        }
    }

    //Forward DCT
    public double[][] FDCT()
    {
        double[][] uvResult = new double[8][8];
        double _cu, _cv, _sum = 0;

        for(int u=0; u<N; u++)
        {
            _cu = (u==0) ? Math.sqrt(1.0/this.N) : Math.sqrt(2.0/this.N);
            for(int v=0; v<N; v++)
            {
                _cv= (v==0) ? Math.sqrt(1.0/this.N) : Math.sqrt(2.0/this.N);
                _sum = 0;
                for(int x=0;x<N;x++) {
                    for (int y = 0; y < N; y++) {
                        _sum += this.block[x][y] * Math.cos((2 * x + 1) * u * Math.PI / (2 * N))
                                * Math.cos((2 * y + 1) * v * Math.PI / (2 * N));
                    } // for y
                } // for x
                uvResult[u][v] = _cu * _cv * _sum;
            } // for v
        } // for u

    this.block = uvResult;
    return uvResult;
    }

    //Inverse DCT
    public double[][] IDCT(double [][] _dcted)
    {
        double[][] xyResult = new double[8][8];
        double _cu, _cv, _sum = 0;

        for(int x=0; x<N; x++)
        {
            for(int y=0; y<N; y++)
            {
                _sum = 0;
                for(int u=0; u<N; u++){
                    _cu = (u==0) ? Math.sqrt(1.0/2.0) : 1;
                    for(int v=0; v<N; v++){
                        _cv = (v==0) ? Math.sqrt(1.0/2.0) : 1;
                        _sum += _cu * _cv * _dcted[u][v] * Math.cos((2 * x + 1) * u * Math.PI / (2 * N))
                                 * Math.cos((2 * y + 1) * v * Math.PI / (2 * N));
                    } // for v
                }// for u
                xyResult[x][y] = _sum/4;
            } // for y

        } // for x

        return xyResult;
    }

    //Inverse DCT and modify this.block
    public double[][] IDCT()
    {
        double[][] xyResult = new double[8][8];
        double _cu, _cv, _sum = 0;

        for(int x=0; x<N; x++)
        {
            for(int y=0; y<N; y++)
            {
                _sum = 0;
                for(int u=0; u<N; u++){
                    _cu = (u==0) ? Math.sqrt(1.0/2.0) : 1;
                    for(int v=0; v<N; v++){
                        _cv = (v==0) ? Math.sqrt(1.0/2.0) : 1;
                        _sum += _cu * _cv * this.block[u][v] * Math.cos((2 * x + 1) * u * Math.PI / (2 * N))
                                * Math.cos((2 * y + 1) * v * Math.PI / (2 * N));
                    } // for v
                }// for u
                xyResult[x][y] = _sum/4;
            } // for y

        } // for x

        this.block = xyResult;
        return xyResult;
    }


    //Create Zigzag Matrix
    public double[][] ZigZag(int _N, int _m)
    {
        int x = 0;
        int y = 0;
        int _xNext = 0;
        int _yNext = 0;

        double[][] result = new double[_N][_N];
        result[0][0] = this.block[0][0];
        while(x < _N && y < _N && _m>0)
        {
            //Left up part of the matrix
            if(x + y < _N && x < _N && y < _N)
            {
                if(y==0 && x < (_N-1)){
                    x+=1;
                    //System.out.print(this.block[x][y] + "|");
                    result[x][y] = this.block[x][y];
                    _m -= 1;
                    _xNext = -1;
                    _yNext = 1;
                }
                else if(x == 0 && y < (_N-1)){
                    y+=1;
                    //System.out.print(this.block[x][y] + "|");
                    result[x][y] = this.block[x][y];
                    _m -= 1;
                    _xNext = 1;
                    _yNext = -1;
                }
                else if(x==_N-1 && y==0) {
                    x += 1;
                }
                else if(y ==_N-1 && x==0) {
                    y += 1;
                }
            }
            //Right Down part of the matrix
            if(x+y >=8)
            {
                if(x==_N)
                    x=x-1;
                else if(y==_N)
                    y=y-1;

                if(y== (_N-1)){
                    x+=1;
                    //System.out.print(this.block[x][y] + "|");
                    result[x][y] = this.block[x][y];
                    _m -= 1;
                    _xNext = 1;
                    _yNext = -1;
                }
                else if(x == (_N-1)) {
                    y += 1;
                    //System.out.print(this.block[x][y] + "|");
                    result[x][y] = this.block[x][y];
                    _m -= 1;
                    _xNext = -1;
                    _yNext = 1;
                }
            }
            x += _xNext;
            y += _yNext;
            if(x<_N && y<_N) {
                //System.out.print(this.block[x][y] + "|");
                result[x][y] = this.block[x][y];
                _m -= 1;
            }
        }
        this.block = result;
        return result;
    }

    //Print myself for test
    public void PrintMe(String _title)
    {
        System.out.println(_title);
        for(int i=0;i<Block.N;i++)
        {
            System.out.println();
            for (int j = 0; j < Block.N; j++)
            {
                System.out.print(this.block[i][j] + "  ");
            }
        }
    }


    public static void main(String[] args) {

        // Test for Block
        /*
        Pixel values
        49  61  69  61  78 89 100 112
        68  60  51  42  62 69 80  89
        90  81  58  49  69 72 68  69
        100 91  79  72  69 68 59  58
        111 100 101 91  82 71 59  49
        131 119 120 102 90 90 81  59
        148 140 129 99  92 78 59  39
        151 140 142 119 98 90 72  39

        Forward DCT

        672 101 13 21 -13 0 6 9
        -104 -144 35 -5 9 -17 -3 0
         31 -21 -1 -7 -9 0 0 4
         22 -21 -9 -13 -2 3 2 2
         9 -2 -17 -11 0 5 1 -2
         4 6 1 0 -2 -7 0 -1
         21 -12 -3 2 -4 3 0 -1
         -5 8 -3 2 1 -6 0 2
        */

        double[][] _input = { {49, 61, 69, 61, 78, 89, 100, 112}
                            , {68, 60, 51, 42, 62, 69, 80, 89}
                            , {90, 81, 58, 49, 69, 72, 68, 69}
                            , {100, 91, 79, 72, 69, 68, 59, 58}
                            , {111, 100, 101, 91, 82, 71, 59, 49}
                            , {131, 119, 120, 102, 90, 90, 81, 59}
                            , {148, 140, 129, 99, 92, 78, 59, 39}
                            , {151, 140, 142, 119, 98, 90, 72, 39} };
        //,()

        double[][] _output = { {672, 101, 13, 21, -13, 0, 6, 9}
                            , {-104, -144, 35, -5, 9, -17, -3, 0}
                            , {31, -21, -1, -7, -9, 0, 0, 4}
                            , {22, -21, -9, -13, -2, 3, 2, 2}
                            , {9, -2, -17, -11, 0, 5, 1, -2}
                            , {4, 6, 1, 0, -2, -7, 0, -1}
                            , {21, -12, -3, 2, -4, 3, 0, -1}
                            , {-5, 8, -3, 2, 1, -6, 0, 2} };

        Block _blockTest = new Block(_input);
        double[][] _dctResult = _blockTest.FDCT();
        for(int i=0;i<8;i++) {
            //System.out.println("");
            //System.out.print(_result[i][j] + " ");
            for (int j = 0; j < 8; j++)
                if ((_dctResult[i][j] - _output[i][j]) > 1) {
                    System.err.println("==> [Failed!] DCT Test Failed!");
                    System.err.flush();
                    return;
                }
        }
        //System.err.println("==> [Passed!] Test Passed");



        /*---------------------- Test For IDCT  -----------------------*/
//        double[][] _idctResult =  _blockTest.IDCT(_output);
//        for(int i=0;i<8;i++) {
//            System.out.println("");
//            for (int j = 0; j < 8; j++) {
//                System.out.print(_idctResult[i][j] + " ");
//            }
//        }


        /*-------------Test for ZigZag--------------------*/
        double[][] tem = _blockTest.ZigZag( 8, 14-1);
        for(int i=0;i<8;i++){
            System.out.println();
            for(int j=0;j<8;j++){
                System.out.print(tem[i][j] + " ");
            }
        }
    }

}
