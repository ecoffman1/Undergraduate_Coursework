alphabet = list("abcdefghijklmnopqrstuvwxyz")

def cleanString(string):
    for char in string:
        if char not in alphabet and char != " ":
            string = string.replace(char,"")
    return string

def decode(ciphertext,n):
    plaintext = ""

    for char in ciphertext:
        index = alphabet.index(char)
        index = (index-n)%26
        plaintext += alphabet[index]

    return plaintext
def createVocab(filename):
    with open(filename, 'r', encoding='utf-8') as file:
        vocab = file.read()
        vocab = vocab.lower()
        vocab = cleanString(vocab)
        vocab = vocab.split(" ")
    return vocab

def testWord(ciphertext,vocab):
    for i in range(1,26):
        plaintext = decode(ciphertext, i)
        if plaintext in vocab:
            return i

def optionOne():
    ciphertext = input("Please Input Cipher Text >> ")
    ciphertext = ciphertext.lower()
    ciphertext = cleanString(ciphertext) 
    ciphertext = ciphertext.split(" ")

    filename = input("Please Enter the File Name For the Vocab >> ")
    print("")
    vocab = createVocab(filename)

    key = None
    i = 0
    while not key and i < len(ciphertext):
        key = testWord(ciphertext[i], vocab)
        i += 1
    if not key:
        print("No Valid Key Found!")
        return
    plaintext = ""
    for word in ciphertext:
        plaintext += decode(word,key) + " "

    print(f"Plaintext: {plaintext}")
    print(f"Key: {key}")
    print()

running = True
while running:
    print("Please Choose a Menu Option:\n1. Brute Foce\n2. Quit\n")
    option = input(">> ")

    if option == "1":
        optionOne()
    elif option == "2":
        running = False
    else:
        "Not a Valid Menu Option"
