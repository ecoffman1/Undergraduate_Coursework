import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
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

public class relativefreq {

  public static class PairsRelativeOccurrenceMapper extends Mapper<LongWritable, Text, wordpair, IntWritable> {
    private wordpair wordPair = new wordpair();
    private IntWritable ONE = new IntWritable(1);
    private IntWritable totalCount = new IntWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        int neighbors = context.getConfiguration().getInt("neighbors", 1);
        String[] tokens = value.toString().split("\\s+");
        if (tokens.length > 1) {
            for (int i = 0; i < tokens.length; i++) {
                    tokens[i] = tokens[i].replaceAll("\\W+","");

                    if(tokens[i].equals("")){
                        continue;
                    }

                    wordPair.setWord(tokens[i]);
                    int start = (i - neighbors < 0) ? 0 : i - neighbors;
                    int end = (i + neighbors >= tokens.length) ? tokens.length - 1 : i + neighbors;
		            for (int j = start; j <= end; j++) {
                        if (j == i) continue;
                        wordPair.setNeighbor(tokens[j].replaceAll("\\W",""));
			            context.write(wordPair, ONE);
                    }
                    wordPair.setNeighbor("*");
                    totalCount.set(end - start);
                    context.write(wordPair, totalCount);
            }//end of for
        }//end of if
    }//end of map method
  }//end of mapper class



  public  static class PairsCombiner extends Reducer<wordpair,IntWritable,wordpair,IntWritable> {
    private IntWritable totalCount = new IntWritable();
    @Override
    protected void reduce(wordpair key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        for (IntWritable value : values) {
             count += value.get();
        }
        totalCount.set(count);
        context.write(key,totalCount);
    }
  }

  public static class PairsRelativeOccurrenceReducer extends Reducer<wordpair, IntWritable, wordpair, DoubleWritable> {
    private DoubleWritable totalCount = new DoubleWritable();
    private DoubleWritable relativeCount = new DoubleWritable();
    private Text flag = new Text("*");

    @Override
    protected void reduce(wordpair key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

        /* ----------DO NOT REVISE THE CODE ABOVE THIS LINE---------- */
        int count = 0;
        for (IntWritable value : values) {
             count += value.get();
        }
        if(key.getNeighbor().equals(flag)){
            totalCount.set(count);
        } else {
            relativeCount.set(count/totalCount.get());
            context.write(key,relativeCount);
        }


        /* ----------DO NOT REVISE THE CODE BELOW THIS LINE---------- */
        
    }// end of reduce method

    private int getTotalCount(Iterable<IntWritable> values) {
        int count = 0;
        for (IntWritable value : values) {
            count += value.get();
        }
        return count;
    }
  }//end of reducer class

  public static void main(String[] args) throws Exception {
      Configuration conf = new Configuration();
      String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
      if (otherArgs.length != 2) {
          System.err.println("Usage: relativefreq <in> <out>");
          System.exit(1);
      }
      Job job = new Job(conf, "relativefreq");
      job.setJarByClass(relativefreq.class);
      job.setMapperClass(PairsRelativeOccurrenceMapper.class);
      job.setReducerClass(PairsRelativeOccurrenceReducer.class);
      job.setCombinerClass(PairsCombiner.class);
      job.setNumReduceTasks(3);
      job.setOutputKeyClass(wordpair.class);
      job.setOutputValueClass(IntWritable.class);
      FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
      FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
      System.exit(job.waitForCompletion(true) ? 0 : 1);

  }//end of main method
}//end of program
