from Crypto.Hash import SHA256
from Crypto.Random import get_random_bytes

dict = {}

count = 0
while True:
    count += 1
    message = get_random_bytes(16)
    hash = SHA256.new(message).digest()
    byte = hash[0]
    if byte in dict:
        break
    else:
        dict[byte] = [message,hash]

print(f"Matching messages found after {count} iterations: \n\t1:Message:{dict[byte][0]}, \n\t  Digest:{dict[byte][1]}\n\t2:Message:{message}, \n\t  Digest:{hash}")

counts = []
for i in range(20):
    dict = {}
    count = 0
    while True:
        count += 1
        message = get_random_bytes(16)
        hash = SHA256.new(message).digest()
        byte = hash[0]
        if byte in dict:
            break
        else:
            dict[byte] = False
    counts.append(count)

average_count = round(sum(counts)/len(counts),3)

print(f"\nAverage Number of Trials: {average_count}")