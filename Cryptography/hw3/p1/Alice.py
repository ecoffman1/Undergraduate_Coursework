import json
from base64 import b64encode
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
from Crypto.Random import get_random_bytes

message = input("Please input an 18 byte message for Alice to send >> ")
data = message.encode('utf-8')

key = get_random_bytes(16)

shared_key = b64encode(key).decode('utf-8')
result = {'key':shared_key}
file_name = "key.json"
with open(file_name, "w") as json_file:
    json.dump(result, json_file, indent=4)

cipher = AES.new(key, AES.MODE_CBC)
ct_bytes = cipher.encrypt(pad(data, AES.block_size))
iv = b64encode(cipher.iv).decode('utf-8')
ct = b64encode(ct_bytes).decode('utf-8')
result = {'iv':iv, 'ciphertext':ct}
print(f"Ciphertext:{ct}")

file_name = "ctext.json"
with open(file_name, "w") as json_file:
    json.dump(result, json_file, indent=4)
