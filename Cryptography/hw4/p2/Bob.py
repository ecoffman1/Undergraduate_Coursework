from Crypto.PublicKey import RSA
from Crypto.Signature import pkcs1_15
from Crypto.Hash import SHA256
import json
from base64 import b64decode

key = RSA.importKey(open('publickey.pem').read())

with open('sigtext.json', 'r') as f:
    sigtext = json.load(f)
    message = b64decode(sigtext["message"])
    signature = b64decode(sigtext["signature"])

print(f"Derived Signature {signature}")
hash = SHA256.new(message)

try:
    pkcs1_15.new(key).verify(hash, signature)
    print("The signature is valid.")
except (ValueError, TypeError):
    print ("The signature is not valid.")

