package agent;

import java.util.concurrent.Semaphore;

/**
 * Base class for simple agents
 */
public abstract class Agent {
	Semaphore stateChange = new Semaphore(1, true); 
	Semaphore pause = new Semaphore(0, true);
	boolean isPaused = false;
	private AgentThread agentThread;

	protected Agent() {
	}

	/**
	 * This should be called whenever state has changed that might cause
	 * the agent to do something.
	 */
	protected void stateChanged() {
		stateChange.release();
	}

	/**
	 * Agents must implement this scheduler to perform any actions appropriate for the
	 * current state.  Will be called whenever a state change has occurred,
	 * and will be called repeated as long as it returns true.
	 *
	 * @return true iff some action was executed that might have changed the
	 *         state.
	 */
	protected abstract boolean pickAndExecuteAnAction();

	/**
	 * Return agent name for messages.  Default is to return java instance
	 * name.
	 */
	protected String getName() {
		return StringUtil.shortName(this);
	}

	/**
	 * The simulated action code
	 */
	protected void Do(String msg) {
		print(msg, null);
	}

	/**
	 * Print message
	 */
	protected void print(String msg) {
		print(msg, null);
	}

	/**
	 * Print message with exception stack trace
	 */
	protected void print(String msg, Throwable e) {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append(": ");
		sb.append(msg);
		sb.append("\n");
		if (e != null) {
			sb.append(StringUtil.stackTraceString(e));
		}
		System.out.print(sb.toString());
	}

	public void msgPause(){
		if (!isPaused)
		{
			isPaused = true;
		}
		else if (isPaused)
		{
			pause.release();
			isPaused = false;
		}
	}

	/**
	 * Start agent scheduler thread.  Should be called once at init time.
	 */
	public synchronized void startThread() {
		if (agentThread == null) {
			agentThread = new AgentThread(getName());
			agentThread.start(); 
		} else {
			agentThread.interrupt(); // don't worry about this for now
		}
	}

	/**
	 * Stop agent scheduler thread.
	 */
	
	public void stopThread() {
		if (agentThread != null) {
			agentThread.stopAgent();
			agentThread = null;
		}
	}

	/**
	 * Agent scheduler thread, calls respondToStateChange() whenever a state
	 * change has been signalled.
	 */
	private class AgentThread extends Thread {
		private volatile boolean goOn = false;

		private AgentThread(String name) {
			super(name);
		}

		public void run() {
			goOn = true;

			while (goOn) {
				if (isPaused) {
					try {
						pause.acquire();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				try {
					stateChange.acquire();					
					while (pickAndExecuteAnAction()) ;
				} catch (InterruptedException e) {
				} catch (Exception e) {
					print("Unexpected exception caught in Agent thread:", e);
				}
			}
		}

		private void stopAgent() {
			goOn = false;
			this.interrupt();
		}
	}
}

