print("Ethan Coffman, Factorial Calculator")

#Get number from user
num = input("Enter Number Here >> ")
num = int(num)

#Loop through numbers to get factorial 
for i in range (1,num):
    num = num*i

#Print out result  
print(f"your number is {num}")