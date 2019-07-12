package assignment1;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration; 

public class Main extends Application{
	
	private Pane pane, anchorPane;
	private Button bnMap,bnSolution,bnRun,bnTrain,bnOpenTrain,bnSave;
	private TextField tvLeft,tvMid,tvRight;
	private TextArea status;
	private Circle car;
	private Rectangle destination; 
	private ArrayList<Line> lineset = new ArrayList<Line>();
	private ArrayList<double[]> function = new ArrayList<double[]>();
	private Timeline timeline,delete_timeline,callback_timeline,show_path_timeline;
	
	private ArrayList<String> maze  = new ArrayList<String>();
	private ArrayList<Double> RBFNweight = new ArrayList<Double>();
	private ArrayList<Double> train_left = new ArrayList<Double>(),
			train_mid = new ArrayList<Double>(),
			train_right = new ArrayList<Double>(),
			train_y = new ArrayList<Double>();
	
	private double[][] RBFNc = new double[3][3];	// hidden = 3 input_fix = 3 
	private double [] RBFNsigma = new double[3];	// hidden = 3 
	private double weight = 4.0;
	private double biasX = 200, biasY = 400;		//270,330
	private double degree  ,orign_degree ;
	private double carX,carY,car_beginX = 0,car_beginY = 0;
	private double fps = 100;
	private int hidden;
	private int count = 0, count_orign_in_map = 0; 
	private double left, midium , right;
	private double maxdistance = 0;
	private double destination_height,destination_width,car_destination_x,car_destination_y;
	private boolean delete_flag = true,end_flag = false,distination_flag = false;
	
	
	private ArrayList<String> record4D = new ArrayList<String>();
	private ArrayList<String> record6D = new ArrayList<String>();
	private ArrayList<Double> callback_xyaxis = new ArrayList<Double>();
	private int callback_index = 0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		AnchorPane mainPane = (FXMLLoader.load(getClass().getResource("main_layout2.fxml")));
		Scene scene = new Scene(mainPane,800,600);
		pane = (Pane) scene.lookup("#pane");
		anchorPane = (Pane) scene.lookup("#AnchorPane");
		bnMap = (Button) scene.lookup("#button_openMap");
		bnSolution = (Button) scene.lookup("#button_openSolution");
		bnRun = (Button) scene.lookup("#button_run");
		tvLeft = (TextField)scene.lookup("#textView_left");
		tvMid = (TextField)scene.lookup("#textView_mid");
		tvRight = (TextField)scene.lookup("#textView_right");
		status = (TextArea)scene.lookup("#status");
		bnTrain = (Button) scene.lookup("#button_train");
		bnOpenTrain = (Button) scene.lookup("#button_opentrain");
		bnTrain = (Button) scene.lookup("#button_train");
		bnSave = (Button)scene.lookup("#button_savetrain");
		
		
		
		hidden = Integer.parseInt(((TextField)scene.lookup("#textView_hidden")).getText().toString());
		status.setText(String.format("\t\t\t\t\t\t\tBiasX = %.1f, BiasY = %.1f, amplify weight = %.1f\n"
				+ "The default map is bonus map, you can click *Run* button to do the testing for the map many time without reload map, or you can click the *Open Map* Button to open another map\n"
				+ "and you can click the *Open Solution* button to open the solution and let the car automatic drive to destination.\n"
				+ "all of imformation beside sensor's distance will show in this textView, the other infomation will show in the left hand side.\n",biasX,biasY,weight));
		
		
		String[] map = {"0,0,90",
		"7,38",
		"4,24",
		"-6,-3",
		"-6,22",
		"18,22",
		"18,50",
		"-8,50",
		"-8,38",
		"16,38",
		"16,24",
		"-20,24",
		"-20,62",
		"30,62",
		"30,10",
		"6,10",
		"6,-3",
		"-6,-3"};
		for(int i = 0 ; i < map.length ; i++ ) {
			maze.add(map[i]);
		}
		DrawMaze();
		//Run();
		//System.out.format("left :%f mid:%f right:%f \n",Detect(45), Detect(0),Detect(-45));
		//degree+=0;
		//System.out.format("left :%f mid:%f right:%f \n",Detect(45), ,Detect(-45));
		
