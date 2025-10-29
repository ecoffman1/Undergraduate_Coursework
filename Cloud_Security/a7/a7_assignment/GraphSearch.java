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

import javax.naming.Context;

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
 * It inputs a map in adjacency list format, and performs a breadth-first search.
 * The input format is
 * ID   EDGES|WEIGHTS|DISTANCE|COLOR
 * where
 * ID = the unique identifier for a node (assumed to be an int here)
 * EDGES = the list of edges emanating from the node (e.g. 3,8,9,12)
 * WEIGHTS = the list of link weights
 * DISTANCE = the to be determined distance of the node from the source
 * COLOR = a simple status tracking field to keep track of when we're finished with a node
 * It assumes that the source node (the node from which to start the search) has
 * been marked with distance 0 and color GRAY in the original input.  All other
 * nodes will have input distance Integer.MAX_VALUE and color WHITE.
 */
public class GraphSearch extends Configured implements Tool {

  /**
   * Nodes that are Color.WHITE or Color.BLACK are emitted, as is. For every
   * edge of a Color.GRAY node, we emit a new Node with distance increased by
   * the link weight. The Color.GRAY node is then colored black and is also emitted.
   */

  public static class MapClass extends
      Mapper<LongWritable, Text, IntWritable, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {

      Node node = new Node(value.toString());

      // For each GRAY node, emit each of the edges as a new node (also GRAY)

      if (node.getColor() == Node.Color.GRAY) {
        Iterator w = node.getWeights().iterator();
        for (int v : node.getEdges()) {
	        if(w.hasNext()) {
		        int ww = (int) w.next();
            Node vnode = new Node(v);
	          vnode.setDistance(node.getDistance() + ww);
            vnode.setColor(Node.Color.GRAY);
            context.write(new IntWritable(vnode.getId()), vnode.getLine());
          } // end of if
        } // end of for

        // We're done with this node now, color it BLACK

        node.setColor(Node.Color.BLACK);
      } //end of if

      // No matter what, we emit the node
      // If the node came into this method GRAY, it will be output as BLACK
      // If the node came into this method WHITE or BLACK, it will be output as is

      context.write(new IntWritable(node.getId()), node.getLine());
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
     * - The link weights
     * - The minimum distance
     * - The proper Color
     */
    
    @Override
    public void reduce(IntWritable key, Iterable<Text> values, Context context)
        throws IOException, InterruptedException {

      List<Integer> edges = null;
      List<Integer> weights = null;
      int distance = Integer.MAX_VALUE;
      Node.Color color = Node.Color.WHITE;
      int currentDistance = -1;

			/* ----------DO NOT REVISE THE CODE ABOVE THIS LINE---------- */
      for (Text value : values) {
        Node u = new Node(key.get() + "\t" + value.toString());

        if (u.getEdges().size() > 0) {
          edges = u.getEdges();
          weights = u.getWeights();
        }

        if (u.getDistance() < distance) {
          distance = u.getDistance();
          color = u.getColor();
        }
      }

			/* ----------DO NOT REVISE THE CODE BELOW THIS LINE---------- */      

      // Finally, emit the complete node

      Node n = new Node(key.get());
      n.setDistance(distance);
      n.setEdges(edges);
      n.setWeights(weights);
      n.setColor(color);
      context.write(key, new Text(n.getLine()));
    }
  }

  static int printUsage() {
    System.out.println("graphsearch [-m <num mappers>] [-r <num reducers>]");
    ToolRunner.printGenericCommandUsage(System.out);
    return -1;
  }

  private Job getJob(String[] args) throws IOException {
    Job job = new Job(getConf(), "graphsearch");
    job.setJarByClass(GraphSearch.class);

    // the keys are the unique identifiers for a Node (ints in this case).
    job.setOutputKeyClass(IntWritable.class);
    // the values are the string representation of a Node
    job.setOutputValueClass(Text.class);

    job.setMapperClass(MapClass.class);
    job.setReducerClass(Reduce.class);


    for (int i = 0; i < args.length; ++i) {
      if ("-m".equals(args[i])) {
        i++;
      } else if ("-r".equals(args[i])) {
        job.setNumReduceTasks(Integer.parseInt(args[++i]));
      }
    }

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
      String input;
      if (iterationCount == 0)
        input = "input-graph";
      else
        input = "output-graph-" + iterationCount;

      String output = "output-graph-" + (iterationCount + 1);

      Job job = getJob(args);
      FileInputFormat.addInputPath(job, new Path(input));
      FileOutputFormat.setOutputPath(job, new Path(output));
      job.waitForCompletion(true);
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
    int res = ToolRunner.run(new Configuration(), new GraphSearch(), args);
    System.exit(res);
  }

}
