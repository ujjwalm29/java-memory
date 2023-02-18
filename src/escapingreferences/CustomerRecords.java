package escapingreferences;
import java.util.HashMap;
import java.util.Map;

public class CustomerRecords {
	private Map<String, Customer> records;
	
	public CustomerRecords() {
		this.records = new HashMap<String, Customer>();
	}
	
	public void addCustomer(Customer c) {
		this.records.put(c.getName(), c);
	}
		
	public Map<String, Customer> getCustomers() {
		return this.records;
	}
}















/*
 ANSWER : We could implement Iterable in the class and return an iterator. But iterator also has a .remove() method
 Another solution is, we can return a new HashMap of values. return new HashMap<String, Customer>(this.records);

 But user might still think that the HashMap is modifiable. Might do something like c.getCustomers().clear().
 Although it won't affect the original map. This is not ideal.

 So the answer is, return an unmodifiable map by using Collections class

 return Collections.unmodifiableMap(this.records);

 What to do with custom objects? Check the LinkedIn learning course! (Or, don't. Not sponsored, just giving credits :-) )
 */