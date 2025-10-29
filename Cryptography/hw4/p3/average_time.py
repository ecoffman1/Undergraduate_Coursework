import time
from Crypto.PublicKey import RSA
from Crypto.Random import get_random_bytes
from Crypto.Signature import pkcs1_15
import hmac
import hashlib
from Crypto.Hash import SHA256

def RSA_sign(key,message):
    hash = SHA256.new(message)
    signature = pkcs1_15.new(key).sign(hash)
    return signature

def RSA_verify(key,signature,message):
    hash = SHA256.new(message)
    try:
        pkcs1_15.new(key).verify(hash, signature)
        return True
    except(ValueError, TypeError):
        return False
    
def HMAC_generate(key,message):
    hmac_object = hmac.new(key, message, hashlib.sha256)
    hash = hmac_object.hexdigest()      
    

message = input("Please input an 7 byte message for testing >> ").encode('utf-8')

#RSA
RSA_result = {"Sign":None,"Verify":None}

key = RSA.generate(2048)
sign_times = []
verify_times = []
for i in range(100):
    start_time = time.perf_counter()
    signature = RSA_sign(key,message)
    end_time = time.perf_counter()

    time_taken = (end_time-start_time)*1000
    sign_times.append(time_taken)

    start_time = time.perf_counter()
    RSA_verify(key,signature,message)
    end_time = time.perf_counter()

    time_taken = (end_time-start_time)*1000
    verify_times.append(time_taken)
    
RSA_result["Sign"] = sum(sign_times) / len(sign_times)
RSA_result["Verify"] = sum(verify_times) / len(verify_times)

#HMAC
HMAC_result = {"Generate":None}

key = get_random_bytes(16)

generate_times = []
for i in range(100):
    start_time = time.perf_counter()
    HMAC_generate(key,message)
    end_time = time.perf_counter()

    time_taken = (end_time-start_time)*1000
    generate_times.append(time_taken)

HMAC_result["Generate"] = sum(generate_times) / len(generate_times)

print("")

print("RSA:")
print(f"\tSign: {round(RSA_result["Sign"],3)}ms")
print(f"\tVerify: {round(RSA_result["Verify"],3)}ms")

print("")

print("HMAC:")
print(f"\tGenerate: {round(HMAC_result["Generate"],3)}ms")





