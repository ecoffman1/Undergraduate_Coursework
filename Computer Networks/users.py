import math

#max number of users allowed
max = 5
#probability any user will be on at a time
prob_user = .4
#maximum tolerance for blocking
block = .2

def test(users):
    sum = 0
    for i in range(max+1, users+1):
        sum += math.comb(users,i)*(prob_user**i)*((1-prob_user)**(users-i))
    return sum

#starting test
num_users = max+1

while(True):
    prob = test(num_users)
    if(prob < block):
        print(f"{num_users} results in a {prob} blocking probability, continuing...")
        num_users += 1
    else:
        print(f"{num_users} results in a {prob} blocking probability, done.")
        break
    


