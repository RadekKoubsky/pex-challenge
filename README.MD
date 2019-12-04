### Pex Challenge
Original assignment is in [this gist link](https://gist.github.com/ehmo/e736c827ca73d84581d812b3a27bb132#file-input-txt).

Read the list of images and find 3 most prevalent colors in the RGB scheme in hexadecimal format (#000000 - #FFFFFF) in each image, and write the result into a CSV file in a form of url,color,color,color.

Please focus on speed and resources. The solution should be able to handle input files with more than a billion URLs, using limited resources (e.g. 1 CPU, 512MB RAM). Keep in mind that there is no limit on the execution time, but make sure you are utilizing the provided resources as much as possible at any time during the program execution.

## Implementation
The implementation uses producer-consumer pattern supported by BlockingQueue.

The number of producers is based on available CPU cores to achieve maximal CPU utilization. Producer threads are executed in a semaphore bounded executor that limits number of tasks in the executor queue to avoid out of memory error from creating tasks for billions of image urls.

There is only one consumer which writes the output to a single file.

The most 3 prevalent colors are computed using hash map and min heap.

## Further optimizations
Further optimizations can be made (after profiling) by identifying 
parts of code that uses serialized access e.g. writing to a file, blocking queue (implementing custom synchronizer might be faster)  

## Build and run
Run `mvn clean package` to build the application.

Run `java -jar target/pex-challenge-1.0-SNAPSHOT.jar images.txt` to run the application or using maven `mvn exec:java -Dexec.args="images.txt"`

##Test run
Run on i7-4600U with 2 cores and 4 threads, 12 GB memory. 

Total time 250 seconds.