import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.lang.Math;
import javax.swing.JList;

class Model
{
	int dest_x;
	int dest_y;
	static int speed = 4;
	ArrayList<MapItem> items;
	public int current_item;
	ItemYComparator comparator;

	Model()
	{
		this.items = new ArrayList<MapItem>();
		this.comparator = new ItemYComparator();
	}

	public void update()
	{

	}

    public void reset()
    {

    }

	public void setDestination(int x, int y)
	{
		this.dest_x = x;
		this.dest_y = y;
	}


	public void addMapItem(int x, int y)
	{
		this.items.add(MapItem.newItem(x, y, this.current_item));
		items.sort(this.comparator);
	}

	public void change_item()
	{
		this.current_item++;
		if(this.current_item == Main.MapItemTypes.length){
			this.current_item = 0;
		}
	}
	public Json marshal()
	{
		Json map = Json.newObject();
		Json list_of_map_items = Json.newList();
		map.add("items", list_of_map_items);
		for (MapItem item : this.items)
		{
			list_of_map_items.add(item.marshal());
		}
		return map;
	}

	public void unmarshal(Json list){
		this.items = new ArrayList<MapItem>();
		for(int i = 0; i < list.size(); i++){
			Json ob = list.get(i);
			MapItem temp = MapItem.newItem(0,0,(int)ob.getLong("type"));
			temp.unmarshal(ob);
			items.add(temp);
		}
		this.comparator = new ItemYComparator();
	}
}

class MapItem
{
	protected int x;
	protected int y;
	public int type;

	public static MapItem newItem(int x, int y, int type) {
		if (type == 3 || type == 9)
			return new Jumper(x, y, type);
		else if (type == 4 || type == 7)
			return new Strafer(x, y, type);
		else if (type == 2)
			return new grower(x, y, type);
		else
			return new MapItem(x, y, type);
	}

	double distance(int x, int y){
		int xd = this.x - x;
		int yd = this.y - y;
		return Math.sqrt((xd*xd)+(yd*yd));
	}

	protected MapItem(int x, int y, int type)
	{
		this.x = x;
		this.y = y;
		this.type = type;
	}
	void unmarshal(Json ob){
		this.x = (int)ob.getLong("x");
		this.y = (int)ob.getLong("y");
		this.type = (int)ob.getLong("type");
	}
	Json marshal(){
		Json ob = Json.newObject();
		ob.add("x", this.x);
		ob.add("y", this.y);
		ob.add("type", this.type);
		return ob;
	}

	Point pos(int time)
	{
		return new Point(this.x, this.y);
	}

	double scale(int time){
		return 1.0;
	}
}

class Jumper extends MapItem
{
	Jumper(int x, int y, int type){
		super(x, y, type);
	}

	Point pos(int time)
	{
		return new Point(this.x, this.y - (int)Math.max(0., 50 * Math.sin(((double)time) * 2 * Math.PI / 30)));
	}
}

class Strafer extends MapItem
{
	Strafer(int x, int y, int type){
		super(x, y, type);
	}

	Point pos(int time)
	{
		return new Point(this.x - (int)(50 * Math.sin(((double)time) * 2 * Math.PI / 30)), this.y);
	}
}


class grower extends MapItem
{
	grower(int x, int y, int type){
		super(x, y, type);
	}

	double scale(int time){
		return Math.max(1 , 2*Math.sin(((double)time) * 2 * Math.PI / 30));
	}
}

// Sorts map items by their y-values
class ItemYComparator implements Comparator<MapItem>
{
	// Returns a negative value if a.y > b.y
	// Returns zero if a.y == b.y
	// Returns a positive value if a.y < b.y
	public int compare(MapItem a, MapItem b) {
		return a.y - b.y;
	}
}

class Point{
	int x;
	int y;
	Point(int x, int y){
		this.x = x;
		this.y = y;
	}
}





