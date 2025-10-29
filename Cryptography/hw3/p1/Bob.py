import json
from base64 import b64decode
from Crypto.Cipher import AES
from Crypto.Util.Padding import unpad

try:
    with open("key.json", 'r') as file:
        key_info = json.load(file)
        key = key_info["key"]
        key = b64decode(key)
    with open("ctext.json", 'r') as file:
        b64 = json.load(file)
    iv = b64decode(b64['iv'])
    ct = b64decode(b64['ciphertext'])
    print(f"Recieved Ciphertext:{b64['ciphertext']}")
    cipher = AES.new(key, AES.MODE_CBC, iv)
    pt = unpad(cipher.decrypt(ct), AES.block_size)
    print(f"Plaintext: {pt.decode("utf-8")}")

except (ValueError, KeyError):
    print("Incorrect decryption")