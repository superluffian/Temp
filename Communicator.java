package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speaker and a listener are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {
	/**
	 * Allocate a new communicator.
	 */
	private Lock lock;
	private Condition speakerCondt;
	private Condition listenerCondt;
	private int speaker = 0;
	private int listener = 0;	
	private Boolean isWordReady = false;
	private int word = 0;

	
	public Communicator() {
		speaker = 0;
		listener = 0;
		isWordReady = false;
		lock = new Lock();
		speakerCondt = new Condition(lock);
		listenerCondt = new Condition(lock);
				
	}

	/**
	 * Wait for a thread to listen through this communicator, and then transfer
	 * <i>word</i> to the listener.
	 * 
	 * <p>
	 * Does not return until this thread is paired up with a listening thread.
	 * Exactly one listener should receive <i>word</i>.
	 * 
	 * @param word the integer to transfer.
	 */
	public void speak(int inputword) {
		lock.acquire();
		speaker ++;
		while( listener == 0 || isWordReady ) //while no available listener or word is ready(but listener hasn't fetched it)
			speakerCondt.sleep();
		word = inputword;
		isWordReady = true;
		listenerCondt.wake();	
		
		speaker--;
		lock.release();
		
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 * 
	 * @return the integer transferred.
	 */
	public int listen() {
		lock.acquire();
		listener ++;
		while(!isWordReady){
			listenerCondt.sleep();
			speakerCondt.wakeAll(); //(although it's not efficient, it can keep code simple enough)
		}
		int myWord = word;
		
		isWordReady = false;
		listener --;
		lock.release();
	
		return myWord;
	}
}
	

