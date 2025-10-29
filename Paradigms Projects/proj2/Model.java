import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

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
		this.items = new ArrayList();
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
		MapItem i = new MapItem(x, y, this.current_item);
		this.items.add(i);
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
		this.items = new ArrayList();
		for(int i = 0; i < list.size(); i++){
			Json ob = list.get(i);
			items.add(new MapItem(ob));
		}
		this.comparator = new ItemYComparator();
	}
}
class MapItem
{
	public int x;
	public int y;
	public int type;

	MapItem(int x, int y, int type)
	{
		this.x = x;
		this.y = y;
		this.type = type;
	}
	MapItem(Json ob){
		this.x = (int)ob.getLong("x");
		this.y = (int)ob.getLong("y");
		this.type = (int)ob.getLong("type");
	}
	Json marshal(){
		Json ob = Json.newObject();
		ob.add("x", x);
		ob.add("y", y);
		ob.add("type", type);
		return ob;
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



