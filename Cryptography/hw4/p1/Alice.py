import json
from base64 import b64encode
import hmac
import hashlib
from Crypto.Random import get_random_bytes

message = input("Please input an 18 byte message for Alice to send >> ")
data = message.encode('utf-8')

key = get_random_bytes(16)

shared_key = b64encode(key).decode('utf-8')
result = {'key':shared_key}
file_name = "key.json"
with open(file_name, "w") as json_file:
    json.dump(result, json_file, indent=4)

hmac_object = hmac.new(key, data, hashlib.sha256)
hash = hmac_object.hexdigest()

result = {'message':message,'hash':hash}

file_name = "mactext.json"
with open(file_name, "w") as json_file:
    json.dump(result, json_file, indent=4)
