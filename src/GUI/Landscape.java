package GUI;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import Ecosystem.*;

public class Landscape {

	Tile[][] land;
	String weather;
	int temperature = 50;

	public Landscape() {
		land = new Tile[100][120];
		String[] tiles = null;
		int counter = 0;

		try
		{
			Scanner reader = new Scanner(new File("Summative Graphics\\landscape.txt"));
			while (reader.hasNextLine())
			{
				String in =	reader.nextLine();
				tiles = in.split(",");

				for (int c = 0; c < land[0].length; c++)
				{
					try 
					{
						land[counter][c] = new Tile (tiles[c]);
					} 
					catch (ArrayIndexOutOfBoundsException e) 
					{
						System.out.println("ohno");
					}
				}

				counter++;
			}

			reader.close();
		}
		catch (IOException e)
		{
			System.out.println("error landscape");
		}

		// generate plants
		for (int r = 0; r < land.length; r++)
		{
			for (int c = 0; c < land[0].length; c++)
			{
				if (land[r][c].territory.ground.equals("grass"))
				{
					int chance = (int) (Math.random() * 401);

					if(chance == 91 || chance == 92)
						land[r][c].territory.grow("shrub", 44);
					else if (chance == 93)
						land[r][c].territory.grow("tree", 111);
					 
					/*else if (chance >= 70)									OTHER PLANTs
						land[r][c].territory.grow("tree");
					else if (chance >= 60)
						land[r][c].territory.grow("tree");
					else if (chance >= 50)
						land[r][c].territory.grow("tree");
					 */
				}
			}
		}
	}

	public void show(Graphics g) {

		for (int r = 0; r < land.length; r++)
		{
			for (int c = 0; c < land[0].length; c++)
			{
				// draw ground and plant --> maybe move this somewhere?
				try 
				{	
					g.drawImage(land[r][c].territory.groundImg, c * 10, r * 10, 10, 10, null);

				} 
				catch (NullPointerException e) {}
			}
		}

		for (int r = 0; r < land.length; r++)
		{
			for (int c = 0; c < land[0].length; c++)
			{
				// draw ground and plant --> maybe move this somewhere?
				try {	
					if (land[r][c].occupied())
					{
						if (land[r][c].animal.controlled())
							g.drawImage(land[r][c].animal.appearance, c * 10 - 20, r * 10 - 20, 60, 60, null);					
						else
							g.drawImage(land[r][c].animal.appearance, c * 10 - 10, r * 10 - 10, 40, 40, null);					
						
					}
					if (land[r][c].territory.plant != null)
						g.drawImage(land[r][c].territory.plantImg, c * 10 -land[r][c].territory.plant.size/2 , r * 10 - land[r][c].territory.plant.size/2, land[r][c].territory.plant.size, land[r][c].territory.plant.size, null);
					if (land[r][c].territory.hasResource())
					{
						for (int x = 0; x < land[r][c].territory.resourceList().size() -1; x++) 
							g.drawImage(land[r][c].territory.resourceList().get(x).resourceImage, c * 10 - 15, r * 10 - 15, 30, 30, null);
								
					}
						
				} catch (NullPointerException e) {}
			}
		}

		//		Image appearance = null;
		//		try {
		//			appearance = ImageIO.read(new File("Summative Graphics\\animal2.png"));
		//		}
		//		catch (Exception ex) {}
		//
		//		g.drawImage(appearance, 12, 12, 111,111, null);
	}

	public void populate(Animal animal) {

		//for every cell in the grid, place true or false value --> true is more likely with a higher density
		for (int r = 0 ; r < land.length ; r++)
		{
			for (int c = 0 ; c < land[0].length ; c++)
			{
				if (Math.floor(Math.random () * 650) < 1 && !land[r][c].territory.ground.equals("water"))
				{
					Animal newAnimal = null;
					if (animal instanceof Mammal)
						newAnimal = new Mammal((Mammal) animal);

					land[r][c].add(newAnimal);
				}
			}
		}
	}

	public ArrayList<String> makeInstructions(Pair[][] vis, int wantX, int wantY) {
		ArrayList<String> instruct = new ArrayList<String>();
		
		if (wantX != -1) {
			Pair cur = vis[wantX][wantY];
			int curx = wantX, cury = wantY;
			
			while (cur.x != -1) {
				if (cur.x == curx + 1) {
					instruct.add(0, "left");
				}
				else if (cur.x == curx - 1) {
					instruct.add(0, "right");
				}
				else if (cur.y == cury + 1) {
					instruct.add(0, "up");
				}
				else {
					instruct.add(0, "down");
				}
				cur = vis[cur.x][cur.y];
			}
		}
		
		return instruct;
	}
	