		//Pane pane = (Pane)scene.lookup("pane");
		//pane.setStyle("-fx-background-color:#00ff00");
		//pane.setBackground(new Background(new BackgroundFill(Color.web("#00ff00"), CornerRadii.EMPTY, Insets.EMPTY)));
		//Controller controller = new Controller(scene);
		primaryStage.setTitle("NN homework");
		primaryStage.setScene(scene);
		primaryStage.show();
		//Test();
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getCode()==KeyCode.UP) {
					car.setCenterY(car.getCenterY()-3);
				}
				if(event.getCode()==KeyCode.DOWN) {
					car.setCenterY(car.getCenterY()+3);
				}
				if(event.getCode()==KeyCode.LEFT) {
					car.setCenterX(car.getCenterX()-3);
				}
				if(event.getCode()==KeyCode.RIGHT) {
					car.setCenterX(car.getCenterX()+3);
				}
				if(event.getCode()==KeyCode.Q) {
					System.out.println("tab pressed ");
					Action(Detect(45),Detect(0),Detect(-45));
				}
			
			
			}
			});
		bnMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
			
			private BufferedReader br;
			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				maze.clear();
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showOpenDialog((Stage)scene.getWindow());				
				String str = new String();
				try {
					FileReader	reader = new FileReader(file);
					br = new BufferedReader(reader);
					while((str = br.readLine())!=null ) {
							maze.add(str);
							System.out.println(str);
					}
					 
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					status.setText(status.getText().toString()+" File Read Error! \n");
					e.printStackTrace();
				}catch (IOException e) {
					status.setText(status.getText().toString()+" File Read Error! \n");
					e.printStackTrace();
				}
				DrawMaze();
				
			}
			
		});
		bnRun.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println(pane.getChildren().size()+"   "+count_orign_in_map);
				int size = pane.getChildren().size();
				for(int i = 0 ; i <size-count_orign_in_map ; i++) {
					pane.getChildren().remove(pane.getChildren().size()-1);
					
				}
				for(int i = 0 ; i < lineset.size() ; i++) {
					lineset.get(i).setStroke(Color.AZURE);
				}
				System.out.println(pane.getChildren().size()+"   "+count_orign_in_map);
				car.setCenterX(car_beginX);
				car.setCenterY(car_beginY);
				carX = car_beginX;
				carY = car_beginY;
				degree = orign_degree;
				end_flag = false;
				delete_flag = true;
				distination_flag = false;
				status.clear();
				
				Run();
			}
			
		});
		bnSolution.setOnMouseClicked(new EventHandler<MouseEvent>(){
			
			private BufferedReader br;
			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showOpenDialog((Stage)scene.getWindow());				
				String str = new String();
				ArrayList<String> solution = new ArrayList<String>();
				try {
					FileReader	reader = new FileReader(file);
					br = new BufferedReader(reader);
					while((str = br.readLine())!=null ) {
							solution.add(str);
							System.out.println(str);
					}
					 
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					status.setText(status.getText().toString()+" File Read Error! \n");
					e.printStackTrace();
				}catch (IOException e) {
					status.setText(status.getText().toString()+" File Read Error! \n");
					e.printStackTrace();
				}
				int size = pane.getChildren().size();
				for(int i = 0 ; i <size-count_orign_in_map ; i++) {
					pane.getChildren().remove(pane.getChildren().size()-1);
					
				}
				
				for(int i = 0 ; i < lineset.size() ; i++) {
					lineset.get(i).setStroke(Color.AZURE);
				}
				car.setCenterX(car_beginX);
				car.setCenterY(car_beginY);
				carX = car_beginX;
				carY = car_beginY;
				degree = orign_degree;
				end_flag = false;
				delete_flag = true;
				distination_flag = false;
				status.clear();
				
				Callback(solution,file.getName());
			}

			
			
		});
		bnTrain.setOnMouseClicked(new EventHandler<MouseEvent>() {
			private BufferedReader br;
			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				train_y.clear();
				train_left.clear();
				train_mid.clear();
				train_right.clear();
				RBFNweight.clear();
				
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showOpenDialog((Stage)scene.getWindow());	
				
				String str = new String();
				try {
					FileReader	reader = new FileReader(file);
					br = new BufferedReader(reader);
					while((str = br.readLine())!=null ) {
							int beginIndex, endIndex = str.length();
							for(int i = 0 ; i < 4 ;i++) {
								beginIndex = str.lastIndexOf(" ");
								switch(i) {
									case 0:{
										// normalization;
										train_y.add(Math.abs(Double.parseDouble(str.substring(beginIndex+1, endIndex)))/40);
										break;
									}
									case 1:{
										train_right.add(Double.parseDouble(str.substring(beginIndex+1, endIndex)));
										if(train_right.get(train_right.size()-1) > maxdistance) {
											maxdistance = train_right.get(train_right.size()-1);
										}
										break;	
									}
									case 2 :{
										train_mid.add(Double.parseDouble(str.substring(beginIndex+1, endIndex)));
										if(train_mid.get(train_mid.size()-1) > maxdistance) {
											maxdistance = train_mid.get(train_mid.size()-1);
										}
										break;
									}
									case 3:{
										train_left.add(Double.parseDouble(str.substring(beginIndex+1, endIndex)));
										if(train_left.get(train_left.size()-1) > maxdistance) {
											maxdistance = train_left.get(train_left.size()-1);
										}
										break;									
									}
								}
								//System.out.println("beginIndex: " + beginIndex + "endIndex: " + endIndex);
								endIndex = beginIndex;
								if(beginIndex >= 0) {
									str = str.substring(0, beginIndex);
								}
							}
							//System.out.println(str);
					}
					 
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					status.setText(status.getText().toString()+" File Read Error! \n");
					e.printStackTrace();
				}catch (IOException e) {
					status.setText(status.getText().toString()+" File Read Error! \n");
					e.printStackTrace();
				}
				// normalization
				for(int i = 0 ; i < train_right.size(); i++) {
					 double temp = train_right.get(i) / maxdistance;
					 train_right.remove(i);
					 train_right.add(i,temp);
					 
					 temp = train_mid.get(i) / maxdistance;
					 train_mid.remove(i);
					 train_mid.add(i,temp);
					
					 temp = train_left.get(i) / maxdistance;
					 train_left.remove(i);
					 train_left.add(i,temp);
				}
				
				Platform.runLater(() ->{
					TrainRBFN(scene);	
				});
				
			}
			
		});
		bnOpenTrain.setOnMouseClicked(new EventHandler<MouseEvent>() {
			private BufferedReader br;
			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showOpenDialog((Stage)scene.getWindow());				
				String str = new String();
				try {
					FileReader	reader = new FileReader(file);
					br = new BufferedReader(reader);
					str = br.readLine();
					double theta = Double.parseDouble(str);
					int index = 0;
					while((str = br.readLine())!=null ) {
							int beginIndex, endIndex = str.length();
							for(int i = 0 ; i < 5 ;i++) {
								beginIndex = str.lastIndexOf(" ");
								switch(i) {
									case 0:{
										RBFNsigma[index] = Double.parseDouble(str.substring(beginIndex+1, endIndex));
										break;
									}
									case 1:{
										RBFNc[index][2] = (Double.parseDouble(str.substring(beginIndex+1, endIndex)));
										break;	
									}
									case 2 :{
										RBFNc[index][1] = (Double.parseDouble(str.substring(beginIndex+1, endIndex)));
										break;
									}
									case 3:{
										RBFNc[index][0] = (Double.parseDouble(str.substring(beginIndex+1, endIndex)));
										break;
									}
									case 4 :{
										RBFNweight.add(Double.parseDouble(str.substring(beginIndex+1, endIndex)));
									}
								}
								//System.out.println("beginIndex: " + beginIndex + "endIndex: " + endIndex);
								endIndex = beginIndex;
								if(beginIndex >= 0) {
									str = str.substring(0, beginIndex);
								}
							}
					}
					RBFNweight.add(theta);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					status.setText(status.getText().toString()+" File Read Error! \n");
					e.printStackTrace();
				}catch (IOException e) {
					status.setText(status.getText().toString()+" File Read Error! \n");
					e.printStackTrace();
				}
				

				status.setText(status.getText().toString()+
						"Open RBFN model params success!\n");
				status.selectPositionCaret(status.getText().length());
				System.out.println("Open RBFN model params success!");
			}
			
		});
		bnSave.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				try {
					PrintWriter writer;
					writer = new PrintWriter("RBFN model params.txt", "UTF-8");
					writer.println(RBFNweight.get(RBFNweight.size()-1));
					for(int i = 0 ; i < RBFNweight.size()-1 ; i++) {
						writer.print(RBFNweight.get(i)+" ");
						for(int j = 0 ; j < hidden ; j++) {
							writer.print(RBFNc[i][j]+" ");
						}
						writer.println(RBFNsigma[i]);
					}
					writer.close();
				} catch (FileNotFoundException e) {
					status.setText(status.getText()+"RBFN model params.txt Write Out Error\n");
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					status.setText(status.getText()+"RBFN model params.txt Write Out Error\n");
					e.printStackTrace();
				}

				status.setText(status.getText().toString()+
						"Save Weight success!\n");
				status.selectPositionCaret(status.getText().length());
				System.out.println("Save Weight success!");
			}
			
		});
		
	
	
	
	
	
	
	
	}
	private void DrawMaze() {
		pane.getChildren().clear();
		lineset.clear();
		function.clear();
		
		for(int i = 3 ;i<maze.size()-1;i++) {
			int spilt = maze.get(i).indexOf(",");
			int spilt_next = maze.get(i+1).indexOf(",");
			//System.out.format("%s , %s\n",(maze.get(i).substring(0, spilt)), (maze.get(i).substring(spilt+1, maze.get(i).length())));
			//System.out.format("|%s| , |%s|\n",(maze.get(i+1).substring(0, spilt_next)), (maze.get(i+1).substring(spilt_next+1, maze.get(i+1).length())));
			
			double x1 = Double.parseDouble(maze.get(i).substring(0, spilt))*weight+biasX;
			double y1 = Double.parseDouble(maze.get(i).substring(spilt+1, maze.get(i).length()))*-weight+biasY;
			double x2 = Double.parseDouble(maze.get(i+1).substring(0, spilt_next))*weight+biasX;
			double y2 = Double.parseDouble(maze.get(i+1).substring(spilt_next+1, maze.get(i+1).length()))*-weight+biasY;
			
			
			Line line = new Line(x1,y1,x2,y2);
			double[] temp = { ((y1 - y2) / (x1 - x2)),((x1 * y2 - x2 * y1) / (x1 - x2)),1};
			x1 = Math.round(x1*10000.0)/10000.0;
			x2= Math.round(x2*10000.0)/10000.0;
			
			if(x1==x2) {
				temp[0] = x1;
				temp[1] = 0 ;
				temp[2] = -1;
			}
			//System.out.format("i %d a %f b %f v %f\n", i,temp[0],temp[1],temp[2]);
			
			line.setStroke(Color.AZURE);
		    line .setStrokeWidth(1.0);
		    pane.getChildren().add(line);
		    lineset.add(line);
		    function.add(temp);
		}
		
		
		int spilt = maze.get(0).indexOf(",");
		int spilt2 = maze.get(0).indexOf(",",spilt+1); 
		car_beginX = Double.parseDouble(maze.get(0).substring(0, spilt))*weight+biasX;
		car_beginY = Double.parseDouble(maze.get(0).substring(spilt+1,spilt2))*-weight+biasY;
		
		degree = Double.parseDouble(maze.get(0).substring(spilt2+1,maze.get(0).length()));
		orign_degree = degree;
		
		car = new Circle(car_beginX,car_beginY,3.0*weight);
		//System.out.format("spilt %f //spilt2  %f",car_beginX,car_beginY);
		System.out.format(" beginx %f beginy %f\n",car.getCenterX(),car.getCenterY());
		carX = car_beginX;
		carY = car_beginY;
		car.setFill(Color.WHITE);
		pane.getChildren().add(car);
		
		
		//maze[1]
		int spilt3 = maze.get(1).indexOf(",");
		int spilt4 = maze.get(2).indexOf(",");
		destination_width = Math.abs(Double.parseDouble(maze.get(1).substring(0, spilt3))-Double.parseDouble(maze.get(2).substring(0, spilt4)));
		destination_height = Math.abs(Double.parseDouble(maze.get(1).substring(spilt3+1, maze.get(1).length()))-Double.parseDouble(maze.get(2).substring(spilt4+1, maze.get(2).length())));
		
		destination = new Rectangle(Double.parseDouble(maze.get(1).substring(0, spilt3))*weight+biasX,
				Double.parseDouble(maze.get(1).substring(spilt3+1, maze.get(1).length()))*-weight+biasY,destination_width*weight,destination_height*weight);
		destination.setFill(Color.AQUAMARINE);
		pane.getChildren().add(destination);
		
		
		int spilt5 = maze.get(maze.size()-1).indexOf(",");
		int spilt6 = maze.get(maze.size()-2).indexOf(",");
		Line l = new Line(Double.parseDouble(maze.get(maze.size()-1).substring(0, spilt5))*weight+biasX,car_beginY,Double.parseDouble(maze.get(maze.size()-2).substring(0, spilt6))*weight+biasX,car_beginY);
		l.setStroke(Color.web("EE2222"));
		pane.getChildren().add(l);
		
		pane.setBackground(new Background(new BackgroundFill(Color.web("333333"), CornerRadii.EMPTY, Insets.EMPTY)));
		pane.setPrefHeight(500);
		pane.setPrefWidth(700);
		anchorPane.setBackground(new Background(new BackgroundFill(Color.web("555555"), CornerRadii.EMPTY, Insets.EMPTY)));
		count_orign_in_map = pane.getChildren().size();
		status.setText(status.getText()+"DrawMap Success!\n");
		
	}
	private double Action(double left, double mid, double right) {
		// recieve degree & case result to move 
		//System.out.println("carX : "+car.getTranslateX()+" carY : "+car.getTranslateY()+"degree : "+degree);
		
		/*
		 * fuzzifier
		 */
		double[] left_set = {Far(left),Midium(left),Near(left)};
		double[] mid_set = {Far(mid),Midium(mid),Near(mid)};
		double[] right_set = {Far(right),Midium(right),Near(right)};
		double[] signifincation = new double[6];
		double[] rule_membership = new double[3];
		signifincation[1]= 0;
		signifincation[3]= 0;
		signifincation[5]= 0;
		
		// Max of each direction
		for(int i = 0 ; i < 3 ; i++ ) {
			System.out.format("%d. left: %f mid: %f right: %f \n",i, left_set[i],mid_set[i],right_set[i]);
			if(left_set[i]>signifincation[1]) {
				signifincation[1] = left_set[i];
				signifincation[0]= i;
			}
			if(mid_set[i]>signifincation[3]) {
				signifincation[3] = mid_set[i];
				signifincation[2]= i;
			}
			if(right_set[i]>signifincation[5]) {
				signifincation[5] = right_set[i];
				signifincation[4]= i;
			}
		}
		
		/*
		 * fuzzy rule base
		 */
		//rule 1  -- go straight (degree 0)
 			// left is mid/near         mid is mid/far           right is mid/near
		if(signifincation[0] != 0 && signifincation[2] != 2 && signifincation[4] != 0) {
			rule_membership[0] = Min(signifincation[1],signifincation[3],signifincation[5]);
		}
		
		//rule 2  -- turn right (degree  minus)
			// left is mid/near         mid is mid/near           right is mid/far
		if(signifincation[0] != 0 && signifincation[2] != 0 && signifincation[4] != 2) {
			rule_membership[1] = Min(signifincation[1],signifincation[3],signifincation[5]);
		}
		
		//rule 3  -- turn left (degree  plus)
			// left is mid/far         mid is mid/near           right is mid/near
		if(signifincation[0] != 2 && signifincation[2] != 0 && signifincation[4] != 0) {
			rule_membership[2] = Min(signifincation[1],signifincation[3],signifincation[5]);
		}
		
		//rule 4 --turn right (degree minus if left is near no matter)
		if(left_set[2]>0.7) {
			rule_membership[1] = Math.max(rule_membership[1],left_set[2]);  //(ensure turn right  )
		}
		
		//rule 5 --turn left (degree plus if right is near no matter)
		if(right_set[2]>0.7) {
			rule_membership[2] = Math.max(rule_membership[2],right_set[2]);  //(ensure turn left  )
		}
		
		/*
		 * fuzzy inference
		 */
		int index = 0;
		for(int i = 0 ; i < 3 ; i++ ) {
			if(rule_membership[i]>rule_membership[index]) {
				index = i;
			}
		}
		double theta =  ComputeTheta(left,mid,right);  
		
		if(index == 2) {
			theta = -theta;
		}
		/*
		if(index == 0) {
			theta = 0;
		}else if(index == 1) {
			double inference1,inference2,inference3,inference4;
			inference1 = 20*Sigmoid(left_set[2]*mid_set[2])+20;
			inference2 = 20*Sigmoid(left_set[2]*mid_set[1])+20;
			inference3 = 20*Sigmoid(left_set[1]*mid_set[2])+10;
			inference4 = 15*Sigmoid(left_set[1]*mid_set[1])+10;
			/*
			 * Defuzzification
			 /
			theta = -(inference1*left_set[2]*mid_set[2] + inference2*left_set[2]*mid_set[1] + 
					inference3*left_set[1]*mid_set[2] + inference4*left_set[1]*mid_set[1] /
					(left_set[2]*mid_set[2]+left_set[2]*mid_set[1]+left_set[1]*mid_set[2]+left_set[1]*mid_set[1]));
		}else {
			double inference1,inference2,inference3,inference4;
			inference1 = 20*Sigmoid(right_set[2]*mid_set[2])+20;
			inference2 = 20*Sigmoid(right_set[2]*mid_set[1])+20;
			inference3 = 20*Sigmoid(right_set[1]*mid_set[2])+10;
			inference4 = 15*Sigmoid(right_set[1]*mid_set[1])+10;
			/*
			 * Defuzzification
			 /
			theta = (inference1*right_set[2]*mid_set[2] + inference2*right_set[2]*mid_set[1] + 
					inference3*right_set[1]*mid_set[2] + inference4*right_set[1]*mid_set[1] /
					(right_set[2]*mid_set[2]+right_set[2]*mid_set[1]+right_set[1]*mid_set[2]+right_set[1]*mid_set[1]));
		}
		*/
		//move
		double deltaX, deltaY;
		deltaX = weight*(Math.cos(Math.toRadians(degree+theta))
				+Math.sin(Math.toRadians(degree))
				*Math.sin(Math.toRadians(theta)));
		deltaY = -weight*(Math.sin(Math.toRadians(degree+theta))
				-Math.cos(Math.toRadians(degree))
				*Math.sin(Math.toRadians(theta)));
		
		
		car.setCenterX(carX+deltaX);
	    car.setCenterY(carY+deltaY);
		
		carX = car.getCenterX();
		carY = car.getCenterY();
		Line p = new Line(carX,carY,carX,carY);
		p.setStroke(Color.DARKTURQUOISE);
		p.setStrokeWidth(5.0);
		pane.getChildren().add(p);
		count++;
		
		degree = degree +Math.toDegrees(Math.asin( 2*Math.sin(Math.toRadians(theta)*weight / (2*car.getRadius() ) )));
		if(degree > 270) {
			degree = degree-360;
		}
		else if(degree < -90) {
			degree = degree+360;
		}
		
		if(theta!=0) {
			record4D.add(mid/weight+" "+right/weight+" "+left/weight+" "+-theta);
			record6D.add((car.getCenterX()-biasX)/weight+" "+(car.getCenterY()-biasY)/-weight+" "+mid/weight+" "+right/weight+" "+left/weight+" "+-theta);
			
		}
		else {
			record4D.add(mid/weight+" "+right/weight+" "+left/weight+" "+theta);
			record6D.add((car.getCenterX()-biasX)/weight+" "+(car.getCenterY()-biasY)/-weight+" "+mid/weight+" "+right/weight+" "+left/weight+" "+theta);
			
		}
		
		status.setText(status.getText().toString()+
				String.format("car's x: %.5f	car's y: %.5f	phi degree: %.5f    theta degree : %.5f \nleft distance: %.5f	front distance: %.5f    right distance: %.5f	rule: %d \n\n",
						car.getCenterX(),
						car.getCenterY(),
						degree,
						theta,
						left,
						mid,
						right,
						index));
				
				/*"car's x : "+car.getCenterX()+
				"\t car's y: "+car.getCenterY()+
				"\t phi degree: "+degree+
				"\t left distance: "+left+
				"\t front distance: "+mid+
				"\t right distance: "+right+
				"\t theta degree: "+theta+
				"\t rule: "+index+
				"\n");*/
		status.selectPositionCaret(status.getText().length());
		System.out.format("car x: %f  car y: %f degree: %f theta: %f left: %f mid: %f right: %f rule: %d \n",
				car.getCenterX(),
				car.getCenterY(),
				degree,
				theta,
				left,
				mid,
				right,
				index);
		
		//collision detect
		for(int i = 0; i<lineset.size();i++) {
			Shape intersect = Shape.intersect(lineset.get(i),car);
		      if (intersect.getBoundsInLocal().getWidth() != -1){
				timeline.stop();
				delete_timeline.stop();
				end_flag = true;
				status.setText(status.getText().toString()+" collision! \n");
				System.out.println(" collision! ");
				break;
			}
		}
		
		// end of the game
		Shape intersect = Shape.intersect(destination, car);
	      if (intersect.getBoundsInLocal().getWidth() != -1) { 	
			if(!distination_flag) {
				distination_flag = true;
				car_destination_x = car.getCenterX();
				car_destination_y = car.getCenterY();
			}
			System.out.println(Math.abs(car.getCenterX()-car_destination_x)+" "+Math.abs(destination_width*weight)+" "+
					Math.abs(car.getCenterY()-car_destination_y)+" "+Math.abs(destination_height*weight));
			if(Math.abs(car.getCenterX()-car_destination_x) > Math.abs(Math.min(car.getRadius(),destination_width*weight))
					|| Math.abs(car.getCenterY()-car_destination_y) > Math.abs(Math.min(car.getRadius(),destination_height*weight))) {
				delete_timeline.stop();
				timeline.stop();
				end_flag = true;
				status.setText(status.getText().toString()+" Arrive! \n");
				System.out.println(" Arrive! ");
				Record(record4D,"train4D");
				Record(record6D,"train6D");
			}
		}
		return 0;
	}
 	private void Run(){
 		count=pane.getChildren().size();
 		timeline= new Timeline(new KeyFrame(Duration.millis(4000/fps),new EventHandler<ActionEvent>() {
 			// delivery degree & result to action
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				for(int i = 0 ; i < lineset.size() ; i++) {
					lineset.get(i).setStroke(Color.AZURE);
				}
				left = Detect(45);
				midium = Detect(0);
				right = Detect(-45);
				tvLeft.setText(String.valueOf(left));
				tvMid.setText(String.valueOf(midium));
				tvRight.setText(String.valueOf(right));
				Action(left,midium,right);
				timeline.stop();
				delete_timeline.play();
				if(end_flag) {
					timeline.setCycleCount(0);
				}
			}
		}));
 		delete_timeline= new Timeline(new KeyFrame(Duration.millis(4000/fps),new EventHandler<ActionEvent>() {
 			// delivery degree & result to action
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				if(end_flag) {
					delete_timeline.setCycleCount(0);
				}
				if(delete_flag) {
					delete_flag = false ;
					timeline.stop();
				}else {
					delete_flag = true ;
					
					//System.out.println((pane.getChildren().size()-count));
					int size = (pane.getChildren().size()-count);
					for(int i = 0 ;i < size ; i++) {
						pane.getChildren().remove(pane.getChildren().size()-2);
					}
					timeline.play();
					delete_timeline.stop();
				}
					
			}
			
		}));
 		delete_timeline.setCycleCount(-1);
 		//delete_timeline.play();
 		timeline.setCycleCount(-1);
 		timeline.play();
 	}
	private double Detect(double direction_degree) {
		int index = 0;
		double x = 1000*Math.cos(Math.toRadians(degree+direction_degree))+carX;
		double y = (-1000*Math.sin(Math.toRadians(degree+direction_degree))+carY);
		double[] line_function = {((carY - y) / (carX - x)),((carX * y - x * carY) / (carX - x)),1};
		double[] intersection = new double[2];
		double[] intersection_min = new double[2];
		double range,min_range=999999999;

		x = Math.round(x*10000.0)/10000.0;
		carX = Math.round(carX*10000.0)/10000.0;
		//to be union as a form ,so do the y
		carY = Math.round(carY*10000.0)/10000.0;
		//System.out.println("carX: "+carX+" x: "+x);
		if(x == carX) {
			line_function[0] = x; 
			line_function[1] = 0; 
			line_function[2] = -1;
		}
		Line  sensor = new Line(carX,carY,x,y);
		pane.getChildren().add(sensor);
		for(int i = 0; i<lineset.size();i++) {
			Shape intersect = Shape.intersect(lineset.get(i), sensor);
		      if (intersect.getBoundsInLocal().getWidth() != -1) { 
		        	//if(lineset.get(i).getBoundsInLocal().intersects(sensor.getBoundsInLocal())) 
				if(function.get(i)[2]==1 && line_function[2]==1) {
					intersection[0] = (line_function[1]-function.get(i)[1])/(function.get(i)[0]-line_function[0]);
					intersection[1] = function.get(i)[0]*intersection[0]+function.get(i)[1];
				}else if(function.get(i)[2]==-1){
					intersection[0] = function.get(i)[0];
					intersection[1] = line_function[0]*intersection[0]+line_function[1];
				}else {
					//System.out.println("enter else");
					intersection[0] = line_function[0];
					intersection[1] = function.get(i)[0]*intersection[0]+function.get(i)[1];
				}
				range = Math.sqrt(Math.pow(intersection[0]-carX, 2)+Math.pow(intersection[1]-carY, 2));
				if(range < min_range) {
					index = i;
					min_range = range;
					intersection_min[0] = intersection[0];
					intersection_min[1] = intersection[1];
				}
			}
		}
		
		pane.getChildren().remove(pane.getChildren().size()-1);
		Line l = new Line(intersection_min[0],intersection_min[1],carX,carY);
		l.setStroke(Color.CHARTREUSE);
		pane.getChildren().add(l);
		lineset.get(index).setStroke(Color.GOLD);
		System.out.format("i %d v %f cx %f x %f ix %f iy %f r%f ix divider %f degre %f \n", index,line_function[2] ,carX,x,intersection_min[0],intersection_min[1],min_range,function.get(index)[0],direction_degree);
		return min_range;
	}
	private void Record(ArrayList<String> TXT,String file_name) {
		try {
			PrintWriter writer;
			writer = new PrintWriter(file_name+".txt", "UTF-8");
			for(int i = 0 ; i < TXT.size() ; i++) {
				writer.println(TXT.get(i));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			status.setText(status.getText()+file_name+".txt Write Out Error\n");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			status.setText(status.getText()+file_name+".txt Write Out Error\n");
			e.printStackTrace();
		}
	}
	private double Far(double x ) {
		if(x>26*weight)
			return 1;
		else
			return Math.exp(-Math.pow(x-25*weight, 2)/(2*62*weight));
	}
	private double Midium(double x ) {
			return Math.exp(-Math.pow(x-14*weight, 2)/(2*52*weight));
	}
	private double Near(double x ) {
		if(x<6*weight)
			return 1;
		else
			return Math.exp(-Math.pow(x-6.5*weight, 2)/(2*42*weight));
	}
	private double Min(double x1, double x2 ,double x3 ) {
		
		return Math.min(Math.min(x1, x2),x3);
	}
	public  double Sigmoid(double x) {
	    return (1/( 1 + Math.pow(Math.E,(-1*x))));
	  }
	private void Callback(ArrayList<String> solution , String type) {
			status.setText(status.getText().toString()+" CallBack Start! \n");
			System.out.println("CallBack Start! ");
			tvLeft.clear();
			tvMid.clear();
			tvRight.clear();
			callback_index = 0;
			callback_xyaxis.add(carX);
			callback_xyaxis.add(carY);
			callback_timeline= new Timeline(new KeyFrame(Duration.millis(5000/fps),new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent arg0) {
					// TODO Auto-generated method stub
					
					
					double deltaX,deltaY;
					int spilt = solution.get(callback_index).toString().lastIndexOf(" ");
					double theta = Double.parseDouble(
							solution.get(callback_index).toString()
							.substring(spilt+1,solution.get(callback_index).toString().length()));
					theta = -theta;
					
					System.out.println(theta);
					
					deltaX = weight*(Math.cos(Math.toRadians(degree+theta))
							+Math.sin(Math.toRadians(degree))
							*Math.sin(Math.toRadians(theta)));
					deltaY = -weight*(Math.sin(Math.toRadians(degree+theta))
							-Math.cos(Math.toRadians(degree))
							*Math.sin(Math.toRadians(theta)));
					
					car.setCenterX(carX+deltaX);
				    car.setCenterY(carY+deltaY);
				    carX = car.getCenterX();
				    carY = car.getCenterY();
				    
				    callback_xyaxis.add(carX);
				    callback_xyaxis.add(carY);
					
				    degree = degree + Math.toDegrees(Math.asin( 2*Math.sin(Math.toRadians(theta) * weight / (2*car.getRadius() ) )));
					
				    if(degree > 270) {
						degree = degree-360;
					}
					else if(degree < -90) {
						degree = degree+360;
					}
					
					
					for(int i = 0; i<lineset.size();i++) {
						Shape intersect = Shape.intersect(lineset.get(i),car);
					      if (intersect.getBoundsInLocal().getWidth() != -1){
							callback_timeline.stop();
							end_flag = true;
							status.setText(status.getText().toString()+" CallBack collision! \n");
							System.out.println("CallBack collision! ");
							break;
						}
					}
					
					// end of the game
					Shape intersect = Shape.intersect(destination, car);
				      if (intersect.getBoundsInLocal().getWidth() != -1) { 	
						if(!distination_flag) {
							distination_flag = true;
							car_destination_x = car.getCenterX();
							car_destination_y = car.getCenterY();
						}
						if(Math.abs(car.getCenterX()-car_destination_x) > Math.abs(Math.min(car.getRadius(),destination_width*weight))
								|| Math.abs(car.getCenterY()-car_destination_y) > Math.abs(Math.min(car.getRadius(),destination_height*weight))) {
							callback_timeline.stop();
							show_path_timeline.setCycleCount(callback_xyaxis.size()/2);
							show_path_timeline.play();
							status.setText(status.getText().toString()+"CallBack Arrive! \n");
							System.out.println("Callback Arrive! ");
							
						}
				      }
				      callback_index++;
				}
				
			}));
			show_path_timeline= new Timeline(new KeyFrame(Duration.millis(5000/fps),new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					// TODO Auto-generated method stub
					Line p = new Line(callback_xyaxis.get(callback_xyaxis.size()-2),callback_xyaxis.get(callback_xyaxis.size()-1),
							callback_xyaxis.get(callback_xyaxis.size()-2),callback_xyaxis.get(callback_xyaxis.size()-1));
					p.setStroke(Color.DARKTURQUOISE);
					p.setStrokeWidth(5.0);
					pane.getChildren().add(p);
					
					// remove x,y 
					callback_xyaxis.remove(callback_xyaxis.size()-1);
					callback_xyaxis.remove(callback_xyaxis.size()-1);
					
					int value = show_path_timeline.getCycleCount();
					System.out.println(value);
					show_path_timeline.stop();
					show_path_timeline.setCycleCount(value-1);
					show_path_timeline.play();
					if(show_path_timeline.getCycleCount() == 1) {
						status.setText(status.getText().toString()+" CallBack Finish!  \n");
						System.out.println("CallBack Finish! ");
						show_path_timeline.stop();
						show_path_timeline.setCycleCount(0);
					}
					
				}
				
			}));
			
			callback_timeline.setCycleCount(-1);
			callback_timeline.play();
			
	}
	private int TrainRBFN(Scene scene) {
		//TODO train RBFN
		// declare parameter
		double min_ef = -1;
		int groupNum = Integer.parseInt(((TextField)scene.lookup("#textView_gn")).getText().toString());
		double phi_global = Double.parseDouble(((TextField)scene.lookup("#textView_phi1")).getText().toString());
		double phi_local  = Double.parseDouble(((TextField)scene.lookup("#textView_phi2")).getText().toString());
		int epoch  = Integer.parseInt(((TextField)scene.lookup("#textView_epoch")).getText().toString());
		
		
		double time = System.currentTimeMillis();

		// init parameter
		ArrayList<ArrayList<Double>> totalWeight = new ArrayList<ArrayList<Double>>();
		for(int gn = 0 ; gn < groupNum ; gn++) {
			totalWeight.add(new ArrayList<Double>());
			for(int j = 0 ; j < hidden ; j++) {
				totalWeight.get(gn).add((double)Math.random()*2-1);
			}
			totalWeight.get(gn).add(-1.0);
		}
	
		double[][][] c =  new double[groupNum][hidden][3];	// 3 is fix number for input 
		for(int i = 0 ; i < hidden ; i++) {
			int tempIndex = (int)(Math.random()*train_left.size());
			for(int gn = 0 ; gn < groupNum ; gn++) {
				tempIndex = (int)(Math.random()*train_left.size());
				c[gn][i][0] = train_left.get(tempIndex);
				c[gn][i][1] = train_mid.get(tempIndex);
				c[gn][i][2] = train_right.get(tempIndex);
			}
		}
		
		double[][] sigma = new double[groupNum][hidden]; // three same value for init
		double[] max = new double[groupNum]; 
		//each 2 perceptron distance, thus nested loop is needed ,owing to all of group has same initial, compute c[0] is enough 
		for(int gn = 0; gn < groupNum ; gn++) {
			max[gn] = 0;
			for(int i = 0 ; i < hidden ; i++) {	
				for(int j = 0 ; j < hidden; j++) {
					double d = Math.sqrt(Math.pow(c[gn][i][0] - c[gn][j][0], 2) + Math.pow(c[gn][i][1] - c[gn][j][1], 2) + Math.pow(c[gn][i][2] - c[gn][j][2], 2));
					if(d > max[gn] ) {
						max[gn] = d;
					}
				}	
			}
			for(int j = 0 ; j < hidden ; j++) {
				sigma[gn][j] = max[gn]/Math.sqrt(hidden);	
			}
		}
		
		// init RBFN 
		RBFNweight = totalWeight.get(0);
		RBFNc = c[0];
		RBFNsigma = sigma[0];
		
		
		double golbal_best_error;
		double local_best_error[] = new double[groupNum];
		double velocity[][] = new double[groupNum][hidden+1];
		double velocityc[][][] = new double[groupNum][hidden][3];
		double velocitysigma[][] = new double[groupNum][hidden];
		
		@SuppressWarnings("unchecked")
		ArrayList<Double> local_best_weight[] = new ArrayList[groupNum];
		ArrayList<Double> golbal_best_weight  = new ArrayList<Double>();
		double local_best_c[][][] = new double[groupNum][hidden][3];
		double local_best_sigma[][] = new double[groupNum][hidden];
		double golbal_best_c[][]  = new double[hidden][3];
		double golbal_best_sigma[]  = new double[hidden];
		
		//init variable
		golbal_best_error = 999999;
		for(int i = 0 ; i < groupNum ; i++) {
			local_best_error[i] = 999999;
			local_best_weight[i] = new ArrayList<Double>(); 
		}
		for(int gn = 0 ; gn < groupNum ; gn++) {
			for(int i = 0 ; i < hidden ; i++) {
				for(int j = 0 ; j< 3 ; j++) {
					velocityc[gn][i][j] = 0;
				}
				velocitysigma[gn][i] = 0;
			}
		}
		// training 
		/*
		 * while(error > threshold){
		 * 		for each agent 
		 * 		do :
		 * 		for(int i = 0 to 3){
		 * 			compute z(|D|);
		 * 		}
		 * 		compute  output y 
		 * 		compute  error 
		 * 		compare error and do GA
		 * 		adjust c w sigma  
		 * }
		 * */
		int index = 0;
		while(epoch >= 0) {
			//get all of e
			double[] e = new double[groupNum];
			double[][] z = new double[groupNum][hidden];
			double[][]x = new double[groupNum][4];	// 4 for fix 3 input plus 1 bias
 			for(int gn = 0 ; gn < groupNum ; gn++) {
				for(int j = 0 ; j < hidden ; j++) {
					 x[gn][0] = train_left.get(index);
					 x[gn][1] = train_mid.get(index);
					 x[gn][2] = train_right.get(index);
					 x[gn][3] = 1;
					 
					 z[gn][j] = Gauss(c[gn][j],sigma[gn][j],train_left.get(index),train_mid.get(index),train_right.get(index));
					 
					 /*if(gn==0) {
						 System.out.println(String.format("%d.  z: %f. ",epoch,z[gn][j]));
					 }*/
				}
				double y = 0;
				for(int j = 0 ; j < hidden ; j++) {
					y += totalWeight.get(gn).get(j) * z[gn][j];
				}
				y += totalWeight.get(gn).get(totalWeight.get(gn).size()-1) * 1;
				y = 2 * Sigmoid(y) - 1;
				//System.out.println(String.format("%d.  training: groupNumber %d. y's-> %f (actual y %f)",epoch,gn,y,train_y.get(index)));
				e[gn] = (Math.abs(y - train_y.get(index)));
				e[gn] *= maxdistance;
				if(e[gn] == 0) {
					e[gn] = 0.0000001; 
				}
			//	System.out.println(String.format("dif:%f  y: %f  e[gn]:%f ",Math.abs(y - train_y.get(index)),train_y.get(index),e[gn]));	
				//System.out.println(String.format("total: %f, e[gn]: %f, y: %f, train_y: %f", total,e[gn],y,train_y.get(index)));
				index = (index+1) % train_y.size();
			}
 			
 			
			//System.out.println("test point"+1);
			//update min error 
 			double min_e = e[0];
 			
			for(int gn = 1 ; gn < groupNum ; gn++) {
				if(e[gn] < min_e) {
					min_e = e[gn];
				}
			}
			if(epoch == 0) {
 				int final_group = 0;
 				min_ef = min_e;
				for(int i = 0 ; i < groupNum ; i++) {
					if(e[final_group] > e[i]) {
						final_group = i ;
						min_ef = e[final_group];
					}
				}
				RBFNweight = totalWeight.get(final_group);
				RBFNc = c[final_group];
				RBFNsigma = sigma[final_group];
				break;
			}
		
			//System.out.println("test point"+2);
			
						
			// PSO
 			// update best perform history
 			for(int gn = 0 ; gn < groupNum ; gn++) {
 				if(e[gn] < golbal_best_error) {
 					golbal_best_error = e[gn];
 					golbal_best_weight.clear();
 					
 					for(int i = 0 ; i < totalWeight.get(gn).size() ; i++) {
 						golbal_best_weight.add(totalWeight.get(gn).get(i));	
 					}
 					//golbal_best_c  = c[gn].clone();
 					for(int i = 0 ; i < hidden ; i ++) {
 						for(int j = 0 ; j < 3 ; j++) {
 							golbal_best_c[i][j] = c[gn][i][j];
 						}
 					}
 					//golbal_best_sigma = sigma[gn].clone();
 					for(int i = 0 ; i < hidden ; i ++) {
 							golbal_best_sigma[i] = sigma[gn][i];
 					}
 					
 				}
 				if(e[gn] < local_best_error[gn]) {
 					local_best_error[gn] = e[gn];
 					local_best_weight[gn].clear();
 					for(int i = 0 ; i < totalWeight.get(gn).size() ; i++) {
 						local_best_weight[gn].add(totalWeight.get(gn).get(i));
 					 }
 					//local_best_c[gn] = c[gn].clone();
 					for(int i = 0 ; i < hidden ; i ++) {
 						for(int j = 0 ; j < 3 ; j++) {
 							local_best_c[gn][i][j] = c[gn][i][j];
 						}
 					}
 					//local_best_sigma[gn] = sigma[gn].clone();
 					for(int i = 0 ; i < hidden ; i ++) {
							local_best_sigma[gn][i] = sigma[gn][i];
 					}
 				}
 			}
 			/*for(int i = 0 ; i < 4 ; i++) {
				System.out.print(golbal_best_weight.get(i)+" ");
 			}
 			System.out.println("global");
 			for(int gn = 0 ; gn < groupNum ; gn++) {
 				for(int i = 0 ; i < 4 ; i++) {
 					System.out.print(local_best_weight[gn].get(i)+" ");
				 }
 				System.out.println("end");
 			}
 			System.out.println("local");
 			for(int gn = 0 ; gn < groupNum ; gn++) {
 				for(int i = 0 ; i < 4 ; i++) {
 					System.out.print(totalWeight.get(gn).get(i)+" ");
				 }
 				System.out.println("");
 			}
 			System.out.println("weight");*/
 			
 			// update weight
			for(int gn = 0 ; gn < groupNum ; gn++) {
				//System.out.print("e[gn]: "+ e[gn] + " ");
				for(int i = 0 ; i < hidden+1 ;i++) {
					velocity[gn][i] = velocity[gn][i]
							+ phi_local * ( local_best_weight[gn].get(i) - totalWeight.get(gn).get(i))
							+ phi_global * ( golbal_best_weight.get(i) - totalWeight.get(gn).get(i));
					if(velocity[gn][i] > 2) {
						velocity[gn][i] = 2;
					}
					double temp = totalWeight.get(gn).get(i) + velocity[gn][i];
					/*System.out.println(String.format("totalWeight: %f, local_best: %f, golbal_best: %f, velocity: %f",
						totalWeight.get(gn).get(i),
						local_best_weight[gn].get(i),
						golbal_best_weight.get(i),
						velocity[gn][i]));*/
					temp = Normalization(temp);
					
					totalWeight.get(gn).remove(i);
					totalWeight.get(gn).add(i,temp); 
					
					if(totalWeight.get(gn).get(i).isNaN() || Double.isInfinite(totalWeight.get(gn).get(i))) {
						System.out.println("error5");
						System.exit(0);
					}
				}
				//System.out.println();
			}
			
			

			//System.out.println("test point"+6);
			//update c 
			for(int gn = 0 ; gn < groupNum ; gn++) {
				for(int i = 0 ; i < hidden ; i++) {
					for(int j = 0 ; j < 3 ;j++) {
						velocityc[gn][i][j] = velocityc[gn][i][j]
								+ phi_local * ( local_best_c[gn][i][j] - c[gn][i][j])
								+ phi_global * ( golbal_best_c[i][j] - c[gn][i][j]);
						if(velocityc[gn][i][j] > 2) {
							velocityc[gn][i][j] = 2;
						}
						c[gn][i][j] = c[gn][i][j] + velocityc[gn][i][j];
						
						//System.out.print(String.format("c[gn][%d][%d]: %f (%f) local: %f, golbal: %f ", i,j,c[gn][i][j],velocityc[gn][i][j],local_best_c[gn][i][j],golbal_best_c[i][j]));
						if(Double.isInfinite(c[gn][i][j])) {
							System.out.println(String.format("error4 totalWeight: %f, e[gn]: %f, z[gn][i]: %f, sigma[gn][i]: %f, ",totalWeight.get(gn).get(i),e[gn],z[gn][i],sigma[gn][i]));
							System.exit(0);
						}
					}
				}
				//System.out.println("");
			}

			//System.out.println("test point"+7);
			//update sigma
			for(int gn = 0 ; gn < groupNum ; gn++) {
				for(int i = 0 ; i <hidden ; i++) {
					
					velocitysigma[gn][i] = velocitysigma[gn][i]
							+ phi_local * ( local_best_sigma[gn][i] - sigma[gn][i])
							+ phi_global * ( golbal_best_sigma[i] - sigma[gn][i]);
					if(velocitysigma[gn][i] > 2) {
						velocitysigma[gn][i] = 2;
					}
					sigma[gn][i] = sigma[gn][i] + velocitysigma[gn][i];
					
					if(Double.isNaN(sigma[gn][i])) {
						System.out.println(String.format("error3 totalWeight: %f, e[gn]: %f, z[gn][i]: %f,",totalWeight.get(gn).get(i),e[gn],z[gn][i]));
						System.exit(0);
					}
				}
			}
	
			
			
			System.out.println(String.format("epoch :%d, min error: %f", epoch,min_e));
			epoch--;
		}
		
		status.setText(status.getText().toString()+
				String.format("end of training epoch :%d,  last epoch min error: %f\n",
						Integer.parseInt(((TextField)scene.lookup("#textView_epoch")).getText().toString()),
						min_ef));
		status.selectPositionCaret(status.getText().length());
		((TextField)scene.lookup("#textView_training_time")).setText((String.valueOf((System.currentTimeMillis()-time)/1000))+"'s");
		return 0;
	}
	private double ComputeTheta(double left , double mid , double right){
		 double z;
		 double y = 0;
		 for(int i = 0 ; i < hidden ; i++) {
			 z = Gauss(RBFNc[i], RBFNsigma[i], left / maxdistance, mid / maxdistance, right / maxdistance);
			 y += RBFNweight.get(i) * z;
			 System.out.println(" z : " + z+"   y : " + y+"weight: "+RBFNweight.get(i));
		 }
		 y += RBFNweight.get(RBFNweight.size()-1)*1;
		 
		 y = 2*Sigmoid(y)-1;
		 
		return y*40;
	}
	
	private double Gauss(double[] c , double sigma , double left , double mid , double right) {
		double distance = Math.pow(c[0]-left, 2) + Math.pow(c[1] - mid , 2) + Math.pow(c[2] - right, 2);
		//System.out.println(String.format("sigma %f.  left: %f. mid %f right %f , distance %f",sigma,left,mid,right,distance));
		/*if(Double.isNaN(Math.exp(- distance / (2*Math.pow(sigma, 3))))) {
			 System.out.println("error sigma:" + sigma +" distance: " + distance);
			 System.out.println(String.format("c0: %f, c1: %f, c2: %f ", c[0],c[1],c[2]));
			 System.out.println(String.format("left: %f, mid: %f, right: %f ", left,mid,right));
			 System.exit(0);
		 }
		if(Double.isInfinite(Math.exp(- distance / (2*Math.pow(sigma, 3))))) {
			 System.out.println("error2 sigma:" + sigma +" distance: " + distance);
			 System.out.println(String.format("c0: %f, c1: %f, c2: %f ", c[0],c[1],c[2]));
			 System.exit(0);
		}*/
		//System.out.println("distance: "+ distance + " exp: " + - distance / (2*Math.pow(sigma, 2))+"sigma: "+ sigma);
		return Math.exp(- distance / (2*Math.pow(sigma,1/2)));
	}
	
	private double Normalization(double d) {
		if(d>1) {
			return 1;
		}else if( d< -1) {
			return -1;
		}else {
			return d;
		}
	}
}
