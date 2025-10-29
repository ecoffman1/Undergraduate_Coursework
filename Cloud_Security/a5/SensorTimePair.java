import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The {@code SensorTimePair} class represents a composite key that consists of a sensor ID and a timestamp.
 * This class is used in Hadoop MapReduce jobs for secondary sorting, where records are sorted first by sensor ID
 * and then by timestamp. The class implements the {@code Writable} and {@code WritableComparable} interfaces,
 * which allow it to be used as a key in Hadoop's MapReduce framework.
 */
public class SensorTimePair implements Writable, WritableComparable<SensorTimePair> {
    /** Define a few constants here. */

    static final int NUM_SENSORS = 100;
    static final int NUM_REDUCERS = 3;

    /** The sensor ID, represented as an {@code IntWritable}. */
    private IntWritable sensorId;

    /** The timestamp, represented as a {@code Text}. */
    private Text timestamp;

    /**
     * Default constructor that initializes the {@code sensorId} and {@code timestamp} fields
     * to empty {@code IntWritable} and {@code Text} objects, respectively.
     */
    public SensorTimePair() {
        this.sensorId = new IntWritable();
        this.timestamp = new Text();
    }

    /**
     * Sets the sensor ID.
     *
     * @param sensorId The integer value representing the sensor ID.
     */
    public void setSensorId(int sensorId) {
        this.sensorId = new IntWritable(sensorId);
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp The string value representing the timestamp.
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = new Text(timestamp);
    }

    /**
     * Returns the sensor ID.
     *
     * @return The sensor ID as an {@code IntWritable}.
     */
    public IntWritable getSensorId() {
        return this.sensorId;
    }

    /**
     * Returns the timestamp.
     *
     * @return The timestamp as a {@code Text}.
     */
    public Text getTimestamp() {
        return this.timestamp;
    }

    /**
     * Compares this {@code SensorTimePair} to another {@code SensorTimePair}.
     * Sorting is performed first by sensor ID and then by timestamp.
     *
     * @param other The other {@code SensorTimePair} to compare against.
     * @return A negative integer, zero, or a positive integer as this object is less than, equal to,
     * or greater than the specified object.
     */
    @Override
    public int compareTo(SensorTimePair other) {
        int returnVal = this.sensorId.compareTo(other.getSensorId());
        if(returnVal != 0){
            return returnVal;
        }
        returnVal = this.timestamp.compareTo(other.getTimestamp());
        return returnVal;
    }

    /**
     * Reads the {@code SensorTimePair} from a {@code DataInput} stream.
     *
     * @param in The input stream from which to read the data.
     * @return A {@code SensorTimePair} object populated with data from the input stream.
     * @throws IOException If an error occurs during reading.
     */
    public static SensorTimePair read(DataInput in) throws IOException {
        SensorTimePair sensorTimePair = new SensorTimePair();
        sensorTimePair.readFields(in);
        return sensorTimePair;
    }

    /**
     * Serializes the object by writing its fields to a {@code DataOutput} stream.
     *
     * @param out The output stream to which the data is written.
     * @throws IOException If an error occurs during writing.
     */
    @Override
    public void write(DataOutput out) throws IOException {
        this.sensorId.write(out);
        this.timestamp.write(out);
    }

    /**
     * Deserializes the object by reading its fields from a {@code DataInput} stream.
     *
     * @param in The input stream from which the data is read.
     * @throws IOException If an error occurs during reading.
     */
    @Override
    public void readFields(DataInput in) throws IOException {
        this.sensorId.readFields(in);
        this.timestamp.readFields(in);
    }

    /**
     * Returns a string representation of the {@code SensorTimePair}.
     * The string format is: "sensorId, timestamp,"
     *
     * @return A string representing the sensor ID and timestamp.
     */
    @Override
    public String toString() {
        return this.sensorId + ", " + this.timestamp + ",";
    }

    /**
     * Determines whether two {@code SensorTimePair} objects are equal.
     * Two objects are considered equal if their sensor ID and timestamp are equal.
     *
     * @param object The object to compare with this {@code SensorTimePair}.
     * @return {@code true} if the objects are equal; {@code false} otherwise.
     */
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        SensorTimePair that = (SensorTimePair) object;
        return java.util.Objects.equals(sensorId, that.sensorId) && java.util.Objects.equals(timestamp, that.timestamp);
    }

    /**
     * Returns the hash code of this {@code SensorTimePair}.
     * The hash code is computed using the sensor ID.
     *
     * @return The hash code for this {@code SensorTimePair}.
     */
    public int hashCode() {
        int partition = NUM_SENSORS / NUM_REDUCERS;
        int id = this.sensorId.get();
        if(id >= 99){
            id = 98;
        }
        return id / partition;
    }
}