from Crypto.PublicKey import RSA

mykey = RSA.generate(2048)

with open("publickey.pem", "wb") as f:
    data = mykey.public_key().export_key()
    f.write(data)

pwd = b'password'
with open("privatekey.pem", "wb") as f:
    data = mykey.export_key()
    f.write(data)