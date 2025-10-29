from Crypto.PublicKey import RSA
from Crypto.Signature import pkcs1_15
from Crypto.Hash import SHA256
import json
from base64 import b64encode

message = input("Please input an 18 byte message for Alice to send >> ").encode("utf-8")
key = RSA.importKey(open('privatekey.pem').read())

hash = SHA256.new(message)

signature = pkcs1_15.new(key).sign(hash)
signature = b64encode(signature).decode('utf-8')

message =  b64encode(message).decode('utf-8')

result = {"message": message, "signature":signature}


with open("sigtext.json", 'w') as f:
    json.dump(result, f, indent=4)