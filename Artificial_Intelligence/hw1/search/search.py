# search.py
# ---------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
#
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


"""
In search.py, you will implement generic search algorithms which are called by
Pacman agents (in searchAgents.py).
"""

import util

class SearchProblem:
    """
    This class outlines the structure of a search problem, but doesn't implement
    any of the methods (in object-oriented terminology: an abstract class).

    You do not need to change anything in this class, ever.
    """

    def getStartState(self):
        """
        Returns the start state for the search problem.
        """
        util.raiseNotDefined()

    def isGoalState(self, state):
        """
          state: Search state

        Returns True if and only if the state is a valid goal state.
        """
        util.raiseNotDefined()

    def getSuccessors(self, state):
        """
          state: Search state

        For a given state, this should return a list of triples, (successor,
        action, stepCost), where 'successor' is a successor to the current
        state, 'action' is the action required to get there, and 'stepCost' is
        the incremental cost of expanding to that successor.
        """
        util.raiseNotDefined()

    def getCostOfActions(self, actions):
        """
         actions: A list of actions to take

        This method returns the total cost of a particular sequence of actions.
        The sequence must be composed of legal moves.
        """
        util.raiseNotDefined()


def tinyMazeSearch(problem):
    """
    Returns a sequence of moves that solves tinyMaze.  For any other maze, the
    sequence of moves will be incorrect, so only use this for tinyMaze.
    """
    from game import Directions
    n = Directions.NORTH
    s = Directions.SOUTH
    w = Directions.WEST
    return  [n,s,s, s, w, s, w, w, s, w]

#Returns sequence to hit a wall for condition
def touch_wall(problem):
    from game import Directions
    n = Directions.NORTH
    e = Directions.EAST
    s = Directions.SOUTH
    w = Directions.WEST
    direction = None
    for neighbor in problem.getSuccessors(problem.getStartState()):
        node,direction,cost = neighbor
        if problem.isWall(node):
            direction = direction
            break
    if direction == n:
        return [n,s] 
    if direction == e:
        return [e,w]
    if direction == s:
        return [s,n]
    if direction == w:
        return [w,e]  

def convert(problem,list):
    from game import Directions
    n = Directions.NORTH
    s = Directions.SOUTH
    w = Directions.WEST
    e = Directions.EAST

    output = touch_wall(problem)
    for i in range(1,len(list)):
        first_x, first_y = list[i-1]
        second_x, second_y = list[i]
        if first_x < second_x:
            output.append(e)
        if first_x > second_x:
            output.append(w)
        if first_y < second_y:
            output.append(n)
        if first_y > second_y:
            output.append(s)
    return output

def depthFirstSearch(problem):
    stack = util.Stack()
    stack.push(problem.getStartState())
    fringe = [[problem.getStartState()]]
    visited = []
    result = []
    print(problem.getSuccessors(problem.getStartState()))
    while not(stack.isEmpty()):
        node = stack.pop()
        visited.append(node)
        if problem.isGoalState(node):
            for i in range(len(fringe)):
                if fringe[-i][-1] == node:
                    result = fringe[-i]
                    break
        options = problem.getSuccessors(node)
        neighbors = []
        for option in options:
            loc = option[0]
            if not problem.isWall(loc) and not loc in visited:
                neighbors.append(loc)
                stack.push(loc)
        for path in fringe:
            if path[-1] == node:
                fringe.remove(path)
                for neighbor in neighbors:
                    new_path = path.copy()
                    new_path.append(neighbor)
                    fringe.append(new_path)

    return convert(problem,result)

def breadthFirstSearch(problem):
    queue = util.Queue()
    queue.push(problem.getStartState())
    fringe = [[problem.getStartState()]]
    visited = []
    result = []

    while not(queue.isEmpty()):
        node = queue.pop()
        visited.append(node)
        if problem.isGoalState(node):
            for i in range(len(fringe)):
                if fringe[i][-1] == node:
                    result = fringe[i]
                    break
        options = problem.getSuccessors(node)
        neighbors = []
        for option in options:
            loc = option[0]
            if not problem.isWall(loc) and not loc in visited:
                neighbors.append(loc)
                queue.push(loc)
        for path in fringe:
            if path[-1] == node:
                fringe.remove(path)
                for neighbor in neighbors:
                    new_path = path.copy()
                    new_path.append(neighbor)
                    fringe.append(new_path)

    return convert(problem,result)

def uniformCostSearch(problem):

    from game import Directions
    n = Directions.NORTH
    s = Directions.SOUTH
    queue = util.PriorityQueue()
    queue.push([problem.getStartState(),touch_wall(problem),0],0)
    visited = {}

    while not queue.isEmpty():
        node,actions,priority = queue.pop()
        if not node in visited or priority < visited[node]:
            visited[node] = priority
        else:
            continue

        if problem.isGoalState(node):
            return actions

        for neighbor in problem.getSuccessors(node):
            node,action,cost = neighbor
            if problem.isWall(node):
                continue
            new_actions = actions.copy()
            new_actions.append(action)
            new_priority = priority + cost
            queue.push([node,new_actions,new_priority],new_priority)
    util.raiseNotDefined()

def nullHeuristic(state, problem=None):
    """
    A heuristic function estimates the cost from the current state to the nearest
    goal in the provided SearchProblem.  This heuristic is trivial.
    """
    return 0

def aStarSearch(problem, heuristic=nullHeuristic):
    from game import Directions
    w = Directions.WEST
    e = Directions.EAST
    queue = util.PriorityQueue()
    queue.push([problem.getStartState(),touch_wall(problem),0],0)
    visited = {}

    while not queue.isEmpty():
        node,actions,priority = queue.pop()
        if not node in visited or priority < visited[node]:
            visited[node] = priority
        else:
            continue

        if problem.isGoalState(node):
            return actions

        for neighbor in problem.getSuccessors(node):
            node,action,cost = neighbor
            if problem.isWall(node):
                continue
            new_actions = actions.copy()
            new_actions.append(action)
            path_cost = priority + cost
            new_priority = priority + heuristic(node,problem)

            queue.push([node,new_actions,path_cost],new_priority)
    util.raiseNotDefined()


# Abbreviations
bfs = breadthFirstSearch
dfs = depthFirstSearch
astar = aStarSearch
ucs = uniformCostSearch
