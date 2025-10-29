import java.io.IOException;
import java.util.StringTokenizer;

import javax.naming.Context;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The {@code SecondarySorting} class demonstrates a MapReduce job that performs
 * secondary sorting based on sensor data. The input consists of sensor readings
 * where each record contains a timestamp, sensor ID, and a reading value.
 * <p>
 * The class includes:
 * - A custom {@link Mapper} implementation to parse input data and emit
 *   a composite key ({@link SensorTimePair}) for secondary sorting.
 * - A default {@link Reducer} that sorts the data by timestamp for each sensor.
 * <p>
 * Input format example:
 * <pre>
 * timestamp1, sensorId1, reading1
 * timestamp2, sensorId2, reading2
 * </pre>
 * <p>
 * Usage:
 * <pre>
 * hadoop jar SecondarySorting.jar SecondarySorting <input_path> <output_path>
 * </pre>
 */
public class SecondarySorting {

    /**
     * The {@code SecondarySortingMapper} class is responsible for parsing each line of input data
     * and outputting a key-value pair. The key is a composite of sensor ID and timestamp
     * ({@link SensorTimePair}), and the value is the reading ({@link DoubleWritable}).
     */
    public static class SecondarySortingMapper extends Mapper<LongWritable, Text, SensorTimePair, DoubleWritable> {

        private SensorTimePair sensorTimePair = new SensorTimePair();
        private DoubleWritable reading = new DoubleWritable();

        /**
         * Parses the input line and emits a {@link SensorTimePair} and reading.
         * <p>
         * Input format:
         * <pre>
         * timestamp, sensorId, reading
         * </pre>
         *
         * @param key     The input key, which is the byte offset of the line in the file.
         * @param value   The input line of text.
         * @param context The context to write output key-value pairs to.
         * @throws IOException          If an input or output error occurs.
         * @throws InterruptedException If the task is interrupted.
         */
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // Please implement the map method as specified in the JavaDoc/instructions.

            /* ----------DO NOT REVISE THE CODE ABOVE THIS LINE---------- */
            StringTokenizer itr = new StringTokenizer(value.toString(),", ");
            SensorTimePair pair = new SensorTimePair();
            
            pair.setTimestamp(itr.nextToken());

            int id = Integer.parseInt(itr.nextToken());
            pair.setSensorId(id);

            double reading = Double.parseDouble(itr.nextToken());
            DoubleWritable reading_value = new DoubleWritable(reading);

            context.write(pair,reading_value);
            /* ----------DO NOT REVISE THE CODE BELOW THIS LINE---------- */
        }
    }


    /**
     * The {@code main} method configures and starts the MapReduce job for secondary sorting.
     * It sets up the job with the mapper, partitioner, reducer, input, and output formats.
     *
     * @param args Command-line arguments, expecting input and output file paths.
     * @throws Exception If the job configuration or execution fails.
     */
    public static void main(String[] args) throws Exception {
        // 1. Create a new Hadoop configuration object.
        Configuration conf = new Configuration();

        // 2. Parse the input arguments using the GenericOptionsParser.
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: SecondarySorting <in> <out>");
            System.exit(1);
        }

        // 3. Create and configure the MapReduce job.
        Job job = new Job(conf, "SecondarySorting");
        job.setJarByClass(SecondarySorting.class);

        // 4. Set the Mapper class for the job.
        job.setMapperClass(SecondarySortingMapper.class);

        // 5. Set the types for the Mapper's output key and value.
        job.setMapOutputKeyClass(SensorTimePair.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        // 6. Set the Reducer class.
        job.setReducerClass(Reducer.class);

        // 7. Set the number of reduce tasks.
        job.setNumReduceTasks(3);

        // 8. Specify the input and output paths for the job.
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        // 9. Run the MapReduce job.
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}