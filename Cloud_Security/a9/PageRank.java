/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.IOException;
import java.util.Iterator;
import java.util.List;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.util.GenericOptionsParser;
/**
 * This is an example Hadoop Map/Reduce application.
 *
 * It inputs a map in adjacency list format, and performs PageRank.
 * The input format is
 * ID   EDGES|MASS
 * where
 * ID = the unique identifier for a node (assumed to be an int here)
 * EDGES = the list of edges emanating from the node (e.g. 3,8,9,12)
 * MASS  = the to be determined PageRank
 */
public class PageRank extends Configured implements Tool {
    public static int pass=0;
    public static double missingMass=0;
    public static int totalNodes=0;
    public static double alpha=0;

 // public static final Log LOG = LogFactory.getLog("org.apache.hadoop.examples.PageRank");


  public static class MapClass extends
      Mapper<LongWritable, Text, IntWritable, Text> {
   
    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
        if (value.toString().length()>0) {
            Node node = new Node(value.toString());
             
            if (pass == 1) {
                if (node.getEdges()!=null) {
                    for (int v : node.getEdges()) {
                        Node vnode = new Node(v);
            	          vnode.setMass(node.getMass()/node.getEdges().size());
            	          context.write(new IntWritable(vnode.getId()), vnode.getLine());
                    } 
                }
                else {
                    Node vnode=new Node(-1);
                    vnode.setEdges(null);
                    vnode.setMass(node.getMass());
                    context.write(new IntWritable(vnode.getId()), vnode.getLine());
                }
                node.setMass(0);
                context.write(new IntWritable(node.getId()), node.getLine());
            } 
            else { // pass 2

			/* ----------DO NOT REVISE THE CODE ABOVE THIS LINE---------- */
              double missing = PageRank.missingMass/PageRank.totalNodes+node.getMass();
              node.setMass(PageRank.alpha*(1.0/PageRank.totalNodes) + (1-PageRank.alpha)*missing);
              context.write(new IntWritable(node.getId()), node.getLine());
			/* ----------DO NOT REVISE THE CODE BELOW THIS LINE---------- */   

            }
        }
    }
  }

  /**
   * A reducer class that just emits the sum of the input values.
   */
  public static class Reduce extends
      Reducer<IntWritable, Text, IntWritable, Text> {

    /**
     * Make a new node which combines all information for this single node id.
     * The new node should have
     * - The full list of edges
     * - The PageRank
     * 
     * In addition, missingMass is updated in reduce() method.
     */
    @Override
    public void reduce(IntWritable key, Iterable<Text> values, Context context) 
        throws IOException, InterruptedException {
     // LOG.info("Reduce executing for input key [" + key.toString() + "]");

			/* ----------DO NOT REVISE THE CODE ABOVE THIS LINE---------- */

      if(key.get() == -1){
        double missing = 0;
        for (Text value : values) {
          Node u = new Node(key.get() + "\t" + value.toString());
          missing = missing + u.getMass();
        }
        PageRank.missingMass = missing;
      } else {
        Node node = new Node(key.get());
        for (Text value : values) {
          Node u = new Node(key.get() + "\t" + value.toString());
          if(u.getEdges() != null){
            node.setEdges(u.getEdges());
          }
          node.setMass(node.getMass() + u.getMass());
        }
        context.write(new IntWritable(node.getId()), node.getLine());  
      }
			/* ----------DO NOT REVISE THE CODE BELOW THIS LINE---------- */   

    }
  }

  private Job getJob(String[] args) throws IOException {
    Job job = new Job(getConf(), "PageRank");
    job.setJarByClass(PageRank.class);

    // the keys are the unique identifiers for a Node (ints in this case).
    job.setOutputKeyClass(IntWritable.class);
    // the values are the string representation of a Node
    job.setOutputValueClass(Text.class);

    job.setMapperClass(MapClass.class);
    job.setReducerClass(Reduce.class);

    // 3 reducers
    job.setNumReduceTasks(3);



    for (int i = 0; i < args.length; ++i) {
      if ("-N".equals(args[i])) {
        totalNodes = Integer.parseInt(args[++i]);
      } 
      else if ("-alpha".equals(args[i])) {
        alpha = Double.parseDouble(args[++i]);
      }
    }

    //LOG.info("The number of reduce tasks has been set to " + conf.getNumReduceTasks());
    //LOG.info("The number of mapper tasks has been set to " + conf.getNumMapTasks());

    return job;
  }

  /**
   * The main driver for word count map/reduce program. Invoke this method to
   * submit the map/reduce job.
   *
   * @throws IOException
   *           When there is communication problems with the job tracker.
   */
  public int run(String[] args) throws Exception {

    int totalCount=4;

    for (int i = 0; i < args.length; ++i) {
      if ("-i".equals(args[i])) {
        totalCount= Integer.parseInt(args[++i]);
      }
    }
    int iterationCount = 0;

    while (keepGoing(iterationCount,totalCount)) {
      pass = 1;
      String input;
      if (iterationCount == 0)
        input = "input-graph";
      else
        input = "output-graph-" + iterationCount;

      String output = "output-graph-" + (iterationCount + 1)+"intermediate";

      Job job1 = getJob(args);
      FileInputFormat.addInputPath(job1, new Path(input));
      FileOutputFormat.setOutputPath(job1, new Path(output));
      job1.waitForCompletion(true);
      
      //second pass 
      pass = 2;
      input = "output-graph-" + (iterationCount+1)+"intermediate";

      output = "output-graph-" + (iterationCount + 1);
  
      Job job2 = getJob(args);
      FileInputFormat.addInputPath(job2, new Path(input));
      FileOutputFormat.setOutputPath(job2, new Path(output));
      job2.waitForCompletion(true);
      iterationCount++;
    }

    return 0;
  }

  private boolean keepGoing(int iterationCount, int totalCount) {
    if(iterationCount >= totalCount) {
      return false;
    }

    return true;
  }

  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new PageRank(), args);
    System.exit(res);
  }

}