	public ArrayList<String> findResource(int r, int c, Resource resource) {

		Pair vis[][] = new Pair[land.length][land[0].length];
		
		vis[r][c].x = -1;
		vis[r][c].y = -1;
		vis[r][c].visited = true;
		
		Queue<Pair> q = new LinkedList<Pair>();

		q.add(vis[r][c]);

		int wantX = -1, wantY = -1;
		
		while (!q.isEmpty()) {
			
			boolean keepSearching = true;
			
			Pair cur = q.poll();
			
			ArrayList<Resource> res = land[cur.x][cur.y].territory.resources();
			for (int i = 0; i < res.size(); i++) {
				if (res.get(i).getName() == resource.getName()){
					wantX = cur.x;
					wantY = cur.y;
					while (!q.isEmpty()) {
						q.poll();
					}
					keepSearching = false;
				}
			}
			
			// mark the wanted thing with -1
			
			if (keepSearching) {
				//left
				if (cur.y-1 > 0) {
					if (!vis[cur.x][cur.y-1].visited) {
						q.add(new Pair(cur.x, cur.y-1));
						vis[cur.x][cur.y-1].x = cur.x;
						vis[cur.x][cur.y-1].y = cur.y;
						vis[cur.x][cur.y-1].visited = true;
					}
				}
				// right
				if (cur.y+1 > 0) {
					if (vis[cur.x][cur.y+1].visited) {
						q.add(new Pair(cur.x, cur.y+1));
						vis[cur.x][cur.y+1].x = cur.x;
						vis[cur.x][cur.y+1].y = cur.y;
						vis[cur.x][cur.y+1].visited = true;
					}
				}
				// up
				if (cur.x-1 > 0) {
					if (vis[cur.x-1][cur.y].visited) {
						q.add(new Pair(cur.x-1, cur.y));
						vis[cur.x-1][cur.y].x = cur.x;
						vis[cur.x-1][cur.y].y = cur.y;
						vis[cur.x-1][cur.y].visited = true;
					}
				}
				// down
				if (cur.x+1 > 0) {
					if (vis[cur.x+1][cur.y].visited) {
						q.add(new Pair(cur.x+1, cur.y));
						vis[cur.x+1][cur.y].x = cur.x;
						vis[cur.x+1][cur.y].y = cur.y;
						vis[cur.x+1][cur.y].visited = true;
					}
				}
			}
		}
		
		return makeInstructions(vis, wantX, wantY);
	}

	public void advance() {

		// set up nextGen array
		Tile nextGen[][] = new Tile [land.length][land[0].length];

		for (int r = 0; r < land.length; r++) {
			for (int c = 0; c < land[0].length; c++)
			{
				nextGen[r][c] = new Tile(land[r][c]);
				nextGen[r][c].animal = null;
			}

		}

		// coordinate animal movement
		for (int r = 0; r < land.length; r++)
		{
			for (int c = 0; c < land[0].length; c++)
			{
				if (land[r][c].planted())
				{
					land[r][c].territory.release();
				}
				
				if (land[r][c].occupied() && land[r][c].animal.health() >= 1)
				{	
					//System.out.println(r + ", " + c);
					land[r][c].animal.update();

					
					int upDown = (int) (Math.random() * 3) - 1;
					int leftRight = (int) (Math.random() * 3) - 1;

					if (c == 0 && leftRight == -1)
						leftRight = 1;
					if (c == land[0].length-1 && leftRight == 1)
						leftRight = -1;
					if (r == 0 && upDown == -1)
						upDown = 1;
					if (r == land.length-1 && upDown == 1)
						upDown = -1;

					if (!nextGen[r + upDown][c + leftRight].occupied() && !nextGen[r + upDown][c + leftRight].territory.ground.equals("water"))
						nextGen[r + upDown][c + leftRight].add(land[r][c].animal);
					else if (!nextGen[r + upDown][c].occupied() && !nextGen[r + upDown][c].territory.ground.equals("water"))
						nextGen[r + upDown][c].add(land[r][c].animal);
					else if (!nextGen[r][c + leftRight].occupied() && !nextGen[r][c + leftRight].territory.ground.equals("water"))
						nextGen[r][c + leftRight].add(land[r][c].animal);
					else if (!nextGen[r][c].occupied())
						nextGen[r][c + leftRight].add(land[r][c].animal);
				}
			}
		}

		// coordinate animal interactions
		for (int r = 0; r < nextGen.length; r++)
		{
			for (int c = 0; c < nextGen[0].length; c++)
			{
				if (nextGen[r][c].occupied())
				{
					Animal baby = null;
					int emptyRow = -1, emptyCol = -1;

					if (r + 1 < nextGen.length && nextGen[r + 1][c].occupied())
						baby = nextGen[r][c].animal.mate(nextGen[r + 1][c].animal);
					else {
						emptyRow = r + 1;
						emptyCol = c;
					}

					int compare = r - 1;				
					while (c + 1 < nextGen[0].length && compare <= r + 1 && compare > -1 && compare < nextGen.length) {
						if (nextGen[compare][c + 1].occupied() && baby == null) {
							baby = nextGen[r][c].animal.mate(nextGen[compare][c + 1].animal);
						}
						else {
							emptyRow = compare;
							emptyCol = c + 1;
						}

						compare++;
					}

					if (baby != null && emptyRow != -1 && Math.random() < 0.3) {
						nextGen[emptyRow][emptyCol].add(baby);
						System.out.println("BABY MADE" + emptyRow + emptyCol);
					}
				}

			}
		}

		land = nextGen; 
	}
	
	public void updateRR(double rate)
	{
		for (int row = 0; row < land.length; row++)
		{
			for (int col = 0; col < land[0].length; col++)
			{
				land[row][col].territory.changeRate(rate);
			}
				
		}
	}
	
	public void updateTemp(int temp)
	{
		temperature = temp;
	}
}

class Pair {
	
	public int x, y;
	public boolean visited = false;
	
	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
}
