import json
import time
from base64 import b64encode
from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from Crypto.Random import get_random_bytes

def AES_encrypt(key,data):
    cipher = AES.new(key, AES.MODE_CBC)
    ciphertext = cipher.encrypt(pad(data, AES.block_size))
    return cipher.iv, ciphertext

def AES_decrypt(key,iv,ciphertext):
    cipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext = unpad(cipher.decrypt(ciphertext), AES.block_size)

def RSA_encrypt(key,data):
    cipher = PKCS1_OAEP.new(key)
    ciphertext = cipher.encrypt(data)
    return ciphertext

def RSA_decrypt(key,ciphertext):
    cipher = PKCS1_OAEP.new(key)
    plaintext = cipher.decrypt(ciphertext)
    

message = input("Please input an 7 byte message for testing >> ")

AES_result = {128:{"encrypt":None,"decrypt":None},192:{"encrypt":None,"decrypt":None},256:{"encrypt":None,"decrypt":None}}

for size in AES_result:
    key = get_random_bytes(int(size//8))
    encrypt_times = []
    decrypt_times = []
    for i in range(100):
        new_message = message + str(i)
        data = new_message.encode('utf-8')
        start_time = time.perf_counter()
        iv, ciphertext = AES_encrypt(key,data)
        end_time = time.perf_counter()

        time_taken = (end_time-start_time)*1000
        encrypt_times.append(time_taken)

        start_time = time.perf_counter()
        AES_decrypt(key,iv,ciphertext)
        end_time = time.perf_counter()

        time_taken = (end_time-start_time)*1000
        decrypt_times.append(time_taken)

    AES_result[size]["encrypt"] = sum(encrypt_times) / len(encrypt_times)
    AES_result[size]["decrypt"] = sum(decrypt_times) / len(decrypt_times)

RSA_result = {1024:{"encrypt":None,"decrypt":None},2048:{"encrypt":None,"decrypt":None},4096:{"encrypt":None,"decrypt":None}}

for size in RSA_result:
    key = RSA.generate(size)
    encrypt_times = []
    decrypt_times = []
    for i in range(100):
        new_message = message + str(i)
        data = new_message.encode('utf-8')
        start_time = time.perf_counter()
        ciphertext = RSA_encrypt(key,data)
        end_time = time.perf_counter()

        time_taken = (end_time-start_time)*1000
        encrypt_times.append(time_taken)

        start_time = time.perf_counter()
        RSA_decrypt(key,ciphertext)
        end_time = time.perf_counter()

        time_taken = (end_time-start_time)*1000
        decrypt_times.append(time_taken)
    
    RSA_result[size]["encrypt"] = sum(encrypt_times) / len(encrypt_times)
    RSA_result[size]["decrypt"] = sum(decrypt_times) / len(decrypt_times)

print("AES:")
for size in AES_result:
    print(f"\t{size}-bit:")
    print(f"\t\tEncrypt: {round(AES_result[size]["encrypt"],3)}ms")
    print(f"\t\tDecrypt: {round(AES_result[size]["decrypt"],3)}ms")

print("")

print("RSA:")
for size in RSA_result:
    print(f"\t{size}-bit:")
    print(f"\t\tEncrypt: {round(RSA_result[size]["encrypt"],3)}ms")
    print(f"\t\tDecrypt: {round(RSA_result[size]["decrypt"],3)}ms")





