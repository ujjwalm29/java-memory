## The Basics

### The stack
- There are multiple stacks that exist simultaneously.
- All threads have their own stack.
- Java knows when data on stack can be destroyed.
- All local variables created are created on the stack. Once method is complete, all variables are popped.
- Primitives are always stored on stack

### The heap
- One heap shared across all processes.
- Heap Size = Total Memory - Stack
- ALL OBJECTS STORED ON HEAP
- Let's say we write `String name="hello"`
  - The object is created in the heap
  - A variable is created on the stack which points to the heap
- `new` keyword basically means "find some space on the heap for this object". 
- In Java, developers cannot access the memory directly (unlike C++)

### `final` keyword and concept of `const correctness`
- An object with `final` keyword cannot change it's value. A compiler error will be thrown
- BUT, there is a caveat.
- Example
```
final Customer c;
c = new Customer("Ravi"); 
c = new Customer("Jay"); // Case #1 : Compiler Error

BUT 

c.setName("Jay"); // Case #2 : This is allowed
```
- So.... is `final` keyword really working?
  - Unfortunately, it's not possible to fix the state of the object using final
  - If Case #2 was NOT allowed, it's called `const correctness`
  - Unfortunately, Java doesn't support `const correctness`
- So, with `final`, the reference to the object cannot be changed, but the value inside the object can change.

#### Check memorytest package for a small exercise!

### Escaping References

Example : 
```java
public class CustomerRecords {

  private Map<String, Customer> records;

  public CustomerRecords () {
      this.records = new HashMap<String, Customer> ();
  }
  public void addCustomer (Customer c) {
      this.records.put (c.getName (), c);
  }
  public Map<String, Customer> getCustomers() {
      return this.records;  // PROBLEM
  }
}
```

The above given code is an example of escaping references.<br>
At first glance, it looks like the `records` variable is set as private with appropriate getter and setter. But if you notice, the getter actually returns the reference to the entire object. This is NOT safe.

Somebody using the code can easily do something malicious : 

```
public void printCustomers(CustomerRecords c){
    Map<String, Customer› records = c.getCustomers();
    
    records.clear();
}
```

This is clearly a problem and this kind of code should be avoided.

Check escapingreferences package for an exercise!

