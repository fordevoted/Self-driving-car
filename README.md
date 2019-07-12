# Self-driving-car
  Self driving car is a kind of self driving car algorithm demo program using Java to program.
  ![fordevoted](https://imgur.com/1G3Qc9L.png "Self driving car")
  
## OverView
  Self driving car is automobile's trend nowadays, and this program use three algorithms to accomplish the task. In this program, you can upload the maps file, and the auto will drive to the final by itself.<br>
  the button introduction are following:<br>
  * Open Map: open maps file, and draw the maps
  * Open Solution: open solution file(4D, 6D are accepted), auto will drive accroding to solution file and draw the path.
  * Run : auto will drive by itself.
  * Train: open training dataset, and train the RBFN (if use RBFN)
  * Load Traing Result: load the RBFN parameter.
  * Save training Result: save the RBFN parameter.
## Maps format
  >0,0,90<br>
18,40<br>
30,37<br>
-6,-3<br>
-6,22<br>
18,22<br>
18,50<br>
30,50<br>
30,10<br>
6,10<br>
6,-3<br>
-6,-3<br>

first line is beginning（x,y,φdegree）<br>
2,3 line indicate destination position<br>
line 2 is left-higher point（x,y） in the region<br>
line 3 is right-lower corner（x,y） in the region<br>
the point (x,y) of rail will show in line 4 to the last line<br>
last line will be same to line 4 to form a enclourse rail<br>
the beginning right-higher corner is (-6,-3);left-lower corner is (6,-3)<br>
beginning is (-6,0) -> (6,0)<br>

## Usages
  Choose the self driving algorithms, and download the folder, then use Java compiler to compile. 
## Feature
  I used fuzzy system to decide the turning degree and direction of the auto, and implement 4 different algorithms to decide the turning degree by input the three detect distance of right, front, left.
  #### Fuzzy Rule
    Design 5 fuzzy rule to decide the decide the degree.  
  #### RBFN (GN)
    Used RBFN to decide the turning degree, input 3 detect distance, output the turining degree, and train the RBFN by using genetic algorithm
  #### RBFN (PSO)
    Used RBFN to decide the turning degree, input 3 detect distance, output the turining degree, and train the RBFN by using PSO algorithm
  #### RBFN (GN PSO)
    Used RBFN to decide the turning degree, input 3 detect distance, output the turining degree, and train the RBFN by using PSO-GN algorithm
## License
##### Fordevoted
 105082015 資工三B 陳昱瑋 NCU CSIE
## Contact
 210509fssh@gmail.com
