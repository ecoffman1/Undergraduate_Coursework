# Function to print the Sudoku board
def print_board(board):
    for i in range(len(board)):
        if i % 3 == 0 and i != 0:
            print("-" * 21)
        for j in range(len(board[0])):
            if j % 3 == 0 and j != 0:
                print("|", end=" ")
            if board[i][j] == 0:
                print(".", end=" ")
            else:
                print(board[i][j], end=" ")
        print()
# Find the next empty cell (denoted by 0)
def find_empty(board):
    for i in range(len(board)):
        for j in range(len(board[0])):
            if(board[i][j] == 0):
                return i, j
    return -1, -1
# Check if the current value is valid at the given position
def is_valid(board , num, pos):
    x, y = pos
    if num in board[x]:
        return False
    for i in range(len(board)):
        if num == board[i][y]:
            return False
    quandrant_x = x // 3 * 3
    quandrant_y = y // 3 * 3
    for i in range(3):
        for j in range(3):
            cur_x = quandrant_x + i
            cur_y = quandrant_y + j
            if num == board[cur_x][cur_y]:
                return False
    return True
# Main backtracking solver
def solve_sudoku(board):
    x,y = find_empty(board)
    if x == -1:
        return True
    for i in range(9):
        num = i + 1
        if(not is_valid(board,num,[x,y])):
            continue
        board[x][y] = num
        if solve_sudoku(board):
            return True
        board[x][y] = 0    
    return False
# Sudoku Puzzle (use the image provided to fill in the board)
sudoku_board = [
    [0,1,3,0,0,0,7,0,0],
    [0,0,0,5,2,0,4,0,0],
    [0,8,0,0,0,0,0,0,0],
    [0,0,0,0,1,0,0,8,0],
    [9,0,0,0,0,0,6,0,0],
    [2,0,0,0,0,0,0,0,0],
    [0,5,0,4,0,0,0,0,0],
    [7,0,0,6,0,0,0,0,0],
    [0,0,0,0,0,0,0,1,0],
]
# Solve and print
print("Initial Sudoku Puzzle:")
print_board(sudoku_board)
value = solve_sudoku(sudoku_board)
if value:
    print("\nSolved Sudoku Puzzle:")
    print_board(sudoku_board)
else:
    print('No solution exists')
