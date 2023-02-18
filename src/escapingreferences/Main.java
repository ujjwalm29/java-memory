package escapingreferences;
public class Main {

	/**
	 * Fix the files in the package to not allow any "escaped references". Answers at the bottom of the file!
	 * Credits :  https://www.linkedin.com/learning/java-memory-management
	 */
	public static void main(String[] args) {
		CustomerRecords records = new CustomerRecords();

		records.addCustomer(new Customer("John"));
		records.addCustomer(new Customer("Simon"));
				
		for (Customer next : records.getCustomers().values())
				{
					System.out.println(next);
				}

	}
	
}
