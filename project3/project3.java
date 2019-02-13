package project3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class project3 {
	static public class Job{
		private int timein;
		private int totalLength;
		private char name;
		public Job(char c1, int i1, int i2){
			
			//int i1 = 0;
			//int i2 = 0;
			//char c1 = 'A';
			//some code to turn the string into stuff
			timein = i1;
			totalLength = i2;
			name = c1;
		}
		void decrementLength(){
			totalLength--;
		}
		int getTimeIn(){
			return timein;
		}
		int getTotalLength(){
			return totalLength;
		}
		char getName(){
			return name;
		}
			
	}
	static public class Scheduler{
		private	ArrayList<Job> jobs;
		private ArrayList<ArrayList<Character>> diagram = new ArrayList<ArrayList<Character>>(0);
			
		public Scheduler(String s/*takes a filename as a string*/){
			//code to read the file and import it into jobs
			Scanner x = null;
			try {
				x = new Scanner(new File(s));
				System.out.println("Success!\n");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(x.hasNext()){
				String a = x.next();
				String b = x.next();
				String c = x.next();
				jobs.add(new Job(a.charAt(0), Integer.parseInt(b), Integer.parseInt(c)));
			}
			x.close();
		}
		void updateDiagram(char x, int time){//given a time and a character it marks the graph at the appropriate spots 
			if (diagram.isEmpty()){
				diagram.add(new ArrayList<Character>());
				for(int i = 0; i < jobs.size(); i++){
					diagram.get(0).add(jobs.get(i).getName());
				}
			}
			//finds the position of the letter on the chart
			diagram.add(new ArrayList<Character>());
			for(int i = 0; i < diagram.get(0).size(); i++){
				if(diagram.get(0).get(i) == x){
					diagram.get(diagram.size()-1).add('X');
				}else{
					diagram.get(diagram.size()-1).add(' ');
				}
			}
			
		}
		void printDiagram(){
			//prints the diagram
			for(int i = 0; i < diagram.get(0).size();i++){
				for(int j = 0; j < diagram.size(); j++){
					System.out.print(diagram.get(j).get(i));
				}
				System.out.println("\n");
			}
			
			this.jobs = new ArrayList<Job>();
		}
		void FCFS(){
			int time = 0;
			while(!jobs.isEmpty()){
				//find who came in first most recently
				int mostRecent = 0;
				int position = 0;
				for(int i = 0; i < jobs.size(); i++){
					if(jobs.get(i).getTimeIn() < mostRecent || jobs.get(i).getTimeIn() == 0){
						mostRecent = jobs.get(i).getTimeIn();
						position = i;
					}
				}
				this.updateDiagram(jobs.get(position).getName(), time);
				jobs.get(position).decrementLength();
				if(jobs.get(position).getTotalLength() <= 0){
					jobs.remove(position);
				}
				time++;
			}
			this.printDiagram();
		}
		void RR(){
			ArrayList<Job> queue = new ArrayList<Job>(0); //we use the stack to simulate turn taking
			int wait = 1;
			int time = 0;
			while(!jobs.isEmpty()){
				for(int i = 0; i < wait; i++){
					//add jobs onto the end of the queue at their respective entry time
					for(int j = 0; j < jobs.size(); j++){
						if(jobs.get(j).getTimeIn() == time){
							queue.add(jobs.get(j));
						}
					}
					//if the queue isn't empty, add a bunch of junk to the diagram
					if (!queue.isEmpty()){
						this.updateDiagram(queue.get(0).getName(), time);
						jobs.get(0).decrementLength();
						if(jobs.get(0).getTotalLength() <= 0){
							time++;
							break;
						}
					}
					time++;
				}
				//maintenance stuff...?
				//after the set number of quanta, move whoever is at the front of the queue to the back, as long as they still have time left
				if(queue.get(0).getTotalLength() <= 0){
					for(int j = 0; j < jobs.size(); j++){
						if(jobs.get(j).getName() == queue.get(0).getName()){
							jobs.remove(j);
							break;
						}
					}
					queue.remove(0);
				}else{
					queue.add(queue.get(0));
					queue.remove(0);
				}
			}
			this.printDiagram();
			
		}
		void SPN(){//Shortest process next
			int time = 0;
			int SL = 0; //integer for tracking shortest length
			boolean running = false;
			ArrayList<Job> LJList = new ArrayList<Job>(0);
			while(!jobs.isEmpty()){
				//when a process shows up it is put into the local jobs list
				for(int i = 0; i < jobs.size(); i++){
					//add jobs onto the end of the queue at their respective entry time
					if(jobs.get(i).getTimeIn() == time){
						LJList.add(jobs.get(i));
					}
				}
				//if a process isn't running search the local jobs list for the shortest job
				if(!running){
					SL = 0;
					for(int i = 0; i < LJList.size(); i++){
						if(LJList.get(i).getTotalLength() <= LJList.get(SL).getTotalLength()){
							SL = i;
						}
					}
					running = true;
				}
				//that process runs to completion
				this.updateDiagram(LJList.get(SL).getName(), time);
				LJList.get(SL).decrementLength();
				//when the process finishes, remove from jobs list and local job list and stop running
				if(LJList.get(SL).getTotalLength() == 0){
					for(int j = 0; j < jobs.size(); j++){
						if(jobs.get(j).getName() == LJList.get(SL).getName()){
							jobs.remove(j);
							break;
						}
					}
					LJList.remove(SL);
					running = false;
				}
				time++;
			}
			this.printDiagram();
		}
		void SRT(){ //Shortest remaining time. 
			int time = 0;
			int SL = 0; //integer for tracking shortest length
			ArrayList<Job> LJList = new ArrayList<Job>(0);
			while(!jobs.isEmpty()){
				//when a process shows up it is put into the local jobs list
				for(int i = 0; i < jobs.size(); i++){
					//add jobs onto the end of the queue at their respective entry time
					if(jobs.get(i).getTimeIn() == time){
						LJList.add(jobs.get(i));
					}
				}
				//search the local jobs list for the shortest job
				SL = 0;
				for(int i = 0; i < LJList.size(); i++){
					if(LJList.get(i).getTotalLength() <= LJList.get(SL).getTotalLength()){
						SL = i;
					}
				}
				//that process runs once
				this.updateDiagram(LJList.get(SL).getName(), time);
				LJList.get(SL).decrementLength();
				//if the process finishes, remove from jobs list and local job list and stop running
				if(LJList.get(SL).getTotalLength() == 0){
					for(int j = 0; j < jobs.size(); j++){
						if(jobs.get(j).getName() == LJList.get(SL).getName()){
							jobs.remove(j);
							break;
						}
					}
					LJList.remove(SL);
				}
				time++;
			}
			this.printDiagram();
		}
		void HRRN(){//???? max((arrival_time + exec_time)/exec_time)
			int time = 0;
			int HRRNmax = 0;
			boolean running = false;
			ArrayList<Job> LJList = new ArrayList<Job>(0);
			while(!jobs.isEmpty()){
				//when a process shows up it is put into the local jobs list
				for(int i = 0; i < jobs.size(); i++){
					//add jobs onto the end of the queue at their respective entry time
					if(jobs.get(i).getTimeIn() == time){
						LJList.add(jobs.get(i));
					}
				}
				//if not running find the max((arrival_time + exec_time)/exec_time) job
				if(!running){
					HRRNmax = 0;
					for(int i = 0; i < LJList.size(); i++){
						//comparison if statement
						if(((LJList.get(i).getTotalLength() + LJList.get(i).getTimeIn())/LJList.get(i).getTotalLength()) >= ((LJList.get(HRRNmax).getTotalLength() + LJList.get(HRRNmax).getTimeIn())/LJList.get(HRRNmax).getTotalLength())){
							HRRNmax = i;
						}
					}
					running = true;
				}
				//that process runs to completion
				this.updateDiagram(LJList.get(HRRNmax).getName(), time);
				LJList.get(HRRNmax).decrementLength();
				//when the process finishes, remove from jobs list and local job list and stop running
				if(LJList.get(HRRNmax).getTotalLength() == 0){
					for(int j = 0; j < jobs.size(); j++){
						if(jobs.get(j).getName() == LJList.get(HRRNmax).getName()){
							jobs.remove(j);
							break;
						}
					}
					LJList.remove(HRRNmax);
					running = false;
				}
				time++;
			}
			this.printDiagram();
		}
		void FB(){//feedback, I heard you like queues.
			//QueueCeption is a list of all the queues for tracking processes. New queues are added as they are needed
			//ArrayList<Job>[] QueueCeption = (ArrayList<Job>[])new ArrayList[3];
			ArrayList<ArrayList<Job>> QueueCeption = new ArrayList<ArrayList<Job>>(3);
			int wait = 1;
			int time = 0;
			int currQ = 0;	//our current queue
			Job placeholder;
			while(!jobs.isEmpty()){
				//when a process shows up it is put into the first queue
				for(int i = 0; i < jobs.size(); i++){
					//add jobs onto the end of the queue at their respective entry time
					if(jobs.get(i).getTimeIn() == time){
						QueueCeption.get(0).add(jobs.get(i));
					}
				}
				//run the process at the beginning of the earliest queue for the required wait time
				for(int i = 0; i < 3; i++){
					if(!QueueCeption.get(i).isEmpty()){
						currQ = i;
						for(int j = 0; j < wait;j++){
							this.updateDiagram(QueueCeption.get(i).get(0).getName(), time);
							QueueCeption.get(i).get(0).decrementLength();
						}
						break;
					}
				}
				//place the current process on hold
				placeholder = QueueCeption.get(currQ).get(0);
				//remove it from the queue
				QueueCeption.get(currQ).remove(0);
				//if it was finished remove it from jobs
				if(placeholder.getTotalLength() == 0){
					for(int i = 0; i < jobs.size(); i++){
						if(jobs.get(i).getName() == placeholder.getName()){
							jobs.remove(i);
						}
					}
				}else{
				//else place the process at the end of the next queue
					if(currQ + 1 < 2){
						QueueCeption.get(currQ + 1).add(placeholder);
					}else{
						QueueCeption.get(3).add(placeholder);
					}
				}
				time++;
			}
			this.printDiagram();
			
		}
		void ALL(){
			ArrayList<Job> temp = jobs;
			this.FCFS();
			jobs = temp;
			this.RR();
			jobs = temp;
			this.SPN();
			jobs = temp;
			this.SRT();
			jobs = temp;
			this.HRRN();
			jobs = temp;
			this.FB();
		}
	}
	
	public static void main(String[] args) {
		Scheduler s = new Scheduler("jobs.txt");
		switch(args[0]){
		case "FCFS": 
			s.FCFS();
			break;
		case "RR": 
			s.RR();
			break;
		case "SPN": 
			s.SPN();
			break;
		case "SRT": 
			s.SRT();
			break;
		case "HRRC": 
			s.HRRN();
			break;
		case "FB": 
			s.FB();
			break;
		case "ALL": 
			s.ALL();
			break;
		default:
			System.out.println("Incorrect command line command.");
		}
	}
}
