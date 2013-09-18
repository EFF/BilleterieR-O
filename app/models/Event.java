package models;

import java.util.ArrayList;
import java.util.Iterator;

public class Event {
	public long id;
	public ArrayList<Category> categories;
	
	public Event(){
		categories = new ArrayList<>();
	}
	
	public int getTotalNumberOfTickets(){
		int total = 0;
		
		Iterator<Category> itr = categories.iterator();
		while(itr.hasNext()){
			Category currentCategory = itr.next();
			total += currentCategory.numberOfTickets; 
		}
		
		return total;
	}
}
