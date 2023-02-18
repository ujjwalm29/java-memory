package memorytest;


public class Main {

	/**
	 * Guess the output of the following code without running the code!
	 * Credits :  https://www.linkedin.com/learning/java-memory-management
	 */

	public static void main(String args[]) {
		Main main = new Main();
		main.start();
	}
	
	public void start() {
		String last = "Z";
		Container container = new Container();
		container.setInitial("C");
		another(container,last);
		System.out.println(container.getInitial());
	}
	
	public void another(Container initialHolder, String newInitial) {
		newInitial.toLowerCase();
		initialHolder.setInitial("B");
		Container initial2 = new Container();
		initialHolder=initial2;
		System.out.println(initialHolder.getInitial());
		System.out.println(newInitial);
	}
}
