DWT and DCT compression on picture

What is it?
---------------
It is an simple program to deal with the Discrete Cosine Transformation (DCT) 
and Discrete Wavelet Transformation(DWT) Transformation to compress pictures.

How to use it?
---------------
Input to this program will be 2 parameters where:
1. The first parameter is the name of the input image file. (file format
is RGB files meaning RRR....GGG...BBB.. for each image.(width 512 and 
height 512 for processing convinence))

2. The second parameter is an integral number that defines the number of
coefficients to use for decoding. The interpretation of this parameter of
decoding is different for both the DCT and DWT cases so as to use the same
number of coefficients. Please see the Detail section for an
explanation


Typical invocations this program would look like
java main Image.rgb 262144
Here you are making use of all the coefficients to decode because the total
number of coefficients for each channel are going to be 512*512= 262144. Hence
the output for each DCT and DWT should be exactly the same as the image with no
loss.

java main Image.rgb 131072
Here you are making use of 131072 (half of the total number) of coefficients
for decoding. The exact coefficients you use will vary depending on DCT or DWT.
Refer to the implementation section for this.
java main Image.rgb 16384

Here you are making use of 16384 (one-sixteenth of the total number) of
coefficients for decoding. The exact coefficients you use will vary depending
on DCT or DWT. Refer to the implementation section for this.

Licensing
-----------------
WTFPL: Do What the Fuck You Want to public

Contacts
-----------------
If you have any questions on this program, you could email
chenlong888@gmail.com


Detail
-----------------
For the DCT decoding, we use
1. first iteration - the DC coefficient for each block (total 4096 coefficients)
2. second iteration – the DC, AC1 coefficient of each block (total 8192
coefficients)
3. third iteration – the DC, AC1, AC2 coefficient of each block (total 12288
coefficients) ...
...
64.sixty forth iteration – the DC, AC1, AC2 .... AC64 coefficient of each block
(total 512*512= 262144 coefficients)

For the DWT decoding, you will use
1. first iteration - the first 4096 coefficients in zigzag order.
2. second iteration – the first 8192 coefficients in zigzag order.
3. third iteration – the first 12288 coefficients in zigzag order ....
....
64.sixty forth iteration – all the total 512*512= 262144 coefficients.
