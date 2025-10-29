from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
import json
from base64 import b64decode

pwd = b'secret'
key = RSA.importKey(open('privatekey.pem').read())
cipher = PKCS1_OAEP.new(key)

with open('ctext.json', 'r') as f:
    ctext = json.load(f)
    ciphertext = ctext["ciphertext"]
    ciphertext = b64decode(ciphertext)
    print(f"Receieved Ciphertext:{ctext['ciphertext']}")
message = cipher.decrypt(ciphertext).decode('utf-8')

print(f"Message: {message}")

