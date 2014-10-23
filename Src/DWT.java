package usc.csci576.assignment2.longchen;

import java.util.ArrayList;

/**
 * Created by chenlong on 10/21/14.
 */
public class DWT {

    public DWT() {
    }

    // give in an 2-dimension array
    // return an 1-dimension with zigzag
    public ArrayList<Double> DwtZigzag(double[][] _pic, int _N, int _x, int _y, int _numCofficient) {
        //if _numCofficient is bigger than the whole picture, output the whole picture.
        if (_numCofficient > _N * _N) {
            _numCofficient = _N * _N;
        }

        if (_numCofficient <= 0) {
            ArrayList<Double> _tem = new ArrayList<Double>(); //_tem is empty
            return _tem;
        } else if (_N == 1) {
            _numCofficient -= 1;
            ArrayList<Double> _tem = new ArrayList<Double>();
            _tem.add(new Double(_pic[_x][_y]));
            return _tem;
        } else {
            // split array to 4 parts
            /*_______
            * |LL|LH|
            * |--|--|
            * |HL|HH|
            * |--|--|
            */
            ArrayList<Double> _result = new ArrayList<Double>();
            _result = DwtZigzag(_pic, _N / 2, _x, _y, _numCofficient);
            _numCofficient -= (_N / 2) * (_N / 2);
            _result.addAll(DwtZigzag(_pic, _N / 2, _x + _N / 2, _y, _numCofficient));
            _numCofficient -= (_N / 2) * (_N / 2);
            _result.addAll(DwtZigzag(_pic, _N / 2, _x, _y + _N / 2, _numCofficient));
            _numCofficient -= (_N / 2) * (_N / 2);
            _result.addAll(DwtZigzag(_pic, _N / 2, _x + _N / 2, _y + _N / 2, _numCofficient));
            _numCofficient -= (_N / 2) * (_N / 2);

            return _result;
        }
    }


    public double[][] IDwtZigzag(ArrayList<Double> _zigzag, int _N, int _y)
    {
        if(_y>_zigzag.size())
        {
            _y = _zigzag.size();
        }
        if (_N == 2) {
            double[][] _temp = new double[_N][_N];
            _temp[0][0] = _zigzag.get(_y );
            _temp[1][0] = _zigzag.get(_y +1);
            _temp[0][1] = _zigzag.get(_y+2);
            _temp[1][1] = _zigzag.get(_y+3);
            return _temp;
        }
        else {
            // split array to 4 parts
            /*_______
            * |LL|LH|
            * |--|--|
            * |HL|HH|
            * |--|--|
            */
            double[][] _temp = new double[_N][_N];
            double[][] _ll = IDwtZigzag(_zigzag, _N / 2, _y);
            double[][] _lh = IDwtZigzag(_zigzag, _N / 2, _y+(_N*_N/4));
            double[][] _hl = IDwtZigzag(_zigzag, _N / 2, _y+2*(_N*_N/4));
            double[][] _hh = IDwtZigzag(_zigzag, _N / 2, _y+3*(_N*_N/4));

            //Merge all this four parts into one matrix

            //Merge LL
            if (_ll != null) {
                for (int j = 0; j < _N / 2; j++) {
                    for (int i = 0; i < _N / 2; i++) {
                        _temp[j][i] = _ll[j][i];
                    }
                }
            }


            //Merge LH
            if (_hl != null) {
                for (int j = 0; j < _N / 2; j++) {
                    for (int i = 0; i < _N/2; i++) {
                        _temp[j+_N/2][i] = _lh[j][i];
                    }
                }
            }

            //Merge HL
            if (_hl != null) {
                for (int j = 0; j < _N/2; j++) {
                    for (int i = 0; i < _N / 2; i++) {
                        _temp[j][i+_N/2] = _hl[j][i];
                    }
                }
            }



            //Merge HH
            if (_hh != null) {
                for (int j = 0; j < _N/2; j++) {
                    for (int i = 0; i < _N/2; i++) {
                        _temp[j+_N/2][i+_N/2] = _hh[j][i];
                    }
                }
            }

            return _temp;
        }
    }