Some alternatives : 
- Return unmodifiable Collections.
- For strings, it's ok to return since Strings are immutable
- For custom objects
  - Create an interface with just getters. Implement the interface in the custom class.
  - Return an object of the interface rather than the custom class
  - Someone can still access the mutable object by casting :(
  - In general, return new object rather than returning existing reference

<b><i><mark>Fun FACT!! - Internalized Strings </mark> </b></i>

String are objects and immutable in Java. Java has an optimization for strings using these 2 facts.

If we create 2 string like this : 
```
String first = "hello";
String second = "hello";
```
Java does NOT create 2 objects in the heap for this. It is smart enough to point both the variables to the same string reference.<br><br>

This is called <b>Internalized Strings</b><br>
You can verify the above by using `==` to compare the 2 strings.<br>
By default, this only happens for literals and not for calculated strings. BUT, you can force this by using the `intern()` method on calculated Strings.

## Garbage Collection

- Cleaning Stack is easy. When closing brace appears, clear stack for that particular method
- Java doesn't give us the option to choose whether memory is allocated on stack or heap. It makes the most efficient choice and simplifies developer experience
- Java has automatic garbage collection (Unlike C++, where you need to use `free`)

### How does java prevent memory leaks?

1. Java runs on a Virtual Machine. So even if there is a leak, problem will not spread throughout the system, only the virtual machine.
2. Garbage Collection strategy

### What is the garbage collection strategy?

**Any object on the heap which cannot be reached through a reference from the stack is "eligible for garbage collection"**

IMPORTANT NOTE : Java does NOT want developers to interfere with the Garbage Collection process. It's meant to be automatic<br>

Garbage Collection will STOP all threads until it is complete. Hence, we want GC to be quick and infrequent.

However, there are a few methods in the Java API which we can use to interact with the Garbage Collector
- `System.gc()` - This will *suggest* the JVM to run the GC.
- `finalize()` - GC calls this method when garbage collection is finishing.

Both are pretty useless.. never use them!

### Soft Leaks

Note : `Soft Leaks` is not an official term.

Soft leaks occur when an object is referenced on the stack even though it is never used again.
- Could be in your code or in a library that you might be using!
- Could cause Out Of Memory problems!

### Monitoring applications and detecting leaks

We will use VisualVM.<br>
It will be located at `<JDK installation folder>/bin/jvisualvm.exe`. Or, download from [here](https://visualvm.github.io/download.html).<br>

VisualVM is a very useful tool. Play around with it, check process parameters and check the Monitor tab for some cool graphs!<br><br>

If the heap graph in the monitor is plateauing and process is stuck, that means that GC is desperately trying to free up memory and process will crash soon. There is a leak, and we need to find it!!<br><br>

### How does garbage collection work?

Algorithm is called "mark and sweep".<br>

Steps
1. All the threads are stopped.
2. GC checks every variable on the stack and follows all the references(recursive) and marks them.
3. Rest of the heap memory is cleared.
4. Whatever was NOT cleared from the heap, is moved to single block of contiguous memory.

But if GC stops the entire process, users will notice.<br>
Hence, we go for **Generational Garbage Collection**

Most objects don't live for very long => If any object survives few cycles of GC, it will likely live forever.

- Heap is divided into 2 sections, young and old. 
- Young section is smaller than old.
- Young section will fill up quite fast. GC will only clear of the young section of heap. Since they were created recently, most of them can be cleared off.
- Now, all surviving objects in young section are copied over to old section.

This first cycle is called **Minor Collection**.

GC will only run on Old section when it is needed, for example if it getting full. This is called **Major collection**.<br>
This process will take longer than minor collection(mark, delete, copy).<br>

Both the parts of GC can be visualized in VisualVM. Install the plugin VisualGC ( Go to Tools -> Plugins -> Available -> VisualGC).<br>
Note : In VisualGC tab, young generation is split between Eden, S0 and S1.

There are 2 more memory spaces, PermGen(deprecated) and Metaspace.

MetaSpace(Java 8 onwards) : 
- Objects in MetaSpace are NEVER deleted
- If application runs out of MetaSpace, application will crash.
- Class metadata is stored in MetaSpace
- Metaspace memory is allocated OUTSIDE the JVM.

### How to detect and debug memory leak in Java?

1. Create a heap dump of the process. You can do that by opening VisualVM -> Monitor -> Click on heap dump.
2. We need to analyze this heap dump. One of the tools to do that is [Eclipse Memory Analyzer](https://www.eclipse.org/mat/). Install it.
3. In VisualVM, copy/note the path of the heap dump.
4. Open MAT, click on File -> Open Heap Dump and open your heap dump.
5. Follow the steps. The analyzer will show you the suspected memory leak.
6. If you click on `Details`, it will show you the exact object which is creating the issue.

## Tuning the VM

Heap Size
- You can set the maximum heap size by `-Xmx` option. Example `-Xmx10m` is 10 Megabyte
- You can set the starting heap size by `-Xms` option.
- To get notified whenever a Garbage collection takes place, use `-verbose:gc` option.
- By default, young generation is 1/3 times of heap size
  - Use `-Xmn` to set young generation size. Recommendation : 1/4 to 1/3 of heap size
- For automatic heap dump on OutOfMemory, use `-XX:HeapDumpOnOutOfMemory`
- To find out all java options, use `-XX:+PrintCommandLineFlags`

## Useful Links

https://reflectoring.io/create-analyze-heapdump/

https://www.baeldung.com/visualvm-jmx-remote








