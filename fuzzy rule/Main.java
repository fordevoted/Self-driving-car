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
	private Button bnMap,bnSolution,bnRun;
	private TextField tvLeft,tvMid,tvRight;
	private TextArea status;
	private Circle car;
	private Rectangle destination; 
	private ArrayList<Line> lineset = new ArrayList<Line>();
	private ArrayList<double[]> function = new ArrayList<double[]>();
	private Timeline timeline,delete_timeline,callback_timeline,show_path_timeline;
	
	private ArrayList<String> maze  = new ArrayList<String>();
	private double weight = 4.0;
	private double biasX = 200, biasY = 400;		//270,330
	private double degree  ,orign_degree ;
	private double carX,carY,car_beginX = 0,car_beginY = 0;
	private double fps = 100;
	private int count = 0, count_orign_in_map = 0; 
	private double left, midium , right;
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
		AnchorPane mainPane = (FXMLLoader.load(getClass().getResource("main_layout.fxml")));
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
			ArrayList<String> solution = new ArrayList<String>();
			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showOpenDialog((Stage)scene.getWindow());				
				String str = new String();
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
			System.out.format("i %d a %f b %f v %f\n", i,temp[0],temp[1],temp[2]);
			
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
		double theta =  0;  
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
			 */
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
			 */
			theta = (inference1*right_set[2]*mid_set[2] + inference2*right_set[2]*mid_set[1] + 
					inference3*right_set[1]*mid_set[2] + inference4*right_set[1]*mid_set[1] /
					(right_set[2]*mid_set[2]+right_set[2]*mid_set[1]+right_set[1]*mid_set[2]+right_set[1]*mid_set[1]));
		}
		
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
 		timeline= new Timeline(new KeyFrame(Duration.millis(5000/fps),new EventHandler<ActionEvent>() {
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
 		delete_timeline= new Timeline(new KeyFrame(Duration.millis(5000/fps),new EventHandler<ActionEvent>() {
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
					
				    degree = degree +Math.toDegrees(Math.asin( 2*Math.sin(Math.toRadians(theta)*weight / (2*car.getRadius() ) )));
					
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
}