    //Encode signal using DWT(by filter of average number)
    public ArrayList<Double> FDWT(ArrayList<Double> _pic) {
        ArrayList<Double> _temp = new ArrayList<Double>(_pic.size());
        //Initialize _temp
        _temp = (ArrayList<Double>) _pic.clone();
        for (int j = _pic.size(); j >= 2; j = j / 2) {

            for (int i = 0; i < j / 2; i += 1) {
                double _average = (_pic.get(2 * i) + _pic.get(2 * i + 1)) / 2;
                double _minus = _pic.get(2 * i) - _average;
                _temp.set(i, _average);
                _temp.set(j / 2 + i, _minus);
            }
            _pic = (ArrayList<Double>) _temp.clone();
        }

        return _pic;
    }


    //Inverse DWT(by filter of average number)
    public ArrayList<Double> IDWT(ArrayList<Double> _dwt) {
        ArrayList<Double> _temp = new ArrayList<Double>(_dwt.size());
        //Initialize _temp
        _temp = (ArrayList<Double>) _dwt.clone();
        for (int j = 2; j <= _dwt.size(); j = j * 2) {
            for (int i = 0; i < j / 2; i += 1) {
                double _pre = _dwt.get(i) + _dwt.get(j / 2 + i);
                double _late = _dwt.get(i) - _dwt.get(j / 2 + i);
                _temp.set(i * 2, _pre);
                _temp.set(i * 2 + 1, _late);
            }
            _dwt = (ArrayList<Double>) _temp.clone();
        }

        return _dwt;
    }


    public static void main(String[] args) {
        DWT _dwt = new DWT();

        /* ----------------- Test for zigzag --------------------*/
        double[][] _input = {{49, 61, 69, 61, 78, 89, 100, 112}
                            , {68, 60, 51, 42, 62, 69, 80, 89}
                            , {90, 81, 58, 49, 69, 72, 68, 69}
                            , {100, 91, 79, 72, 69, 68, 59, 58}
                            , {111, 100, 101, 91, 82, 71, 59, 49}
                            , {131, 119, 120, 102, 90, 90, 81, 59}
                            , {148, 140, 129, 99, 92, 78, 59, 39}
                            , {151, 140, 142, 119, 98, 90, 72, 39}};

        ArrayList<Double> _output = _dwt.DwtZigzag(_input, 8, 0, 0, 64);
        for (Double _tem : _output) {
            System.out.print(_tem + " ");
        }

        /*-----------------end Test for zigzag-----------------------*/


        /* ----------------- Test for Izigzag --------------------*/

        System.out.println("\n Output the Origin: \n");
        for (int i = 0; i < _input.length; i++) {
            System.out.print("\n");
            for (int j = 0; j < _input[0].length; j++) {
                System.out.print(_input[i][j] + " ");
            }
        }


        double[][] _IDWZout = _dwt.IDwtZigzag(_output, 8, 0);

        System.out.println("\n Output the matrix: \n");

        for (int i = 0; i < _IDWZout.length; i++) {
            System.out.print("\n");
            for (int j = 0; j < _IDWZout[0].length; j++) {
                System.out.print(_IDWZout[i][j] + " ");
            }
        }
    }
}
//      for(Double _tem : _output)
//      {
//          System.out.print(_tem + " ");
//      }

        /*-----------------end Test for Izigzag-----------------------*/



//        /* ----------------------Test for DWT------------------------*/
//
//        System.out.println("Origin queue is: ");
//        for(Double _tem : _output)
//        {
//            System.out.print(_tem + " ");
//        }
//
//        System.out.println("\nResult of FDWT is: ");
//        ArrayList<Double> out = _dwt.FDWT(_output);
//        for(Double _tem : out)
//        {
//            System.out.print(_tem + " ");
//        }
//
//        ArrayList<Double> _iverseOutPut = _dwt.IDWT(out);
//        System.out.println("\nResult of IDWT is: ");
//        for(Double _tem : _iverseOutPut)
//        {
//            System.out.print(_tem + " ");
//        }




        /* ------------------End Test for DWT------------------------*/



