package usc.csci576.assignment2.longchen;

/**
 * Created by chenlong on 10/16/14.
 */
public class DCT {



    //split the graph into 8*8 blocks.
    public Block[][] SplitChanneal(byte[][] _graph, int _y, int _x)
    {
        if((_x % 8 != 0) || (_y % 8 != 0)){
            System.err.println("[ERROR] Input data should begins on a multiple of 8!");
            return null;
        }

        // number of blocks
        int _yBlock = _y/8;
        int _xBlock = _x/8;

        Block[][] _blocks = new Block[_yBlock][_xBlock];

        for(int i=0; i<_y; i+=8)
            for (int j = 0; j < _x; j+=8) {
                _blocks[i/8][j/8] = new Block(_graph, i, j);
            }

        return _blocks;
    }

    //Get front m block zig matrix.
    public Block[][] ZigZag(Block[][] _graph, int _N, int _m)
    {
        int x = 0;
        int y = 0;
        int _xNext = 0;
        int _yNext = 0;

        //Initialize the block of pictures.
        Block[][] result = new Block[_N][_N];
        for(int i=0;i<_N;i++){
            for(int j=0;j<_N;j++){
                Block _tem = new Block();
                _tem.Clear();
            }
        }

        result[0][0] = _graph[0][0];

        while(x < _N && y < _N && _m>0)
        {
            //Left up part of the matrix
            if(x + y < 8 && x < _N && y < _N)
            {
                if(y==0 && x < (_N-1)){
                    x+=1;
                    //System.out.print(_graph[x][y] + "|");
                    result[x][y] = _graph[x][y];
                    _m -= 1;
                    _xNext = -1;
                    _yNext = 1;
                }
                else if(x == 0 && y < (_N-1)){
                    y+=1;
                    //System.out.print(_graph[x][y] + "|");
                    result[x][y] = _graph[x][y];
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
                    //System.out.print(_graph[x][y] + "|");
                    result[x][y] = _graph[x][y];
                    _m -= 1;
                    _xNext = 1;
                    _yNext = -1;
                }
                else if(x == (_N-1)){
                    y+=1;
                    //System.out.print(_graph[x][y] + "|");
                    result[x][y] = _graph[x][y];
                    result[x][y] = _graph[x][y];
                    _m -= 1;
                    _xNext = -1;
                    _yNext = 1;
                }
            }
            x += _xNext;
            y += _yNext;
            if(x<_N && y<_N) {
                //System.out.print(_graph[x][y] + "|");
                result[x][y] = _graph[x][y];
                _m -= 1;
            }
        }

        return result;
    }


    public static void main(String[] args)
    {

    }
}
