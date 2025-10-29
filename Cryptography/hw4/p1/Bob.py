import json
from base64 import b64decode
import hmac
import hashlib


with open("key.json", 'r') as file:
    key_info = json.load(file)
    key = key_info["key"]
    key = b64decode(key)
with open("mactext.json", 'r') as file:
    mactext = json.load(file)
    message = mactext['message']
    data = message.encode('utf-8')
    recieved_hash = mactext['hash']

hmac_object = hmac.new(key, data, hashlib.sha256)
calculated_hash = hmac_object.hexdigest()
print(f"Derived Hash: {calculated_hash}")

if(calculated_hash == recieved_hash):
    print("Message Verified")
else:
    print("Message Modified")


