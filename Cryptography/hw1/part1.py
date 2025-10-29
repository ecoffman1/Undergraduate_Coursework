alphabet = list("abcdefghijklmnopqrstuvwxyz")

def cleanString(string):
    for char in string:
        if char not in alphabet and char != " ":
            string = string.replace(char,"")
    return string

def encode(plaintext,n):
    plaintext = plaintext.lower()
    plaintext = cleanString(plaintext)
    ciphertext = ""

    for char in plaintext:
        if char == " ":
            ciphertext += " "
            continue
        index = alphabet.index(char)
        index = (index+n)%26
        ciphertext += alphabet[index]
    
    return ciphertext

def decode(ciphertext,n):
    ciphertext = ciphertext.lower()
    ciphertext = cleanString(ciphertext)
    plaintext = ""

    for char in ciphertext:
        if char == " ":
            plaintext += " "
            continue
        index = alphabet.index(char)
        index = (index-n)%26
        plaintext += alphabet[index]

    return plaintext

running = True

while running:
    print("Please Choose a Menu Option:\n1. Encode\n2. Decode\n3. Quit\n")
    option = input(">> ")

    if option == "1":
        plaintext = input("Please Enter Plaintext >> ")
        offset = input("Please Enter Offset >> ")
        offset = int(offset)
        print(f"Encoded Message: {encode(plaintext,offset)}")
    elif option == "2":
        ciphertext = input("Please Enter Ciphertext >> ")
        offset = input("Please Enter Offset >> ")
        offset = int(offset)
        print(f"Decoded Message: {decode(ciphertext,offset)}")
    elif option == "3":
        running = False
    else:
        print("Not a Valid Menu Option!")

    print("\n")

