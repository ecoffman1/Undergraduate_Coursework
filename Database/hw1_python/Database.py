import csv
import os.path
import configparser

class DB:

    #default constructor
    def __init__(self):
        self.filestream = None
        self.numRecords = -1
        self.recordSize = -1
        self.sizes = []

    #create config
    def create_config(self, filename):
        config = configparser.ConfigParser()

        sizes = self.determineRecordSize(filename)
        totalSize = 82
        # for n in sizes:
        #     totalSize += n
        config[filename] = {'numRecords':self.getNumRecords(filename), 'record_size': totalSize, 'sizes': sizes}
        # Write the configuration to a file
        with open('config.ini', 'w') as configfile:
            config.write(configfile)
    
    #read config or create one if it doesnt exist
    def read_config(self, filename):
        # Create a ConfigParser object
        config = configparser.ConfigParser()

        #check if database has config, if not create one
        if not(filename in config.sections()):
            self.create_config(filename)

        # Read the configuration file
        config.read('config.ini')
        self.recordSize = config.getint(filename,'record_size')
        self.numRecords = config.getint(filename,'numRecords')
        self.sizes = config.get(filename,'sizes')

    #return number of records
    def getNumRecords(self, filename):
        csv_filename = filename + ".csv"
        with open(csv_filename, "r") as csv_file:
            reader = csv.reader(csv_file)
            num_lines = sum(1 for row in reader)
        return num_lines

    def determineRecordSize(self, filename):
        csv_filename = filename + ".csv"
        with open(csv_filename, "r") as csv_file:
            largest = []
            first_row = next(csv.reader(csv_file))
            for i in range(len(first_row[0])):
                largest.append(len(first_row[0][i]))

            for line in csv_file:
                csv_reader = csv.reader([line])
                row = next(csv_reader)
                for i in range(len(row)):
                    if len(row[i]) > largest[i]:
                        largest[i] = len(row[i])
        return largest
                    
                

    # Formatting files with spaces so each field is fixed length, i.e. ID field has a fixed length of 10
    def writeRecord(self, filestream, COLLEGE_ID, STATE, CITY, NAME):
        try:
            filestream.write("{:10.10}".format(COLLEGE_ID))
            filestream.write("{:15.15}".format(STATE))
            filestream.write("{:15.15}".format(CITY))
            filestream.write("{:40.40}".format(NAME))
            filestream.write("\n")
            return True
        except IOError:
            return False
       
    #create database
    def createDB(self,filename):
        #Generate file names
        csv_filename = filename + ".csv"
        text_filename = filename + ".data"

        # Read the CSV file line by line and write into data file
        with open(csv_filename, "r") as csv_file, open(text_filename, "w") as outfile:
            for line in csv_file:
                csv_reader = csv.reader([line])
                row = next(csv_reader)
                self.writeRecord(outfile, row[0], row[1], row[2], row[3])

    # #read the database
    def open(self, filename):
        self.filestream = filename + ".data"
        
        if not os.path.isfile(self.filestream):
            print(str(self.filestream)+" not found")
        else:
            self.text_filename = open(self.filestream, 'r+')
            self.read_config(filename)

    def readRecord(self, recordNum, COLLEGE_ID, STATE, CITY, NAME):
        status = False

        if 0 <= recordNum < self.numRecords:
            self.text_filename.seek(recordNum * self.recordSize)
            line = self.text_filename.readline().rstrip('\n')
            COLLEGE_ID[0] = line[:10].strip()
            STATE[0] = line[10:25].strip()
            CITY[0] = line[25:40].strip()
            NAME[0] = line[40:80].strip()
            status = True
            
        return status

    #overwrite record method
    def overwriteRecord(self, record_num, COLLEGE_ID, STATE, CITY, NAME):
        try:
            # Calculate the byte offset of the record
            offset = record_num * self.recordSize

            # Move to the beginning of the specified record
            self.text_filename.seek(offset)

            # Call writeRecord to output the passed-in parameters
            self.writeRecord(self.text_filename, COLLEGE_ID, STATE, CITY, NAME)
            return True
        except IOError:
            return False


    def binarySearch(self, COLLEGE_ID, STATE, CITY, NAME):

        low = 0
        high = self.numRecords - 1
        self.found = False
        failure = False

        target_id = id[0]  # Do not strip leading zeros

        while not self.found and high >= low and not failure:
            self.middle = (low + high) // 2
            try:
                temp_id = [None]  # Use a list to hold the ID read from the record
                self.readRecord(self.middle, temp_id, STATE, CITY, NAME)
            except Exception as e:
                failure = True
                break

            mid_id = temp_id[0]  # Do not strip leading zeros


            if mid_id == target_id:
                self.found = True
                # print("Record found at record number: ", self.middle)
            elif mid_id < target_id:
                low = self.middle + 1
            else:
                high = self.middle - 1

        return self.found


    #close the database
    def close(self):
        self.text_filename.close()

