
def DFS(A):
    target = (len(A)-1,len(A[0])-1)
    def recurse(x,y,path):
        if(A[x][y] == 0):
            return -1
        path.append((x,y))
        if (x,y) == target:
            return path
        if x+1 < len(A) and not (x+1,y) in path:
            result = recurse(x+1,y,path)
            if result != -1:
                return result
        if y+1 < len((A)[0]) and not (x,y+1) in path:
            result = recurse(x,y+1,path)
            if result != -1:
                return result
        if x-1 > 0 and not (x-1,y) in path:
            result = recurse(x-1,y,path)
            if result != -1:
                return result
        if y-1 > 0 and not (x,y-1) in path:
            result = recurse(x,y-1,path)
            if result != -1:
                return result
        return -1
    result = recurse(0,0,[])
    if result == -1:
        print(result)
    output = ""
    for i in range(len(result)):
        output += f"{result[i]}"
        if(i == len(result)-1):
            print(output)
            return
        output += "->"

def BFS(A):
    queue = []
    expended = []
    fringe = []
    target = (len(A)-1,len(A[0])-1)

    queue.append((0,0))
    fringe.append([(0,0)])
    
    result = []
    while len(queue) != 0:
        node = queue.pop(0)
        x,y = node
        expended.append(node)
        if(node == target):
            print(node)
            for path in fringe:
                if path[-1] == target:
                    result = path
                    break
        neighbors = []
        if x+1 < len(A) and not (x+1,y) in expended and A[x+1][y] == 1:
            queue.append((x+1,y))
            neighbors.append((x+1,y))
        if y+1 < len(A[0]) and not (x,y+1) in expended and A[x][y+1] == 1:
            queue.append((x,y+1))
            neighbors.append((x,y+1))
        if x-1 > 0 and not (x-1,y) in expended and A[x-1][y] == 1:
            queue.append((x-1,y))
            neighbors.append((x-1,y))
        if y-1 > 0 and not (x,y-1) in expended and A[x][y-1] == 1:
            queue.append((x,y-1))
            neighbors.append((x,y-1))

        
        for path in fringe:
            if path[-1] == node:
                fringe.remove(path)
                for neighbor in neighbors:
                    new_path = path.copy()
                    new_path.append(neighbor)
                    fringe.append(new_path)


    if result == []:
        print(-1)
        return
    output = ""
    for i in range(len(result)):
        output += f"{result[i]}"
        if(i == len(result)-1):
            print(output)
            return
        output += "->"
         


array = [[1,1,1,0,1],
         [0,0,1,0,0],
         [1,1,1,1,1],
         [1,1,0,1,1],
         [1,1,0,1,1]
         ]
DFS(array)
BFS(array)

