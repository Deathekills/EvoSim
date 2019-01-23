package Ecosystem;

public class Feline extends Mammal implements Carnivore{

	public int fangSize, claw;
	
	public Feline(String imageName, int size, int speed, int lifespan, String gender) {
		super(imageName, size, speed, lifespan, gender);
		furDensity = 20;
		furLength = 30;
		intelligence = 30;
		fangSize = 30;
		claw = 30;
	}
	
	public Feline(Feline feline) {
		super(feline);
		this.fangSize = feline.fangSize;
		this.claw = feline.claw;
	}
	
	public Feline(Feline feline, boolean canMate) {
		super(feline, canMate);
		this.fangSize = feline.fangSize;
		this.claw = feline.claw;
	}
	
	public Animal mate(Animal mate) {
		if (this.canMate(mate))
		{
			this.mateTimer = 3;
			return new Feline(this, this.canMate(mate));
		}
		
		return null;	
	}
	
	public void stalk(){
		
	}
	public void chase(Animal prey){
		
	}
	public void feed(Animal prey){
		
	}
}
