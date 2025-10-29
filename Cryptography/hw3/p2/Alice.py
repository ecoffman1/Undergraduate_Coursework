from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
import json
from base64 import b64encode

message = input("Please input an 18 byte message for Alice to send >> ").encode("utf-8")
key = RSA.importKey(open('publickey.pem').read())
cipher = PKCS1_OAEP.new(key)
ciphertext = cipher.encrypt(message)
ciphertext = b64encode(ciphertext).decode('utf-8')
result = {"ciphertext":ciphertext}
print(f"Ciphertext:{ciphertext}")

with open("ctext.json", 'w') as f:
    json.dump(result, f, indent=4)