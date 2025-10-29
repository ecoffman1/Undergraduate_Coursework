
import java.io.IOException;
import java.util.StringTokenizer;

import javax.naming.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
*	user:
*/

public class invertedindex {

	/**
	 * TokenizerMapper is a Hadoop Mapper class that reads input text files and outputs
	 * key-value pairs, where the key is a combination of a word and its corresponding file name,
	 * and the value is the frequency of that word in the file.
	 *
	 * This class processes the text in two steps:
	 * 1. The `map` method tokenizes each line of text from the input file and counts the frequency
	 *    of each word-file pair.
	 * 2. The `cleanup` method is called once at the end of the map phase, writing all the word-file
	 *    pairs along with their frequencies to the context.
	 */
	public static class TokenizerMapper extends Mapper<Object, Text, WordFilePair, Text>
	{
		/**
		 * A HashMap to store the frequency count of word-file pairs.
		 * The key is the word and file name, and the value is the count of occurrences.
		 */
		private HashMap<WordFilePair, Integer> count = new HashMap<>();

		/**
		 * Processes each line of input text by tokenizing it into words and tracking
		 * the frequency of each word in combination with the file it appears in.
		 *
		 * @param key the input key (not used in this implementation).
		 * @param value the input value, representing a line of text.
		 * @param context the context object that allows interaction with the Hadoop framework.
		 * @throws IOException if there is an error during input/output operations.
		 * @throws InterruptedException if the thread executing the mapper is interrupted.
		 */
		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException
		{
			// Get the current file name from the input split
			String fileName = ((org.apache.hadoop.mapreduce.lib.input.FileSplit) context.getInputSplit()).getPath().getName();

			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				String word = itr.nextToken();
				WordFilePair wordFilePair = new WordFilePair(word, fileName);
				if(count.containsKey(wordFilePair)) {
					count.put(wordFilePair, count.get(wordFilePair) + 1);
				} else{
					count.put(wordFilePair, 1);
				}
			}
		}

		/**
		 * Called once at the end of the map phase to write all the word-file pairs and their counts to the context.
		 * This ensures that the output of the mapper is available for further processing by the reducer.
		 *
		 * @param context the context object that allows interaction with the Hadoop framework.
		 * @throws IOException if there is an error during input/output operations.
		 * @throws InterruptedException if the thread executing the mapper is interrupted.
		 */
		@Override
		public void cleanup(Context context) throws IOException, InterruptedException
		{
			// Iterate over the map and write each word-file pair and its count to the context
			for (Map.Entry<WordFilePair, Integer> entry : count.entrySet()) {
				WordFilePair wordFilePair = entry.getKey();
				Integer wordCount = entry.getValue();

				// Write the word-file pair and its count to the context
				context.write(wordFilePair, new Text(wordCount.toString()));
			}
		}
	}


	/**
	 * A Reducer class for processing word and file name pairs with associated counts.
	 * This Reducer expects a single value (count) for each unique word-fileName pair
	 * emitted from the Mapper. It combines file names and their associated word counts
	 * for each word and outputs the result in a format where multiple occurrences of the
	 * same word across different files are concatenated into a single output string.
	 */
	public static class IntSumReducer extends Reducer<WordFilePair,Text,Text,Text>
	{
		// StringBuilder to accumulate file names and counts for the current word
		private StringBuilder combinedTerm = new StringBuilder();
		// Holds the previous word to detect changes in the current word
		private String previousTerm = "";

		/**
		 * The reduce method processes each unique word-file pair (the key) and its associated count (the value).
		 * It combines file names and counts for the same word and writes the results once the word changes.
		 *
		 * @param key     The WordFilePair key, which consists of a word and a file name.
		 * @param values  An iterable containing the count associated with the word-file pair. In this case, we assume
		 *                only one count per word-file pair since the Mapper already aggregates the counts.
		 * @param context The Hadoop context to write the output key-value pairs.
		 *
		 * @throws IOException          If there is an error during input or output.
		 * @throws InterruptedException If the job is interrupted during execution.
		 */
		@Override
		public void reduce(WordFilePair key, Iterable<Text> values, Context context) throws IOException, InterruptedException
		{
			Text word = key.getWord();
			if(previousTerm.length()==0){
				previousTerm = word.toString();
			}
			if(!word.toString().equals(previousTerm)){
				context.write(new Text(previousTerm), new Text(combinedTerm.toString()));
				previousTerm = word.toString();
				combinedTerm.setLength(0);
			}
			for (Text count : values) { // names is Iterable, so this works
        		combinedTerm.append(key.getFileName()+": "+ count.toString()+";  ");
    		}
			
		}

		/**
		 * The cleanup method is called after all the data for a key has been processed by the reducer.
		 * This method ensures that the last word and its associated combined file names and counts
		 * are written to the output context.
		 *
		 * @param context The Hadoop context to write the final key-value pairs.
		 *
		 * @throws IOException          If there is an error during input or output.
		 * @throws InterruptedException If the job is interrupted during execution.
		 */
		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException
		{
			// Write the last term after the reducer has processed all input
			context.write(new Text(previousTerm), new Text(combinedTerm.toString()));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
		System.err.println("Usage: invertedindex <in> <out>");
		System.exit(2);
		}
		Job job = new Job(conf, "inverted index");
		job.setNumReduceTasks(3);
		job.setJarByClass(invertedindex.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(WordFilePair.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}