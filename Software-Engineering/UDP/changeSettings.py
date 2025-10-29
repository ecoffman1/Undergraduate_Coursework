import json
import os

# call me when the button is pressed to change json configuration file
# setting is name of setting (ex: "udp_ip")
# newValue is what it will be changed to

# Get the path to config.json
current_script_dir = os.path.dirname(os.path.abspath(__file__))
config_path = os.path.join(current_script_dir, 'config.json')

def changeSettings(setting, newValue):
    try:

        # get user input for setting and new value
        #setting = input("Enter the setting you would like to change: ").strip()
        #newValue = input(f"Enter the value you would like to change {setting} to: ").strip()

        # read existing configuration
        with open(config_path, "r") as config_file:
            config = json.load(config_file)

        # make sure the setting is a setting in configuration file
        if setting not in config:
            print(f"Error: '{setting}' does not exist in config.json. No changes made.")
            return  # exit the function without making changes

        # update the specific setting
        config[setting] = newValue

        # write the updated configuration back to the file
        with open(config_path, "w") as config_file:
            json.dump(config, config_file, indent=4)

        print(f"Updated '{setting}' to '{newValue}' in config.json")
    
    except FileNotFoundError:
        print("Error: config.json not found!")
    except json.JSONDecodeError:
        print("Error: Invalid JSON format in config.json!")

