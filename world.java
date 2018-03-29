
import tester.*;
import javalib.funworld.*;
import javalib.worldimages.*;
import javalib.colors.*;
import java.awt.Color;
import java.util.*;


interface lom{
	public WorldImage missImage();
	public lom moveSprite();
	public miss getfirst();
	public lom getrest();
	public lom addmiss();
	public boolean hitground();
}

class emptymiss implements lom {
	emptymiss() {}
	
	public WorldImage missImage() {
		return new RectangleImage(new Posn(0,0), 10, 10, Color.white); 
	}
	
	public lom moveSprite() {return this;}
	
	public miss getfirst() {
		throw new RuntimeException("empty list");
	}
	public lom getrest() {
		throw new RuntimeException("empty list");
	}
	public lom addmiss() {
		Random rand = new Random();
		return new consmiss(new miss(new Posn(
				rand.nextInt(580) + 10, 0), new Posn(0, rand.nextInt(6) + 8)), this);
	}
	
	public boolean hitground() {
		return false;
	}
	
}

class consmiss implements lom {
	miss first;
	lom rest;
	
	consmiss(miss first, lom rest) {
		this.first = first;
		this.rest = rest;
	}
	
	public WorldImage missImage() {
		return this.first.missImage().overlayImages(this.rest.missImage());
	}
	
	public lom moveSprite() {
		return new consmiss(this.first.moveSprite(), this.rest.moveSprite());
	}
	
	public miss getfirst() {return this.first;}
	public lom getrest() {return this.rest;}
	public lom addmiss() {return this;}
	public boolean hitground() {
		return this.first.loc.y > 575 ||
				this.rest.hitground();
	}
	
}

//represent  missile
class miss { 
	Posn loc;
	Posn v;
	miss(Posn loc, Posn v) {
		this.loc = loc;
		this.v = v;
	}
	//make the missile move
	public miss moveSprite( ) { 
		return new miss(new Posn(this.loc.x , this.loc.y + this.v.y), this.v);
	}
	
	WorldImage missImage() {
		return new RectangleImage(this.loc, 20, 20, Color.red);
	}
	
}

//represent the bullet
interface lob {
	public lob moveSprite();
	public WorldImage bullImage();
	public lob collide(miss m);
	public boolean collideha(miss m);
}
class emptybull implements lob{
	emptybull(){}
	public lob moveSprite() {return this;}
	public WorldImage bullImage() {
		return new RectangleImage(new Posn(0,0), 10, 10, Color.white); 
	}
	public lob collide(miss m) {return this;}
	public boolean collideha(miss m) {return false;}
}

class consbull implements lob {
	bull first;
	lob rest;
	
	consbull(bull first, lob rest) {
		this.first = first;
		this.rest = rest;
	}
	
	public lob moveSprite() {
		return new consbull(this.first.moveSprite(), this.rest.moveSprite());
	}
	
	public WorldImage bullImage() {
		return this.first.bullImage().overlayImages(this.rest.bullImage());
	}
	
	public lob collide(miss m) {
		if (Math.abs(m.loc.x - this.first.loc.x) < 15 &&
		Math.abs(m.loc.y - this.first.loc.y) < 15) 
			return this.rest;
		else
			return new consbull(this.first, this.rest.collide(m));
	}
	
	public boolean collideha(miss m){
		if (Math.abs(m.loc.x - this.first.loc.x) < 15 &&
				Math.abs(m.loc.y - this.first.loc.y) < 15) 
					return true;
				else
					return this.rest.collideha(m);
	}
	
}

class bull { 
	Posn loc;
	Posn v;
	bull(Posn loc, Posn v) { 
		this.loc = loc;
		this.v = v;
	}
	//make the bullets move
	public bull moveSprite( ) { 
		return new bull(new Posn(this.loc.x , this.loc.y + this.v.y), this.v);
	}
	
	WorldImage bullImage() {
		return new RectangleImage(this.loc, 10, 10, Color.green);
	}

	
}
//represent the gun
class gun { 
	Posn loc;
	gun(Posn loc) { 
		this.loc = loc;
	}
	
	WorldImage gunImage( ) {
		return new RectangleImage(this.loc, 100, 60, Color.black).overlayImages(
				new RectangleImage(new Posn(this.loc.x,this. loc.y-30), 10, 15, Color.black));
	}
	public gun onKeyEvent(String ke) { 
		if (ke.equals("left"))
			return this.moveLeft();
		else if (ke.equals("right"))
			return this.moveRight();
		else 
			return this;
	}
	public gun moveLeft( ) { 
		return new gun(new Posn(this.loc.x - 9, this.loc.y));
		
	}
	public gun moveRight( ) {
		return new gun(new Posn(this.loc.x + 9, this.loc.y));
	}

}

//make the world
class woyaokule extends World { 
	gun gun;
	lob lob;
	lom lom;
	int count;
	
	woyaokule(gun gun, lob lob, lom lom, int count){
		this.gun = gun;
		this.lob = lob;
		this.lom = lom;
		this.count = count;
	}
    /*
	public int count() {
		int i = 0;
		if (this.lob.collideha(this.lom.getfirst()))
			return i = i + 1;
		else
			return i;
	}
	
	public World count() {
		if (this.lob.collideha(this.lom.getfirst()))
			return new woyaokule(this.gun, this.lob, this.lom, this.count + 1);
		else
			return this;
	}
	*/
	
	public WorldImage makeImage() {
		return this.lob.bullImage().overlayImages(this.gun.gunImage().overlayImages(this.lom.missImage()))
				.overlayImages(new TextImage(new Posn(550, 20), "Score: " + this.count, Color.black));
	}
	
	public World onTick() {
		return new woyaokule(this.gun, this.lob.moveSprite(), this.lom.moveSprite(), this.count).remove();
	}
	
	public World onKeyEvent(String ke) {
		if (ke.equals("up"))
			return new woyaokule(this.gun.onKeyEvent(ke), new consbull(new bull(new Posn(this.gun.loc.x, 565),
					new Posn(0, -3)), this.lob), this.lom, this.count);
		return new woyaokule(this.gun.onKeyEvent(ke),this.lob, this.lom, this.count);
	}
	
	public World remove() {
		if (this.lob.collideha(this.lom.getfirst()))
			return new woyaokule(this.gun, this.lob.collide(this.lom.getfirst()), this.lom.getrest().addmiss(), this.count + 1);
		else
			return this;
	}
	
	public WorldEnd worldEnds() {
		if (this.lom.hitground())
			return new WorldEnd(true,
					this.makeImage().overlayImages(
							new TextImage(new Posn(300, 300),
									"You died", Color.black)));
		else
			return new WorldEnd(false, this.makeImage());
	}

}



	 
	 
		 
		 
		 
	 

	 
	


  
 

