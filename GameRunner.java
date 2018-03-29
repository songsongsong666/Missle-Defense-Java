import tester.*;
import javalib.funworld.*;
import javalib.worldimages.*;
import javalib.worldcanvas.*;
import javalib.colors.*;
import java.awt.Color;

public class GameRunner {
	
	gun gun1 = new gun(new Posn(300,600));
	bull bull1 = new bull(new Posn(300, 565), new Posn(0,-4));
	miss miss1 =new miss(new Posn (300,0), new Posn(0, 9));
	woyaokule wykl = new woyaokule(gun1,new consbull(bull1, new emptybull()),
			new consmiss(miss1, new emptymiss()), 0);
	WorldCanvas c1 = new WorldCanvas(600,600);

	//boolean makeDranwing = 
		//	c1.show();
	
	boolean run() {
		return this.wykl.bigBang(600, 600, 0.1);
	}
	
	public static void main(String [] argv){
		GameRunner ku = new GameRunner();
		ku.run();
	}
}
