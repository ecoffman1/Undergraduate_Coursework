import java.util.*;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;

public class Node {

  private final int id;
  private double mass;
  private List<Integer> edges = new ArrayList<Integer>();

  public Node(String str) {

    String[] map = str.split("\\s+");
    String key = map[0];
    String value = map[1];

    String[] tokens = value.split("\\|");
    this.id = Integer.parseInt(key);
    //System.out.println("map0"+map[0]+"map1"+"token[0]"+token[0]+"token[1]:"+token[1]);
    if(!tokens[0].equals("NULL")){
        for (String s : tokens[0].split(",")) {
             if (s.length() > 0) {
                if(s!=null){
                    edges.add(Integer.parseInt(s));
                }
             }
        }
    }
    else{
       edges=null;
    }

	mass=Double.parseDouble(tokens[1]);
 

  }

  public Node(int id) {
    this.id = id;
  }

  public int getId() {
    return this.id;
  }

  public double getMass() {
    return this.mass;
  }

  public void setMass(double mass) {
    this.mass = mass;
  }

  public List<Integer> getEdges() {
    return this.edges;
  }

  public void setEdges(List<Integer> edges) {
    this.edges = edges;
  }

  public Text getLine() {
    StringBuffer s = new StringBuffer();
    if(edges!=null && !edges.isEmpty()){
        for (int v : edges) {
            s.append(v);
            if(v==this.edges.get(this.edges.size()-1)){
                break;
            }
            s.append(",");
        }
    }
    else{
        s.append("NULL");
    }
    s.append("|");
   
    s.append(this.mass);
   
	
    return new Text(s.toString());
  }

}
