from Database import DB

def print_record_number(recordNum, COLLEGE_ID, STATE, CITY, NAME):
    print(f"Record {recordNum}, ID: {COLLEGE_ID[0]:<10} STATE: {STATE[0]:<15} CITY: {CITY[0]:<15} NAME: {NAME[0]:<40}")


def print_record_id(COLLEGE_ID, STATE, CITY, NAME):
    print(f"ID: {COLLEGE_ID[0]:<10} STATE: {STATE[0]:<15} CITY: {CITY[0]:<15} NAME: {NAME[0]:<40}")

def main():

    def read_record(recordNum):
        status = sample.readRecord(recordNum, COLLEGE_ID, STATE, CITY, NAME)
        if status:
            print_record_number(recordNum, COLLEGE_ID, STATE, CITY, NAME)
        else:
            print("Failed to read record number ", recordNum)

    filepath = "small-colleges"
    # Create an instance of the DB class
    sample = DB()

    # Create a database 
    sample.createDB(filepath)

    # opens filepath and sets the record size
    sample.open(filepath)

    COLLEGE_ID = [""]
    STATE = [""]
    CITY = [""]
    NAME = [""]

    print()
    read_record(0)
    print()
    read_record(14)
    print()
    read_record(6)
    print()
    read_record(-1)
    print()
    read_record(1000)

    #Print Menu Options
    print("""
        1) Create new database
        2) Open database
        3) Close database
        4) Display record
        5) Update record
        6) Create report
        7) Add record
        8) Delete record
        9) Quit
        """)

    while(True):
        option = input("Choose a menu option")
    
main()